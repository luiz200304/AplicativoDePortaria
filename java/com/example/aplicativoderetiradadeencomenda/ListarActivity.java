package com.example.aplicativoderetiradadeencomenda;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ListarActivity extends AppCompatActivity {

    ArrayList<EncomendaItem> lista = new ArrayList<>();
    EncomendaAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);

        listView = findViewById(R.id.listViewEncomendas);

        adapter = new EncomendaAdapter();
        listView.setAdapter(adapter);

        carregarDoBanco();
    }

    private void carregarDoBanco() {
        lista.clear();

        try {
            DatabaseHelper helper = new DatabaseHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();

            Cursor check = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='encomenda'",
                    null
            );
            if (!check.moveToFirst()) {
                check.close();
                Toast.makeText(this, "Tabela 'encomenda' não existe.", Toast.LENGTH_LONG).show();
                return;
            }
            check.close();

            Cursor c = db.rawQuery(
                    "SELECT id, morador, unidade, pagina, data, hora, retirada FROM encomenda ORDER BY id DESC",
                    null
            );

            while (c.moveToNext()) {
                int id = c.getInt(0);
                String morador = c.isNull(1) ? "Sem nome" : c.getString(1);
                String unidade = c.isNull(2) ? "" : String.valueOf(c.getInt(2));
                String pagina = c.isNull(3) ? "—" : c.getString(3);
                String data = c.isNull(4) ? "" : c.getString(4);
                String hora = c.isNull(5) ? "" : c.getString(5);
                String retirada = c.isNull(6) ? "Não" : c.getString(6);

                EncomendaItem it = new EncomendaItem(id, unidade, morador, pagina, data, hora, retirada);
                lista.add(it);
            }
            c.close();

            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao carregar lista: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    static class EncomendaItem {
        int id;
        String unidade;
        String morador;
        String pagina;
        String data;
        String hora;
        String retirada;

        EncomendaItem(int id, String unidade, String morador, String pagina, String data, String hora, String retirada) {
            this.id = id;
            this.unidade = unidade;
            this.morador = morador;
            this.pagina = pagina;
            this.data = data;
            this.hora = hora;
            this.retirada = retirada;
        }
    }

    class EncomendaAdapter extends ArrayAdapter<EncomendaItem> {
        EncomendaAdapter() {
            super(ListarActivity.this, R.layout.item_encomenda, lista);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.item_encomenda, parent, false);
            }

            EncomendaItem e = lista.get(position);

            TextView tvUnidadeMorador = v.findViewById(R.id.tvUnidadeMorador);
            TextView tvPagina = v.findViewById(R.id.tvPagina);
            TextView tvData = v.findViewById(R.id.tvData);
            TextView tvHora = v.findViewById(R.id.tvHora);
            TextView tvRetirado = v.findViewById(R.id.tvRetirado);
            Button btnRetirar = v.findViewById(R.id.btnRetirar);

            tvUnidadeMorador.setText((e.unidade != null && !e.unidade.isEmpty() ? e.unidade + " - " : "") + e.morador);
            tvPagina.setText("Pág. " + e.pagina);
            tvData.setText(e.data);
            tvHora.setText(e.hora);
            tvRetirado.setText(e.retirada != null && e.retirada.toLowerCase().contains("retir") ? "Sim" : "Não");

            // Retirar -> atualiza o registro no banco
            btnRetirar.setOnClickListener(view -> {
                if (e.retirada != null && e.retirada.toLowerCase().contains("retir")) {
                    Toast.makeText(ListarActivity.this, "Já foi retirado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    DatabaseHelper helper = new DatabaseHelper(ListarActivity.this);
                    SQLiteDatabase db = helper.getWritableDatabase();

                    ContentValues cv = new ContentValues();
                    cv.put("retirada", "retirado");
                    String nowDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String nowTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                    cv.put("dataRetirada", nowDate);
                    cv.put("horaRetirada", nowTime);

                    int rows = db.update("encomenda", cv, "id = ?", new String[]{String.valueOf(e.id)});
                    if (rows > 0) {
                        Toast.makeText(ListarActivity.this, "Encomenda marcada como retirada.", Toast.LENGTH_SHORT).show();
                        e.retirada = "retirado";
                        e.data = nowDate;
                        e.hora = nowTime;
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(ListarActivity.this, "Falha ao atualizar banco.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(ListarActivity.this, "Erro: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            return v;
        }
    }
}
