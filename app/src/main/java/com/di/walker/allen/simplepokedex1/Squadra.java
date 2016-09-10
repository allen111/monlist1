package com.di.walker.allen.simplepokedex1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        squadItems= new ArrayList<SquadItem>();
        bindViews();
        bindList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.squad_menu,menu);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.clear_all){

            AlertDialog.Builder builder =new AlertDialog.Builder(this);
            builder.setTitle("Clear all").setCancelable(true).setMessage("vuoi cancellare la tua squadra?");

            builder.setPositiveButton("clear", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    editor= sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    tooltip.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("DI1", "onClick: cancel");
                }
            });


            AlertDialog dialog=builder.create();
            dialog.show();













            return true;
        }
        return false;
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
        Log.d("CLR2", "bindList: ");
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        sharedPreferences = getSharedPreferences("PokeSquad", Context.MODE_PRIVATE);
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
    public void OnCardClicked(View view, int poke_num,int position) {
        Log.d("CL2", "OnCardClicked: "+poke_num);
        Intent i= new Intent(this,DetailActivity.class);
        i.putExtra("PokeNum", "" + poke_num);
        i.putExtra("squad",true);
        i.putExtra("pos",position);
        startActivity(i);
    }
}
// TODO: remove a poke