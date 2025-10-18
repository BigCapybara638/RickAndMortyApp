package com.example.rickandmortyapp.api

import com.google.gson.annotations.SerializedName

data class RickResponse(
    @SerializedName("info") val info: List<Info>,
    @SerializedName("result") val result: List<PersonageItem>
)

data class Info(
    @SerializedName("count") val count: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("next") val next: String,
)

data class PersonageItem(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("species") val species: String,
    @SerializedName("gender") val pages: String,
    @SerializedName("image") val imageUrl: String,
)
