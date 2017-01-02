package com.example.prekshasingla.homegenie.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.prekshasingla.homegenie.Data.Contract.WeatherEntry;
import com.example.prekshasingla.homegenie.Data.Contract.MasterControlEntry;
import com.example.prekshasingla.homegenie.Data.Contract.PresetEntry;

/**
 * Created by prekshasingla on 15/12/16.
 */
public class DbHelper  extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "weather.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_Weather_TABLE = "CREATE TABLE " +WeatherEntry.TABLE_NAME + " (" +
                WeatherEntry.COLUMN_CITY + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_DATE+ " REAL NOT NULL, "+
                WeatherEntry.COLUMN_FRIENDLY_DATE+ " TEXT NOT NULL, "+
                WeatherEntry.COLUMN_RAIN+" REAL NOT NULL, "+
                WeatherEntry.COLUMN_ICON+" TEXT NOT NULL, "+
                WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +
                WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL , "+
                WeatherEntry.COLUMN_LOCATION + " TEXT NOT NULL ,"+
                WeatherEntry.COLUMN_UNIT_TYPE + " TEXT NOT NULL);";

        final String SQL_CREATE_Master_Control_TABLE = "CREATE TABLE " + MasterControlEntry.TABLE_NAME + " (" +
                 MasterControlEntry.COLUMN_STATUS+" INTEGER NOT NULL);";

         final String SQL_CREATE_PRESET_TABLE = "create table if not exists "
                + PresetEntry.TABLE_NAME + "( "+ PresetEntry.COLUMN_NAME + " VARCHAR(25) primary key, " + PresetEntry.COLUMN_START_TIME + " VARCHAR(25), " + PresetEntry.COLUMN_STOP_TIME + " VARCHAR(25) , "
                + PresetEntry.COLUMN_STATUS + " INTEGER ," + PresetEntry.COLUMN_REQCODEONN+" VARCHAR(10) , "+PresetEntry.COLUMN_REQCODEOFF+" VARCHAR(10) "+" );";


        sqLiteDatabase.execSQL(SQL_CREATE_Weather_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_Master_Control_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PRESET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MasterControlEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PresetEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
