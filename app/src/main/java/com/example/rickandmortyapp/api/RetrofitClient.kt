package com.example.rickandmortyapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val RickAndMorty_BASE_URL = "https://rickandmortyapi.com/api/"

    private val characterRetrofit = Retrofit.Builder()
        .baseUrl(RickAndMorty_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    // API сервис
    val rickApiService: RickApiService by lazy {
        com.example.rickandmortyapp.api.RetrofitClient.characterRetrofit.create(RickApiService::class.java)
    }
}