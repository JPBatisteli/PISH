package com.example.jardigii.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.jardigii.R;
import com.example.jardigii.model.Sensores;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TempoRealFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TempoRealFragment extends Fragment {
    private TextView temperatura;
    private TextView luminosidade;
    private TextView umidadeSolo;
    private TextView umidade;
    private String userID;
    private FirebaseAuth mAuth;
    ArrayList<Sensores> entries = new ArrayList<>();
    private ToggleButton botaoRegar;
    private ToggleButton botaoServo;
    private DatabaseReference referencia= FirebaseDatabase.getInstance().getReference();
    private ArrayList<Sensores>listSensores=new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TempoRealFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TempoRealFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TempoRealFragment newInstance(String param1, String param2) {
        TempoRealFragment fragment = new TempoRealFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_tempo_real, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        userID=user.getUid();

        temperatura=view.findViewById(R.id.temperaturaID);
        umidade=view.findViewById(R.id.umidadeID);
        umidadeSolo=view.findViewById(R.id.umidadesoloID);
        luminosidade=view.findViewById(R.id.luminosidadeID);
        botaoServo=view.findViewById(R.id.botaoServoID);
        tempReal();
       // salvandoDados();
        //showData();
        botaoRegar=view.findViewById(R.id.botaoRegarID);

        abrirSolenoid(view);
        abrirServo(view);



        return view;
    }

    private void abrirServo(View view) {

        botaoServo=view.findViewById(R.id.botaoServoID);

        botaoServo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                DatabaseReference regar=referencia.child("Stream").child("Servo");
                if(isChecked){
                    regar.setValue("ABRE");

                }else{
                    regar.setValue("FECHA");
                }
            }
        });
    }

    private void abrirSolenoid(View view) {
        botaoRegar=view.findViewById(R.id.botaoRegarID);
        botaoRegar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DatabaseReference regar=referencia.child("Stream").child("Solenoide");
                if(isChecked){
                    regar.setValue("ABERTA");

                }else{
                    regar.setValue("FECHADA");
                }
            }
        });


    }

    private  void salvandoDados(){
        final DatabaseReference dadosSensores=referencia.child("Sensores");
        dadosSensores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> itens= dataSnapshot.getChildren().iterator();
                entries.clear();
                while (itens.hasNext()){
                      DataSnapshot item=itens.next();
                      String temp,lumino,umid,umidsolo,date,hr;
                      temp=item.child("Temperatura").getValue().toString();
                      lumino=item.child("Luminosidade").getValue().toString();
                      umid=item.child("Umidade").getValue().toString();
                      umidsolo=item.child("UmidadeSolo").getValue().toString();
                      date=item.child("Data").getValue().toString();
                      hr=item.child("Hora").getValue().toString();
                      //Sensores entry= new Sensores(temp,lumino,umid,umidsolo,date,hr);
                      //entries.add(entry);
                }
                dadosSensores.child("Sensores").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private void tempReal(){

        DatabaseReference tempoReal=referencia.child("TempoReal");

        tempoReal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                double temp1=Double.parseDouble(dataSnapshot.child("Temperatura").getValue().toString());
                double lumino1= Double.parseDouble(dataSnapshot.child("Luminosidade").getValue().toString());
                double umi1=Double.parseDouble(dataSnapshot.child("Umidade").getValue().toString());
                double umisolo1=Double.parseDouble(dataSnapshot.child("UmidadeSolo").getValue().toString());

                temperatura.setText(temp1+"%");
                luminosidade.setText(lumino1+"");
                umidade.setText(umi1+"%");
                umidadeSolo.setText(umisolo1+"%");

                if(temp1<10 || temp1>50){
                    temperatura.setTextColor(Color.RED);
                }
                if(lumino1<0.4 ){
                    luminosidade.setTextColor(Color.RED);
                }
                if(umi1<20){
                    umidade.setTextColor(Color.RED);
                }
                if(umisolo1<20 ){
                    umidadeSolo.setTextColor(Color.RED);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showData() {
        final DatabaseReference dadosSensores=referencia.child("Sensores");

           dadosSensores.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   Log.i("temperatura", "aaa" + dataSnapshot.getRef());
                   if(dataSnapshot.getValue() !=null){
                       for (DataSnapshot ds: dataSnapshot.getChildren()) {
                           Sensores sensores = new Sensores();
                           sensores=ds.getValue(Sensores.class);
                           entries.add(sensores);

            /*sensores.setTemperatura(ds.child(userID).getValue(Sensores.class).getTemperatura());
            sensores.setUmidade(ds.child(userID).getValue(Sensores.class).getUmidade());
            sensores.setLuminosidade(ds.child(userID).getValue(Sensores.class).getLuminosidade());
            sensores.setUmidadeSolo(ds.child(userID).getValue(Sensores.class).getUmidadeSolo());
            sensores.setData(ds.child(userID).getValue(Sensores.class).getData());
            sensores.setHora(ds.child(userID).getValue(Sensores.class).getHora());*/




                       }
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });



    }

}
