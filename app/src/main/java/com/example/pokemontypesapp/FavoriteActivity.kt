package com.example.pokemontypesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokemontypesapp.databinding.ActivityFavoritesBinding

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Vinculación de vistas
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = binding.recyclerViewFavorites
        recyclerView.layoutManager = LinearLayoutManager(this)

        val database = PokemonDatabase.getDatabase(this)
        val pokemonDao = database.pokemonDao()

        // Cargar la lista de favoritos en un hilo de fondo
        Thread {
            val favorites = pokemonDao.getAllFavorites()
            val adapter = PokemonAdapter(favorites, pokemonDao)
            runOnUiThread {
                recyclerView.adapter = adapter
            }
        }.start()
    }
}
