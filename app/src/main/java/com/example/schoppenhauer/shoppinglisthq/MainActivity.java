package com.example.schoppenhauer.shoppinglisthq;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ShoppingMemoDataSource dataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShoppingMemo testmemo = new ShoppingMemo("Birnen",5,102);
        Log.d(TAG, "Inhalt der Testmemo: " + testmemo.toString());

        dataSource = new ShoppingMemoDataSource(this);
        Log.d(TAG, "onCreate: Die Datenquelle wird geöffnet");
        dataSource.open();

        Log.d(TAG, "onCreate: Die Datenquelle wird geschlossen");
        dataSource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
