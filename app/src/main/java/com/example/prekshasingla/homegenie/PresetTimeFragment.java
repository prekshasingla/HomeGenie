package com.example.prekshasingla.homegenie;



import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.prekshasingla.homegenie.Data.Contract;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PresetTimeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    PresetTime_Adapter adapter;
    Cursor cursor;
    int listItemPosition;
    static ListView list;
    LoaderManager loaderManager;
    String[] projection;


    public PresetTimeFragment() {
    }


        @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            loaderManager=getLoaderManager();

            projection= new String[]{ Contract.PresetEntry.COLUMN_NAME,Contract.PresetEntry.COLUMN_START_TIME,
                    Contract.PresetEntry.COLUMN_STOP_TIME,Contract.PresetEntry.COLUMN_STATUS };

            loaderManager.initLoader(0, null,this );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootview=inflater.inflate(R.layout.fragment_preset_time, container, false);


        list=(ListView)rootview.findViewById(R.id.listview_preset);


        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                listItemPosition=position;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Delete this Preset Time?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String item=(String)list.getAdapter().getItem(listItemPosition);

                                String selection=Contract.PresetEntry.COLUMN_NAME+"=?";
                                String[] selectionArgs=new String[1];
                                selectionArgs[0]=item;

                                int deletedRows = getContext().getContentResolver().delete(
                                        Contract.PresetEntry.CONTENT_URI,
                                        selection,selectionArgs);

                                adapter.removeAll(listItemPosition);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).show();

                return false;
            }
        });

        FloatingActionButton fab=(FloatingActionButton)rootview.findViewById(R.id.fab_presetfragment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent mainIntent = new Intent(getActivity(), AddPresetActivity.class);
                getActivity().startActivityForResult(mainIntent,1);
            }
        });
        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        loaderManager.restartLoader(0, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        CursorLoader cl= new CursorLoader(getActivity(), Contract.PresetEntry.CONTENT_URI,
                new String[]{Contract.PresetEntry.COLUMN_NAME, Contract.PresetEntry.COLUMN_START_TIME,
                        Contract.PresetEntry.COLUMN_STOP_TIME, Contract.PresetEntry.COLUMN_STATUS},
                        null,null,null);

        Log.d("Uri",""+Contract.PresetEntry.CONTENT_URI);
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){

        if(data!=null) {
            Log.d("data",""+data.getCount());

            cursor = data;

            String[] nameArr,startArr,stopArr,statusArr;
            int i=0;
            int count = cursor.getCount();
            Log.d("data",""+count);
            nameArr=new String[count];
            startArr=new String[count];
            stopArr=new String[count];
            statusArr=new String[count];
            while(cursor.moveToNext()){

                nameArr[i]=cursor.getString(0);
                startArr[i]=cursor.getString(1);
                stopArr[i]=cursor.getString(2);
                statusArr[i]=cursor.getString(3);
                i++;
            }

            final List<String> names=new ArrayList<String>(Arrays.asList(nameArr));
            final List<String> start =new ArrayList<String>(Arrays.asList(startArr));
            final List<String> stop=new ArrayList<String>(Arrays.asList(stopArr));
            final List<String> status=new ArrayList<String>(Arrays.asList(statusArr));


            adapter=new PresetTime_Adapter(getActivity(),names,start,stop,status);
            list.setAdapter(adapter);
        }
        else
            Log.d("OnLoadFinished ","null");
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader){

        adapter.clear();
        adapter.notifyDataSetChanged();
       }
}
