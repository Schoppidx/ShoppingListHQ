package com.example.schoppenhauer.shoppinglisthq;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 20.02.2018.
 */

public class ShoppingMemoDataSource {

    private static final String TAG = ShoppingMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ShoppingMemoDbHelper dbHelper;

    private String[] columns = {
            ShoppingMemoDbHelper.COLUMN_ID,
            ShoppingMemoDbHelper.COLUMN_PRODUCT,
            ShoppingMemoDbHelper.COLUMN_QUANTITY,
            ShoppingMemoDbHelper.COLUMN_CHECKED
    };

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

    public ShoppingMemo createShoppingMemo(String product, int quantity) {
        ContentValues values = new ContentValues();
        values.put(ShoppingMemoDbHelper.COLUMN_PRODUCT, product);
        values.put(ShoppingMemoDbHelper.COLUMN_QUANTITY, quantity);

        long instertId = database.insert(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST, null, values);

        Cursor cursor = database.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST, columns,
                ShoppingMemoDbHelper.COLUMN_ID + "=" + instertId, null, null, null, null);
        cursor.moveToFirst();
        ShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor);

        return shoppingMemo;
    }

    public void deleteShoppingMemo(ShoppingMemo memo) {
        long id = memo.getId();

        database.delete(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,
                ShoppingMemoDbHelper.COLUMN_ID + "="+id,null);

        Log.d(TAG, "deleteShoppingMemo: Eintrag gelöscht " + id + " " + memo.toString());

    }

    public ShoppingMemo updateShoppingMemo(long id,String newProduct, int newQuantity, boolean newChecked) {
        int intValueChecked = newChecked? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(ShoppingMemoDbHelper.COLUMN_PRODUCT,newProduct);
        values.put(ShoppingMemoDbHelper.COLUMN_QUANTITY,newQuantity);
        values.put(ShoppingMemoDbHelper.COLUMN_CHECKED,intValueChecked);

        database.update(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,values,ShoppingMemoDbHelper.COLUMN_ID + "="+id,null);

        Cursor cursor = database.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST, columns,
                ShoppingMemoDbHelper.COLUMN_ID + "=" + id, null, null, null, null);
        cursor.moveToFirst();
        ShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor);

        return shoppingMemo;
    }
    private ShoppingMemo cursorToShoppingMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_ID);
        int idProduct = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_PRODUCT);
        int idQuantity = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_QUANTITY);
        int idChecked = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_CHECKED);

        int intValueChecked = cursor.getInt(idChecked);
        String product = cursor.getString(idProduct);
        int quantity = cursor.getInt(idQuantity);
        long index = cursor.getInt(idIndex);
        boolean isChecked = (intValueChecked != 0);

        return new ShoppingMemo(product, quantity, index, isChecked);
    }

    public List<ShoppingMemo> getAllShoppingMemos() {
        List<ShoppingMemo> shoppingMemoList = new ArrayList<>();
        Cursor cursor = database.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST, columns,
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            ShoppingMemo memo;
            do {
                memo = cursorToShoppingMemo(cursor);
                shoppingMemoList.add(memo);
                Log.d(TAG, "ID: " + memo.getId() + " Inhalt " + memo.toString());

            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "getAllShoppingMemos: Keine Einträge in der DB vorhanden zum auflisten");
        }
        cursor.close();
        return shoppingMemoList;
    }

    public String getShareString() {
        StringBuilder sb = new StringBuilder("Einkaufsliste:\n");
        Cursor cursor = database.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST, columns,
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            ShoppingMemo memo;
            do {
                memo = cursorToShoppingMemo(cursor);
                sb.append(memo.toString() + "\n");
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "getAllShoppingMemos: Keine Einträge in der DB vorhanden zum auflisten");
        }
        cursor.close();
        return sb.toString();
    }
}
