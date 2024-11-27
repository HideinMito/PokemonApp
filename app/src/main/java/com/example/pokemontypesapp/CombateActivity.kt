package com.example.pokemontypesapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class CombateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combate)

        val etPokemon1 = findViewById<EditText>(R.id.etPokemon1)
        val etPokemon2 = findViewById<EditText>(R.id.etPokemon2)
        val btnConfirmPokemon1 = findViewById<Button>(R.id.btnConfirmPokemon1)
        val btnConfirmPokemon2 = findViewById<Button>(R.id.btnConfirmPokemon2)
        val ivPokemonSprite1 = findViewById<ImageView>(R.id.ivPokemonSprite1)
        val ivPokemonSprite2 = findViewById<ImageView>(R.id.ivPokemonSprite2)
        val btnIniciarCombate = findViewById<Button>(R.id.btnIniciarCombate)
        val tvResultadoCombate = findViewById<TextView>(R.id.tvResultadoCombate)

        // Confirmar y cargar imagen de Pokémon 1
        btnConfirmPokemon1.setOnClickListener {
            val pokemon1 = etPokemon1.text.toString().trim()
            if (pokemon1.isNotEmpty()) {
                cargarImagenPokemon(pokemon1, ivPokemonSprite1)
            } else {
                Toast.makeText(this, "Introduce el nombre del Pokémon 1", Toast.LENGTH_SHORT).show()
            }
        }

        // Confirmar y cargar imagen de Pokémon 2
        btnConfirmPokemon2.setOnClickListener {
            val pokemon2 = etPokemon2.text.toString().trim()
            if (pokemon2.isNotEmpty()) {
                cargarImagenPokemon(pokemon2, ivPokemonSprite2)
            } else {
                Toast.makeText(this, "Introduce el nombre del Pokémon 2", Toast.LENGTH_SHORT).show()
            }
        }

        // Iniciar el combate y mostrar resultado
        btnIniciarCombate.setOnClickListener {
            val pokemon1 = etPokemon1.text.toString().trim()
            val pokemon2 = etPokemon2.text.toString().trim()

            if (pokemon1.isNotEmpty() && pokemon2.isNotEmpty()) {
                val ganador = if ((1..2).random() == 1) pokemon1 else pokemon2
                tvResultadoCombate.text = "Ganador del combate: $ganador"
            } else {
                Toast.makeText(this, "Introduce ambos Pokémon", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para cargar la imagen del Pokémon desde la API
    private fun cargarImagenPokemon(pokemonName: String, imageView: ImageView) {
        val api = PokeApiService.instance.create(PokeApi::class.java)

        api.getPokemon(pokemonName).enqueue(object : retrofit2.Callback<PokemonResponse> {
            override fun onResponse(
                call: retrofit2.Call<PokemonResponse>,
                response: retrofit2.Response<PokemonResponse>
            ) {
                if (response.isSuccessful) {
                    val spriteUrl = response.body()?.sprites?.front_default
                    Glide.with(this@CombateActivity)
                        .load(spriteUrl)
                        .into(imageView)
                } else {
                    Toast.makeText(this@CombateActivity, "Pokémon no encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<PokemonResponse>, t: Throwable) {
                Toast.makeText(this@CombateActivity, "Error al cargar imagen", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
