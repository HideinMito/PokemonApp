package com.example.pokemontypesapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var api: PokeApi
    private lateinit var ivPokemonSprite: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de vistas
        val etPokemonName = findViewById<EditText>(R.id.etPokemonName)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        ivPokemonSprite = findViewById(R.id.ivPokemonSprite)
        progressBar = findViewById(R.id.progressBar)

        // Iniciar la API
        api = PokeApiService.instance.create(PokeApi::class.java)

        btnSearch.setOnClickListener {
            val pokemonName = etPokemonName.text.toString().lowercase().trim()
            if (pokemonName.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                searchPokemon(pokemonName, tvResult)
            } else {
                tvResult.text = "Por favor ingresa un nombre de Pokémon."
            }
        }
    }

    private fun searchPokemon(pokemonName: String, tvResult: TextView) {
        api.getPokemon(pokemonName).enqueue(object : Callback<PokemonResponse> {
            override fun onResponse(call: Call<PokemonResponse>, response: Response<PokemonResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val pokemon = response.body()

                    // Concatenamos todos los tipos de Pokémon, por ejemplo "agua, volador"
                    val typeNames = pokemon?.types?.joinToString { it.type.name } ?: "Unknown"
                    val spriteUrl = pokemon?.sprites?.front_default

                    // Mostrar la imagen usando Glide
                    Glide.with(this@MainActivity)
                        .load(spriteUrl)
                        .into(ivPokemonSprite)

                    // Mostrar los tipos en el TextView
                    tvResult.text = "Tipo(s): $typeNames"

                    // Llamar a la función para obtener debilidades y fortalezas para cada tipo
                    fetchTypeWeakness(pokemon?.types, tvResult) // Cambié esto para pasar la lista de tipos
                } else {
                    tvResult.text = "Pokémon no encontrado!"
                    ivPokemonSprite.setImageResource(0) // Limpia la imagen si hay error
                }
            }

            override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                tvResult.text = "Error: ${t.message}"
                ivPokemonSprite.setImageResource(0) // Limpia la imagen si falla
            }
        })
    }

    private fun fetchTypeWeakness(types: List<TypeSlot>?, tvResult: TextView) {
        val allWeaknesses = mutableListOf<String>()
        val allStrengths = mutableListOf<String>()

        // Iterar sobre cada tipo y obtener debilidades y fortalezas
        types?.forEach { typeSlot ->
            api.getType(typeSlot.type.name).enqueue(object : Callback<TypeResponse> {
                override fun onResponse(call: Call<TypeResponse>, response: Response<TypeResponse>) {
                    if (response.isSuccessful) {
                        val typeResponse = response.body()

                        // Concatenar debilidades y fortalezas de este tipo
                        val weaknesses = typeResponse?.damage_relations?.double_damage_from?.joinToString { it.name } ?: "No hay debilidades"
                        val strengths = typeResponse?.damage_relations?.double_damage_to?.joinToString { it.name } ?: "No hay fortalezas"

                        allWeaknesses.add(weaknesses)
                        allStrengths.add(strengths)

                        // Actualizar el resultado una vez que todos los tipos hayan sido procesados
                        if (allWeaknesses.size == types.size) {
                            tvResult.text = buildString {
                                append("Tipo(s): ${types.joinToString { it.type.name }}\n")
                                append("Débil contra: ${allWeaknesses.joinToString(", ")}\n")
                                append("Eficaz contra: ${allStrengths.joinToString(", ")}")
                            }
                        }
                    } else {
                        tvResult.text = "Tipo no encontrado!"
                    }
                }

                override fun onFailure(call: Call<TypeResponse>, t: Throwable) {
                    tvResult.text = "Error: ${t.message}"
                }
            })
        }
    }
}
