package com.udacity.stockhawk.data;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by ron on 19/02/17.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        StockWidgetViewsFactory stockWidgetViewsFactory = new StockWidgetViewsFactory(
                getApplicationContext(), intent);
        return stockWidgetViewsFactory;
    }
}
