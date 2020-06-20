package com.example.jardigii.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.LoginFilter;
import android.view.View;
import android.widget.Toast;

import com.example.jardigii.R;
import com.example.jardigii.config.ConfiguracaoFireBase;
import com.example.jardigii.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
private TextInputEditText campoEmail,campoSenha;
private FirebaseAuth autenticacao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail=findViewById(R.id.editLoginEmail);
        campoSenha=findViewById(R.id.editLoginSenha);
        autenticacao= ConfiguracaoFireBase.getFirebaseAutenticacao();
    }


    public void logarUsuario(Usuario usuario){

        autenticacao.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    abrirTelaPrincipal();
                }else{
                    Toast.makeText(LoginActivity.this,"  Erro ao autenticar usuario!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validarAutenticacaoUsuario(View view){
        String email=campoEmail.getText().toString();
        String senha=campoSenha.getText().toString();

        if(!email.isEmpty()){
            if(!senha.isEmpty()){
               Usuario usuario=new Usuario();
               usuario.setEmail(email);
               usuario.setSenha(senha);
               logarUsuario(usuario);

            }else{
                Toast.makeText(LoginActivity.this,"Peencha o senha!",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(LoginActivity.this,"Peencha o e-mail!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual=autenticacao.getCurrentUser();
        if(usuarioAtual !=null){
            abrirTelaPrincipal();
        }
    }

    public void abrirTelaCadastro(View view){
        Intent intent=new Intent(LoginActivity.this,CadastroActivity.class);
        startActivity(intent);
    }
    public void abrirTelaPrincipal(){
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
