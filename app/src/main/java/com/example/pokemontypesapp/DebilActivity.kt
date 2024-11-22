package com.example.pokemontypesapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokemontypesapp.databinding.ActivityDebilBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DebilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDebilBinding
    private lateinit var api: PokeApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar el binding
        binding = ActivityDebilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar la instancia de Retrofit y el API
        api = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApi::class.java)

        // Configurar RecyclerView
        val recyclerView = binding.recyclerViewDebil
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obtener los tipos del Pokémon desde el Intent
        val pokemonTypes = intent.getStringExtra("pokemonTypes")?.split(",") ?: listOf()

        if (pokemonTypes.isNotEmpty()) {
            fetchTypeWeakness(pokemonTypes) { weaknesses ->
                if (weaknesses.isNotEmpty()) {
                    val weaknessList = weaknesses.map { PokemonType(it) }
                    val adapter = PokemonTypeAdapter(weaknessList)
                    binding.recyclerViewDebil.adapter = adapter
                } else {
                    showToast("No se encontraron debilidades para los tipos proporcionados.")
                }
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

    private fun fetchTypeWeakness(types: List<String>, callback: (List<String>) -> Unit) {
        val allWeaknesses = mutableListOf<String>()
        var completedRequests = 0

        types.forEach { type ->
            api.getType(type).enqueue(object : retrofit2.Callback<TypeResponse> {
                override fun onResponse(
                    call: retrofit2.Call<TypeResponse>,
                    response: retrofit2.Response<TypeResponse>
                ) {
                    completedRequests++
                    if (response.isSuccessful) {
                        val typeResponse = response.body()
                        val weaknesses = typeResponse?.damage_relations?.double_damage_from
                            ?.map { it.name } ?: listOf()
                        allWeaknesses.addAll(weaknesses)
                    }

                    if (completedRequests == types.size) {
                        callback(allWeaknesses.distinct())
                    }
                }

                override fun onFailure(call: retrofit2.Call<TypeResponse>, t: Throwable) {
                    completedRequests++
                    showToast("Error al obtener datos para el tipo: $type")

                    if (completedRequests == types.size) {
                        callback(allWeaknesses.distinct())
                    }
                }
            })
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
