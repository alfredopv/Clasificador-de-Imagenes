package com.example.alfredo.nutritionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

public class EditPerfilActivity extends Activity {
    DatabaseReference mDataRef;
    Button saveBtn;
    FirebaseAuth mAuth;

    String userNameString, heightString, weightString, ageString,genderString, pwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);
        final KeyUser keyUser = new KeyUser();
        mDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(keyUser.KU);
        mAuth = FirebaseAuth.getInstance();

        TextView tone = (TextView)findViewById(R.id.tone);
        TextView ttwo = (TextView)findViewById(R.id.ttwo);
        TextView tthree = (TextView)findViewById(R.id.tthree);
        TextView tfour = (TextView)findViewById(R.id.tfour);
        TextView tfive = (TextView)findViewById(R.id.tfive);
        TextView tsix = (TextView)findViewById(R.id.tsix);
        Button saveBtn = (Button)findViewById(R.id.saveBtn);
        final EditText eone = (EditText)findViewById(R.id.eone);
        final EditText etwo = (EditText)findViewById(R.id.etwo);
        final EditText ethree = (EditText)findViewById(R.id.ethree);
        final EditText efour = (EditText)findViewById(R.id.efour);
        final EditText efive = (EditText)findViewById(R.id.efive);
        final EditText esix = (EditText)findViewById(R.id.esix);




        eone.setHint(KeyUser.userName);
            etwo.setHint(KeyUser.userHeight);
            ethree.setHint(KeyUser.userWeight);
            efour.setHint(KeyUser.userAge);
            efive.setHint(KeyUser.userGender);
            esix.setHint("****");
            //System.out.println(userNameString);




            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwd = esix.getText().toString();
                     userNameString = eone.getText().toString();
                        heightString = etwo.getText().toString();
                        weightString = ethree.getText().toString();
                        ageString = efour.getText().toString();
                       genderString = efive.getText().toString();
                        pwd = esix.getText().toString();

                    if (!userNameString.matches("")) {
                        mDataRef.child("userName").setValue(userNameString);

                    }

                    if (!ageString.matches("")) {
                            mDataRef.child("userAge").setValue(ageString);

                        }

                        if (!genderString.matches("")) {
                            mDataRef.child("userGender").setValue(genderString);

                        }

                        if (!heightString.matches("")) {
                            mDataRef.child("userHeight").setValue(heightString);
                        }

                    if (!weightString.matches("")) {
                        mDataRef.child("userWeight").setValue(weightString);
                    }



                      /*  if (!emailString.matches("")) {
                            mDataRef.child("emailUser").setValue(emailString);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            user.updateEmail(emailString)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User email address updated.");
                                            }
                                        }
                                    });

                        }
                        */
                        if(!pwd.matches("")){
                            mDataRef.child("passWordUser").setValue(pwd);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            user.updatePassword(pwd)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User password updated.");
                                            }
                                        }
                                    });
                        }

                        Toast.makeText(EditPerfilActivity.this, "Informacion actualizada", Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                    finish();
                    startActivity(new Intent(EditPerfilActivity.this, LoginActivity.class));

                    }

                });

    }

}
