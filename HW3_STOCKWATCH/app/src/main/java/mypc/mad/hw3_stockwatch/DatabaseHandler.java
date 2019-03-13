package mypc.mad.hw3_stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    // DB Name
    private static final String DATABASE_NAME = "StockAppDB";
    // DB Table Name
    private static final String TABLE_NAME = "StockWatchTable";

    //DB Columns
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";
    // DB Table Create Code

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " TEXT not null)";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        database=getWritableDatabase();
        Log.d(TAG, "DatabaseHandler: Constructor Done");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Creating New DB if not exist...!");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void addStock(Stock stock)
    {
        Log.d(TAG, "addStock: Adding "+stock.getStock_symbol());
        ContentValues values=new ContentValues();
        values.put(SYMBOL,stock.getStock_symbol());
        values.put(COMPANY,stock.getCompany_name());
        database.insert(TABLE_NAME,null,values);
        Log.d(TAG, "addStock: Add Complete");
    }
    public ArrayList<String[]> loadStocks()
    {
        Log.d(TAG, "loadStocks: Loading Stocks from Database...");
        ArrayList<String[]> stocks= new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{SYMBOL, COMPANY}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order
        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                stocks.add(new String[]{symbol, company});
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadStocks: Completed!!!");
        return stocks;
    }

    public void deleteStock(String stock_symbol) {
        Log.d(TAG, "deleteStock: "+stock_symbol);
        int numRows=database.delete(TABLE_NAME,SYMBOL+" =?",new String[]{stock_symbol});
        Log.d(TAG, "deleteStock: no of records deleted = "+numRows);
    }

    public void shutDown() {
        database.close();
    }
}