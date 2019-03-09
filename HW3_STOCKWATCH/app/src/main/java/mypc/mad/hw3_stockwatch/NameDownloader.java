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

public class NameDownloader extends AsyncTask<String, Void, String> {
    private MainActivity mainActivity;
    private final String stockUrl = "https://api.iextrading.com/1.0/ref-data/symbols";
    private static final String TAG = "NameDownloader";
    private HashMap<String, String> symNameHashMap = new HashMap<String, String>();

    public NameDownloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        String output = getURLData(stockUrl);
        return output;
    }

    public String getURLData(String sUrl) {
        Uri dataUri = Uri.parse(sUrl);
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

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }
        Log.d(TAG, "doInBackground: " + sb.toString());
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        HashMap<String, String> stockMap = parseJSON(s);
        mainActivity.updateData(stockMap);
    }

    protected HashMap<String, String> parseJSON(String s) {
        try {
            JSONArray jObjMain = new JSONArray(s);
            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jStock = (JSONObject) jObjMain.get(i);
                String symbol = jStock.getString("symbol");
                String cName = jStock.getString("name");
                symNameHashMap.put(symbol, cName);
            }
            return symNameHashMap;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
