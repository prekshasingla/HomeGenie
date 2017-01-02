package com.example.prekshasingla.homegenie;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.prekshasingla.homegenie.Data.Contract;
import com.example.prekshasingla.homegenie.Data.Provider;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddPresetActivityFragment extends Fragment {


    public AddPresetActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.fragment_add_preset, container, false);

        class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

            int flag=0;
            public SelectDateFragment(int flag){
                this.flag=flag;
            }
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                return new DatePickerDialog(getActivity(), this, yy, mm, dd);
            }

            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                populateSetDate(yy, mm+1, dd);
            }
            public void populateSetDate(int year, int month, int day) {
                if(flag==1){
                    Button dob=(Button) rootview.findViewById(R.id.btn1);
                    dob.setText(day+"/"+month+"/"+year);
                }else   if(flag==3){
                    Button dob = (Button) rootview.findViewById(R.id.btn3);
                    dob.setText(day + "/" +month + "/" + year);

                }
            }

        }
        class TimePickerFragment extends DialogFragment
                implements TimePickerDialog.OnTimeSetListener {
            int flag=0;
            public TimePickerFragment(int flag){
                this.flag=flag;
            }
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {

                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                return new TimePickerDialog(getActivity(), this, hour, minute,
                        DateFormat.is24HourFormat(getActivity()));
            }

            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                if(flag==2){
                    Button dob=(Button) rootview.findViewById(R.id.btn2);
                    dob.setText(hourOfDay+":"+minute);
                }else   if(flag==4){
                    Button dob = (Button) rootview.findViewById(R.id.btn4);
                    dob.setText(hourOfDay+":"+minute);

                }
            }
        }
        final Button btn1=(Button)rootview.findViewById(R.id.btn1);
        final Button btn2=(Button)rootview.findViewById(R.id.btn2);
        final Button btn3=(Button)rootview.findViewById(R.id.btn3);
        final Button btn4=(Button)rootview.findViewById(R.id.btn4);
        Button save=(Button)rootview.findViewById(R.id.btn_addpreset_save);
        final EditText presetName=(EditText)rootview.findViewById(R.id.edittext1_addpreset);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SelectDateFragment(1).show(getFragmentManager(),"Select Date");
            }

        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SelectDateFragment(3).show(getFragmentManager(),"Select Date");
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerFragment(2).show(getFragmentManager(),"Select time");
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerFragment(4).show(getFragmentManager(),"Select time");
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] projection= new String[1];
                projection[0]= Contract.PresetEntry.COLUMN_NAME;

                String name[];
                int i=0;
                Cursor cursor = getContext().getContentResolver().query(
                        Contract.PresetEntry.CONTENT_URI,
                        projection,null,null,null);

                int count = cursor.getCount();
                name= new String[count];
                while(cursor.moveToNext()){
                    name[i]=cursor.getString(0);
                    i++;
                }

                List<String> names=new ArrayList<String>(Arrays.asList(name));

                if(presetName.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Name cannot be blank", Toast.LENGTH_SHORT).show();
                }else {
                    if (names.contains(presetName.getText().toString())) {
                        Toast.makeText(getContext(), "Name already used. Choose a different name", Toast.LENGTH_SHORT).show();
                    } else {
                        if(btn1.getText().toString().equals("pick date")||btn2.getText().toString().equals("pick time")
                                ||btn3.getText().toString().equals("pick date")||btn4.getText().toString().equals("pick time")){
                            Toast.makeText(getContext(), "Choose Date and Time", Toast.LENGTH_SHORT).show();
                        }else {

                            ContentValues presetValues = new ContentValues();

                            presetValues.put(Contract.PresetEntry.COLUMN_NAME, presetName.getText().toString());
                            presetValues.put(Contract.PresetEntry.COLUMN_START_TIME, btn1.getText().toString() + " " + btn2.getText().toString());
                            presetValues.put(Contract.PresetEntry.COLUMN_STOP_TIME, btn3.getText().toString() + " " + btn4.getText().toString());
                            presetValues.put(Contract.PresetEntry.COLUMN_STATUS, 0);
                            presetValues.put(Contract.PresetEntry.COLUMN_REQCODEOFF, (String)null);
                            presetValues.put(Contract.PresetEntry.COLUMN_REQCODEONN, (String)null);

                            Uri insertedUri = getContext().getContentResolver().insert(
                                    Contract.PresetEntry.CONTENT_URI,
                                    presetValues
                            );

                            getActivity().setResult(1);
                            getActivity().finish();
                        }
                    }
                }
            }
        });

        return rootview;
    }

}
