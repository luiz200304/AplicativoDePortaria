package com.example.aplicativoderetiradadeencomenda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        Button btnListar = findViewById(R.id.btnListar);

        btnRegistrar.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrarActivity.class)));

        btnListar.setOnClickListener(v ->
                startActivity(new Intent(this, ListarActivity.class)));
    }
}
