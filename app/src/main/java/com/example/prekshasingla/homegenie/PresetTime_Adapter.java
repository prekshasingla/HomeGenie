package com.example.prekshasingla.homegenie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prekshasingla.homegenie.Data.Contract;
import com.example.prekshasingla.homegenie.Data.Provider;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by prekshasingla on 06/12/16.
 */
public class PresetTime_Adapter extends ArrayAdapter<String>
{

    private Context context;
    private List<String> name;
    private List<String> starttime;
    private List<String> stoptime;
    private List<String> status;
    int flag;

    public PresetTime_Adapter(Context context, List<String> name,List<String> starttime,List<String> stoptime,List<String> status)
    {
        super(context,0,name);
        this.context = context;
        this.name = new ArrayList<String>();
        this.starttime = new ArrayList<String>();
        this.stoptime = new ArrayList<String>();
        this.status = new ArrayList<String>();
        this.name = name;
        this.starttime=starttime;
        this.stoptime=stoptime;
        this.status=status;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_listview_preset, parent, false);
        }

        TextView t1 = (TextView) convertView.findViewById(R.id.list_item_preset_text1);
        TextView t2 = (TextView) convertView.findViewById(R.id.list_item_preset_text2);
        TextView t3 = (TextView) convertView.findViewById(R.id.list_item_preset_text3);
        TextView t4 = (TextView) convertView.findViewById(R.id.list_item_preset_text4);
        final ImageView i4 = (ImageView) convertView.findViewById(R.id.list_item_preset_image);
        flag=0;

        t1.setText(name.get(position));
        t2.setText(starttime.get(position));
        t3.setText(stoptime.get(position));
        t4.setVisibility(View.INVISIBLE);
        if(status.get(position).equals("1")){
            flag=1;
            i4.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_alarm_on_black_24dp));
        }
        if(status.get(position).equals("3")){
            i4.setVisibility(View.INVISIBLE);
            t4.setVisibility(View.VISIBLE);
        }
        if(status.get(position).equals("4")){
            i4.setVisibility(View.INVISIBLE);
            t4.setText("CANCELLED");
            t4.setVisibility(View.VISIBLE);
        }
        i4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==0){

                    i4.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_alarm_on_black_24dp));

                    String[] selectionArgs= new String[1];
                    selectionArgs[0]=name.get(position);

                    ContentValues presetValues = new ContentValues();

                    presetValues.put(Contract.PresetEntry.COLUMN_STATUS,0);

                    int updatedRows = getContext().getContentResolver().update(
                            Contract.PresetEntry.CONTENT_URI,
                            presetValues, Provider.sPresetNameSelection,selectionArgs
                    );

                    Calendar cal1=Calendar.getInstance();
                    Calendar cal3=Calendar.getInstance();
                    SimpleDateFormat sdf=new SimpleDateFormat("d/M/yyyy H:m");
                    try {Date date=sdf.parse(starttime.get(position));
                        Date stopDate=sdf.parse(stoptime.get(position));
                        cal1.setTime(date);
                        cal3.setTime(stopDate);
                        Log.e("date",starttime.get(position)+"");
                    }catch (ParseException e){

                    }
                    String code=getCode(starttime.get(position));
                    Log.e("onClick: ",""+code);
                    String start= code+"1";
                    String stop=code+"0";

                    selectionArgs[0]=name.get(position);

                    ContentValues presetValues1 = new ContentValues();

                    presetValues1.put(Contract.PresetEntry.COLUMN_REQCODEONN, start);
                    presetValues1.put(Contract.PresetEntry.COLUMN_REQCODEOFF, stop);

                     int updatedRows1 = getContext().getContentResolver().update(
                            Contract.PresetEntry.CONTENT_URI,
                            presetValues1, Provider.sPresetNameSelection,selectionArgs
                    );


                    int startcode=Integer.parseInt(start);
                    int stopcode=Integer.parseInt(stop);
                    Intent intent = new Intent(getContext(), PresetBroadcastReceiver.class);
                    intent.putExtra("status",1);
                    intent.putExtra("name",name.get(position));
                    intent.putExtra("position",position);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            getContext(),startcode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                    AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(getContext().ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,cal1.getTimeInMillis(), pendingIntent);

                    Intent stopIntent = new Intent(getContext(), PresetBroadcastReceiver.class);
                    stopIntent.putExtra("status",0);
                    stopIntent.putExtra("name",name.get(position));
                    stopIntent.putExtra("position",position);

                    PendingIntent pendingIntentStop = PendingIntent.getBroadcast(
                            getContext(),stopcode, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManagerStop = (AlarmManager)getContext().getSystemService(getContext().ALARM_SERVICE);
                    alarmManagerStop.set(AlarmManager.RTC_WAKEUP,cal3.getTimeInMillis(), pendingIntentStop);

                    flag=1;



                }else{
                    int startCode=-1;
                    int stopCode=-1;

                    String[] selectionArgs= new String[1];
                    selectionArgs[0]=name.get(position);

                    String[] projection= new String[2];
                    projection[0]= Contract.PresetEntry.COLUMN_REQCODEONN;
                    projection[1]=Contract.PresetEntry.COLUMN_REQCODEOFF;

                    int i=0;

                    Cursor cursor = getContext().getContentResolver().query(
                            Contract.PresetEntry.CONTENT_URI,
                            projection,Provider.sPresetNameSelection,selectionArgs,null);
                    while(cursor.moveToNext()) {

                        startCode = Integer.parseInt(cursor.getString(0));
                        stopCode = Integer.parseInt(cursor.getString(1));
                    }

                    i4.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_alarm_off_black_24dp));


                    ContentValues presetValues = new ContentValues();

                    presetValues.put(Contract.PresetEntry.COLUMN_STATUS,0);

                    int updatedRows = getContext().getContentResolver().update(
                            Contract.PresetEntry.CONTENT_URI,
                            presetValues, Provider.sPresetNameSelection,selectionArgs
                    );

                    Intent intent = new Intent(getContext(), PresetBroadcastReceiver.class);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            getContext(),startCode, intent, 0);
                    AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(getContext().ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);

                    PendingIntent pendingStopIntent = PendingIntent.getBroadcast(
                            getContext(),stopCode, intent, 0);
                    AlarmManager alarmManagerStop = (AlarmManager)getContext().getSystemService(getContext().ALARM_SERVICE);
                    alarmManagerStop.cancel(pendingStopIntent);

                    flag=0;
                }
            }
        });

        return convertView;
    }

    public String getCode(String date)
    {
        String coded="";
        String[] splitted = date.split("[/ :]");
        for (int i=0;i<splitted.length;i++) {
            if(i==2) {
                splitted[i] ="";
            }
            coded=coded + splitted[i];
        }
        return coded;
    }

    public void removeAll(int listItemPosition) {
        name.remove(listItemPosition);
        starttime.remove(listItemPosition);
        stoptime.remove(listItemPosition);
        status.remove(listItemPosition);

    }
}
