package com.bichngoc.android_day9.retrofit;

import com.bichngoc.android_day9.utils.Global;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(Global.BASE_URL)//lời gọi gốc API
            .addConverterFactory(GsonConverterFactory.create());//convert toàn bộ dl json sang các Object java

    private static Retrofit retrofit;
    //static: sd để t.h lời gọi API nhiều nơi toàn bộ app. Hủy khi nào class hủy

    public static Retrofit getInstances() {
        if (retrofit == null) {//chưa được k.tạo lần nào
            retrofit = builder.build();//hàm tạo-dòng 9
        }
        return retrofit;
        //tối ưu: singleton pattern: 1 O sd nhiều lần (ở nhiều acti/class#) nhưng k.tạo 1 lần
    }

    //class bên ngoài chủ yêu tương tác qua đây. instances -> h tạo service
    public static <T> T createServices(Class<T> servicesClass) { //class linh động: truyen dl gì thi tra ve dl tuong ung
        return getInstances().create(servicesClass);//tao 2 O khac nhau
    }

//    public IA createServices(IA ia) {
//        return getInstances().create(ia.getClass());
//    }
}

