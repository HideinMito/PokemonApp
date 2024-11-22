package com.example.pokemontypesapp

data class PokemonType(
    val name: String
)

data class PokemonResponse(
    val id: Int,
    val name: String,
    val types: List<TypeSlot>,
    val sprites: Sprites
)

data class Sprites(
    val front_default: String
)

data class TypeSlot(
    val type: Type
)


data class TypeInfo(
    val name: String,
    val url: String
)

data class TypeResponse(
    val damage_relations: DamageRelations
)

data class DamageRelations(
    val double_damage_to: List<Type>,
    val double_damage_from: List<Type>
)

data class Type(
    val name: String
)