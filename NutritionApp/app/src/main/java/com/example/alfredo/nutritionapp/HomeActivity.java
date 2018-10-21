package com.example.alfredo.nutritionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity {
Button in;
Button up;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        in = (Button)findViewById(R.id.in);
        up = (Button)findViewById(R.id.up);

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent( HomeActivity.this, SignUpActivity.class);
                startActivity(it);
                        }
                    });


        in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent( HomeActivity.this, LoginActivity.class);
                startActivity(it);
            }
        });
    }
}
