package com.bichngoc.android_day9.asynctask;

import android.os.AsyncTask;

import com.bichngoc.android_day9.interfaces.ISortedHoursListener;
import com.bichngoc.android_day9.models.DataHours;
import com.bichngoc.android_day9.models.Day;

import java.util.ArrayList;
import java.util.List;

public class SortHourAsyncTask extends AsyncTask<Void, Void, Void> {
    private List<DataHours> mData;
    private ArrayList<Day> mOutput;
    private static String currentDate = "";
    private ISortedHoursListener callback;

    public SortHourAsyncTask(List<DataHours> mData, ISortedHoursListener callback) {
        this.mData = mData;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mOutput = new ArrayList<>();
        mOutput.clear();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (int i = 0; i < mData.size(); i++) {
            String dt_txt = mData.get(i).getDtTxt();//de lay ngay va gio
            String date = dt_txt.split(" ")[0];
            String time = dt_txt.split(" ")[1];
            if (mOutput.size() <= 0) {
                Day firstDay = new Day();
                firstDay.setDate(date);
                ArrayList<DataHours> dayHours = new ArrayList<>();
                dayHours.add(mData.get(i));
                firstDay.setDataHours(dayHours);
                currentDate = date;
                firstDay.setExpand(false);
                mOutput.add(firstDay);
            } else {
                if (currentDate.equals(date)) {
                    Day currentDay = mOutput.get(mOutput.size() - 1);
                    ArrayList<DataHours> currentHours = currentDay.getDataHours();
                    currentHours.add(mData.get(i));
                } else {
                    Day newDay = new Day();
                    newDay.setDate(date);
                    ArrayList<DataHours> dayHours = new ArrayList<>();
                    dayHours.add(mData.get(i));
                    newDay.setDataHours(dayHours);
                    currentDate = date;
                    newDay.setExpand(false);
                    mOutput.add(newDay);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        if (callback != null) {
            callback.onSortedHoursListener(mOutput);
        }
    }
}
