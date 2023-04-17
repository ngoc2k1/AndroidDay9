package com.bichngoc.android_day9.retrofit;

import com.bichngoc.android_day9.models.CityWeather;
import com.bichngoc.android_day9.models.Forecast;
import com.bichngoc.android_day9.utils.Global;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

//NƠI THỰC HIỆN CÁC LỜI GỌI
public interface IWeatherServices {//đoạn đầu là BASE_URL, h thực hiện link đầy đủ

    //https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}
    @GET(Global.CURRENT_WEATHER_BY_CITY_NAME)
    Call<CityWeather> getWeatherByCityName(@Query("q") String cityname,
                                           @Query("appid") String appid,
                                           @Query("lang") String language);
    //trả về đối tượng CityWeather. Để hoàn thiện link truyền 3 annotation
    //@Query gán các data
    @GET(Global.CURRENT_WEATHER_BY_CITY_NAME)
    Call<CityWeather> getWeatherByLocation(@Query("lat") String lat,
                                           @Query("lon") String lon,
                                           @Query("appid") String appid,
                                           @Query("lang") String language);

    @GET(Global.CURRENT_FORECAST_BY_CITY_NAME)
    Call<Forecast> getForcastByCityName(@Query("q") String cityname,
                                        @Query("appid") String appid,
                                        @Query("lang") String language);
}

