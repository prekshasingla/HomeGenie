package com.example.prekshasingla.homegenie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by prekshasingla on 06/12/16.
 */
public class DBAdapter {
    private static DatabaseHelper DBHelper;
    static SQLiteDatabase db;


    public DBAdapter(Context context)
    {
        DBHelper = new DatabaseHelper(context);
    }


    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public  long getDate(){
        String qry= "select date from weather; ";
        Cursor cursor=db.rawQuery(qry,null);
        int count = cursor.getCount();
        long date=0;
        while(cursor.moveToNext()){
            date=cursor.getLong(0);

        }

        return date;
    }
    public List<String> getNames(){
        String names[];
        int i=0;
        String qry= "select name,starttime from presettable; ";
        Cursor cursor=db.rawQuery(qry,null);
        int count = cursor.getCount();
        names= new String[count];
        while(cursor.moveToNext()){
            names[i]=cursor.getString(0);
            Log.e("getname:",names[i]);
            i++;
        }

        List<String> nameList=new ArrayList<String>(Arrays.asList(names));
        return nameList;
    }


    public List<String> getStart(){
        String names[];
        int i=0;
        String qry= "select starttime from presettable; ";
        Cursor cursor=db.rawQuery(qry,null);
        int count = cursor.getCount();
        names= new String[count];
        while(cursor.moveToNext()){
            names[i]=cursor.getString(0);
            i++;
        }


        List<String> nameList=new ArrayList<String>(Arrays.asList(names));
        return nameList;
    }
    public List<String> getStop(){
        String names[];
        int i=0;
        String qry= "select stoptime from presettable; ";
        Cursor cursor=db.rawQuery(qry,null);
        int count = cursor.getCount();
        names= new String[count];
        while(cursor.moveToNext()){
            names[i]=cursor.getString(0);
            i++;
        }

        List<String> nameList=new ArrayList<String>(Arrays.asList(names));
        return nameList;
    }

    public List<String> getStatus(){
        String names[];
        int i=0;
        String qry= "select status from presettable; ";
        Cursor cursor=db.rawQuery(qry,null);
        int count = cursor.getCount();
        names= new String[count];
        while(cursor.moveToNext()){
            names[i]=cursor.getString(0);
            i++;

        }

        List<String> nameList=new ArrayList<String>(Arrays.asList(names));
        return nameList;
    }

    public int getStatusByName(String name){
        String qry= "select status from presettable where name='"+name+"'; ";
        Cursor cursor=db.rawQuery(qry,null);
        int status=-1;
        while (cursor.moveToNext())
        {
            status=cursor.getInt(0);
        }
        return status;
    }


    public String getReqcodeoff(String name){
        String qry= "select reqcodeoff from presettable where name='"+name+"'; ";
        Cursor cursor=db.rawQuery(qry,null);
        String stop=null;
        while (cursor.moveToNext())
        {
            stop=cursor.getString(0);
        }

        return stop;
    }
    public String getReqcodeonn(String name){
        String qry= "select reqcodeonn from presettable where name='"+name+"'; ";
        Cursor cursor=db.rawQuery(qry,null);
        String start=null;
        while (cursor.moveToNext())
        {
            start=cursor.getString(0);
        }

        return start;
    }

    public void updateReqcode(String name,String start, String stop) {
        String qr1="update presettable set reqcodeonn="+start+" where name='"+name+"';";
        db.execSQL(qr1);
        String qr2="update presettable set reqcodeoff="+stop+" where name='"+name+"';";
        db.execSQL(qr2);
    }

    public long insert(String name,String start, String stop) {
        //db = DBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(DatabaseHelper.ID, id1);
        contentValues.put(DatabaseHelper.NAME, name);
        contentValues.put(DatabaseHelper.START_TIME, start);
        contentValues.put(DatabaseHelper.STOP_TIME, stop);
        contentValues.put(DatabaseHelper.STATUS,0);
        contentValues.put(DatabaseHelper.REQCODEONN,(String)null);
        contentValues.put(DatabaseHelper.REQCODEOFF,(String)null);

        long id = db.insert(DatabaseHelper.PRESET_TABLE, null, contentValues);
        //DBHelper.close();
        return id;
    }


    public double getRain(){
        double rainfall=0;
        String qry="select rain from weather";
        Cursor c=db.rawQuery(qry,null);
        while (c.moveToNext())
        {
            rainfall=c.getDouble(0);
        }
        return rainfall;
    }

    public Cursor showWeather()
    {
        String qry="select * from weather;";
        Cursor c=db.rawQuery(qry,null);
        return c;
    }


    public void update_weatheri(int weatherId,String city,long date,String friendlyDateText,double rain,String icon,
                                String description ,double high, double low, float humidity ,double windSpeedStr,
                                double windDirStr,double pressure){//db = DBHelper.getWritableDatabase();

        ContentValues weatherValues = new ContentValues();
        weatherValues.put(DatabaseHelper.COLUMN_ICON, icon);

        weatherValues.put(DatabaseHelper.COLUMN_CITY, city);
        weatherValues.put(DatabaseHelper.COLUMN_DATE, date);
        weatherValues.put(DatabaseHelper.COLUMN_FRIENDLY_DATE, friendlyDateText);
        weatherValues.put(DatabaseHelper.COLUMN_RAIN, rain);
        weatherValues.put(DatabaseHelper.COLUMN_HUMIDITY, humidity);
        weatherValues.put(DatabaseHelper.COLUMN_PRESSURE, pressure);
        weatherValues.put(DatabaseHelper.COLUMN_WIND_SPEED, windSpeedStr);
        weatherValues.put(DatabaseHelper.COLUMN_DEGREES, windDirStr);
        weatherValues.put(DatabaseHelper.COLUMN_MAX_TEMP, high);
        weatherValues.put(DatabaseHelper.COLUMN_MIN_TEMP, low);
        weatherValues.put(DatabaseHelper.COLUMN_SHORT_DESC, description);
        weatherValues.put(DatabaseHelper.COLUMN_WEATHER_ID, weatherId);

        Cursor c=showWeather();
        int count=c.getCount();
        if(count==0)
            db.insert(DatabaseHelper.WEATHER_TABLE, null, weatherValues);
        else
        {
            db.update(DatabaseHelper.WEATHER_TABLE, weatherValues,null,null);
        }//DBHelper.close();
        //return id;

    }

    public void close() {
        DBHelper.close();
    }

    public void deletePreset(String item) {
        String qry="delete from "+DatabaseHelper.PRESET_TABLE+" where "+DatabaseHelper
                .NAME+" = '"+item+"';";
        db.execSQL(qry);
    }

    public void updateStatus(int status,String name) {
        String qry="update "+DatabaseHelper.PRESET_TABLE+" set "+DatabaseHelper
                .STATUS+" = '"+status+"' where "+DatabaseHelper.NAME+" ='"+name+"';";
        db.execSQL(qry);
    }

    public void updateCurrentStatus(int item) {
        String qry="select * from mastercontrol";
        Cursor cursor=db.rawQuery(qry,null);
        int count=cursor.getCount();
        Log.e("ffsssss",count+"");
        if(count==0){
            String qr1="insert into mastercontrol values("+item+");";
            db.execSQL(qr1);
        }else{
            String qr1="update mastercontrol set status="+item+";";
            db.execSQL(qr1);
        }


    }

    public String getIcon(){
        String qry="select icon from weather;";
        Cursor c=db.rawQuery(qry,null);
        String icon=null;
        while (c.moveToNext()){
            icon=c.getString(0);
        }
        return icon;
    }

    public int getCurrentStatus() {
        String qry="select status from mastercontrol;";
        Cursor cursor=db.rawQuery(qry,null);
        int currentStatus=0;
        while(cursor.moveToNext()){
            currentStatus=cursor.getInt(0);

        }
        return currentStatus;
    }

    public Cursor getPresetAlarms() {
        String qry="select * from presettable;";
        Cursor cursor=db.rawQuery(qry,null);

        return cursor;
    }

    public String getCurrentLocation() {
        String qry="select location from weather;";
        Cursor cursor=db.rawQuery(qry,null);
        String currentLocation=null;
        while(cursor.moveToNext()){
            currentLocation=cursor.getString(0);

        }
        return currentLocation;
    }

    public String getCurrentUnitType() {
        String qry="select unitType from weather;";
        Cursor cursor=db.rawQuery(qry,null);
        String currentUnitType=null;
        while(cursor.moveToNext()){
            currentUnitType=cursor.getString(0);

        }
        return currentUnitType;
    }


    static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "AutoIrrigateDB";

        private static final String NAME="name";
        private static final String START_TIME = "starttime";
        private static final String STOP_TIME = "stoptime";
        private static final String STATUS = "status";
        private static final String REQCODEONN="reqcodeonn";
        private static final String REQCODEOFF="reqcodeoff";

        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_FRIENDLY_DATE = "friendlydate";

        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        public static final String COLUMN_ICON = "icon";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";

        public static final String COLUMN_RAIN = "rain";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";
        public static final String COLUMN_LOCATION = "location";

        public static final String COLUMN_UNIT_TYPE = "unitType";


        public static final String COLUMN_STATUS = "status";

        private static final String PRESET_TABLE = "presettable";
        public static final String WEATHER_TABLE = "weather";
        public static final String STATUS_TABLE = "mastercontrol";


        private static final String TAG = "DBAdapter";

        private static final int DATABASE_VERSION = 1;



        private static final String PRESET_CREATE = "create table if not exists "
                + PRESET_TABLE + "( "+ NAME + " VARCHAR(25) primary key, " + START_TIME + " VARCHAR(25), " + STOP_TIME + " VARCHAR(25) , "
                + STATUS + " INTEGER ," + REQCODEONN+" VARCHAR(10) , "+REQCODEOFF+" VARCHAR(10) "+" );";


        private static final String  STATUS_CREATE=" create table if not exists "
                +STATUS_TABLE+ "("+ COLUMN_STATUS+" INTEGER NOT NULL);";





        final String WEATHER_CREATE = "CREATE TABLE IF NOT EXISTS " +WEATHER_TABLE + " (" +
                COLUMN_CITY + " TEXT NOT NULL, " +
                COLUMN_DATE+ " REAL NOT NULL, "+
                COLUMN_FRIENDLY_DATE+ " TEXT NOT NULL, "+
                COLUMN_RAIN+" REAL NOT NULL, "+
                COLUMN_ICON+" TEXT NOT NULL, "+
                COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                COLUMN_WEATHER_ID + " INTEGER NOT NULL," +
                COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                COLUMN_MAX_TEMP + " REAL NOT NULL, " +
                COLUMN_HUMIDITY + " REAL NOT NULL, " +
                COLUMN_PRESSURE + " REAL NOT NULL, " +
                COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                COLUMN_DEGREES + " REAL NOT NULL , "+
                COLUMN_LOCATION + " TEXT NOT NULL ,"+
                COLUMN_UNIT_TYPE + " TEXT NOT NULL)";



        DatabaseHelper(Context context) {

            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(PRESET_CREATE);
            db.execSQL(WEATHER_CREATE);
            db.execSQL(STATUS_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS" + PRESET_TABLE);
            db.execSQL("DROP TABLE IF EXISTS" + WEATHER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS" + STATUS_TABLE);

            onCreate(db);
        }
    }
}

