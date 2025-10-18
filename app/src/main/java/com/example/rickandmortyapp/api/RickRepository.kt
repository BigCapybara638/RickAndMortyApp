package com.example.rickandmortyapp.api

class RickRepository {
    private val apiService = RetrofitClient.rickApiService

    suspend fun getCharacterList(limit: Int = 50): List<CharacterItem> {
        return try {
            val response = apiService.getCharacterList()
            response.result?.take(limit) ?: emptyList()
        } catch (e: Exception) {
            println("🔴 Real API failed: ${e.message}, returning empty list")
            emptyList()
        }
    }
}