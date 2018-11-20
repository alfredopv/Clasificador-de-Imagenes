package com.example.alfredo.nutritionapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class SignUpActivity extends AppCompatActivity {

    //VIEWS AND WIDGET FIELDS
    Button createUser;
    EditText userEmailEdit, userPassWordEdit;

    //FIREBASE AUTHENTICATION FIELDS
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    DatabaseReference mDatabaseRef, mUserCheckData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //ASSIGN ID
        createUser = (Button) findViewById(R.id.createUserBtn);
        userEmailEdit = (EditText) findViewById(R.id.emailEditTextCreate);
        userPassWordEdit = (EditText) findViewById(R.id.PassEditTextCreate);

        //ASSIGN INSTANCES
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUserCheckData = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {


                    mUserCheckData.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                           //checkUserValidation(dataSnapshot, emailForVer);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }



        };


        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String userEmailString, userPassString;

                userEmailString = userEmailEdit.getText().toString().trim();
                userPassString = userPassWordEdit.getText().toString().trim();

                if (!TextUtils.isEmpty(userEmailString) && !TextUtils.isEmpty(userPassString)) {

                    mAuth.createUserWithEmailAndPassword(userEmailString, userPassString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                DatabaseReference mChildDatabase = mDatabaseRef.child("Users").push();

                                String key_user = mChildDatabase.getKey();

                                mChildDatabase.child("isVerified").setValue("unverified");
                                mChildDatabase.child("userKey").setValue(key_user);
                                mChildDatabase.child("emailUser").setValue(userEmailString);
                                mChildDatabase.child("passWordUser").setValue(userPassString);

                                Toast.makeText(SignUpActivity.this, "Us*er Account Created", Toast.LENGTH_LONG).show();



                            } else {
                                Toast.makeText(SignUpActivity.this, "Failed to create User Account", Toast.LENGTH_LONG).show();

                            }

                        }
                    });

                }


            }
        });



    }

    private void checkUserValidation(DataSnapshot dataSnapshot, String emailForVer) {


        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext())
        {

            DataSnapshot dataUser = (DataSnapshot) iterator.next();

            if( dataUser.child("emailUser").getValue().toString().equals(emailForVer))
            {

                if (dataUser.child("isVerified").getValue().toString().equals("unverified"))
                {

                   // Intent in = new Intent( SignUpActivity.this, ProfileUser.class);
                   // in.putExtra("USER_KEY" , dataUser.child("userKey").getValue().toString());
                   // startActivity(in);

                }else
                {

                   // startActivity(new Intent(SignUpActivity.this, MainActivity2.class));

                }

            }

        }

    }

}
