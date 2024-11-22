package com.example.pokemontypesapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokemontypesapp.databinding.ActivityEficazBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EficazActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEficazBinding
    private lateinit var api: PokeApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar el binding
        binding = ActivityEficazBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar la instancia de Retrofit y el API
        api = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApi::class.java)

        // Configurar RecyclerView
        val recyclerView = binding.recyclerViewEficaz
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obtener los tipos del Pokémon desde el Intent
        val pokemonTypes = intent.getStringExtra("pokemonTypes")?.split(",") ?: listOf()

        if (pokemonTypes.isNotEmpty()) {
            fetchTypeStrength(pokemonTypes) { strengths ->
                val strengthList = strengths.map { PokemonType(it) }
                val adapter = PokemonTypeAdapter(strengthList)
                recyclerView.adapter = adapter
            }
        } else {
            showToast("Tipos de Pokémon no encontrados.")
        }

        // Configurar el botón para regresar a la actividad principal
        binding.btnBackToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    /**
     * Función para obtener las fortalezas de un tipo usando la API.
     */
    private fun fetchTypeStrength(types: List<String>, callback: (List<String>) -> Unit) {
        val allStrengths = mutableListOf<String>()

        types.forEach { type ->
            api.getType(type).enqueue(object : retrofit2.Callback<TypeResponse> {
                override fun onResponse(
                    call: retrofit2.Call<TypeResponse>,
                    response: retrofit2.Response<TypeResponse>
                ) {
                    if (response.isSuccessful) {
                        val typeResponse = response.body()
                        val strengths = typeResponse?.damage_relations?.double_damage_to
                            ?.map { it.name } ?: listOf()
                        allStrengths.addAll(strengths)

                        // Llamar al callback cuando todos los tipos hayan sido procesados
                        if (allStrengths.size >= types.size) {
                            callback(allStrengths.distinct()) // Eliminar duplicados
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<TypeResponse>, t: Throwable) {
                    callback(listOf("Error: ${t.message}"))
                }
            })
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
