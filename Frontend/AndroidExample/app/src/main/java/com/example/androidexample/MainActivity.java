package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //CICD Testing comment
    //test

    private Button signUpBtn;
    private Button loginBtn;
    //Siyona's Postman
    //public static final String APP_URL = "https://6b079280-9c45-42ce-bb8d-58ed0c3119fd.mock.pstmn.io";
    //Clair's Postman
    //public static final String APP_URL = "https://918520df-aecd-4d51-8614-fb9fc58525ec.mock.pstmn.io";
    public static final String APP_URL = "http://coms-3090-017.class.las.iastate.edu:8080";
    //public static final String APP_URL = "http://10.48.15.2:8080";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUpBtn = findViewById(R.id.btnSignUp);
        loginBtn = findViewById(R.id.btnLogin);

        /* button click listeners */
        loginBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
        loginBtn.setBackgroundColor(Color.BLACK);
        signUpBtn.setBackgroundColor(Color.BLACK);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSignUp) {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        } else if (id == R.id.btnLogin) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}