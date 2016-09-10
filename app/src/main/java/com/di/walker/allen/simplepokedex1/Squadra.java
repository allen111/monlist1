package com.di.walker.allen.simplepokedex1;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Squadra extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<SquadItem> squadItems;
    private SquadListAdapter squadListAdapter;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squadra);

        sharedPreferences = getSharedPreferences("PokeSquad", Context.MODE_PRIVATE);
        //editor= sharedPreferences.edit();
        squadItems= new ArrayList<SquadItem>();
        bindViews();
        bindList();

    }

    private void bindRec() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        squadListAdapter = new SquadListAdapter(squadItems);
        recyclerView.setAdapter(squadListAdapter);

        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

    }

    private void bindList() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        Map<String,?> map =sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : map.entrySet()){
            squadItems.add(new SquadItem(entry.getKey(),(Integer) entry.getValue()));
        }
        Collections.sort(squadItems);
        bindRec();

    }

    private void bindViews() {
        recyclerView =(RecyclerView)findViewById(R.id.squadRecView);
        progressBar= (ProgressBar) findViewById(R.id.progreSquadList);

    }
}
