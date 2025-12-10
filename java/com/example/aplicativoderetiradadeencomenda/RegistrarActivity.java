package com.example.aplicativoderetiradadeencomenda;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrarActivity extends AppCompatActivity {

    EditText edtMorador, edtUnidade, edtPagina, edtData, edtHora, edtDescricao;
    Spinner spRetirada;
    Button btnSalvar, btnFoto;
    ImageView imgPreview;

    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    private Uri imageUri;
    String caminhoDaFoto = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        // ---- CAMPOS ----
        edtMorador = findViewById(R.id.edtMorador);
        edtUnidade = findViewById(R.id.edtUnidade);
        edtPagina = findViewById(R.id.edtPagina);
        edtData = findViewById(R.id.edtData);
        edtHora = findViewById(R.id.edtHora);

        spRetirada = findViewById(R.id.spRetirada);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnFoto = findViewById(R.id.btnFoto);
        imgPreview = findViewById(R.id.imgPreview);

        // ---- OPÇÕES DO SPINNER ----
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"aguardandoRetirada", "retirado"}
        );
        spRetirada.setAdapter(adapter);

        // ---- DATA E HORA AUTOMÁTICOS ----
        edtData.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        edtHora.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));

        // ---- BOTÕES ----
        btnFoto.setOnClickListener(v -> escolherFoto());
        btnSalvar.setOnClickListener(v -> salvar());

        pedirPermissaoCamera();
    }

    private void pedirPermissaoCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    100
            );
        }
    }

    private void escolherFoto() {
        String[] opcoes = {"Tirar Foto", "Escolher da Galeria"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Selecionar Foto");
        dialog.setItems(opcoes, (d, i) -> {
            if (i == 0) tirarFoto();
            else escolherGaleria();
        });
        dialog.show();
    }

    private void tirarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File foto = new File(getExternalFilesDir(null), "foto_" + System.currentTimeMillis() + ".jpg");
        imageUri = Uri.fromFile(foto);
        caminhoDaFoto = foto.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void escolherGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PHOTO) {
                imgPreview.setImageURI(imageUri);
            } else if (requestCode == PICK_IMAGE) {
                imageUri = data.getData();
                imgPreview.setImageURI(imageUri);
                caminhoDaFoto = imageUri.toString();
            }
        }
    }

    private void salvar() {
        String morador = edtMorador.getText().toString();
        String unidade = edtUnidade.getText().toString();
        String pagina = edtPagina.getText().toString();
        edtDescricao = findViewById(R.id.edtDescricao);
        String data = edtData.getText().toString();
        String hora = edtHora.getText().toString();
        String assinatura = edtDescricao.getText().toString();
        String retirada = spRetirada.getSelectedItem().toString();

        if (morador.isEmpty() || unidade.isEmpty() || pagina.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("porteiro", "PorteiroXPTO");
        cv.put("morador", morador);
        cv.put("unidade", Integer.parseInt(unidade));
        cv.put("pagina", pagina);
        cv.put("data", data);
        cv.put("hora", hora);
        cv.put("retirada", retirada);

        cv.put("foto", caminhoDaFoto);
        cv.put("assinatura", assinatura);
        cv.put("dataRetirada", "");
        cv.put("horaRetirada", "");
        cv.put("id_usuario", 1);
        cv.put("unidadeCondominio", "Bloco A");

        db.insert("encomenda", null, cv);

        Toast.makeText(this, "Encomenda registrada!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
