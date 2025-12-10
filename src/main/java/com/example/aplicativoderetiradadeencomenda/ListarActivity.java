package com.example.aplicativoderetiradadeencomenda;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListarActivity extends AppCompatActivity {

    ArrayList<String> lista = new ArrayList<>();
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);

        //Pega a lista que existe no XML, para conseguir mexer nela pelo código.
        ListView listView = findViewById(R.id.listViewEncomendas);

        carregar();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, pos, id) -> confirmarRetirada(ids.get(pos)));
    }

    //função para buscar encomendas no banco
    private void carregar() {
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, nome, descricao FROM encomenda WHERE retirada = 0", null);

        lista.clear();
        ids.clear();

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String nome = c.getString(1);
            String desc = c.getString(2);

            ids.add(id);
            lista.add(nome + " - " + desc);
        }
    }

    private void confirmarRetirada(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Retirar Encomenda?")
                .setMessage("Confirmar retirada?")
                .setPositiveButton("Sim", (dialog, which) -> retirar(id))
                .setNegativeButton("Não", null)
                .show();
    }

    private void retirar(int id) {
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("UPDATE encomenda SET retirada = 1 WHERE id = " + id);

        carregar();
        adapter.notifyDataSetChanged();
    }
}
