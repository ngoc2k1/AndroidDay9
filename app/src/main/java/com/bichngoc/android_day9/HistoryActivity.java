package com.bichngoc.android_day9;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bichngoc.android_day9.adapters.HistoryAdapter;
import com.bichngoc.android_day9.databases.SqlHelper;
import com.bichngoc.android_day9.interfaces.IIClickItemRecycleView;
import com.bichngoc.android_day9.models.CityWeather;
import com.bichngoc.android_day9.retrofit.IWeatherServices;
import com.bichngoc.android_day9.retrofit.RetrofitClient;
import com.bichngoc.android_day9.utils.Global;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//noi luu tru lich su tim kiem
//load dl o day thay vi HistoryAdapter
public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.llloading)
    LinearLayout llloading;
    @BindView(R.id.rvHistory)
    RecyclerView rvHistory;
    @BindView(R.id.imgSearchHistory)
    ImageView imgSearchHistory;
    private HistoryAdapter mHistoryAdapter;
    private IWeatherServices iWeatherServices;
    private ArrayList<CityWeather> mListCityWeather;//put list cho adapter, adapter chi can doc dl thoi
    private ArrayList<String> mListCityName;//nhan sql tra ve
    private SqlHelper mSqlHelper;
    private static final int CLICKED_CITY = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        mListCityWeather = new ArrayList<>();
        mListCityWeather.clear();
        mListCityName = new ArrayList<>();
        mListCityName.clear();
        mListCityName.addAll(new SqlHelper(getApplicationContext()).getListCityName());
        iWeatherServices = RetrofitClient.createServices(IWeatherServices.class);
        mSqlHelper = new SqlHelper(getApplicationContext());
        if (mListCityName.size() > 0) {
            loadData(mListCityName);
        } else {

        }
    }

    private IIClickItemRecycleView clickItemRecycleView = new IIClickItemRecycleView() {
        @Override
        public void onItemClickListener(View v, int pos) {
            backToMain(pos);
        }
    };

    private void backToMain(int pos) {
        Intent intent = new Intent();
        intent.putExtra("POS", pos);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initView() {
        ButterKnife.bind(this);
        imgBack.setOnClickListener(this);
        imgSearchHistory.setOnClickListener(this);
        ItemTouchHelper.Callback itemCallBack = new DeletionSwipeHelper(0, 0, getApplicationContext(), swipeListener);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemCallBack);
        itemTouchHelper.attachToRecyclerView(rvHistory);
    }

    private DeletionSwipeHelper.OnSwipeListener swipeListener = (viewHolder, position) -> {
        String cityName = mListCityName.get(position);
        mSqlHelper.removeCityName(cityName);
        mListCityWeather.remove(position);
        mHistoryAdapter.notifyDataSetChanged();
    };

    private void loadData(ArrayList<String> mListCityName) {
        llloading.setVisibility(View.VISIBLE);
        for (int i = 0; i < mListCityName.size(); i++) {
            String cityName = mListCityName.get(i);
            iWeatherServices.getWeatherByCityName(cityName, Global.APP_ID, "en").enqueue(cityWeatherCallback(i));
        }
    }

    private Callback<CityWeather> cityWeatherCallback(int position) {
        Callback<CityWeather> cityWeatherCallback = new Callback<CityWeather>() {
            @Override
            public void onResponse(Call<CityWeather> call, Response<CityWeather> response) {

                if (response.code() == 200) {
                    CityWeather cityWeather = response.body();
                    mListCityWeather.add(cityWeather);
                    if (position == mListCityName.size() - 1) {
                        //ptu cuoi cung thi ms pin vao adapter de adapter de chay
                        mHistoryAdapter = new HistoryAdapter(mListCityWeather);
                        mHistoryAdapter.setCallBack(clickItemRecycleView);

                        rvHistory.setAdapter(mHistoryAdapter);
                        llloading.setVisibility(View.GONE);
                        mHistoryAdapter.notifyDataSetChanged();
                    }
                }
//            bindCityWeather(response.body());
            }

            @Override
            public void onFailure(Call<CityWeather> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "fall when load city", Toast.LENGTH_LONG).show();
            }
        };
        return cityWeatherCallback;
    }

//    private Callback<CityWeather> cityWeatherCallback = new Callback<CityWeather>() {
//        @Override
//        public void onResponse(Call<CityWeather> call, Response<CityWeather> response) {
//
//            if (response.code() == 200) {
//                CityWeather cityWeather = response.body();
//                mListCityWeather.add(cityWeather);
//
////                Log.d(TAG, "onResponse: " + response.toString());
//            }
////            bindCityWeather(response.body());
//        }
//
//        @Override
//        public void onFailure(Call<CityWeather> call, Throwable t) {
//            Toast.makeText(getApplicationContext(), "fall when load city", Toast.LENGTH_LONG).show();
//        }
//    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgSearchHistory:
            case R.id.imgBack:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}