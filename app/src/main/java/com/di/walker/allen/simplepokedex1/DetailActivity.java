package com.di.walker.allen.simplepokedex1;

import android.content.Context;
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

import com.di.walker.allen.simplepokedex1.model.Pokemon;
import com.di.walker.allen.simplepokedex1.model.Stat;
import com.di.walker.allen.simplepokedex1.model.Type;
import com.squareup.picasso.Picasso;

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

        Bundle extras =getIntent().getExtras();
        pokeQuery=extras.getString("PokeNum");
        Log.d("DEB1", "onCreate: "+pokeQuery);
        bindViews();
        istance=buildPokeapiInstance();
        startSearch();

    }
    void bindViews(){
        pokeName =(TextView)findViewById(R.id.pkmD_name);
        pokeImg =(ImageView)findViewById(R.id.pkmD_img);
        pkm_det=(LinearLayout)findViewById(R.id.pkmD_detail);
        progBar=(ProgressBar) findViewById(R.id.progBar_d);
        pkmWeight=(TextView)findViewById(R.id.pkmn_weight);
        pkmHp=(TextView)findViewById(R.id.pkmn_hp);
        pkmAttack=(TextView)findViewById(R.id.pkmn_attack);
        pkmDefense=(TextView)findViewById(R.id.pkmn_defense);
        pkmSpeed=(TextView)findViewById(R.id.pkmn_speed);
        pkmTypes=(TextView)findViewById(R.id.pkmn_types);
        Log.d("deb2", "bindViews: ");



    }
    private PokeapiInterface buildPokeapiInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(PokeapiInterface.class);
    }


    private void startSearch(){

        Log.d("deb2", "startSearch: ");
        if(pokeQuery.length()== 0){
            Toast.makeText(this,"errore",Toast.LENGTH_SHORT).show();
            return;
        }
        progBar.setVisibility(View.VISIBLE);
        pkm_det.setVisibility(View.GONE);

        try {


            istance.searchForPokemon(pokeQuery).enqueue(this);
        }catch (RuntimeException e){
            Toast.makeText(this,"chiamata network failed",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
        Log.d("deb2", "onResponse: ");
        Pokemon result=response.body();
        if (result==null){
            Toast.makeText(this,"errore ?",Toast.LENGTH_SHORT).show();
            return;
        }
        int num=result.getId();
        pokeName.setText(result.getName() +" #"+ num);
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
        String types="Types: ";
        for (Type t : result.getTypes()){
            types=types+" "+t.getType().getName();
        }
        pkmTypes.setText(types);
        Picasso.with(this).load(result.getSprites().getFrontDefault()).into(pokeImg);

        progBar.setVisibility(View.GONE);
        pkm_det.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(Call<Pokemon> call, Throwable t) {
        Log.d("deb2", "onFailure: ");
        Toast.makeText(this,"chiamata network failed",Toast.LENGTH_SHORT).show();

    }

}
//TODO implement caching pls