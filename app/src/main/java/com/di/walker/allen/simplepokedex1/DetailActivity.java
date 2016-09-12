package com.di.walker.allen.simplepokedex1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.di.walker.allen.simplepokedex1.model.Pokemon;
import com.di.walker.allen.simplepokedex1.model.Stat;
import com.di.walker.allen.simplepokedex1.model.Type;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
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
    private Boolean squadMode;
    private SharedPreferences sharedPreferences;
    private ArrayList<SquadItem> squadItems;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        squadMode = extras.getBoolean("squad");

        pokeQuery = extras.getString("PokeNum");
        Log.d("DMO", "onCreate:normal mode " + pokeQuery);
        bindViews();


        istance = buildPokeapiInstance();
        startSearch(Integer.parseInt(pokeQuery));
        if (squadMode) {
            position = extras.getInt("pos");
            sharedPreferences = getSharedPreferences("PokeSquad", Context.MODE_PRIVATE);
            bindList();
        }

    }

    private void bindList() {
        squadItems = new ArrayList<SquadItem>();
        Map<String, ?> map = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            squadItems.add(new SquadItem(entry.getKey(), (Integer) entry.getValue()));
        }

        Collections.sort(squadItems);
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
        //inizializzo il client per la cache
        File httpCacheDirectory = new File(DetailActivity.this.getCacheDir(), "responses");
        int cacheSize = 100 * 1024 * 1024; // 100 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache).build();
        //chiamata retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://pokeapi.co/api/v2/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(PokeapiInterface.class);
    }


    private void startSearch(int pokequery) {
//metodo per il recupero dei dati del pokemon
        Log.d("deb2", "startSearch: ");
        if (pokequery <= 0) {
            Toast.makeText(this, "errore", Toast.LENGTH_SHORT).show();
            return;
        }
        progBar.setVisibility(View.VISIBLE);
        pkm_det.setVisibility(View.GONE);
        pokeQuery = "" + pokequery;

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
        // visualizzo le statistiche
        pokeName.setText(String.format("%s #%d", result.getName(), num));
        setTitle(result.getName());
        pkmWeight.setText(String.format("Weight: %d", result.getWeight()));
        for (Stat s : result.getStats()) {
            switch (s.getStat().getName()) {
                case "hp":
                    pkmHp.setText(String.format("HP: %d", s.getBaseStat()));
                    break;
                case "attack":
                    pkmAttack.setText(String.format("Attack: %d", s.getBaseStat()));
                    break;
                case "defense":
                    pkmDefense.setText(String.format("Defense: %d", s.getBaseStat()));
                    break;

                case "speed":
                    pkmSpeed.setText(String.format("Speed: %d", s.getBaseStat()));
                    break;
            }
        }

        String types = "";


        for (Type t : result.getTypes()) {
            types = t.getType().getName() + " " + types;

        }

        types = "Types: " + types;
        pkmTypes.setText(types);


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
            //inizializzo i dati per il client cache

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

    float x1, x2;
    final int MIN_DISTANCE = 150;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();

                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();

                float deltaX = x2 - x1;
                deltaX = Math.abs(deltaX);

                if (deltaX > MIN_DISTANCE) {

                    if (x1 < x2) {

                        //sx to dx indietro
                        if (squadMode) {
                            if (position > 0) {
                                position--;
                                int s_newQuery = squadItems.get(position).getNum();
                                startSearch(s_newQuery);
                            }

                        } else {
                            int newQuery = Integer.parseInt(pokeQuery);

                            if (newQuery > 1) {
                                newQuery--;
                                startSearch(newQuery);
                            } else {
                                Log.d("TEV1", "onTouchEvent:too low ");
                            }
                        }
                    } else {

                        //dx to sx avanti

                        if (squadMode) {
                            if (position < squadItems.size() - 1) {
                                position++;
                                int s_newQuery = squadItems.get(position).getNum();
                                startSearch(s_newQuery);
                            }
                        } else {


                            int newQuery = Integer.parseInt(pokeQuery);

                            if (newQuery < R.string.pokemonCount) {
                                newQuery++;
                                startSearch(newQuery);

                            } else {
                                Log.d("TEV1", "onTouchEvent:too high ");
                            }
                        }
                    }
                }

                break;

            default:
                break;

        }
        return super.onTouchEvent(event);
    }
}
//TODO expand and layout