package com.example.mydelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class seleccionar extends AppCompatActivity {
    private Button btnPedi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar);

        btnPedi=(Button) findViewById(R.id.btnPedi);
        btnPedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(seleccionar.this,pedido.class);
                startActivity(intent);
            }
        });

    }
}