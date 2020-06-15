#include "DHT.h" // Lib sensor de umidade e temperatura
#include <WiFi.h> // Lib modulo wifi
#include "FirebaseESP32.h" // Lib firebase/esp
#include <ESP32Servo.h> // Lib Servo
#include <NTPClient.h> // Lib data e hora
#include <driver/adc.h>

#define ssid "dani" //nome da rede wifi
#define password "dani4321" //senha da rede wifi

#define FIREBASE_HOST "https://jardigi-9258a.firebaseio.com/"
#define FIREBASE_AUTH "vPajhu3L09h4ju3zQtm3uJagjwcQFkFAHaryIAV8"

#define pinoServo 33   // Porta Servo
#define sensorLDR 35   // Porta LDR
#define sensorSolo 32  // Porta Sensor Solo
#define rele  25       // Porta do Relé

DHT dht(26, DHT22);   // Porta Temperatura e Umidade
Servo Servo1;         // Cria objeto de um servo

// Define tempos para exibicao e envio de dados
unsigned long tempo_ant_sensores = 0;
unsigned long tempo_ant = 0;
unsigned long tempo_atual = 0;
unsigned long tempo_envio = 1800000;        // 30 minutos
const unsigned long tempo_exibicao = 5000;  // 5 segundos

double t;                            // Temperatura
double h;                            // Umidade
double lum;                          // Valor LDR
int solo;                            // Variável para valor do sensor de umidade do solo
double UmidadePercentual;            // Umidade percentual do solo
double umidadeMinimaPlanta = 40;     // Valor padrão da umidade minima da planta selecionada
double luminosidadeMaxima = 90;      // Valor padrão da luminosidade minima da planta selecionada
const int solo_seco = 3600;          // Calibra sensor solo
const int solo_molhado = 1300;       // Calibra sensor solo

FirebaseData firebaseData1, firebaseData2, firebaseData3;  // Cria objeto de dados firebase
FirebaseJson json;                                         // Cria um JSON para armazenar os dados do objeto

String caminhoPai = "Stream";
String caminhoFilho[5] = {"/Solenoide", "/Servo", "/Luminosidade", "/UmidadeSolo", "/TempoEnvio"};
size_t tamanhoCaminhoFilho = 5;

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);

String dataFormatada;
String data;
String hora;
String comandoSolenoide;
String comandoServo;

TaskHandle_t Tarefa1; // Usado para auxiliar com a criacao de tarefas nos cores
TaskHandle_t Tarefa2; // Usado para auxiliar com a criacao de tarefas nos cores

//----------------------------------------------------------------------------------//

void verifica_tempUmid() {

  h = dht.readHumidity();     // Lendo umidade
  t = dht.readTemperature(); // Lendo temperatura

  if (!isnan(t) && !isnan(h)) {
    Serial.print("Temperatura: ");
    Serial.print(t);
    Serial.print("ºC\n");

    Serial.print("Umidade do ar: ");
    Serial.print(h);
    Serial.print("%\n");

    json.set("Temperatura", t);
    json.set("Umidade", h);

    Firebase.set(firebaseData1, "TempoReal/Temperatura", t);
    Firebase.set(firebaseData1, "TempoReal/Umidade", h);
  }
  else {
    Serial.println("NaN Temperatura e Umidade");
  }

}

//----------------------------------------------------------------------------------//

void verifica_Lumin() {
  lum = analogRead(sensorLDR); // Ler o pino Analógico onde está o LDR

  // 4095 = 100% iluminido e 0 = 0% iluminado
  lum = 100 *  ((4095 - lum) / 4095); // Transforma valor lido em porcentagem

  Serial.print("Luminosidade: ");
  Serial.print(lum);
  Serial.print("%\n");
  json.set("Luminosidade", lum);

  Firebase.set(firebaseData1, "TempoReal/Luminosidade", lum);

}

//----------------------------------------------------------------------------------//

void verifica_umidSolo() {

  // Lê valor do sensor
  solo = analogRead(sensorSolo);

  // Realiza regra de 3 onde, variavel solo_seco = 0, variavel solo_molhado = 100
  UmidadePercentual = map(solo, solo_seco, solo_molhado, 0, 100);
  Serial.print("Umidade do solo: ");
  Serial.print(UmidadePercentual);
  Serial.print("%\n");

  json.set("UmidadeSolo", UmidadePercentual);

  Firebase.set(firebaseData1, "TempoReal/UmidadeSolo", UmidadePercentual);

}
//----------------------------------------------------------------------------------//

void movimentaServo() {


  // Abre a "estufa"
  if (comandoServo  == "ABRE"  || lum > luminosidadeMaxima) {
    // Vai para posição de abertura
    Servo1.write(180);

  } else if (comandoServo == "FECHA") {

    // Vai para a posição de fechamento
    Servo1.write(50);

    delay(500);
    // Reseta o estado para não forçar o motor
    comandoServo = "PARADO";

  } else {
    // Desliga servo
    Servo1.write(90);
  }

}

//----------------------------------------------------------------------------------//

void solenoide() {

  // O solo é regado automaticamente se a umidade dele estiver abaixo
  // de 30% ou se o usuário manualmente mandar um sinal via app
  if (comandoSolenoide == "ABERTA" || UmidadePercentual < umidadeMinimaPlanta) {

    // Abre solenoide
    digitalWrite(rele, LOW);
    delay(2000);
    comandoSolenoide = "FECHADA";

    // Fecha solenoide
  } else if (comandoSolenoide == "FECHADA") {
    digitalWrite(rele, HIGH);
  }
}

//----------------------------------------------------------------------------------//

void dataHora() {
  while (!timeClient.update()) {
    timeClient.forceUpdate();
  }
  // A data vem no seguinte formato:
  // 2020-05-28T16:00:13Z
  dataFormatada = timeClient.getFormattedDate();

  // Extrai data do formato padrão
  int splitT = dataFormatada.indexOf("T");   // Separa no meio
  data = dataFormatada.substring(0, splitT); // Data vai de 0 até o meio
  Serial.print("Data: ");
  Serial.println(data);

  // Extrai hora do formato padrão
  hora = dataFormatada.substring(splitT + 1, dataFormatada.length() - 1); // Hora vai de meio até o final
  Serial.print("Hora: ");
  Serial.println(hora);

  Firebase.set(firebaseData1, "TempoReal/Data", data);
  Firebase.set(firebaseData1, "TempoReal/Hora", hora);

  json.set("Data", data);
  json.set("Hora", hora);
}

//----------------------------------------------------------------------------------//

void conectaWifi() {
  WiFi.begin(ssid, password);

  // Tenta conectar ao wifi
  while (WiFi.status() != WL_CONNECTED) {
    Serial.println("Conectando ao WiFi...");
  }

  Serial.println("Conectado a rede WiFi");
  Serial.print("Ip: ");
  Serial.println(WiFi.localIP());

}

//----------------------------------------------------------------------------------//

void streamCallback(MultiPathStreamData stream)
{
  Serial.println("Dado na stream disponivel");

  size_t numFilho = sizeof(caminhoFilho) / sizeof(caminhoFilho[0]);

  Serial.println("----------------------------------------------------------------------------------");
  for (size_t i = 0; i < numFilho; i++)
  {
    if (stream.get(caminhoFilho[i]))
    {

      if (stream.dataPath == "/UmidadeSolo") {

        // Recebe dados do usuário de qual é a umidade necessária para a planta
        umidadeMinimaPlanta = stream.value.toDouble();
        Serial.println("Umidade minima da planta: " + stream.dataPath + ", Tipo: " + stream.type + ", Valor: " + umidadeMinimaPlanta);

      } else if (stream.dataPath == "/Luminosidade") {

        // Recebe dados do usuário de qual é a luminosidade necessária para a planta
        luminosidadeMaxima = stream.value.toDouble();
        Serial.println("Luminosidade maxima: " + stream.dataPath + ", Tipo: " + stream.type + ", Valor: " + luminosidadeMaxima);

      } else if (stream.dataPath == "/Solenoide") {

        // Recebe dados do usuário para regar ou não a planta
        comandoSolenoide = stream.value;
        Serial.println("Comando solenoide: " + stream.dataPath + ", Tipo: " + stream.type + ", Valor: " + comandoSolenoide);

      } else if (stream.dataPath == "/Servo") {

        // Recebe dados do usuário para abrir ou não a estufa
        comandoServo = stream.value;
        Serial.println("Comando servo: " + stream.dataPath + ", Tipo: " + stream.type + ", Valor: " + comandoServo);
      } else if (stream.dataPath == "/TempoEnvio") {

        // Recebe dados do usuário para tempo de envio
        tempo_envio = stream.value.toDouble();
        Serial.println("Tempo de Envio: " + stream.dataPath + ", Tipo: " + stream.type + ", Valor: " + tempo_envio);
      } else {
        Serial.println("Nada de novo na Stream");
      }
    }
  }

}

//----------------------------------------------------------------------------------//

void streamTimeoutCallback(bool timeout)
{
  if (timeout)
  {
    Serial.println();
    Serial.println("Timeout de stream, continuando streaming...");
  }
}

//----------------------------------------------------------------------------------//

void setup() {

  // Inicializa serial na velocidade 115200
  Serial.begin(115200);

  // Conecta ao wifi
  conectaWifi();

  // Começa conexão com banco de dados firebase
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);

  // Inicializa recebimento de comandos do firebase
  if (!Firebase.beginMultiPathStream(firebaseData2, caminhoPai, caminhoFilho, tamanhoCaminhoFilho))
  {
    Serial.println("------------------------------------");
    Serial.println("Erro ao começar a stream...");
    Serial.println("Motivo: " + firebaseData2.errorReason());
    Serial.println("------------------------------------");
  }

  // Listen para ouvir recebimento e envio de dados na stream
  Firebase.setMultiPathStreamCallback(firebaseData2, streamCallback, streamTimeoutCallback);


  // Função para obter tempo atual
  timeClient.begin();
  timeClient.setTimeOffset(-10800);

  // Define a entrada do servo
  Servo1.attach(pinoServo, 500, 2400);
  Servo1.setPeriodHertz(50);

  // Define a porta do rele como saída
  pinMode(rele, OUTPUT);

  // Inicializa sensor de temperatura e umidade
  dht.begin();

  //cria tarefa que vai ser executada na função core1, com prioridade 1 e executa no core 0
  xTaskCreatePinnedToCore(
    core0,       /* Funcao task. */
    "core0",     /* Nome da task. */
    10000,       /* Tamanho da pilha da task */
    NULL,        /* Parametro da funcao*/
    10,           /* Prioridade da funcao */
    &Tarefa1,    /* Auxiliar para ajudar com execucao da funcao */
    0);          /* Associa ao core 0 */
  delay(500);

  //cria tarefa que vai ser executada na função core2, com prioridade 1 e executa no core 1
  xTaskCreatePinnedToCore(
    core1,      /* Funcao task. */
    "Core1",    /* Nome da task. */
    10000,      /* Tamanho da pilha da task */
    NULL,       /* Parametro da funcao*/
    10,          /* Prioridade da funcao */
    &Tarefa2,   /* Auxiliar para ajudar com execucao da funcao */
    1);         /* Associa ao core 1 */
  delay(500);

}

//----------------------------------------------------------------------------------//
// Core 0 é responsável pelo envio de dados dos sensores ao firebase
void core0( void * pvParameters ) {
  Serial.print("Funcao core0 rodando no core: ");
  Serial.println(xPortGetCoreID());

  for (;;) {
    tempo_atual = millis();


    // Verifica sensores
    if ((tempo_atual - tempo_ant_sensores) >= tempo_exibicao) {

      tempo_ant_sensores = tempo_atual;

      dataHora();           // Atualiza data e hora
      verifica_Lumin();     // Verifica Luminosidade
      verifica_umidSolo();  // Verifica Umidade do Solo
      verifica_tempUmid();  // Verifica Temperatura e Umidade
      Serial.println("----------------------------------------------------------------------------------");
    }

    // Envia dados para o firebase de x em x minutos
    if ((tempo_atual - tempo_ant) >= tempo_envio) {

      tempo_ant = tempo_atual;

      if (Firebase.pushJSON(firebaseData3, "/Sensores", json)) {

        Serial.println("Dados sensores postados");

      } else {

        Serial.println("Erro na postagem dos dados:" + firebaseData3.errorReason());
      }
    }
    // Evita erro de IDLE de core do esp
    vTaskDelay(10 / portTICK_PERIOD_MS);

  }

}

//----------------------------------------------------------------------------------//
// Core 1 é responsável pelo recebimento de comandos via firebase
void core1( void * pvParameters ) {

  Serial.print("Funcao core1 rodando no core: ");
  Serial.println(xPortGetCoreID());

  // Seta dados padrões na stream
  if (Firebase.setDouble(firebaseData2, caminhoPai + "/Luminosidade", luminosidadeMaxima)) {}
  else {

    Serial.println("Erro no envio de dado da luminosidade:" + firebaseData2.errorReason());

  }
  if (Firebase.setDouble(firebaseData2, caminhoPai + "/UmidadeSolo", umidadeMinimaPlanta)) {}
  else {

    Serial.println("Erro no envio de dado da umidade do solo:" + firebaseData2.errorReason());

  }
  if (Firebase.setString(firebaseData2, caminhoPai + "/Servo", "PARADO")) {}
  else {

    Serial.println("Erro no envio de comando do servo:" + firebaseData2.errorReason());

  }
  if (Firebase.setString(firebaseData2, caminhoPai + "/Solenoide", "FECHADA")) {}
  else {

    Serial.println("Erro no envio de comando da solenoide:" + firebaseData2.errorReason());

  }
  if (Firebase.setDouble(firebaseData2, caminhoPai + "/TempoEnvio", tempo_envio)) {}
  else {

    Serial.println("Erro no envio de comando da solenoide:" + firebaseData2.errorReason());

  }

  for (;;) {

    movimentaServo(); // Dado recebido para ordem manual do servo
    solenoide();      // Dado recebido para ordem manual da solenoide

    // Evita erro de IDLE de core do esp
    vTaskDelay(10 / portTICK_PERIOD_MS);

  }
}

void loop() {

}
