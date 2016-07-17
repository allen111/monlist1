package com.di.walker.allen.simplepokedex1;


import com.di.walker.allen.simplepokedex1.model.Pokemon;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PokeapiInterface {

    @GET("pokemon/{query}")
    Call<Pokemon> searchForPokemon(@Path("query") String query);
}
