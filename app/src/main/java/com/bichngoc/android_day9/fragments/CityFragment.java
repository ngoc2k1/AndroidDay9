package com.bichngoc.android_day9.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bichngoc.android_day9.ForecastActivity;
import com.bichngoc.android_day9.MainActivity;
import com.bichngoc.android_day9.R;
import com.bichngoc.android_day9.models.CityWeather;
import com.bichngoc.android_day9.models.Forecast;
import com.bichngoc.android_day9.retrofit.IWeatherServices;
import com.bichngoc.android_day9.retrofit.RetrofitClient;
import com.bichngoc.android_day9.utils.Global;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CityFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = CityFragment.class.getName();
    private Context mContext;

    @BindView(R.id.tvCityName)
    TextView tvCityName;
    @BindView(R.id.tvTemp)
    TextView tvTemp;
    @BindView(R.id.imgWeather)
    ImageView imgWeather;
    @BindView(R.id.tvDescription)
    TextView tvDescription;
    @BindView(R.id.tvDay)
    TextView tvDay;
    @BindView(R.id.tvSpeedValue)
    TextView tvSpeedValue;
    @BindView(R.id.tvFeelingValue)
    TextView tvFeelingValue;
    @BindView(R.id.tvHumidityValue)
    TextView tvHumidityValue;
    @BindView(R.id.tvPressureValue)
    TextView tvPressureValue;
    @BindView(R.id.tvVisibilityValue)
    TextView tvVisibilityValue;
    @BindView(R.id.tvTempMaxValue)
    TextView tvTempMaxValue;
    @BindView(R.id.tvTempMinValue)
    TextView tvTempMinValue;
    @BindView(R.id.tvSeaLevelValue)
    TextView tvSeaLevelValue;
    @BindView(R.id.tvCountry)
    TextView tvCountry;
    @BindView(R.id.tvSunSets)
    TextView tvSunSets;
    @BindView(R.id.tvSunRises)
    TextView tvSunRises;
    @BindView(R.id.btnFiveDaysThreeHours)
    Button btnFiveDaysThreeHours;

    public CityFragment() {
        // Required empty public constructor
    }

    private String mCityName = null;
    private IWeatherServices iWeatherServices;

    public static CityFragment newInstance(String cityName) {
        Bundle args = new Bundle();
        CityFragment fragment = new CityFragment();
        args.putString(Global.CITY_NAME_KEY, cityName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getString(Global.CITY_NAME_KEY) != null) {
            this.mCityName = getArguments().getString(Global.CITY_NAME_KEY);
        } else {
            this.mCityName = "Hà Nội";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city, container, false);
        this.mContext = getActivity();
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        ButterKnife.bind(this, view);
        btnFiveDaysThreeHours.setOnClickListener(this);
        tvCityName.setOnClickListener(this);
    }

    private void initData() {
        iWeatherServices = RetrofitClient.createServices(IWeatherServices.class);
        iWeatherServices.getWeatherByCityName(this.mCityName, Global.APP_ID, "en").enqueue(new Callback<CityWeather>() {
            @Override
            public void onResponse(Call<CityWeather> call, Response<CityWeather> response) {
                if (response.code() == 200) {
                    Log.d(TAG, "onResponse: " + response.toString());
                    bindCityWeather(response.body());
                }
            }

            @Override
            public void onFailure(Call<CityWeather> call, Throwable t) {
            }
        });
    }

    private void bindCityWeather(CityWeather body) {
        if (body != null) {
            tvCityName.setText(body.getName());

            long epoch = body.getDt();
            Date date = new Date(epoch * 1000);
            SimpleDateFormat a0 = new SimpleDateFormat("EEE | dd MMM yyyy");
            String b0 = a0.format(date);
            tvDay.setText(b0);

            String iconUrl = Global.URL_ICON + body.getWeather().get(0).getIcon() + Global.PICTURE_FORMAT;
            Glide.with(mContext).load(iconUrl).into(imgWeather);


            int celsius = Global.convertKelvinToCelsius(body.getMain().getTemp());
            tvTemp.setText(celsius + Global.DEGREE_CHARACTER);

            String description = body.getWeather().get(0).getDescription();
            tvDescription.setText(description);

            String windSpeed = body.getWind().getSpeed() + " km/h";
            tvSpeedValue.setText(windSpeed);

            tvFeelingValue.setText(Global.convertKelvinToCelsius(body.getMain().getFeelsLike()) + Global.DEGREE_CHARACTER);
            tvHumidityValue.setText(body.getMain().getHumidity() + Global.PERCENT_UNIT);
            tvPressureValue.setText(body.getMain().getPressure() + Global.HPA_UNIT);
            tvSeaLevelValue.setText(body.getMain().getSeaLevel() + Global.HPA_UNIT);
            tvVisibilityValue.setText(body.getVisibility() + " m");
            tvTempMaxValue.setText(Global.convertKelvinToCelsius(body.getMain().getTempMax()) + Global.DEGREE_CHARACTER);
            tvTempMinValue.setText(Global.convertKelvinToCelsius(body.getMain().getTempMin()) + Global.DEGREE_CHARACTER);
            tvCountry.setText("Country: " + body.getSys().getCountry());

            Date timeSunRises = new Date(












                    body.getSys().getSunrise() * 1000);

            SimpleDateFormat a = new SimpleDateFormat("HH:mm:ss");
            String b = a.format(timeSunRises);
            tvSunRises.setText("Sun rises: " + b + " am");

            Date timeSunSets = new Date(body.getSys().getSunset() * 1000);
            SimpleDateFormat a1 = new SimpleDateFormat("HH:mm:ss");
            String b1 = a1.format(timeSunSets);
            tvSunSets.setText("Sun sets: " + b1 + " pm");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFiveDaysThreeHours:
                gotoFiveDays();
                break;
            case R.id.tvCityName:
                animation();
                break;
        }
    }

    private void animation() {
        //animation
        Animation rotation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rotation.setRepeatCount(5);
        tvCityName.startAnimation(rotation);
    }

    private void gotoFiveDays() {
        Intent intent = new Intent(getActivity(), ForecastActivity.class);
        intent.putExtra(Global.CITY_NAME_KEY, mCityName);
        getActivity().startActivity(intent);
    }
}