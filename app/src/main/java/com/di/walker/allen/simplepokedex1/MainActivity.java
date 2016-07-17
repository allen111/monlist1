package com.di.walker.allen.simplepokedex1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Callback<Pokemon>,TextView.OnEditorActionListener{
    private EditText searchBox;
    private Button searchButton;
    private Button gotoButton;
    private ProgressBar progBar;
    private LinearLayout pkmDetaila;
    private TextView pkmName;
    private TextView pkmWeight;
    private TextView pkmHp;
    private TextView pkmAttack;
    private TextView pkmDefense;
    private TextView pkmSpeed;
    private TextView pkmTypes;
    private ImageView pkmImg;


    private PokeapiInterface istance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        istance=buildPokeapiInstance();
        searchButton.setOnClickListener(this);
        gotoButton.setOnClickListener(this);
        searchBox.setOnEditorActionListener(this);
    }

    private PokeapiInterface buildPokeapiInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(PokeapiInterface.class);
    }

    private void bindViews(){
        searchBox=(EditText)findViewById(R.id.searchBox);
        searchButton=(Button)findViewById(R.id.searchButton);
        gotoButton=(Button)findViewById(R.id.listButton);
        progBar=(ProgressBar) findViewById(R.id.progressB);
        pkmDetaila=(LinearLayout)findViewById(R.id.pkmn_detail1);
        pkmName=(TextView)findViewById(R.id.pkmn_name);
        pkmWeight=(TextView)findViewById(R.id.pkmn_weight);
        pkmHp=(TextView)findViewById(R.id.pkmn_hp);
        pkmAttack=(TextView)findViewById(R.id.pkmn_attack);
        pkmDefense=(TextView)findViewById(R.id.pkmn_defense);
        pkmSpeed=(TextView)findViewById(R.id.pkmn_speed);
        pkmTypes=(TextView)findViewById(R.id.pkmn_types);
        pkmImg=(ImageView)findViewById(R.id.pkmn_front);


    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.searchButton){
            startSearch();

        }else{
            if(v.getId()==R.id.listButton){
                Intent i =new Intent(this,monListActivity1.class);
                startActivity(i);
            }
        }



    }

    @Override
    public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
        Pokemon result=response.body();
        if (result==null){
            Toast.makeText(this,"nome errato",Toast.LENGTH_SHORT).show();
            return;
        }
        int num=result.getId();
        pkmName.setText(result.getName() +" #"+ num);
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
        Picasso.with(this).load(result.getSprites().getFrontDefault()).into(pkmImg);

        progBar.setVisibility(View.GONE);
        pkmDetaila.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(Call<Pokemon> call, Throwable t) {
        Toast.makeText(this,"chiamata network failed",Toast.LENGTH_SHORT).show();

    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
        if (v.getId()==R.id.searchBox){

                startSearch();

        }
        return true;
    }



    private void startSearch(){
        Log.d("main", "onClick: searchButton ");
        //chiudi tastiera
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if(searchBox.getText().toString().length()== 0){
            Toast.makeText(this,"inserisci nome o num",Toast.LENGTH_SHORT).show();
            return;
        }
        progBar.setVisibility(View.VISIBLE);
        pkmDetaila.setVisibility(View.GONE);
        String pokemonQuery=searchBox.getText().toString().toLowerCase();

        try {


            istance.searchForPokemon(pokemonQuery).enqueue(this);
        }catch (RuntimeException e){
            Toast.makeText(this,"chiamata network failed",Toast.LENGTH_SHORT).show();
        }
    }
}
