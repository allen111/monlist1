package com.di.walker.allen.simplepokedex1;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.di.walker.allen.simplepokedex1.list.PokeList;
import com.di.walker.allen.simplepokedex1.list.Result;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

public class monListActivity1 extends AppCompatActivity implements Callback<PokeList>, PokeListAdapter.OnCardClikListner, android.widget.SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener, View.OnClickListener {
    private RecyclerView recView;
    private ArrayList<Result> result;
    private ArrayList<Result> SearchResult;
    private PokeListAdapter adapter;
    private ProgressBar progressBar;
    private boolean ready = false;
    private boolean searching = false;
    private android.widget.SearchView searchView;
    private TextView tapD;
    SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView piu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_list1);


        sharedPreferences = getSharedPreferences("PokeSquad", Context.MODE_PRIVATE);

        bindViews();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        //inizializzo la barra di ricerca come searchWidget
        searchView = (android.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search11));
        //implemento i metodi per leggere la query della ricerca
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);
        //riferimento all'item del menu della barra di ricerca per intercettare l'espanzione e il successivo ritorno all'icona
        MenuItem searchMenuItem = menu.findItem(R.id.search11);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, this);

        return true;
    }

    private void bindViews() {
        //qui istanzio le view
        recView = (RecyclerView) findViewById(R.id.recView);
        tapD = (TextView) findViewById(R.id.tapD);
        piu = (TextView) findViewById(R.id.piu);
        if (tapD != null) {
            tapD.setOnClickListener(this);
        }
        progressBar = (ProgressBar) findViewById(R.id.progressList);
        recView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recView.setLayoutManager(layoutManager);

        initSwipe();
        loadJson();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback SimpleitemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                editor = sharedPreferences.edit();
                String name =result.get(viewHolder.getAdapterPosition()).getName();
                if(!sharedPreferences.contains(name)){
                    String toast = "hai aggiunto " + name + " alla tua squadra";
                    Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
                    editor.putInt(name, viewHolder.getAdapterPosition() + 1);
                }else{
                    String toast = "hai gia " + name + " nella tua squadra";
                    Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
                }
                recView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());


                editor.apply();


            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    piu.setY(itemView.getTop());
                    piu.setHeight(itemView.getHeight());
                    piu.setWidth(itemView.getWidth());
                    Paint p = new Paint();
                    p.setColor(Color.GREEN);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        c.drawRoundRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), 10.0f, 10.0f, p);

                    } else {
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);
                    }
                    if (isCurrentlyActive) {

                        piu.setVisibility(View.VISIBLE);

                    } else {
                        piu.setVisibility(View.GONE);
                    }

                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper ith = new ItemTouchHelper(SimpleitemTouchHelper);
        ith.attachToRecyclerView(recView);

    }

    private void loadJson() {
        //istanzio la cache e il client delle cache
        File httpCacheDirectory = new File(monListActivity1.this.getCacheDir(), "responses");
        int cacheSize = 100 * 1024 * 1024; // 100 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache).build();
        //retrofit preparazione e eseguzione della chiamata html per la lista
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://pokeapi.co/api/v2/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PokeListInterface pokelist = retrofit.create(PokeListInterface.class);

        Call<PokeList> call = pokelist.GetListPokemon();
        call.enqueue(this);

    }

    @Override
    public void onResponse(Call<PokeList> call, Response<PokeList> response) {

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
            Log.d("deb", "onFailure: no e?");
        } else {
            Log.d("deb", t.getMessage());
        }

        Toast.makeText(this, "chiamata network failed please retry", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        tapD.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnCardClicked(View view, int position) {
        //apertura di un altra activity con i dettagli con due casi :durante la ricerca e direttamente dalla lista
        Intent i = new Intent(this, DetailActivity.class);

        if (searching) {
            String url = SearchResult.get(position).getUrl();
            String[] splitted = url.split("/");
            String num = splitted[splitted.length - 1];
            i.putExtra("PokeNum", "" + num);

        } else {
            int pos = position + 1;
            i.putExtra("PokeNum", "" + pos);
        }
        i.putExtra("squad", false);

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


    @Override
    public boolean onQueryTextSubmit(String query) {
        //eseguo la ricerca quando viene premuto il tasto submit e modifico la lista
        if (ready) {
            MyParams mp = new MyParams(result, query, new ArrayList<Result>());
            new SearchTask().execute(mp);
            SearchResult = mp.rets;
            adapter = new PokeListAdapter(SearchResult);
            recView.setAdapter(adapter);
            adapter.setOnCardClickListner(this);
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            searching = true;


            return true;
        } else {
            Log.d("DEB", "onQueryTextSubmit: not ready");
            Toast.makeText(this, "errore nella ricerca", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        //eseguo la ricerca all'aggiunta di ogni lettera e modifico la lista
        if (ready) {
            MyParams mp = new MyParams(result, query, new ArrayList<Result>());
            new SearchTask().execute(mp);
            SearchResult = mp.rets;
            adapter = new PokeListAdapter(SearchResult);
            recView.setAdapter(adapter);
            adapter.setOnCardClickListner(this);
            searching = true;

            return true;
        } else {
            Log.d("DEB", "onQueryTextChange: not ready");
            Toast.makeText(this, "errore nella ricerca", Toast.LENGTH_SHORT).show();
            return false;

        }

    }


    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        //set hint e richiedo il focus sulla search view
        searchView.setQueryHint("Search for pokemon");
        searchView.requestFocus();
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        //reinizializzo la lista allo stato base quando si esce dalla ricerca
        adapter = new PokeListAdapter(result);
        recView.setAdapter(adapter);
        adapter.setOnCardClickListner(this);
        searching = false;
        return true;
    }

    @Override
    public void onClick(View v) {
        //in caso di errore si puo chiedere nuovamente la lista
        if (v == tapD && tapD.getVisibility() == View.VISIBLE) {
            loadJson();
            tapD.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}


