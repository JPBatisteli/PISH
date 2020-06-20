package com.example.jardigii.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFireBase {
    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;


    //retorna a instacia do firebasedatabase
    public static DatabaseReference getFirebaseDatabase(){
        if(database==null){
            database= FirebaseDatabase.getInstance().getReference();

        }
        return database;
    }
    //retorna a  intancia do firebaseauth
    public static FirebaseAuth getFirebaseAutenticacao(){
        if(auth==null){
            auth=FirebaseAuth.getInstance();
        }
        return auth;
    }

}
