package com.example.prekshasingla.homegenie.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by prekshasingla on 15/12/16.
 */
public class Contract {

    public static final String CONTENT_AUTHORITY = "com.example.prekshasingla.homegenie.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WEATHER = "weather";
    public static final String PATH_Master_Control = "mastercontrol";
    public static final String PATH_PRESET = "preset";


    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";

        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_FRIENDLY_DATE = "friendlydate";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_SHORT_DESC = "short_desc";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_ICON = "icon";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_RAIN = "rain";
        public static final String COLUMN_DEGREES = "degrees";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_UNIT_TYPE = "unitType";

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static final class MasterControlEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_Master_Control).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_Master_Control;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_Master_Control;

        public static final String TABLE_NAME = "mastercontrol";

        public static final String COLUMN_STATUS = "status";

        public static Uri buildMasterControlUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static final class PresetEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRESET).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRESET;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRESET;

        public static final String TABLE_NAME = "preset";

        public static final String COLUMN_NAME="name";
        public static final String COLUMN_START_TIME = "starttime";
        public static final String COLUMN_STOP_TIME = "stoptime";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_REQCODEONN="reqcodeonn";
        public static final String COLUMN_REQCODEOFF="reqcodeoff";

        public static Uri buildPresetUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }
}
