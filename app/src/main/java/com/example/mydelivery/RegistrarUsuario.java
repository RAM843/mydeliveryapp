package com.example.mydelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mydelivery.Api.Resource;
import com.example.mydelivery.Api.ResourceHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrarUsuario extends AppCompatActivity implements View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        loadComponents();
    }

    EditText nombre,apellido,ci,celular,email,password,cpassword;
    Button btn_ru_guardar,btn_ru_cancelar;
    private  void loadComponents(){
        nombre = findViewById(R.id.ru_nombre);
        apellido = findViewById(R.id.ru_apellidos);
        ci = findViewById(R.id.ru_CI);
        celular = findViewById(R.id.ru_Cel);
        email = findViewById(R.id.ru_email);
        password = findViewById(R.id.ru_password);
        cpassword = findViewById(R.id.ru_cpassword);
        //botones
        btn_ru_guardar = findViewById(R.id.btn_ru_guardar);
        btn_ru_cancelar = findViewById(R.id.btn_ru_cancelar);
        btn_ru_guardar.setOnClickListener(this);
        btn_ru_cancelar.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_ru_guardar:
                guardar();
                break;
            case R.id.btn_ru_cancelar:
                irAlogin();
                break;
        }

    }

    public void guardar(){
        if(password.getText().toString().equals(cpassword.getText().toString())){
            JSONObject jo = new JSONObject();
            try {
                jo.put("nombre",nombre.getText().toString());
                jo.put("apellido",apellido.getText().toString());
                jo.put("ci",ci.getText().toString());
                jo.put("telefono",celular.getText().toString());
                jo.put("email",email.getText().toString());
                jo.put("password",password.getText().toString());
                jo.put("rol","user");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Resource ur = new Resource("usuario");
            ur.post(jo, new ResourceHandler() {
                @Override
                public void onSucces(JSONObject result) {
                    try {
                        Toast.makeText(getApplicationContext(),result.getString("msn"),Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    irAlogin();
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
        else{
            Toast.makeText(this,"las contraseñas no coinciden",Toast.LENGTH_LONG).show();
        }

    }
    public void irAlogin(){
        Intent i = new Intent(this,login.class);
        startActivity(i);

    }
}