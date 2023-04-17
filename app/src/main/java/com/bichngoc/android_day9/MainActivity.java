package com.bichngoc.android_day9;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.bichngoc.android_day9.adapters.CityViewPagerAdapter;
import com.bichngoc.android_day9.databases.Database;
import com.bichngoc.android_day9.databases.SqlHelper;
import com.bichngoc.android_day9.fragments.CityFragment;
import com.bichngoc.android_day9.fragments.LoginFragment;
import com.bichngoc.android_day9.models.City;
import com.bichngoc.android_day9.models.CityWeather;
import com.bichngoc.android_day9.retrofit.IWeatherServices;
import com.bichngoc.android_day9.retrofit.RetrofitClient;
import com.bichngoc.android_day9.utils.Global;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getName();
    private IWeatherServices mWeatherServices;

    @BindView(R.id.imgAdd)
    ImageView imgAdd;
    @BindView(R.id.imgLocation)
    ImageView imgLocation;
    @BindView(R.id.ciVpCityName)
    CircleIndicator ciVpCityName;
    @BindView(R.id.vpCityName)
    ViewPager vpCityName;
    @BindView(R.id.actvSearch)
    AutoCompleteTextView actvSearch;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.imgUser)
    ImageView imgUser;
    private LoginFragment loginFragment;
    private GoogleSignInOptions options;
    private GoogleSignInClient googleSignInClient;
    private ArrayList<String> mListCityName;
    private ArrayList<City> mListCity;
    private CityViewPagerAdapter mCityViewPagerAdapter;
    private SqlHelper mSqlHelper;
    private ArrayList<String> mListNameAssets;
    private static final int CLICKED_CITY = 1234;
    private static final int REQUEST_CODE_PERMISSION = 123;
    private final int LOGIN_GOOGLE = 1001;

    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGoogle();
//        throw new RuntimeException("Test Crash."); // Force a crash
//        try {
//            throw new Exception("Test Crash."); // Force a crash
//        } catch (Exception e) {
//            e.printStackTrace();

//        }
    }

    private void initGoogle() {
        options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, options);//thực hiện lời gọi rồi hiển thị trong form login
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        initView();
        GoogleSignInAccount lastGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (lastGoogleSignInAccount != null) {
            //login before
            Toast.makeText(getApplicationContext(), "Welcome " + lastGoogleSignInAccount.getEmail(), Toast.LENGTH_SHORT);
            imgUser.setVisibility(View.GONE);
            imgLocation.setVisibility(View.VISIBLE);
        } else {

        }
    }

    private void initData() {
        mSqlHelper = new SqlHelper(getApplicationContext());
        mListCityName = mSqlHelper.getListCityName();
        if (mListCityName.size() <= 0) {
            mListCityName.add("Ha Noi");
            mSqlHelper.addNewCity("Ha Noi");
        }
        Log.d(TAG, "init Data " + mSqlHelper.getListCityName().toString());
        Database database = new Database(getApplicationContext());
        mListCity = database.getListCityFromAssets();
        mListNameAssets = new ArrayList<>();
        for (int i = 0; i < mListCity.size(); i++) {
            mListNameAssets.add(mListCity.get(i).getName());
        }
    }

    private void initView() {
        ButterKnife.bind(this);
        imgSearch.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
        imgLocation.setOnClickListener(this);
        imgUser.setOnClickListener(this);

        updateData(mListCityName);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(),
                R.layout.support_simple_spinner_dropdown_item, mListNameAssets);
        actvSearch.setThreshold(0);
        actvSearch.setAdapter(arrayAdapter);
        actvSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String cityName = actvSearch.getText().toString();
                    return true;
                }
                return false;
            }
        });
        actvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickedCity = adapterView.getItemAtPosition(i).toString();
                mListCityName.add(clickedCity);
                mSqlHelper.addNewCity(clickedCity);
                updateData(mListCityName);
                scrollToNewest();
            }
        });
    }

    private void updateData(ArrayList<String> data) {//hien thi list fragment cac thanh pho o man hinh chinh
        mCityViewPagerAdapter = new CityViewPagerAdapter(getSupportFragmentManager(), data);
        vpCityName.setAdapter(mCityViewPagerAdapter);
        //vpCityName.setPageTransformer(false, new DepthPageTransformer());
        mCityViewPagerAdapter.notifyDataSetChanged();
        ciVpCityName.setViewPager(vpCityName);
        if (currentPosition > 0) {
            vpCityName.setCurrentItem(currentPosition, true);
        }
    }

    public void scrollToNewest() {
        vpCityName.setCurrentItem(mListCityName.size() - 1, true);
    }

    public void scrollToPosition(int pos) {
        vpCityName.setCurrentItem(pos, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgUser:
                showLoginForm();
                break;
            case R.id.imgLocation:
                getLocation();
                break;
            case R.id.imgAdd:
                gotoHistoryActivity();
                break;
        }
    }

    private void showLoginForm() {
        loginFragment = LoginFragment.newInstance();
        loginFragment.setGoogleSignInClient(googleSignInClient);
        getSupportFragmentManager().beginTransaction().add(R.id.container2, loginFragment).commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: granted");
            getCurrentLocation();
        } else {
            requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_PERMISSION);
        }
        //        ActivityResultLauncher<String[]> locationPermissionRequest =
//                registerForActivityResult(new ActivityResultContracts
//                                .RequestMultiplePermissions(), result -> {
//                            Boolean fineLocationGranted = result.getOrDefault(
//                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
//                            Boolean coarseLocationGranted = result.getOrDefault(
//                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
//                            if (fineLocationGranted != null && fineLocationGranted) {
//                                // Precise location access granted.
//                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
//                                // Only approximate location access granted.
//                            } else {
//                                // No location access granted.
//                                requestPermissions();
//                            }
//                        }
//                );
//        locationPermissionRequest.launch(new String[]{
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Please grant location permission", Toast.LENGTH_LONG).show();
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {
                Log.d(TAG, "onSuccess: " + location.getLongitude() + " | " + location.getLatitude());
                addCityByLocation(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void addCityByLocation(double lat, double lon) {
        //thực hiện được lời gọi API
        //truyền class muốn tạo  -> khi có services thì t.h lời gọi để getData ra
        mWeatherServices = RetrofitClient.createServices(IWeatherServices.class);
        mWeatherServices.getWeatherByLocation(lat + "", lon + "", Global.APP_ID, "en").enqueue(new Callback<CityWeather>() {
            //        Call<CityWeather> cityWeatherCall=mWeatherServices.getWeatherByLocation(lat + "", lon + "", Global.APP_ID, "en");
//        cityWeatherCall.enqueue(new Callback<CityWeather>() {
            @Override//khi lời gọi API đúng
            public void onResponse(Call<CityWeather> call, Response<CityWeather> response) {
                if (response.code() == 200) {//khi thực hiện lời gọi lên server thì trả về .code và 200 success
                    CityWeather cityWeather = response.body();
                    String city = cityWeather.getName();
                    mSqlHelper.addNewCity(city);
                    mListCityName.clear();
                    mListCityName.addAll(mSqlHelper.getListCityName());
                    updateData(mListCityName);
                }
            }

            @Override
            public void onFailure(Call<CityWeather> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });
    }

    private void gotoHistoryActivity() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivityForResult(intent, CLICKED_CITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CLICKED_CITY) {
            if (resultCode == RESULT_OK) {
                int pos = data.getIntExtra("POS", 0);
                currentPosition = pos;
                vpCityName.setCurrentItem(pos, true);
            }
            if (requestCode == RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult: RESULT_CANCELED");
            }
        }
        if (REQUEST_CODE_PERMISSION == resultCode) {
            getCurrentLocation();
        }
        if (requestCode == LOGIN_GOOGLE) {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            getInfor(accountTask);
        }
    }

    private void getInfor(Task<GoogleSignInAccount> accountTask) {
        try {
            GoogleSignInAccount googleSignInAccount = accountTask.getResult(ApiException.class);
            Log.d(TAG, "getInfo " + googleSignInAccount.getEmail());
            Toast.makeText(getApplicationContext(), "Welcome " + googleSignInAccount.getEmail(), Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().remove(loginFragment).commit();
            getSupportFragmentManager().beginTransaction().add(loginFragment).commit();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
