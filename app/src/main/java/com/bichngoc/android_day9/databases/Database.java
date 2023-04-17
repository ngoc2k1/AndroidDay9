package com.bichngoc.android_day9.databases;

import android.content.Context;

import com.bichngoc.android_day9.models.City;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Database {//load các dl từ file json lên
    private Context mContext;
    private ArrayList<City> mListCity;

    public Database(Context mContext) {
        this.mContext = mContext;
    }

    public ArrayList<City> getListCityFromAssets() {
        mListCity = new ArrayList<>();
        try {
            InputStream inputStream = mContext.getAssets().open("city.list.min.json");
            int count = inputStream.available();//bn phần tử (character)
            //ktao mảng luu tru gtri
            byte[] cityByte = new byte[count];
            inputStream.read(cityByte);//citybyte đã có gtri
            inputStream.close();//city bute có dl rồi thì giai phòng thôi
            String cityJson = new String(cityByte, "UTF-8");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<City>>() {
            }.getType();
            mListCity = gson.fromJson(cityJson, type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mListCity;
    }
}
