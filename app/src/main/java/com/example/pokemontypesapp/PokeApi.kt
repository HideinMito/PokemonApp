package com.example.pokemontypesapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApi {
    @GET("pokemon/{name}")
    fun getPokemon(@Path("name") name: String): Call<PokemonResponse>

    @GET("type/{name}")
    fun getType(@Path("name") name: String): Call<TypeResponse>
}

