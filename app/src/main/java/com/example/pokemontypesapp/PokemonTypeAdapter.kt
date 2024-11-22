package com.example.pokemontypesapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PokemonTypeAdapter(private val pokemonTypes: List<PokemonType>) :
    RecyclerView.Adapter<PokemonTypeAdapter.PokemonTypeViewHolder>() {

    // Crea nuevas vistas (se invoca cuando el RecyclerView necesita una nueva vista)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonTypeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return PokemonTypeViewHolder(view)
    }

    // Reemplaza el contenido de una vista
    override fun onBindViewHolder(holder: PokemonTypeViewHolder, position: Int) {
        val pokemonType = pokemonTypes[position]
        holder.textView.text = pokemonType.name
    }

    // Devuelve el tamaño de la lista de datos
    override fun getItemCount(): Int {
        return pokemonTypes.size
    }

    // ViewHolder que mantiene la referencia de las vistas
    class PokemonTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(android.R.id.text1)
    }
}
