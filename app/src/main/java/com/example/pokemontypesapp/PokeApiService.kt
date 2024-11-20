package com.example.pokemontypesapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PokeApiService {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}