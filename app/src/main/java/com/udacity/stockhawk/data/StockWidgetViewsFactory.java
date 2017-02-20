package com.udacity.stockhawk.data;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ron on 19/02/17.
 */
public class StockWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {


    private final Context mContext;
    List<Stock> mStockLists = new ArrayList<Stock>();
    private DecimalFormat dollarFormat;
    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat percentageFormat;

    public StockWidgetViewsFactory(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mStockLists.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews mView = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);
        Stock stock = mStockLists.get(position);
        mView.setTextViewText(R.id.symbol, stock.getmSymbol());
        mView.setTextViewText(R.id.price, stock.getmPrice());

        float rawAbsoluteChange = Float.parseFloat(stock.getmAbsoluteChange());
        float percentageChange = Float.parseFloat(stock.getmPercentageChange());

        if (rawAbsoluteChange > 0) {
            mView.setInt(R.id.change, "setBackgroundResource",
                    R.drawable.percent_change_pill_green);
        } else {
            mView.setInt(R.id.change, "setBackgroundResource",
                    R.drawable.percent_change_pill_red);
        }

        String change = dollarFormatWithPlus.format(rawAbsoluteChange);
        String percentage = percentageFormat.format(percentageChange / 100);

        if (PrefUtils.getDisplayMode(mContext)
                .equals(mContext.getString(R.string.pref_display_mode_absolute_key))) {
            mView.setTextViewText(R.id.change, change);
        } else {
            mView.setTextViewText(R.id.change, percentage);
        }

        return mView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        mStockLists.clear();
        Cursor c = mContext.getContentResolver().query(Contract.Quote.URI,
                null, null, null, null);

        if (c != null && c.moveToFirst()) {
            do {
                mStockLists.add(new Stock(
                        c.getString(c.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)),
                        c.getString(c.getColumnIndex(Contract.Quote.COLUMN_PRICE)),
                        c.getString(c.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE)),
                        c.getString(c.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE)),
                        c.getString(c.getColumnIndex(Contract.Quote.COLUMN_HISTORY))
                ));
            } while (c.moveToNext());
        }
    }

    public class Stock{

        private String mSymbol;
        private String mPrice;
        private String mAbsoluteChange;
        private String mPercentageChange;
        private String mHistory;

        public String getmSymbol() {
            return mSymbol;
        }

        public void setmSymbol(String mSymbol) {
            this.mSymbol = mSymbol;
        }

        public String getmPrice() {
            return mPrice;
        }

        public void setmPrice(String mPrice) {
            this.mPrice = mPrice;
        }

        public String getmAbsoluteChange() {
            return mAbsoluteChange;
        }

        public void setmAbsoluteChange(String mAbsoluteChange) {
            this.mAbsoluteChange = mAbsoluteChange;
        }

        public String getmPercentageChange() {
            return mPercentageChange;
        }

        public void setmPercentageChange(String mPercentageChange) {
            this.mPercentageChange = mPercentageChange;
        }

        public String getmHistory() {
            return mHistory;
        }

        public void setmHistory(String mHistory) {
            this.mHistory = mHistory;
        }

        public Stock(String mSymbol, String mPrice, String mAbsoluteChange, String mPercentageChange, String mHistory) {
            this.mSymbol = mSymbol;
            this.mPrice = mPrice;
            this.mAbsoluteChange = mAbsoluteChange;
            this.mPercentageChange = mPercentageChange;
            this.mHistory = mHistory;
        }
    }
}
