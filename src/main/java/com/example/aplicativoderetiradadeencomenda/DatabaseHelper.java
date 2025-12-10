package com.example.aplicativoderetiradadeencomenda;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "encomendas.db";
    public static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE encomenda (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "porteiro TEXT," +
                "morador TEXT," +
                "unidade INTEGER," +
                "pagina TEXT," +
                "data TEXT," +
                "hora TEXT," +
                "retirada TEXT," +
                "dataRetirada TEXT," +
                "horaRetirada TEXT," +
                "foto TEXT," +
                "assinatura TEXT," +
                "id_usuario INTEGER," +
                "unidadeCondominio TEXT" +
                ")");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
    }
}
