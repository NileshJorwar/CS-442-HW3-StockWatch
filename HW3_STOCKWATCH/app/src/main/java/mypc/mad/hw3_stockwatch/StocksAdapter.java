package mypc.mad.hw3_stockwatch;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class StocksAdapter extends RecyclerView.Adapter<StocksViewHolder> {

    private static final String TAG = "StocksAdapter";
    private List<Stock> stockList;
    private MainActivity mainActivity;

    public StocksAdapter(List<Stock> stockList, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.stockList = stockList;
    }

    @NonNull
    @Override
    public StocksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.stock_list, viewGroup, false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);
        return new StocksViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull StocksViewHolder stocksViewHolder, int position) {
        Stock stock = stockList.get(position);
        if(stock.getPrice_change()>0)
        {
            stocksViewHolder.priceChangeView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_drop_up_black_24dp, 0, 0, 0);
            changeTextColor("#00ff00",stock,stocksViewHolder);
        }
        else{
            stocksViewHolder.priceChangeView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_drop_down_black_24dp, 0, 0, 0);
            changeTextColor("#ff0000",stock,stocksViewHolder);
        }
        stocksViewHolder.stockSymbolView.setText(stock.getStock_symbol());
        stocksViewHolder.companyNameView.setText(stock.getCompany_name());
        stocksViewHolder.priceView.setText(Double.toString(stock.getPrice()));
        stocksViewHolder.priceChangeView.setText(String.format("%.2f", stock.getPrice_change()));
        stocksViewHolder.changePercView.setText("("+String.format("%.2f", stock.getChange_percentage()) + "%)");
    }
    public void changeTextColor(String colorCode,Stock stock,StocksViewHolder stocksViewHolder)
    {

        //mDrawable.setColorFilter(new PorterDuffColorFilter(0xFF0000,PorterDuff.Mode.SRC_IN));
        stocksViewHolder.stockSymbolView.setTextColor(Color.parseColor(colorCode));
        stocksViewHolder.companyNameView.setTextColor(Color.parseColor(colorCode));
        stocksViewHolder.priceView.setTextColor(Color.parseColor(colorCode));
        stocksViewHolder.priceChangeView.setTextColor(Color.parseColor(colorCode));
        stocksViewHolder.changePercView.setTextColor(Color.parseColor(colorCode));
    }
    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
