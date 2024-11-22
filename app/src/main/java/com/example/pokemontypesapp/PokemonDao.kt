package com.example.pokemontypesapp

import androidx.room.*

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPokemon(pokemon: PokemonEntity)

    @Query("SELECT * FROM pokemon_favorites")
    fun getAllFavorites(): List<PokemonEntity>

    @Delete
    fun deletePokemon(pokemon: PokemonEntity)
}
