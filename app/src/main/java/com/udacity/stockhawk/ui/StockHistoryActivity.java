package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class StockHistoryActivity extends AppCompatActivity {

    private String mSymbol;
    private String mHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_history);

        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()) {
            mSymbol = bundle.getString("Symbol");
        }

        Uri uriForStock = Contract.Quote.makeUriForStock(mSymbol);
        Cursor cursor = getContentResolver().query(uriForStock,null,null,null,null);

        while (cursor.moveToNext()) {
            mHistory = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
        }

        ArrayList<Entry> entries = new ArrayList<Entry>();
        List<String> quotes = Arrays.asList(mHistory.split("\\r?\\n"));
        Collections.reverse(quotes);

        Calendar calendar = Calendar.getInstance();

        ArrayList<String> dates = new ArrayList<String>();

        int i=0;
        for (String quote: quotes){
            String[] dateValues = quote.split(",");
            calendar.setTimeInMillis(Long.parseLong(dateValues[0]));

            String date =  calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR);
            dates.add(date);

            entries.add(new Entry((float)i, Float.parseFloat(dateValues[1])));
            i++;
        }
        Log.d("dates",dates.toString());


        LineChart chart = (LineChart) findViewById(R.id.stock_history_chart);


        LineDataSet dataSet= new LineDataSet(entries, "");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        Object[] datesObject = dates.toArray();
        xAxis.setValueFormatter(new ValueFormatter(Arrays.copyOf(datesObject,datesObject.length,
                String[].class)));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.YELLOW);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisLeft().setTextColor(Color.YELLOW);

        chart.invalidate();
    }


}
