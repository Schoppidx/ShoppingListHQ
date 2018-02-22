package com.example.schoppenhauer.shoppinglisthq;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 20.02.2018.
 */

public class ShoppingMemoDbHelper extends SQLiteOpenHelper {

    private static final String TAG = ShoppingMemoDbHelper.class.getSimpleName();

    public static final String DB_NAME = "shopping_list.db";
    public static final int DB_VERSION = 2;

    public static final String TABLE_SHOPPING_LIST = "shopping_list";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCT = "product";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_CHECKED = "checked";

    public static final String SQL_CREATE = "CREATE TABLE " + TABLE_SHOPPING_LIST +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PRODUCT + " TEXT NOT NULL, " +
            COLUMN_QUANTITY + " INTEGER NOT NULL," +
            COLUMN_CHECKED + " BOOLEAN NOT NULL DEFAULT 0);";

    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST;

    public ShoppingMemoDbHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
        Log.d(TAG, "ShoppingMemoDbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE);
            Log.d(TAG, "Die Tabelle wurde mit der Anweisung " + SQL_CREATE + " angelegt.");
        } catch (Exception e) {
            Log.d(TAG, "Fehler beim Anlegen der Tabelle");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        onCreate(db);
    }
}
