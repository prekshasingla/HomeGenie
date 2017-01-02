package com.example.prekshasingla.homegenie.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.prekshasingla.homegenie.Data.Contract;
import com.example.prekshasingla.homegenie.R;

/**
 * Created by prekshasingla on 30/12/16.
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RVFactory(this.getApplicationContext(), intent);
    }

    /**
     * Equivalent to a CursorAdapter/ArrayAdapter with ListView.
     */
    public class RVFactory implements RemoteViewsFactory {

        private Context context;
        private Cursor cursor;
        private int appWidgetId;

        public RVFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        /**
         * Method where we should initialize all our data collections.
         * In this case, our cursor.
         */
        @Override
        public void onCreate() {

            cursor = getContentResolver().query(
                    Contract.MasterControlEntry.CONTENT_URI,
                    new String[]{Contract.MasterControlEntry.COLUMN_STATUS},null,null,null);

        }

        /**
         * Called when notifyDataSetChanged() is called.
         * Hence we can update the widget with new data!
         */
        @Override
        public void onDataSetChanged() {
            cursor = getContentResolver().query(
                    Contract.MasterControlEntry.CONTENT_URI,
                    new String[]{Contract.MasterControlEntry.COLUMN_STATUS},null,null,null);

        }

        @Override
        public void onDestroy() {
            //close the cursor.
            if (this.cursor != null)
                this.cursor.close();
        }

        @Override
        public int getCount() {
            //Meta-function for the AppWidgetManager
            return (this.cursor != null) ? this.cursor.getCount() : 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(this.context.getPackageName(), R.layout.widget_layout);



           if (this.cursor.moveToNext()) {
                String status = cursor.getString(0);

                Intent fillInIntent = new Intent();
                fillInIntent.putExtra(WidgetProvider.EXTRA_STATUS, status);

                remoteViews.setOnClickFillInIntent(R.id.widget_switch, fillInIntent);
            }


            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            //we have only one type of view to display so returning 1.
            return 1;
        }

        @Override
        public long getItemId(int position) {
            //Return the data from the ID column of the table.
            return this.cursor.getInt(0);
        }

        @Override
        public boolean hasStableIds() {
            /**
             * As the table contains a column called ID,
             * whose value we are returning at getItemId(),
             * and also is a primary column,
             * every Id is unique and hence stable.
             */
            return true;
        }
    }
}


