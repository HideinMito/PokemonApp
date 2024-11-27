package com.example.pokemontypesapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID = "pokemon_battle_channel"
    private lateinit var notificationReceiver: BroadcastReceiver

    private lateinit var api: PokeApi
    private lateinit var ivPokemonSprite: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvWelcome: TextView
    private var pokemonType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de vistas
        val etPokemonName = findViewById<EditText>(R.id.etPokemonName)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        ivPokemonSprite = findViewById(R.id.ivPokemonSprite)
        progressBar = findViewById(R.id.progressBar)
        tvWelcome = findViewById(R.id.tvWelcome)

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

        btnEficaz.setOnClickListener {
            val intent = Intent(this, EficazActivity::class.java)
            intent.putExtra("pokemonTypes", pokemonType)
            startActivity(intent)
        }

        btnDebil.setOnClickListener {
            if (pokemonType.isNotEmpty()) {
                val intent = Intent(this, DebilActivity::class.java)
                intent.putExtra("pokemonTypes", pokemonType)
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

        //Registrar el receptor para recibir notificaciones
        setupNotificationReceiver()
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
                    pokemonType = types.joinToString(",")

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

    private fun setupNotificationReceiver() {
        notificationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val title = intent?.getStringExtra("title") ?: "Notificación"
                val body = intent?.getStringExtra("body") ?: "Sin contenido"

                // Muestra un Toast con la notificación
                Toast.makeText(this@MainActivity, "$title: $body", Toast.LENGTH_LONG).show()
            }
        }

        // Registrar el receptor con las banderas necesarias para Android 12+
        val intentFilter = IntentFilter("com.example.pokemontypesapp")
        registerReceiver(notificationReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver) // Desregistra el receptor al cerrar la actividad
    }
}
