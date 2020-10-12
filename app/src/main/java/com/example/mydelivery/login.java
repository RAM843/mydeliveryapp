package com.example.mydelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mydelivery.Api.Auth;
import com.example.mydelivery.Api.ResourceHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class login extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadComponents();

    }
    EditText login_email, login_password;
    Button btn_login,btn_register;
    private void loadComponents(){
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                irARegistrar();
                break;

        }

    }
    public void login(){
        String email = this.login_email.getText().toString();
        String password = this.login_password.getText().toString();
        Auth a = new Auth();
        a.login(email,password, new ResourceHandler() {
            @Override
            public void onSucces(JSONObject result) {
                try {
                    Toast.makeText(getApplicationContext(),result.getString("msn"),Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(JSONObject error) {
                try {
                    Toast.makeText(getApplicationContext(),error.getString("msn"),Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            });


    }
    public void irARegistrar(){
        Intent i = new Intent(this,RegistrarUsuario.class);
        startActivity(i);
    }
}