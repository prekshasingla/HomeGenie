package com.example.prekshasingla.homegenie.Data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by prekshasingla on 15/12/16.
 */
public class Provider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;

    static final int WEATHER = 100;

    static final int MASTER_CONTROL = 300;

    static final int PRESET = 500;

    public static final String sPresetNameSelection =
            Contract.PresetEntry.TABLE_NAME+
                    "." + Contract.PresetEntry.COLUMN_NAME + " = ? ";




    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        matcher.addURI(authority,Contract.PATH_WEATHER, WEATHER);
        matcher.addURI(authority,Contract.PATH_Master_Control,MASTER_CONTROL);
        matcher.addURI(authority,Contract.PATH_PRESET, PRESET);


        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case WEATHER:
                return Contract.WeatherEntry.CONTENT_TYPE;
            case MASTER_CONTROL:
                return Contract.MasterControlEntry.CONTENT_ITEM_TYPE;
            case PRESET:
                return Contract.PresetEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor=null;
        switch (sUriMatcher.match(uri)) {

            case WEATHER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Contract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MASTER_CONTROL: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Contract.MasterControlEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PRESET: {
                Log.d("Preset","Inside Preset");
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Contract.PresetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                Log.d("dataprset",""+retCursor.getCount());
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
            return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case WEATHER: {
                long _id = db.insert(Contract.WeatherEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Contract.WeatherEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MASTER_CONTROL: {
                long _id = db.insert(Contract.MasterControlEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Contract.MasterControlEntry.buildMasterControlUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PRESET: {

                long _id = db.insert(Contract.PresetEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Contract.PresetEntry.buildPresetUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case WEATHER:
                rowsDeleted = db.delete(Contract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MASTER_CONTROL:
                rowsDeleted = db.delete(Contract.MasterControlEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRESET:
                rowsDeleted = db.delete(Contract.PresetEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            final int match = sUriMatcher.match(uri);
            int rowsUpdated;

            switch (match) {
                case WEATHER:
                    rowsUpdated = db.update(Contract.WeatherEntry.TABLE_NAME, values, selection,
                            selectionArgs);
                    break;
                case MASTER_CONTROL:
                    rowsUpdated = db.update(Contract.MasterControlEntry.TABLE_NAME, values, selection,
                            selectionArgs);
                    break;
                case PRESET:
                    rowsUpdated = db.update(Contract.PresetEntry.TABLE_NAME, values, selection,
                            selectionArgs);
                    break;


                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsUpdated;
    }


}