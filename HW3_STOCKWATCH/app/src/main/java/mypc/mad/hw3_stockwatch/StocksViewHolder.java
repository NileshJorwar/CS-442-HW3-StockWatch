package mypc.mad.hw3_stockwatch;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class StocksViewHolder extends RecyclerView.ViewHolder {

    public TextView stockSymbolView;
    public TextView companyNameView;
    public TextView priceView;
    public TextView priceChangeView;
    public TextView changePercView;
    public StocksViewHolder(@NonNull View itemView) {
        super(itemView);
        stockSymbolView=itemView.findViewById(R.id.stockSymbolId);
        companyNameView=itemView.findViewById(R.id.cmpNameId);
        priceView=itemView.findViewById(R.id.priceId);
        priceChangeView=itemView.findViewById(R.id.priceChngId);
        changePercView=itemView.findViewById(R.id.changePerId);
    }
}
