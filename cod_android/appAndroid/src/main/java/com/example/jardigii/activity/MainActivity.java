package com.example.jardigii.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.jardigii.R;
import com.example.jardigii.config.ConfiguracaoFireBase;
import com.example.jardigii.fragment.AnaliseFragment;
import com.example.jardigii.fragment.TempoRealFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity {
  private DatabaseReference referencia= FirebaseDatabase.getInstance().getReference();
 private FirebaseAuth auth;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth= ConfiguracaoFireBase.getFirebaseAutenticacao();

        Toolbar toolbar=findViewById(R.id.toolbar2);
        toolbar.setTitle("JarDigi");
        setSupportActionBar(toolbar);
    FragmentPagerItemAdapter adapter= new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this).add("Tempo Real", TempoRealFragment.class).add("Análise", AnaliseFragment.class).create());
        ViewPager viewPager=findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab= findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);

    // DatabaseReference test= referencia.child("Solenoide");
        //test.setValue("");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }
    public  void deslogarUsuario(){
        try {
            auth.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
