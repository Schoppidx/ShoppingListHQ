package com.example.schoppenhauer.shoppinglisthq;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.AndroidException;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ShoppingMemoDataSource dataSource;
    private ListView shoppingMemosListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSource = new ShoppingMemoDataSource(this);
        initializeShoppingMemoListView();
        activateAddButtom();
        initializeContextualActionBar();
    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Die Datenquelle wird geschlossen ");
        dataSource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onCreate: Die Datenquelle wird geöffnet");
        dataSource.open();
        Log.d(TAG, "folgende Einträge sind in der DB vorhanden");
        showAllListEntries();
    }

    private void initializeShoppingMemoListView() {
        List<ShoppingMemo> emptyListForInitialisation = new ArrayList<>();
        shoppingMemosListView = findViewById(R.id.listview_shopping_memos);
        ArrayAdapter<ShoppingMemo> shoppingMemoArrayAdapter = new ArrayAdapter<ShoppingMemo>(this,
                android.R.layout.simple_list_item_multiple_choice, emptyListForInitialisation) {
            //Diese Methode wird aufgerufen wenn eine Zeile geschrieben wird
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;

                ShoppingMemo memo = (ShoppingMemo)shoppingMemosListView.getItemAtPosition(position);
                if(memo.isChecked()) {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    textView.setTextColor(Color.DKGRAY);
                }

                return view;
            }
        };
        shoppingMemosListView.setAdapter(shoppingMemoArrayAdapter);

        shoppingMemosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ShoppingMemo memo = (ShoppingMemo) adapterView.getItemAtPosition(position);
                ShoppingMemo updatedShoppingMemo = dataSource.updateShoppingMemo(memo.getId(),memo.getProduct(),
                        memo.getQuantity(),!memo.isChecked());
                showAllListEntries();
            }
        });
    }

    private void showAllListEntries() {
        List<ShoppingMemo> shoppingMemoList = dataSource.getAllShoppingMemos();
        ArrayAdapter<ShoppingMemo> shoppingMemoArrayAdapter = (ArrayAdapter<ShoppingMemo>) shoppingMemosListView.getAdapter();
//        ArrayAdapter<ShoppingMemo> shoppingMemoArrayAdapter = new ArrayAdapter<ShoppingMemo>(this, android.R.layout.simple_list_item_multiple_choice, shoppingMemoList);
//        ListView listView = findViewById(R.id.listview_shopping_memos);
//        listView.setAdapter(shoppingMemoArrayAdapter);

        shoppingMemoArrayAdapter.clear();
        shoppingMemoArrayAdapter.addAll(shoppingMemoList);
        shoppingMemoArrayAdapter.notifyDataSetChanged();
    }

    private AlertDialog createEditShoppingMemoDialog(final ShoppingMemo memo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogsView = inflater.inflate(R.layout.dialog_edit_shopping_memo, null);

        final EditText editTextNewQuantity = dialogsView.findViewById(R.id.editText_new_quantity);
        Log.d(TAG, "createEditShoppingMemoDialog: " + memo.getQuantity());

        editTextNewQuantity.setText(String.valueOf(memo.getQuantity()));

        final EditText editTextNewProduct = dialogsView.findViewById(R.id.editText_new_product);
        editTextNewProduct.setText(String.valueOf(memo.getProduct()));

        builder.setView(dialogsView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String quantityString = editTextNewQuantity.getText().toString();
                        String product = editTextNewProduct.getText().toString();

                        if ((TextUtils.isEmpty(quantityString)) || (TextUtils.isEmpty(product))) {
                            Toast.makeText(MainActivity.this, "Felder durfen nicht leer sein", Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "Ein Eintrag enthielt keinen Text. Daher Abbruch der Änderung.");
                            return;
                        }

                        int quantity_int = Integer.parseInt(quantityString);

                        // An dieser Stelle schreiben wir die geänderten Daten in die SQLite Datenbank
                        ShoppingMemo updatedShoppingMemo = dataSource.updateShoppingMemo(memo.getId(), product, quantity_int,memo.isChecked());

                        Log.d(TAG, "Alter Eintrag - ID: " + memo.getId() + " Inhalt: " + memo.toString());
                        Log.d(TAG, "Neuer Eintrag - ID: " + updatedShoppingMemo.getId() + " Inhalt: " + updatedShoppingMemo.toString());

                        showAllListEntries();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        dialogInterface.cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_teile_shoppinglist);
//        onDataChanged(item);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, dataSource.getShareString());

        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        } else {
            Log.d(TAG, "onOptionsItemSelected: ");
        }
        return true;
    }

    public Intent onDataChanged(MenuItem item) {
//        MenuItem item = menu.findItem(R.id.action_teile_shoppinglist);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, dataSource.getShareString());

        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        } else {
            Log.d(TAG, "onOptionsItemSelected: ");
        }
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void activateAddButtom() {
        Button button = findViewById(R.id.button_add_product);
        final EditText editTextQuantity = findViewById(R.id.editText_quantity);
        final EditText editTextProduct = findViewById(R.id.editText_product);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = editTextQuantity.getText().toString();
                String product = editTextProduct.getText().toString();

                if (TextUtils.isEmpty(quantity)) {
                    editTextQuantity.setError(getString(R.string.editText_errorMessage));
                    editTextQuantity.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(product)) {
                    editTextProduct.setError(getString(R.string.editText_errorMessage));
                }

                int quantity_int = Integer.parseInt(quantity);
                editTextQuantity.setText("");
                editTextProduct.setText("");

                dataSource.createShoppingMemo(product, quantity_int);

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                editTextQuantity.requestFocus();
                showAllListEntries();
            }
        });
    }

    private void initializeContextualActionBar() {
        final ListView shoppingMemoListView = findViewById(R.id.listview_shopping_memos);
        shoppingMemoListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        shoppingMemoListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int selCount = 0;

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
                if (checked) {
                    selCount++;

                } else {
                    selCount--;
                }
                String cabTitel = selCount + " " + getString(R.string.cab_checked_string);
                actionMode.setTitle(cabTitel);
                actionMode.invalidate();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem item = menu.findItem(R.id.cab_change);
                if (selCount == 1) {
                    item.setVisible(true);
                } else {
                    item.setVisible(false);
                }
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                SparseBooleanArray touchedShoppingMemosPosition = shoppingMemoListView.getCheckedItemPositions();
                switch (item.getItemId()) {
                    case R.id.cab_delete:
                        for (int i = 0; i < touchedShoppingMemosPosition.size(); i++) {
                            boolean isChecked = touchedShoppingMemosPosition.valueAt(i);
                            if (isChecked) {
                                int positionInListView = touchedShoppingMemosPosition.keyAt(i);
                                ShoppingMemo memo = (ShoppingMemo) shoppingMemoListView.getItemAtPosition(positionInListView);
                                Log.d(TAG, "onActionItemClicked_delete: Position im Listview: " + positionInListView + " Inhalt " + memo.toString());
                                dataSource.deleteShoppingMemo(memo);
                            }
                        }
                        showAllListEntries();
                        mode.finish();
                        return true;

                    case R.id.cab_change:
                        Log.d(TAG, "Eintrag ändern");
                        for (int i = 0; i < touchedShoppingMemosPosition.size(); i++) {
                            boolean isChecked = touchedShoppingMemosPosition.valueAt(i);
                            if (isChecked) {
                                int positionInListView = touchedShoppingMemosPosition.keyAt(i);
                                ShoppingMemo memo = (ShoppingMemo) shoppingMemoListView.getItemAtPosition(positionInListView);
                                Log.d(TAG, "onActionItemClicked_change: Position im Listview: "+ i + " : " + positionInListView + " Inhalt " + memo.toString());

                                AlertDialog editShoppingMemoDialog = createEditShoppingMemoDialog(memo);
                                editShoppingMemoDialog.show();

                            }
                        }
                        mode.finish();
//                        onDataChanged(item);
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selCount = 0;
            }
        });
    }
}



