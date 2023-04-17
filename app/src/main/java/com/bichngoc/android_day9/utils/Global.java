package com.bichngoc.android_day9.utils;

public class Global {
    public static final String APP_ID = "c1123562b36dd75c08fb253d0411383b";
    public static final String CURRENT_WEATHER_BY_CITY_NAME = "weather?";
    public static final String CURRENT_FORECAST_BY_CITY_NAME = "forecast?";
    public static final String CITY_NAME_KEY = "CITY_NAME";
    public static final String DEGREE_CHARACTER = "\u2103"; //độ C
    public static final String HPA_UNIT = " hPa";
    public static final String PERCENT_UNIT = " %";
    public static final String URL_ICON = "http://openweathermap.org/img/wn/";
    public static final String PICTURE_FORMAT = ".png";

    public static int convertKelvinToCelsius(float kelvin) {
        return (int) Math.round(kelvin - 273.15);
    }

    public static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
}
