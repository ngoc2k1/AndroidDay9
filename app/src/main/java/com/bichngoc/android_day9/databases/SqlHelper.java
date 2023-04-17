package com.bichngoc.android_day9.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

//khi pj run, sql được load lại, load list tp ra (hàm getListCity),
// gán vào mảng mListName ở MainActivity để truyền vào CityAdapter sinh ra 1 list fragment, mỗi fragment truyền tên 1 tp vào

public class SqlHelper extends SQLiteOpenHelper {
    private SQLiteDatabase sqLiteDatabase;
    private static final String DATABASE_NAME = "Cities.db";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "City";
    private static final String COLUMN_NAME = "CityName";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_NAME + " TEXT NOT NULL );";

    public SqlHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addNewCity(String newCityName) {//mới
        if (!checkCityIsExisted(newCityName)) {
            sqLiteDatabase = getWritableDatabase();//quyền ghi
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME, newCityName);
            sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
            sqLiteDatabase.close();
        } else {
            Log.d("SqlHelper", "addNewCity: City existed");
        }
    }

    private boolean checkCityIsExisted(String cityName) {
        boolean result = false;
        ArrayList<String> listCityName = getListCityName();
        for (int i = 0; i < listCityName.size(); i++) {
            if (listCityName.get(i).equals(cityName)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getListCityName() {
        ArrayList<String> listCity = new ArrayList<>();
        sqLiteDatabase = getReadableDatabase();//quyền đọc
        String sql = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);//biến trỏ con chạy, duyệt lần lượt dl trả về
        cursor.moveToFirst();//set con trỏ chạy từ trên xuống
        while (cursor.isAfterLast() == false) {//khi k phai cc
            int indexColumn = cursor.getColumnIndex(COLUMN_NAME);
            String cityName = cursor.getString(indexColumn);
            listCity.add(cityName);
            cursor.moveToNext();
        }
        return listCity;
    }

    public void removeCityName(String cityName) {
        sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, COLUMN_NAME + " LIKE \'" + cityName + "\'", null);
        sqLiteDatabase.close();
    }
}
