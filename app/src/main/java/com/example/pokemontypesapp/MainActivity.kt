package com.example.pokemontypesapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var api: PokeApi
    private lateinit var ivPokemonSprite: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvWelcome: TextView // Nuevo para mostrar el saludo
    private var pokemonType: String = "" // Variable para almacenar el tipo del Pokémon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de vistas
        val etPokemonName = findViewById<EditText>(R.id.etPokemonName)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        ivPokemonSprite = findViewById(R.id.ivPokemonSprite)
        progressBar = findViewById(R.id.progressBar)
        tvWelcome = findViewById(R.id.tvWelcome) // Asigna la referencia

        // Botones
        val btnEficaz = findViewById<Button>(R.id.btnEficaz)
        val btnDebil = findViewById<Button>(R.id.btnDebil)
        val btnAddFavorites = findViewById<Button>(R.id.btnAddFavorites)
        val btnShowFavorites = findViewById<Button>(R.id.btnShowFavorites)
        val btnCombate = findViewById<Button>(R.id.btnCombate)

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

        // Listeners para cambiar a las actividades
        btnEficaz.setOnClickListener {
            val intent = Intent(this, EficazActivity::class.java)
            intent.putExtra("pokemonTypes", pokemonType) // Pasar todos los tipos del Pokémon
            startActivity(intent)
        }

        btnDebil.setOnClickListener {
            if (pokemonType.isNotEmpty()) {
                val intent = Intent(this, DebilActivity::class.java)
                intent.putExtra("pokemonTypes", pokemonType) // Pasar los tipos como string separado por comas
                startActivity(intent)
            } else {
                Toast.makeText(this, "Primero busca un Pokémon para obtener sus tipos.", Toast.LENGTH_SHORT).show()
            }
        }



        btnAddFavorites.setOnClickListener {
            val pokemonName = etPokemonName.text.toString().lowercase().trim()
            if (pokemonName.isNotEmpty() && pokemonType.isNotEmpty()) {
                api.getPokemon(pokemonName).enqueue(object : Callback<PokemonResponse> {
                    override fun onResponse(call: Call<PokemonResponse>, response: Response<PokemonResponse>) {
                        if (response.isSuccessful) {
                            val pokemon = response.body()
                            if (pokemon != null) {
                                val database = PokemonDatabase.getDatabase(this@MainActivity)
                                val pokemonDao = database.pokemonDao()
                                Thread {
                                    val pokemonEntity = PokemonEntity(
                                        id = pokemon.id,
                                        name = pokemon.name,
                                        spriteUrl = pokemon.sprites.front_default
                                    )
                                    pokemonDao.insertPokemon(pokemonEntity)
                                    runOnUiThread {
                                        Toast.makeText(this@MainActivity, "Añadido a favoritos", Toast.LENGTH_SHORT).show()
                                    }
                                }.start()
                            }
                        }
                    }

                    override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Primero busca un Pokémon válido.", Toast.LENGTH_SHORT).show()
            }
        }


        btnShowFavorites.setOnClickListener {
            startActivity(Intent(this, FavoriteActivity::class.java))
        }

        btnCombate.setOnClickListener {
            startActivity(Intent(this, CombateActivity::class.java))
        }
    }

    private fun searchPokemon(pokemonName: String, tvResult: TextView) {
        api.getPokemon(pokemonName).enqueue(object : Callback<PokemonResponse> {
            override fun onResponse(
                call: Call<PokemonResponse>,
                response: Response<PokemonResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val pokemon = response.body()
                    val types = pokemon?.types?.map { it.type.name } ?: listOf()
                    pokemonType = types.joinToString(",") // Concatenar los tipos en un string separado por comas

                    tvResult.text = "Tipo(s): ${types.joinToString(", ")}"

                    val spriteUrl = pokemon?.sprites?.front_default
                    Glide.with(this@MainActivity)
                        .load(spriteUrl)
                        .into(ivPokemonSprite)
                } else {
                    tvResult.text = "Pokémon no encontrado!"
                    ivPokemonSprite.setImageResource(0)
                }
            }

            override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                tvResult.text = "Error: ${t.message}"
                ivPokemonSprite.setImageResource(0)
            }
        })
    }

    private fun fetchPokemonTypes(types: List<TypeSlot>?, tvResult: TextView) {
        val pokemonTypes = types?.joinToString { it.type.name } ?: "Sin tipos"
        tvResult.text = "Tipo(s): $pokemonTypes"
    }
}
