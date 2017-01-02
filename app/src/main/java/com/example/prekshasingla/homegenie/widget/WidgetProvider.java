package com.example.prekshasingla.homegenie.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.prekshasingla.homegenie.MainActivity;
import com.example.prekshasingla.homegenie.MasterControlFragment;
import com.example.prekshasingla.homegenie.R;

/**
 * Created by prekshasingla on 30/12/16.
 */
public class WidgetProvider extends AppWidgetProvider {

    public static final String INTENT_ACTION = "com.example.prekshasingla.homegenie.widget.WidgetProvider.INTENT_ACTION";
    public static final String EXTRA_STATUS = "com.example.prekshasingla.homegenie.widget.WidgetProvider.EXTRA_STATUS";


    private static final String TAG = WidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int i = 0; i< appWidgetIds.length ; i++){

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            remoteViews.setRemoteAdapter(appWidgetIds[i], R.id.LinLayout_master, intent);
            remoteViews.setEmptyView(R.id.widget_button, R.id.empty_view);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        if(intent.getAction().equals(INTENT_ACTION)){


            String status = intent.getStringExtra(EXTRA_STATUS);
            //new MasterControlFragment.SyncTask_PUT().execute(Integer.parseInt(status));
            }

        super.onReceive(context, intent);
    }

}
