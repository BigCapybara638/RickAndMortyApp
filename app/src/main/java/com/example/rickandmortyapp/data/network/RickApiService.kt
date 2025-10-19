package com.example.rickandmortyapp.data.network

import retrofit2.http.GET

interface RickApiService {

    @GET("character/")
    suspend fun getCharacterList(): RickResponse
}