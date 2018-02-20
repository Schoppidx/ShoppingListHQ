package com.example.schoppenhauer.shoppinglisthq;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Administrator on 20.02.2018.
 */

public class ShoppingMemoDataSource {

    private static final String TAG = ShoppingMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ShoppingMemoDbHelper dbHelper;

    public ShoppingMemoDataSource(Context context) {
        Log.d(TAG, "Unsere ShoppingMemoDataSource erzeugt jetzt den dbHelper.");
        dbHelper = new ShoppingMemoDbHelper(context);
    }

    public void open() {
        Log.d(TAG, "open: Eine Referenz auf die Datenbank wird angefragt");
        database = dbHelper.getWritableDatabase();
        Log.d(TAG, "open: Referenz erhalten, Pfad zur DB: " + database.getPath());

    }

    public void close() {
        dbHelper.close();
        Log.d(TAG, "close: Datenbank wurde geschlossen.");
    }
}
