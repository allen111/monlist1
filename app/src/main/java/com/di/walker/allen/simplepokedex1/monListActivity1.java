package com.di.walker.allen.simplepokedex1;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.di.walker.allen.simplepokedex1.list.PokeList;
import com.di.walker.allen.simplepokedex1.list.Result;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

public class monListActivity1 extends AppCompatActivity implements Callback<PokeList>,PokeListAdapter.OnCardClikListner,  android.widget.SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {
    private RecyclerView recView;
    private ArrayList<Result> result;
    private ArrayList<Result> SearchResult;
    private ArrayList<String> list;
    private PokeListAdapter adapter;
    private ProgressBar progressBar;
    private boolean ready = false;
    private boolean searching = false;
    private android.widget.SearchView searchView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_list1);
        Log.d("deb", "onCreate: before bind");

        bindViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);


        searchView = (android.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search11));
        MenuItem searchMenuItem = menu.findItem(R.id.search11);
        searchView.setOnQueryTextListener(this);




        MenuItemCompat.setOnActionExpandListener(searchMenuItem,this);




        // Assumes current activity is the searchable activity
        Log.d("SERD", "onCreateOptionsMenu:middle ");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        Log.d("SERD", "onCreateOptionsMenu: aftr");
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);


        return true;
    }

    private void bindViews() {
        recView = (RecyclerView) findViewById(R.id.recView);
        progressBar = (ProgressBar) findViewById(R.id.progressList);
        recView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recView.setLayoutManager(layoutManager);


        Log.d("deb", "onCreate: before load");
        loadJson();
    }

    private void loadJson() {
        File httpCacheDirectory = new File(monListActivity1.this.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://pokeapi.co/api/v2/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PokeListInterface pokelist = retrofit.create(PokeListInterface.class);

        Call<PokeList> call = pokelist.GetListPokemon();
        Log.d("deb", "onCreate: before enquee");

        call.enqueue(this);
        Log.d("deb", "onCreate: after enquee");

    }

    @Override
    public void onResponse(Call<PokeList> call, Response<PokeList> response) {
        Log.d("deb3", "onCreate: onRes" + response.code());
        if (response.code() > 299) {
            Toast.makeText(this, "codice errore" + response.code(), Toast.LENGTH_SHORT).show();
            return;
        }

        PokeList jsonResponse = response.body();
        result = jsonResponse.getResults();

        adapter = new PokeListAdapter(result);
        recView.setAdapter(adapter);

        adapter.setOnCardClickListner(this);

        progressBar.setVisibility(View.GONE);
        recView.setVisibility(View.VISIBLE);
        ready = true;

    }

    @Override
    public void onFailure(Call<PokeList> call, Throwable t) {
        if (t.getMessage() == null) {
            Log.d("deb", "onFailure: ");
        } else {
            Log.d("deb", t.getMessage());
        }

        Toast.makeText(this, "chiamata network failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnCardClicked(View view, int position) {
        Log.d("deb4", "OnCardClicked: " + position);
        //String num=
        Intent i = new Intent(this, DetailActivity.class);
        int pos = position + 1;
        i.putExtra("PokeNum", "" + pos);
        if(searching){
            String url=SearchResult.get(position).getUrl();
            String[] nums= url.split("/");
            String num= nums[nums.length-1];
            Log.d("deb4", "OnCardClicked: "+num);
            i.putExtra("PokeNum", "" + num);
            startActivity(i);
            return;
        }
        startActivity(i);
    }


    private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {

            CacheControl.Builder cacheBuilder = new CacheControl.Builder();
            cacheBuilder.maxAge(0, TimeUnit.SECONDS);
            cacheBuilder.maxStale(365, TimeUnit.DAYS);
            CacheControl cacheControl = cacheBuilder.build();

            Request request = chain.request();
            if (isNetworkAvailable(monListActivity1.this)) {
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }
            okhttp3.Response originalResponse = chain.proceed(request);
            if (isNetworkAvailable(monListActivity1.this)) {
                int maxAge = 60 * 60; // read from cache
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
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





    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("sr2", "onQueryTextSubmit: " + query);

        MyParams mp=new MyParams(result,query,new ArrayList<Result>());
        new SearchTask().execute(mp);
        SearchResult=mp.rets;
        adapter = new PokeListAdapter(SearchResult);
        recView.setAdapter(adapter);
        adapter.setOnCardClickListner(this);
        searching=true;

        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        Log.d("sr2", "onQueryTextSubmit: " + query);

        MyParams mp=new MyParams(result,query,new ArrayList<Result>());
        new SearchTask().execute(mp);
        SearchResult=mp.rets;
        adapter = new PokeListAdapter(SearchResult);
        recView.setAdapter(adapter);
        adapter.setOnCardClickListner(this);
        searching=true;

        return true;


    }



    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        //Do whatever you want
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        adapter = new PokeListAdapter(result);
        recView.setAdapter(adapter);
        adapter.setOnCardClickListner(this);
        searching=false;
        return true;
    }
}
//TODO immagini sulla lista? tweak cache

//TODO clear searchVIew trash, minor check perche non compaiono quando tastiera up serached
