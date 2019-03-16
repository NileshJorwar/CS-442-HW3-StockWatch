package mypc.mad.hw3_stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class StockDownloader extends AsyncTask<String, Void, String> {
    private MainActivity mainActivity;
    private final java.lang.String financeUrlPart1 = "https://api.iextrading.com/1.0/stock/";
    private final java.lang.String financeUrlPart2 = "/quote?displayPercent=true";
    private static final java.lang.String TAG = "StockDownloader";
    private ArrayList<String> offlineData = new ArrayList<>();

    public StockDownloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        if (strings.length == 2) {
            offlineData.add(strings[0]);
            offlineData.add(strings[1]);
        }
        Uri dataUri = Uri.parse(financeUrlPart1 + strings[0] + financeUrlPart2);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "doInBackground: ResponseCode: " + conn.getResponseCode());
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }
        Log.d(TAG, "doInBackground: " + sb.toString());
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null) {
            Stock stock = parseJSON(s);
            mainActivity.updateFinanceData(stock);
        } else {
            if (offlineData.size() == 2) {
                Stock stock;
                stock = new Stock(offlineData.get(0), offlineData.get(1), 0, 0, 0);
                mainActivity.updateFinanceData(stock);
            }
        }
    }

    protected Stock parseJSON(String s) {
        //Stock stock=null;
        try {
            JSONObject jFinance = new JSONObject(s);
            String symbol = jFinance.getString("symbol");
            String cName = jFinance.getString("companyName");
            String l = jFinance.getString("latestPrice");
            double latestPrice = 0.0;
            if (l != null && !l.trim().isEmpty() && !l.trim().equals("null"))
                latestPrice = Double.parseDouble(l.trim());
            String ch = jFinance.getString("change");
            double change = 0.0;
            if (ch != null && !ch.trim().isEmpty() && !ch.trim().equals("null"))
                change = Double.parseDouble(ch.trim());
            String chP = jFinance.getString("changePercent");
            double changePercent = 0.0;
            if (chP != null && !chP.trim().isEmpty() && !chP.trim().equals("null"))
                changePercent = Double.parseDouble(chP.trim());
            Stock stock = new Stock(symbol, cName, latestPrice, change, changePercent);
            Log.d(TAG, "parseJSON: Stock Symbol and Company Name: " + symbol + ", " + cName);

            return stock;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
