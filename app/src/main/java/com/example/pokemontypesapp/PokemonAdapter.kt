package com.example.pokemontypesapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokemontypesapp.databinding.ItemPokemonFavoriteBinding

class PokemonAdapter(
    private var pokemonList: List<PokemonEntity>,
    private val pokemonDao: PokemonDao
) : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    // ViewHolder que utiliza el binding
    class PokemonViewHolder(val binding: ItemPokemonFavoriteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ItemPokemonFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemonList[position]

        // Asigna datos al ViewHolder
        holder.binding.tvPokemonName.text = pokemon.name
        Glide.with(holder.itemView.context)
            .load(pokemon.spriteUrl)
            .into(holder.binding.ivPokemonSprite)

        // Configuración del botón de eliminar
        holder.binding.btnDelete.setOnClickListener {
            Thread {
                pokemonDao.deletePokemon(pokemon) // Elimina el Pokémon de la base de datos
                (pokemonList as MutableList).removeAt(position) // Elimina de la lista en memoria
                (holder.itemView.context as AppCompatActivity).runOnUiThread {
                    notifyItemRemoved(position) // Actualiza la vista
                    notifyItemRangeChanged(position, itemCount)
                }
            }.start()
        }
    }

    override fun getItemCount(): Int = pokemonList.size
}
