package com.example.prekshasingla.homegenie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;

import com.example.prekshasingla.homegenie.Data.Contract;
import com.example.prekshasingla.homegenie.Data.Provider;

/**
 * Created by prekshasingla on 06/12/16.
 */
public class PresetBroadcastReceiver extends BroadcastReceiver {
    String name;
    String msg;
    int position;
    @Override
    public void onReceive(Context context, Intent intent) {
        int status=intent.getIntExtra("status",-1);
        name=intent.getStringExtra("name");
        position=intent.getIntExtra("position",-1);
        if(status==1){

            String[] projection= new String[1];
            projection[0]= Contract.WeatherEntry.COLUMN_RAIN;
            double rainThreshold=0;
            Cursor cursor = context.getContentResolver().query(
                    Contract.WeatherEntry.CONTENT_URI,
                    projection,null,null,null);
            while(cursor.moveToNext()){
            rainThreshold=cursor.getDouble(0);
            }

            if(rainThreshold>=4){

                ContentValues presetValues = new ContentValues();
                presetValues.put(Contract.PresetEntry.COLUMN_STATUS,4);
                int updatedRows = context.getContentResolver().update(
                        Contract.PresetEntry.CONTENT_URI,
                        presetValues, Provider.sPresetNameSelection,new String[]{name}
                );

                msg="Heavy rainfall detected, watering cancelled";
                showNotification(context,msg);
            }
            else {
                ContentValues masterControlValues = new ContentValues();
                masterControlValues.put(Contract.MasterControlEntry.COLUMN_STATUS,1);
                int updated = context.getContentResolver().update(
                        Contract.WeatherEntry.CONTENT_URI,
                        masterControlValues,null,null);

                msg="Water pump is going to start";
                showNotification(context,msg);
                MasterControlFragment.status_switch.setChecked(true);
            }

        }else{
            msg="Water pump stopped";
            ContentValues masterControlValues = new ContentValues();
            masterControlValues.put(Contract.MasterControlEntry.COLUMN_STATUS,0);
            int updated = context.getContentResolver().update(
                    Contract.WeatherEntry.CONTENT_URI,
                    masterControlValues,null,null);

            ContentValues presetValues = new ContentValues();
            presetValues.put(Contract.PresetEntry.COLUMN_STATUS,3);
            int updatedRows = context.getContentResolver().update(
                    Contract.PresetEntry.CONTENT_URI,
                    presetValues, Provider.sPresetNameSelection,new String[]{name}
            );

            showNotification(context,msg);
            MasterControlFragment.status_switch.setChecked(false);
        }
    }

    private void showNotification(Context context,String message) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.home)
                        .setContentTitle("HomeGenie")
                        .setContentText(message);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }


}

