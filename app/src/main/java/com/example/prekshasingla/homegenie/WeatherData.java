package com.example.prekshasingla.homegenie;

/**
 * Created by prekshasingla on 14/11/16.
 */
public class WeatherData {

    int weatherId ;
    long date ;
    String city;
    String friendlyDateText ;
    double rain;
    String icon;
    String description ;
    double high;
    double low;
    float humidity ;
    double windSpeedStr ;
    double windDirStr;
    double pressure ;
    String location;
    String unitType;

    public WeatherData(int weatherId,String city,long date,String friendlyDateText,double rain,String icon,
            String description ,double high, double low, float humidity ,double windSpeedStr,
            double windDirStr,double pressure,String location,String unitType){
        this.weatherId=weatherId;
        this.date=date;
        this.city=city;
        this.friendlyDateText=friendlyDateText;
        this.rain=rain;
        this.icon=icon;
        this.description=description;
        this.high=high;
        this.low=low;
        this.humidity=humidity;
        this.windSpeedStr=windSpeedStr;
        this.windDirStr=windDirStr;
        this.pressure=pressure;
        this.location=location;
        this.unitType=unitType;
    }
}
