package com.example.yangjingqi.assignment4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "MainActivity";
    private ArrayList<Stock> stocksList = new ArrayList<>();  // Main content is here

    private ArrayList<Stock> list = new ArrayList<>();
    private RecyclerView recyclerView; // Layout's recyclerview
    private SwipeRefreshLayout swiper; // The SwipeRefreshLayout

    private StockAdapter sAdapter; // Data to recyclerview adapter
    private static String MarketWatchURL = "http://www.marketwatch.com/investing/stock/";
    private String symbol;
    private String sel_1;
    private String sel_2;
    private String sym;
    private String cmp;
    private String typ;
    private boolean isAdd = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        sAdapter = new StockAdapter(stocksList, this);
        recyclerView.setAdapter(sAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  

        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
        DatabaseHandler.getInstance(this).setupDb();

    }

    @Override
    protected void onResume() {
        DatabaseHandler.getInstance(this).dumpLog();
        list = DatabaseHandler.getInstance(this).loadStocks();
        stocksList.clear();
        updateOldStock uos = new updateOldStock(MainActivity.this);
        uos.execute(list);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        DatabaseHandler.getInstance(this).shutDown();
        super.onDestroy();
    }

    private void doRefresh() {
        Collections.shuffle(stocksList);

        DatabaseHandler.getInstance(this).dumpLog();
        list = DatabaseHandler.getInstance(this).loadStocks();
        stocksList.clear();
        updateOldStock uos = new updateOldStock(MainActivity.this);
        uos.execute(list);
        swiper.setRefreshing(false);

        Toast.makeText(this, "List content shuffled", Toast.LENGTH_SHORT).show();
    }

    public boolean networkCheck(View v) {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Toast.makeText(this, "You ARE Connected to the Internet!", Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(this, "You are NOT Connected to the Internet!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        final int pos = recyclerView.getChildLayoutPosition(view);
        Stock s = stocksList.get(pos);
        Toast.makeText(view.getContext(), "SHORT CLICK: "+stocksList.get(pos).getSymbol() , Toast.LENGTH_SHORT).show();

        Uri.Builder buildURL = Uri.parse(MarketWatchURL).buildUpon();
        buildURL.appendPath(stocksList.get(pos).getSymbol());
        String urlToUse = buildURL.build().toString();
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        startActivity(i);
        Log.d(TAG, "onClick: ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + urlToUse);
    }

    @Override
    public boolean onLongClick(View view) {
        final int pos = recyclerView.getChildLayoutPosition(view);
        Stock s = stocksList.get(pos);
        Toast.makeText(view.getContext(), "LONG CLICK:"+stocksList.get(pos).getSymbol() , Toast.LENGTH_SHORT).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatabaseHandler.getInstance(MainActivity.this).deleteStock(stocksList.get(pos).getSymbol());
                stocksList.remove(pos);
                sAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setIcon(R.drawable.delete);
        builder.setMessage("Delete Stock " + stocksList.get(pos).getCompany() + "?");
        builder.setTitle("Delete Stock");

        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_menu:

                Boolean net = networkCheck(null);;
                if (net == true) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    final EditText et = new EditText(this);
                    et.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
                    et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    et.setGravity(Gravity.CENTER_HORIZONTAL);
                    builder.setView(et);
                    builder.setIcon(R.drawable.ic_add_alert_black_24dp);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            symbol = et.getText().toString();
                            AsyncStockLoader asl = new AsyncStockLoader(MainActivity.this);
                            asl.execute(symbol);
                        }
                    });
                    builder.setNegativeButton("CANCAL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    builder.setMessage("Please enter a Stock Symbol:");
                    builder.setTitle("Stock Selection");

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;

                } else if (net == false){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Stocks Can Not Be Update Without A Network Connection");
                    builder.setTitle("No Network Connection"  );
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addData (final ArrayList<HashMap<String, String>> sArray, int size) {
        ArrayList<String> listString = new ArrayList<String>();
        if( size == 0 ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Data for stock symbol");
            builder.setTitle("Symbol Not Found: " + symbol);
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
	    }

        for (int i=0; i<sArray.size(); i++){
            sym = sArray.get(i).get("SYM");
            cmp = sArray.get(i).get("NAME");
            typ = sArray.get(i).get("TYPE");
        }
        for (HashMap<String, String> map : sArray){
            listString.add(map.get("SYM") + " - " + map.get("NAME"));
        }

        Collections.sort(listString, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        String[] sList = listString.toArray(new String[listString.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");
        builder.setItems(sList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sel_1 = sArray.get(which).get("SYM");
                sel_2 = sArray.get(which).get("NAME");
                AcyncStockDetail asd = new AcyncStockDetail(MainActivity.this);
                asd.execute(sel_1);
                doSave();
                isAdd = true;
            }
        });
        builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public Stock clickStock(HashMap<String, String> wData) {
        DatabaseHandler.getInstance(this).getOldData_sym();
        if( wData.isEmpty() ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Data for stock symbol");
            builder.setTitle("Symbol Not Found: " + symbol);
            AlertDialog dialog = builder.create();
            dialog.show();
            return null;
        }
        Stock s = new Stock(sel_1, sel_2, wData.get("l"), wData.get("c"), wData.get("cp"));
        return s;
    }

    public void updateData(Stock s) {

        boolean flag = contains_sym(stocksList, sel_1);

        if (flag) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Stock symbol "+symbol+"is already displayed.");
            builder.setTitle("Duplicate Stock " + symbol);
            builder.setIcon(R.drawable.warning);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            stocksList.add(s);
            Sort_by_symbol(stocksList);
            sAdapter.notifyDataSetChanged();
        }
    }

    private static boolean contains_sym(ArrayList<Stock> list, String s) {
        for (Stock object : list) {
            if (object.getSymbol().equals(s)) {
                return true;
            }
        }
        return false;
    }

    public void updateStockList (ArrayList<Stock> sl) {
        Sort_by_symbol(sl);
        stocksList.clear();
        stocksList.addAll(sl);
        Log.d(TAG, "updateStockList: >>><><><><><><><<<<<<<<<<<<<<<<<<<<"+stocksList.size());
        sAdapter.notifyDataSetChanged();
    }

    private void Sort_by_symbol (ArrayList<Stock> sortl){
        Collections.sort(sortl, new Comparator<Stock>() {
            public int compare(Stock v1, Stock v2) {
                return v1.getSymbol().compareTo(v2.getSymbol());
            }
        });
    }

    public Stock update_old_Stock (HashMap<String, String> wData) {
        if (wData.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Data for stock symbol");
            builder.setTitle("Symbol Not Found: " + symbol);
            AlertDialog dialog = builder.create();
            dialog.show();
            return null;
        }
        Stock s = new Stock(wData.get("t"), wData.get("company") , wData.get("l"), wData.get("c"), wData.get("cp"));
        return s;
    }

    public void doSave() {
        String symbolData = sel_1;
        String companyData = sel_2;
        Stock stock = new Stock(symbolData, companyData, null, null, null);

        if (isAdd)
            DatabaseHandler.getInstance(this).addStock(stock);
        else
            DatabaseHandler.getInstance(this).updateStock(stock);
//        finish();
    }
}
