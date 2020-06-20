package com.example.jardigii.model;

import java.util.Date;

public class Sensores {
        private  static String temperatura;
        private static String  luminosidade;
        private static String  umidade;
        private static String  umidadeSolo;
        private static String  data;
        private static String  hora;

    public Sensores() {
    }

    public Sensores(String temperatura, String luminosidade, String umidade, String umidadeSolo, String data, String hora) {
        this.temperatura = temperatura;
        this.luminosidade = luminosidade;
        this.umidade = umidade;
        this.umidadeSolo = umidadeSolo;
        this.data = data;
        this.hora = hora;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getLuminosidade() {
        return luminosidade;
    }

    public void setLuminosidade(String luminosidade) {
        this.luminosidade = luminosidade;
    }

    public String getUmidade() {
        return umidade;
    }

    public void setUmidade(String umidade) {
        this.umidade = umidade;
    }

    public String getUmidadeSolo() {
        return umidadeSolo;
    }

    public void setUmidadeSolo(String umidadeSolo) {
        this.umidadeSolo = umidadeSolo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
