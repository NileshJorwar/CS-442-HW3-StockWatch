package mypc.mad.hw3_stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "MainActivity";
    private final List<Stock> stockArrayList = new ArrayList<>();
    private HashMap<String, String> map = new HashMap<String, String>();
    private MainActivity mainActivity;
    private SwipeRefreshLayout swiper;
    private StocksAdapter stocksAdapter;
    private DatabaseHandler databaseHandler;
    private RecyclerView recyclerView; // Layout's recyclerview
    private static final int ADD_CODE = 1;
    private static final int UPDATE_CODE = 2;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        stocksAdapter = new StocksAdapter(stockArrayList, this);
        recyclerView.setAdapter(stocksAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swiper = findViewById(R.id.swiper);
        databaseHandler = new DatabaseHandler(this);
        if (isOnline() == true)
            new NameDownloader(this).execute();
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline() == true)
                    loadStocksFromDB();
                else {
                    errorDialog("errorDialog: No Internet Connectivity!!", "No Internet Connection", "Stocks Cannot Be Updated Without A Network Connection");
                    swiper.setRefreshing(false);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        getDataFromDB();
        super.onResume();
    }

    public void getDataFromDB() {
        Log.d(TAG, "getDataFromDB: Loading stocks from DB on Startup");
        ArrayList<String[]> list = databaseHandler.loadStocks();
        stockArrayList.clear();
        stocksAdapter.notifyDataSetChanged();
        Log.d(TAG, "getDataFromDB: Loading Done" + list.size());
        for (int j = 0; j < list.size(); j++) {
            databaseHandler.deleteStock(list.get(j)[0]);
        }
        for (int i = 0; i < list.size(); i++) {
            new StockDownloader(MainActivity.this).execute(list.get(i)[0].trim(), list.get(i)[1].trim());
        }
    }


    public void loadStocksFromDB() {
        Log.d(TAG, "loadStocksFromDB: Loading stocks from DB on Startup");
        ArrayList<String[]> list = databaseHandler.loadStocks();
        stockArrayList.clear();
        stocksAdapter.notifyDataSetChanged();
        Log.d(TAG, "loadStocksFromDB: Loading Done" + list.size());
        for (int j = 0; j < list.size(); j++) {
            databaseHandler.deleteStock(list.get(j)[0]);
        }
        for (int i = 0; i < list.size(); i++) {
            new StockDownloader(MainActivity.this).execute(list.get(i)[0].trim());
        }

        swiper.setRefreshing(false);

    }


    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addStock:
                if (!isOnline())
                    errorDialog("errorDialog: No Internet Connectivity!!", "No Internet Connection", "Stocks Cannot Be Updated Without A Network Connection");
                else {
                    if (map.isEmpty())
                        new NameDownloader(this).execute();
                    openDialog();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public void openDialog() {
        Log.d(TAG, "openDialog: stock Selection Dialog!!");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText inputPrompt = new EditText(this);
        inputPrompt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        inputPrompt.setGravity(Gravity.CENTER);
        builder.setView(inputPrompt);
        builder.setTitle("Stock Selection");
        builder.setMessage("Please enter a Stock Symbol:");
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                openSelectionDialog(inputPrompt.getText().toString());
            }

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openSelectionDialog(String stockSymbol) {
        Log.d(TAG, "openSelectionDialog: ");
        final ArrayList<String> selecArr = new ArrayList<>();

        for (String key : map.keySet()) {
            if (key.contains(stockSymbol.trim())) {
                selecArr.add(key + " - " + map.get(key));
            }
        }
        if (selecArr.size() == 0)
            errorDialog("errorDialog: symbol not found", "Symbol Not Found: " + stockSymbol.trim(), "Data for stock symbol");

        else if (selecArr.size() == 1) {
            duplicateStockExists(0, selecArr);
        } else if (selecArr.size() > 1) {
            Collections.sort(selecArr);
            CharSequence symChars[] = selecArr.toArray(new CharSequence[selecArr.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Make a selection");
            builder.setItems(symChars, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    duplicateStockExists(which, selecArr);
                }
            });
            builder.setNegativeButton("NEVERMIND", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void duplicateStockExists(int which, ArrayList<String> selecArr) {
        Stock stock;
        ArrayList<String> symbolList = new ArrayList<>();
        for (int index = 0; index < stockArrayList.size(); index++) {
            stock = stockArrayList.get(index);
            symbolList.add(stock.getStock_symbol());
        }
        if (symbolList.contains(selecArr.get(which).split("-")[0].trim())) {
            warningDialog("warningDialog: duplicate stock", selecArr.get(which).split("-")[0].trim());
        } else {
            new StockDownloader(MainActivity.this).execute(selecArr.get(which).split("-")[0].trim());
        }
    }

    public void warningDialog(String logStmt, String symbol) {
        Log.d(TAG, logStmt);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_warning_black_24dp);
        builder.setTitle("Duplicate Stock");
        builder.setMessage("Stock Symbol " + symbol + " is already displayed");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void errorDialog(String logStmt, String title, String message) {
        Log.d(TAG, logStmt);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: MainActivity");
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock stock = stockArrayList.get(pos);
        Log.d(TAG, "opening marketwatch.com for " + stock.getStock_symbol());
        String marketWatchUrl = "http://www.marketwatch.com/investing/stock/" + stock.getStock_symbol();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketWatchUrl));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        Log.d(TAG, "onLongClick: MainActivity");
        final int pos = recyclerView.getChildLayoutPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_black_24dp);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock Symbol '" + stockArrayList.get(pos).getStock_symbol() + "'?");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                databaseHandler.deleteStock(stockArrayList.get(pos).getStock_symbol());
                stockArrayList.remove(pos);
                Collections.sort(stockArrayList);
                stocksAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    public void updateData(HashMap<String, String> sMap) {
        map.putAll(sMap);
        Log.d(TAG, "updateData: Stock Data: " + map.size());
        if (map != null)
            Toast.makeText(this, "Loaded " + map.size() + " stock symbols.", Toast.LENGTH_SHORT).show();
    }

    public void updateFinanceData(Stock stock) {
        Log.d(TAG, "updateFinanceData: Stock Data for symbol:" + flag);
        stockArrayList.add(stock);
        databaseHandler.addStock(stock);
        Collections.sort(stockArrayList);
        stocksAdapter.notifyDataSetChanged();

    }
}