package com.example.alfredo.nutritionapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button logout, perfil, deteccion,registro;
    Button up;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        KeyUser keyUser = new KeyUser();
        keyUser.load();
        setContentView(R.layout.activity_main);
        logout = (Button)findViewById(R.id.logoutbtn);
        perfil = (Button)findViewById(R.id.perfilbtn);
        deteccion = (Button)findViewById(R.id.deteccion);
        registro = (Button)findViewById(R.id.registro);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent( MainActivity.this,   HomeActivity.class);
                startActivity(intent);

            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this,   EditPerfilActivity.class);
                startActivity(intent);

            }
        });


        deteccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this,   CameraActivity.class);
                startActivity(intent);

            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this,   RegistrosActivity.class);
                startActivity(intent);

            }
        });


    }
}
