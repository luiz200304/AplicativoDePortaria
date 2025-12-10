package com.example.aplicativoderetiradadeencomenda;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrarActivity extends AppCompatActivity {

    EditText edtMorador, edtUnidade, edtPagina, edtData, edtHora, edtFoto, edtDescricao;
    Spinner spRetirada;
    Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        edtMorador = findViewById(R.id.edtMorador);
        edtUnidade = findViewById(R.id.edtUnidade);
        edtPagina = findViewById(R.id.edtPagina);
        edtData = findViewById(R.id.edtData);
        edtHora = findViewById(R.id.edtHora);
        edtFoto = findViewById(R.id.edtFoto);
        edtDescricao = findViewById(R.id.edtDescricao);
        spRetirada = findViewById(R.id.spRetirada);

        btnSalvar = findViewById(R.id.btnSalvar);

        // opções iguais ao HTML
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"aguardandoRetirada", "retirado"}
        );
        spRetirada.setAdapter(adapter);

        // data automática
        SimpleDateFormat sdfData = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        edtData.setText(sdfData.format(new Date()));

        // hora automática
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm", Locale.getDefault());
        edtHora.setText(sdfHora.format(new Date()));

        btnSalvar.setOnClickListener(v -> salvar());
    }

    private void salvar() {

        String morador = edtMorador.getText().toString();
        String unidade = edtUnidade.getText().toString();
        String pagina = edtPagina.getText().toString();
        String data = edtData.getText().toString();
        String hora = edtHora.getText().toString();
        String foto = edtFoto.getText().toString();
        String assinatura = edtDescricao.getText().toString();
        String retirada = spRetirada.getSelectedItem().toString();

        if (morador.isEmpty() || unidade.isEmpty() || pagina.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        // iguais ao Flask
        cv.put("porteiro", "PorteiroXPTO"); // você troca pelo usuário logado
        cv.put("morador", morador);
        cv.put("unidade", Integer.parseInt(unidade));
        cv.put("pagina", pagina);
        cv.put("data", data);
        cv.put("hora", hora);
        cv.put("retirada", retirada);
        cv.put("foto", foto);
        cv.put("assinatura", assinatura);

        cv.put("dataRetirada", "");
        cv.put("horaRetirada", "");
        cv.put("id_usuario", 1); // trocar quando tiver login
        cv.put("unidadeCondominio", "Bloco A");

        db.insert("encomenda", null, cv);

        Toast.makeText(this, "Encomenda registrada!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
