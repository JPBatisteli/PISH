package com.example.jardigii.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.jardigii.R;
import com.example.jardigii.activity.CadastroActivity;
import com.example.jardigii.model.Planta;
import com.example.jardigii.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnaliseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnaliseFragment extends Fragment {
    private TextInputEditText campoNomePlanta, campoTemperatura, campoLuminosidade, campoUmidadeSolo;
    private Button botaoCadastrarPlanta;
    private DatabaseReference referencia= FirebaseDatabase.getInstance().getReference();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AnaliseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnaliseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnaliseFragment newInstance(String param1, String param2) {
        AnaliseFragment fragment = new AnaliseFragment();
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
        final View view=inflater.inflate(R.layout.fragment_analise, container, false);

        campoNomePlanta=view.findViewById(R.id.editNomePlantaID);
        campoTemperatura=view.findViewById(R.id.editTemperaturaID);
        campoUmidadeSolo=view.findViewById(R.id.editUmidadeSoloID);
        campoLuminosidade=view.findViewById(R.id.editLuminosidadeID);

        botaoCadastrarPlanta=view.findViewById(R.id.botaoCadastrarPlantaID);

        botaoCadastrarPlanta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCadastroPlanta(v);
            }
        });


        return view;
    }

    private void salvarPLantaFireBase(Planta planta) {
        DatabaseReference plant=referencia.child("Planta");
        plant.push().setValue(planta);
        Toast.makeText(getContext(),"Cadastrado com Sucesso!",Toast.LENGTH_SHORT).show();
    }

    private void validarCadastroPlanta(View view) {
        String textoPlanta = campoNomePlanta.getText().toString();
        String textoTemperatura = campoTemperatura.getText().toString();
        String textoUmidadeSolo = campoUmidadeSolo.getText().toString();
        String textoLuminosidade = campoLuminosidade.getText().toString();


        if (!textoPlanta.isEmpty()) {
            if (!textoTemperatura.isEmpty()) {
                if (!textoLuminosidade.isEmpty()) {
                    if (!textoUmidadeSolo.isEmpty()) {
                        Planta planta = new Planta();

                        planta.setNomePlanta(textoPlanta);
                        planta.setTemperaturaPlanta(textoTemperatura);
                        planta.setLuminosidadePlanta(textoLuminosidade);
                        planta.setUmidadeSoloPlanta(textoUmidadeSolo);
                         salvarPLantaFireBase(planta);
                         campoNomePlanta.setText("");
                         campoTemperatura.setText("");
                         campoUmidadeSolo.setText("");
                         campoLuminosidade.setText("");

                    }else{
                        Toast.makeText(getContext(),"Preencha a umidade do solo",Toast.LENGTH_SHORT).show();

                    }
                }else{
                    Toast.makeText(getContext(),"Preencha a Luminosidade",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getContext(),"Preencha a Temperatura",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getContext(),"Preencha o nome da planta",Toast.LENGTH_SHORT).show();
        }
    }
}

