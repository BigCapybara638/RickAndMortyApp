package com.example.rickandmortyapp.api

import retrofit2.http.GET

interface RickApiService {

    @GET("character/")
    suspend fun getCharacterList(): RickResponse
}