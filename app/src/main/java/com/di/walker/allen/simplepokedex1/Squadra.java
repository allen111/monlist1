package com.di.walker.allen.simplepokedex1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Squadra extends AppCompatActivity  implements SquadListAdapter.OnSquadCardClikListner{
    private RecyclerView recyclerView;
    private TextView tooltip;
    private ArrayList<SquadItem> squadItems;
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
        SquadListAdapter squadListAdapter = new SquadListAdapter(squadItems);
        recyclerView.setAdapter(squadListAdapter);
        squadListAdapter.setOnSquadCardClikListener(this);

        progressBar.setVisibility(View.GONE);

        if (squadItems.size()==0){
            tooltip.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
        }

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
        tooltip= (TextView)findViewById(R.id.squadTootip);

    }




    @Override
    public void OnCardClicked(View view, int poke_num) {
        Log.d("CL2", "OnCardClicked: "+poke_num);
        Intent i= new Intent(this,DetailActivity.class);


        i.putExtra("PokeNum", "" + poke_num);
        startActivity(i);
    }
}
//TODO:  fix detailActivity swipe ..
// TODO: remove a poke and clear all