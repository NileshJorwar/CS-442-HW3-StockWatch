package mypc.mad.hw3_stockwatch;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
        stocksViewHolder.stockSymbolView.setText(stock.getStock_symbol());
        stocksViewHolder.companyNameView.setText(stock.getCompany_name());
        stocksViewHolder.priceView.setText(Double.toString(stock.getPrice()));
        stocksViewHolder.priceChangeView.setText(String.format("%.2f", stock.getPrice_change()));
        stocksViewHolder.changePercView.setText(String.format("%.2f", stock.getChange_percentage()) + " %");
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
