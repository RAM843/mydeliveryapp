package com.example.mydelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class administrador extends AppCompatActivity {
    private Button btnRes;
    private Button btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);


        btnRes=(Button) findViewById(R.id.btnRes);
        btnRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(administrador.this,restaurant.class);
                startActivity(intent);
            }
        });
        btnMenu=(Button) findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(administrador.this,menu.class);
                startActivity(intent);
            }
        });
    }
}