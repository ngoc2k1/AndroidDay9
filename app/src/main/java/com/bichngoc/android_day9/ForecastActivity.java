package com.bichngoc.android_day9;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bichngoc.android_day9.adapters.ForecastAdapter;
import com.bichngoc.android_day9.asynctask.SortHourAsyncTask;
import com.bichngoc.android_day9.interfaces.IIClickItemRecycleView;
import com.bichngoc.android_day9.interfaces.ISortedHoursListener;
import com.bichngoc.android_day9.models.Day;
import com.bichngoc.android_day9.models.Forecast;
import com.bichngoc.android_day9.retrofit.IWeatherServices;
import com.bichngoc.android_day9.retrofit.RetrofitClient;
import com.bichngoc.android_day9.utils.Global;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ForecastActivity.class.getName();

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.rv5Days3Hours)
    RecyclerView rv5Days3Hours;

    @BindView(R.id.llloading)
    LinearLayout llloading;

    private IWeatherServices mWeatherServices;
    private String mCityName;
    private ArrayList<Day> mDays;
    private ForecastAdapter forecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        initData();
    }

    private void initData() {
        mCityName = getIntent().getStringExtra(Global.CITY_NAME_KEY);
        mWeatherServices = RetrofitClient.createServices(IWeatherServices.class);
        mWeatherServices.getForcastByCityName("Hà Nội", Global.APP_ID, "en").enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                if (response.code() == 200 && response.message().equals("OK")) {
                    Forecast forecast = response.body();
                    Log.d(TAG,"a response "+response.toString());
                    new SortHourAsyncTask(forecast.getList(), sortedHoursListener).execute();
                }
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                Log.d(TAG, "onResponse" + t.getLocalizedMessage());

            }
        });
    }

    private ISortedHoursListener sortedHoursListener = new ISortedHoursListener() {
        @Override
        public void onSortedHoursListener(ArrayList<Day> mdays) {
            mDays = new ArrayList<>();
            mDays.clear();
            mDays.addAll(mdays);
            initView();

        }
    };

    private void initView() {
        ButterKnife.bind(this);
        imgBack.setOnClickListener(this);
        if (mDays != null && mDays.size() > 0) {
            forecastAdapter = new ForecastAdapter(mDays);
            rv5Days3Hours.setAdapter(forecastAdapter);
            forecastAdapter.setCallback(callback);
            forecastAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(), "K co data", Toast.LENGTH_LONG).show();
        }
        llloading.setVisibility(View.GONE);
    }

    private IIClickItemRecycleView callback = new IIClickItemRecycleView() {
        @Override
        public void onItemClickListener(View v, int pos) {
            Day day = mDays.get(pos);
            day.setExpand(!day.isExpand());
            mDays.set(pos, day);
            forecastAdapter.notifyItemChanged(pos);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();//return Main, tat class hien tai
                break;
        }
    }
}