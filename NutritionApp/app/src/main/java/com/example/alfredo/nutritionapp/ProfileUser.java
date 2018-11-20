package com.example.alfredo.nutritionapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileUser extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText userName, userAge, userAlutra,userPeso;
    Button saveBtn;
    String userGenero;

    DatabaseReference mDataRef;

    String keyUser;
    private Spinner spinner;
    private static final String[] paths = {"Hombre", "Mujer"};

    String userNameString, userPesoString, userGeneroString, userAlturaString, userAgeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileuser);
        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(ProfileUser.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        //GET USER KEY FROM INTENT
        keyUser = getIntent().getStringExtra("USER_KEY");



        //FIREBASE DATABASE REFERENCE
        mDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(keyUser);

        userName = (EditText) findViewById(R.id.userNameEditText);
        userAge = (EditText) findViewById(R.id.userEdadEditText);
        userPeso = (EditText) findViewById(R.id.userpeso);
        saveBtn = (Button) findViewById(R.id.userProfileBtn);
        userAlutra = (EditText) findViewById(R.id.userestatura);




        //SAVE BTN LOGIC
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userNameString = userName.getText().toString();
                userPesoString = userPeso.getText().toString();
                userAgeString = userAge.getText().toString();
                userAlturaString = userAlutra.getText().toString();
                userGeneroString = userGenero;



                if(!TextUtils.isEmpty(userAgeString) && !TextUtils.isEmpty(userNameString)
                        && !TextUtils.isEmpty(userPesoString)
                        && !TextUtils.isEmpty(userAlturaString)
                        && !TextUtils.isEmpty(userGeneroString))
                {

                    mDataRef.child("userName").setValue(userNameString);
                    mDataRef.child("userAge").setValue(userAgeString);
                    mDataRef.child("userHeight").setValue(userAlturaString);
                    mDataRef.child("userGender").setValue(userGeneroString);
                    mDataRef.child("userWeight").setValue(userPesoString);



                    mDataRef.child("isVerified").setValue("verfied");
                    KeyUser keyUser = new KeyUser();
                    keyUser.start();

                    Toast.makeText(ProfileUser.this, "User profile added", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ProfileUser.this, MainActivity.class));

                }

            }
        });



    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                userGenero= "H";
                break;
            case 1:
                userGenero = "F";
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
