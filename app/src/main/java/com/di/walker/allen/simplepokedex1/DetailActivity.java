package com.di.walker.allen.simplepokedex1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.di.walker.allen.simplepokedex1.model.Move;
import com.di.walker.allen.simplepokedex1.model.Pokemon;
import com.di.walker.allen.simplepokedex1.model.Stat;
import com.di.walker.allen.simplepokedex1.model.Type;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity implements Callback<Pokemon> {
    private TextView pokeName;
    private ImageView pokeImg;
    private PokeapiInterface istance;
    private String pokeQuery;
    private LinearLayout pkm_det;
    private ProgressBar progBar;

    private TextView pkmWeight;
    private TextView pkmHp;
    private TextView pkmAttack;
    private TextView pkmDefense;
    private TextView pkmSpeed;
    private TextView pkmTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        pokeQuery = extras.getString("PokeNum");
        Log.d("DEB1", "onCreate: " + pokeQuery);
        bindViews();
        istance = buildPokeapiInstance();
        startSearch();

    }

    private void bindViews() {
        pokeName = (TextView) findViewById(R.id.pkmD_name);
        pokeImg = (ImageView) findViewById(R.id.pkmD_img);
        pkm_det = (LinearLayout) findViewById(R.id.pkmD_detail);
        progBar = (ProgressBar) findViewById(R.id.progBar_d);
        pkmWeight = (TextView) findViewById(R.id.pkmn_weight);
        pkmHp = (TextView) findViewById(R.id.pkmn_hp);
        pkmAttack = (TextView) findViewById(R.id.pkmn_attack);
        pkmDefense = (TextView) findViewById(R.id.pkmn_defense);
        pkmSpeed = (TextView) findViewById(R.id.pkmn_speed);
        pkmTypes = (TextView) findViewById(R.id.pkmn_types);
        Log.d("deb2", "bindViews: ");


    }

    private PokeapiInterface buildPokeapiInstance() {

        File httpCacheDirectory = new File(DetailActivity.this.getCacheDir(), "responses");
        int cacheSize = 100 * 1024 * 1024; // 100 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://pokeapi.co/api/v2/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(PokeapiInterface.class);
    }


    private void startSearch() {

        Log.d("deb2", "startSearch: ");
        if (pokeQuery.length() == 0) {
            Toast.makeText(this, "errore", Toast.LENGTH_SHORT).show();
            return;
        }
        progBar.setVisibility(View.VISIBLE);
        pkm_det.setVisibility(View.GONE);

        try {


            istance.searchForPokemon(pokeQuery).enqueue(this);
        } catch (RuntimeException e) {
            Toast.makeText(this, "chiamata network failed", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
        Log.d("deb2", "onResponse: ");
        Pokemon result = response.body();
        if (result == null) {
            Toast.makeText(this, "errore ?", Toast.LENGTH_SHORT).show();
            return;
        }
        int num = result.getId();
        pokeName.setText(result.getName() + " #" + num);
        pkmWeight.setText("Weight: " + result.getWeight());
        for (Stat s : result.getStats()) {
            switch (s.getStat().getName()) {
                case "hp":
                    pkmHp.setText("HP: " + s.getBaseStat());
                    break;
                case "attack":
                    pkmAttack.setText("Attack: " + s.getBaseStat());
                    break;
                case "defense":
                    pkmDefense.setText("Defense: " + s.getBaseStat());
                    break;

                case "speed":
                    pkmSpeed.setText("Speed: " + s.getBaseStat());
                    break;
            }
        }

        String types = "";

        String type="null";
        Type t1=null;
        for (Type t : result.getTypes()) {
            types = t.getType().getName()+ " " +types  ;
            t1=t;
        }
        type=t1.getType().getName();
        types="Types: "+types;
        pkmTypes.setText(types);
        Log.d("TYP", "onResponse: "+type);

        progBar.setVisibility(View.GONE);

        pkm_det.setVisibility(View.VISIBLE);

        Picasso.with(this).load(result.getSprites().getFrontDefault()).into(pokeImg);



    }

    @Override
    public void onFailure(Call<Pokemon> call, Throwable t) {
        Log.d("deb2", "onFailure: ");
        Toast.makeText(this, "chiamata network failed", Toast.LENGTH_SHORT).show();

    }


    private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {

            CacheControl.Builder cacheBuilder = new CacheControl.Builder();
            cacheBuilder.maxAge(0, TimeUnit.SECONDS);
            cacheBuilder.maxStale(365, TimeUnit.DAYS);
            CacheControl cacheControl = cacheBuilder.build();

            Request request = chain.request();
            if (isNetworkAvailable(DetailActivity.this)) {
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }
            okhttp3.Response originalResponse = chain.proceed(request);
            if (isNetworkAvailable(DetailActivity.this)) {
                int maxAge = 600 * 60; // read from cache
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 280; // tolerate 4-weeks stale
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


}
//TODO expand and layout