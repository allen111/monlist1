package com.di.walker.allen.simplepokedex1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.di.walker.allen.simplepokedex1.list.PokeList;
import com.di.walker.allen.simplepokedex1.list.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class monListActivity1 extends AppCompatActivity implements Callback<PokeList>,PokeListAdapter.OnCardClikListner {
    private RecyclerView recView;
    private ArrayList<Result> result;
    private PokeListAdapter adapter;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_list1);
        Log.d("deb", "onCreate: before bind");
        bindViews();
    }

    private void bindViews(){
        recView =(RecyclerView)findViewById(R.id.recView);
        progressBar=(ProgressBar) findViewById(R.id.progressList);
        recView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recView.setLayoutManager(layoutManager);

        Log.d("deb", "onCreate: before load");
        loadJson();
    }

    private void loadJson() {
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl("http://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PokeListInterface pokelist=retrofit.create(PokeListInterface.class);


        Call<PokeList> call = pokelist.GetListPokemon();
        Log.d("deb", "onCreate: before enquee");

        call.enqueue(this);
        Log.d("deb", "onCreate: after enquee");

    }

    @Override
    public void onResponse(Call<PokeList> call, Response<PokeList> response) {
        Log.d("deb", "onCreate: onRes");

        PokeList jsonResponse = response.body();
        result = jsonResponse.getResults();
        adapter = new PokeListAdapter(result);
        recView.setAdapter(adapter);
        adapter.setOnCardClickListner(this);

        progressBar.setVisibility(View.GONE);
        recView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(Call<PokeList> call, Throwable t) {
        Log.d("deb",t.getMessage());
        Toast.makeText(this,"chiamata network failed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnCardClicked(View view, int position) {
        Log.d("PKM", "OnCardClicked: "+position);
        Intent i =new Intent(this,DetailActivity.class);
        int pos=position+1;
        i.putExtra("PokeNum",""+pos);
        startActivity(i);
    }
}
//TODO click e immagini sulla lista?
