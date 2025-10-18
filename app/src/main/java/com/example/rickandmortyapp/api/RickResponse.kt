package com.example.rickandmortyapp.api

import com.google.gson.annotations.SerializedName

data class RickResponse(
    @SerializedName("info") val info: Info,
    @SerializedName("results") val result: List<CharacterItem>
)

data class Info(
    @SerializedName("count") val count: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("next") val next: String,
)

data class CharacterItem(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("species") val species: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("image") val imageUrl: String,
)
