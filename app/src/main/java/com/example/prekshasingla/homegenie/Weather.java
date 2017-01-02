package com.example.prekshasingla.homegenie;


import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prekshasingla.homegenie.Data.Contract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Weather extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mRainView;
    private TextView mCityView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;
    TextView empty_tv;
    View scrollView;
    //DBAdapter dba;
    SharedPreferences prefs;
    String unitType;
    String location;
    Cursor c;
    LoaderManager loaderManager;





    public Weather() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager=getLoaderManager();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //dba= new DBAdapter(getActivity().getApplicationContext());
        loaderManager.initLoader(1, null,this );

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        unitType = prefs.getString(
                getString(R.string.pref_units_key),
                getString(R.string.pref_units_metric));

        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mRainView = (TextView) rootView.findViewById(R.id.detail_rain_textview);
        mCityView = (TextView) rootView.findViewById(R.id.detail_city_textview);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        empty_tv=(TextView)rootView.findViewById(R.id.empty_tv);
        scrollView= rootView.findViewById(R.id.scrollView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        loaderManager.restartLoader(1, null, this);

    }

    public String getFormattedWind(double windSpeed, double degrees) {

        String direction = "Unknown";
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = "N";
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = "NE";
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = "E";
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = "SE";
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = "S";
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = "SW";
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = "W";
        } else if (degrees >= 292.5 && degrees < 337.5) {
            direction = "NW";
        }
        return windSpeed+" kmph "+direction;
    }

    public boolean compareDate(Long dateInMillis) {
        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return true;
        } else
            return false;
    }

    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
       weatherTask.execute(location);
      }

    @Override
    public void onStart() {
        super.onStart();
         }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){

        CursorLoader cl= new CursorLoader(getActivity(), Contract.WeatherEntry.CONTENT_URI,
                new String[]{Contract.WeatherEntry.COLUMN_WEATHER_ID,Contract.WeatherEntry.COLUMN_CITY,
                        Contract.WeatherEntry.COLUMN_DATE,Contract.WeatherEntry.COLUMN_FRIENDLY_DATE,
                        Contract.WeatherEntry.COLUMN_RAIN,Contract.WeatherEntry.COLUMN_ICON,
                        Contract.WeatherEntry.COLUMN_SHORT_DESC,Contract.WeatherEntry.COLUMN_MAX_TEMP,
                        Contract.WeatherEntry.COLUMN_MIN_TEMP,Contract.WeatherEntry.COLUMN_HUMIDITY,
                        Contract.WeatherEntry.COLUMN_WIND_SPEED, Contract.WeatherEntry.COLUMN_DEGREES,
                        Contract.WeatherEntry.COLUMN_PRESSURE, Contract.WeatherEntry.COLUMN_LOCATION,
                        Contract.WeatherEntry.COLUMN_UNIT_TYPE},
                null,null,null);

        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null) {
            Log.d("weather data", "" + data.getCount());

            int i=0;
            Cursor c= data;
            int count=c.getCount();
            if(count==0) {
                Log.d("weather table","null");
                updateWeather();
            }

            else {
                while (data.moveToNext()) {
                    Log.d("weather table","not null");

                    if (!compareDate(data.getLong(data.getColumnIndex(Contract.WeatherEntry.COLUMN_DATE)))) {
                        Log.d("weather table","date diff");

                        updateWeather();
                    }
                    else if (!location.equals(data.getString(data.getColumnIndex(Contract.WeatherEntry.COLUMN_LOCATION)))) {
                        Log.d("weather table","location change");

                        updateWeather();
                    }
                    else if (!unitType.equals(data.getString(data.getColumnIndex(Contract.WeatherEntry.COLUMN_UNIT_TYPE)))) {
                        Log.d("weather table","unit different");

                        updateWeather();
                    }

                    else {
                        Log.d("weather table","inside else");

                        WeatherData[] result;
                        result = new WeatherData[count];
                        Log.d("weather table",""+c.getCount());

                            result[0] = new WeatherData(c.getInt(c.getColumnIndex(Contract.WeatherEntry.COLUMN_WEATHER_ID)),
                                    c.getString(c.getColumnIndex(Contract.WeatherEntry.COLUMN_CITY)),
                                    c.getInt(c.getColumnIndex(Contract.WeatherEntry.COLUMN_DATE)),
                                    c.getString(c.getColumnIndex(Contract.WeatherEntry.COLUMN_FRIENDLY_DATE)),
                                    c.getDouble(c.getColumnIndex(Contract.WeatherEntry.COLUMN_RAIN)),
                                    c.getString(c.getColumnIndex(Contract.WeatherEntry.COLUMN_ICON)),
                                    c.getString(c.getColumnIndex(Contract.WeatherEntry.COLUMN_SHORT_DESC)),
                                    c.getDouble(c.getColumnIndex(Contract.WeatherEntry.COLUMN_MAX_TEMP)),
                                    c.getDouble(c.getColumnIndex(Contract.WeatherEntry.COLUMN_MIN_TEMP)),
                                    c.getFloat(c.getColumnIndex(Contract.WeatherEntry.COLUMN_HUMIDITY)),
                                    c.getDouble(c.getColumnIndex(Contract.WeatherEntry.COLUMN_WIND_SPEED)),
                                    c.getDouble(c.getColumnIndex(Contract.WeatherEntry.COLUMN_DEGREES)),
                                    c.getDouble(c.getColumnIndex(Contract.WeatherEntry.COLUMN_PRESSURE)), location, unitType);

                        empty_tv.setVisibility(View.INVISIBLE);
                        scrollView.setVisibility(View.VISIBLE);

                        mCityView.setText("" + result[0].city);
                        mRainView.setText("Precipitation: " + result[0].rain + " mm");
                        mFriendlyDateView.setText(result[0].friendlyDateText);
                        String iconString = "i" + result[0].icon;
                        Log.d("Icon",iconString);
                        int iconID = getContext().getResources().getIdentifier(iconString, "drawable", getContext().getPackageName());
                        mIconView.setImageDrawable(getContext().getResources().getDrawable(iconID));
                        mDescriptionView.setText(result[0].description);
                        mHighTempView.setText("Max: " + result[0].high);
                        mLowTempView.setText("Min: " + result[0].low);
                        mHumidityView.setText("Humidity: " + result[0].humidity + "%");
                        mWindView.setText("Wind: " + getFormattedWind(result[0].windSpeedStr, result[0].windDirStr));
                        mPressureView.setText("Pressure: " + result[0].pressure);
                    }
                }
            }
    }
        else
            Log.d("OnLoadFinished ","null");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){

    }


    public class FetchWeatherTask extends AsyncTask<String, Void, WeatherData> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
private String getReadableDateString(long time){
    
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        public String getFormattedWind(double windSpeed, double degrees) {

            String direction = "Unknown";
            if (degrees >= 337.5 || degrees < 22.5) {
                direction = "N";
            } else if (degrees >= 22.5 && degrees < 67.5) {
                direction = "NE";
            } else if (degrees >= 67.5 && degrees < 112.5) {
                direction = "E";
            } else if (degrees >= 112.5 && degrees < 157.5) {
                direction = "SE";
            } else if (degrees >= 157.5 && degrees < 202.5) {
                direction = "S";
            } else if (degrees >= 202.5 && degrees < 247.5) {
                direction = "SW";
            } else if (degrees >= 247.5 && degrees < 292.5) {
                direction = "W";
            } else if (degrees >= 292.5 && degrees < 337.5) {
                direction = "NW";
            }
            return windSpeed+" kmph "+direction;
        }

        private double getTempByUnit(double temp, String unitType) {

            if (unitType.equals(getString(R.string.pref_units_imperial))) {
                temp = (temp * 1.8) + 32;
                } else if (!unitType.equals(getString(R.string.pref_units_metric))) {
                Log.d(LOG_TAG, "Unit type not found: " + unitType);
            }

            long roundedTemp = Math.round(temp);

            return roundedTemp;
        }

        private WeatherData getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            final String OWM_CITY = "city";
            final String OWM_CITY_NAME = "name";
            final String OWM_LIST = "list";
            final String OWM_PRESSURE = "pressure";
            final String OWM_HUMIDITY = "humidity";
            final String OWM_WINDSPEED = "speed";
            final String OWM_WIND_DIRECTION = "deg";
            final String OWM_RAIN= "rain";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_WEATHER = "weather";
            final String OWM_ICON = "icon";
            final String OWM_DESCRIPTION = "main";
            final String OWM_WEATHER_ID = "id";

            try {
                JSONObject forecastJson = new JSONObject(forecastJsonStr);

                    JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
                    JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
                    String cityName = cityJson.getString(OWM_CITY_NAME);
                    Time dayTime = new Time();
                    dayTime.setToNow();
                    int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
                    dayTime = new Time();
                    for (int i = 0; i < weatherArray.length(); i++) {
                        long dateTime;
                        String friendlyDate;
                        double pressure;
                        int humidity;
                        double windSpeed;
                        double windDirection;
                        double rain = 0.00;
                        double high;
                        double low;
                        String icon;
                        String description;
                        int weatherId;

                        JSONObject dayForecast = weatherArray.getJSONObject(i);
                        dateTime = dayTime.setJulianDay(julianStartDay + i);

                        friendlyDate = getReadableDateString(dateTime);
                        pressure = dayForecast.getDouble(OWM_PRESSURE);
                        humidity = dayForecast.getInt(OWM_HUMIDITY);
                        windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
                        windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

                        if (dayForecast.has(OWM_RAIN))
                            rain = dayForecast.getDouble(OWM_RAIN);
                        JSONObject weatherObject =
                                dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                        icon = weatherObject.getString(OWM_ICON);

                        description = weatherObject.getString(OWM_DESCRIPTION);
                        weatherId = weatherObject.getInt(OWM_WEATHER_ID);
                        JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                        high = getTempByUnit(temperatureObject.getDouble(OWM_MAX), unitType);
                        low = getTempByUnit(temperatureObject.getDouble(OWM_MIN), unitType);
                        WeatherData weatherValues = new WeatherData(weatherId, cityName, dateTime,
                                friendlyDate, rain, icon, description, high, low, humidity, windSpeed,
                                windDirection, pressure, location, unitType);

                        return weatherValues;
                    }
                    Log.d(LOG_TAG, "FetchWeatherTask Complete.  Inserted");

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected WeatherData doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 1;

            try {
                
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, "cb46fcc1d00c4a44ef1f4f163b4a695c")
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(WeatherData result) {
            if (result != null) {

                ContentValues weatherValues = new ContentValues();

                weatherValues.put(Contract.WeatherEntry.COLUMN_WEATHER_ID, result.weatherId);
                weatherValues.put(Contract.WeatherEntry.COLUMN_CITY, result.city);
                weatherValues.put(Contract.WeatherEntry.COLUMN_DATE, result.date);
                weatherValues.put(Contract.WeatherEntry.COLUMN_FRIENDLY_DATE, result.friendlyDateText);
                weatherValues.put(Contract.WeatherEntry.COLUMN_RAIN, result.rain);
                weatherValues.put(Contract.WeatherEntry.COLUMN_ICON, result.icon);
                weatherValues.put(Contract.WeatherEntry.COLUMN_SHORT_DESC, result.description);
                weatherValues.put(Contract.WeatherEntry.COLUMN_MAX_TEMP, result.high);
                weatherValues.put(Contract.WeatherEntry.COLUMN_MIN_TEMP, result.low);
                weatherValues.put(Contract.WeatherEntry.COLUMN_HUMIDITY, result.humidity);
                weatherValues.put(Contract.WeatherEntry.COLUMN_WIND_SPEED, result.windSpeedStr);
                weatherValues.put(Contract.WeatherEntry.COLUMN_DEGREES, result.windDirStr);
                weatherValues.put(Contract.WeatherEntry.COLUMN_PRESSURE, result.pressure);
                weatherValues.put(Contract.WeatherEntry.COLUMN_LOCATION, result.location);
                weatherValues.put(Contract.WeatherEntry.COLUMN_UNIT_TYPE,result.unitType);

                Cursor cursor = getActivity().getContentResolver().query(Contract.WeatherEntry.CONTENT_URI,
                        new String[]{Contract.WeatherEntry.COLUMN_CITY},
                        null,null,null);

                int count=cursor.getCount();
                Log.e("ffsssss",count+"");

                if(count==0){
                    Uri insertedUri = getContext().getContentResolver().insert(
                            Contract.WeatherEntry.CONTENT_URI,
                            weatherValues
                    );
                }else{
                    int rowsCount= getActivity().getContentResolver().update( Contract.WeatherEntry.CONTENT_URI,
                            weatherValues
                            ,null,null);
                }

                try {

                    empty_tv.setVisibility(View.INVISIBLE);
                    scrollView.setVisibility(View.VISIBLE);

                    mCityView.setText("" + result.city);
                    mRainView.setText("Precipitation: " + result.rain + " mm");
                    mFriendlyDateView.setText(result.friendlyDateText);
                    Picasso.with(getContext()).load("http://openweathermap.org/img/w/"+result.icon+".png").into(mIconView);
                    mDescriptionView.setText(result.description);
                    mHighTempView.setText("Max: " + result.high);
                    mLowTempView.setText("Min: " + result.low);
                    mHumidityView.setText("Humidity: " + result.humidity + "%");
                    mWindView.setText("Wind: " + getFormattedWind(result.windSpeedStr, result.windDirStr));
                    mPressureView.setText("Pressure: " + result.pressure);
                }
                catch (Exception e){}
            }
        }
    }
}
