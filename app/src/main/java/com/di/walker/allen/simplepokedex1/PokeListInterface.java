package com.di.walker.allen.simplepokedex1;

import com.di.walker.allen.simplepokedex1.list.PokeList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by allen on 17-Jul-16.
 */
public interface PokeListInterface {


    @GET("pokemon/?limit="+R.string.pokemonCount)
    Call<PokeList>GetListPokemon();
}
