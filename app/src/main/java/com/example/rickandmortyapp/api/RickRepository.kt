package com.example.rickandmortyapp.api

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.emptyList

class RickRepository(private val context: Context) {
    private val apiService = RetrofitClient.rickApiService
    private val database = AppDatabase.getInstance(context)
    private val characterDao = database.characterDao()

    suspend fun getCharacterList(limit: Int = 50): List<CharacterItem> {
        return try {
            // Пытаемся получить данные из API
            val response = apiService.getCharacterList()
            val characters = response.result?.take(limit) ?: emptyList()

            // Сохраняем в базу данных
            saveCharactersToDatabase(characters)

            println("✅ Данные загружены из API: ${characters.size} персонажей")
            characters

        } catch (e: Exception) {
            println("🔴 API failed: ${e.message}, пытаемся загрузить из кеша")
            // Если API не доступно, загружаем из базы данных
            getCharactersFromDatabase(limit)
        }
    }

    fun getCharactersFlow(): Flow<List<CharacterItem>> {
        return characterDao.getAllCharacters().map { entities ->
            entities.map { it.toCharacterItem() }
        }
    }

    private suspend fun saveCharactersToDatabase(characters: List<CharacterItem>) {
        try {
            val entities = characters.map { it.toCharacterEntity() }
            characterDao.deleteAllCharacters() // Очищаем старые данные
            characterDao.insertCharacters(entities)
            println("✅ Данные сохранены в базу: ${entities.size} персонажей")
        } catch (e: Exception) {
            println("❌ Ошибка сохранения в базу: ${e.message}")
        }
    }

    private suspend fun getCharactersFromDatabase(limit: Int): List<CharacterItem> {
        return try {
            val entities = characterDao.getCharacters(limit)
            val characters = entities.map { it.toCharacterItem() }
            println("✅ Данные загружены из кеша: ${characters.size} персонажей")
            characters
        } catch (e: Exception) {
            println("❌ Ошибка загрузки из кеша: ${e.message}")
            emptyList()
        }
    }
}

// Extension functions для конвертации между Entity и CharacterItem
private fun CharacterItem.toCharacterEntity(): CharacterEntity {
    return CharacterEntity(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        gender = this.gender,
        imageUrl = this.imageUrl,
        episodes = this.episodes.joinToString(",") // Сохраняем episodes как строку
    )
}

private fun CharacterEntity.toCharacterItem(): CharacterItem {
    return CharacterItem(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        gender = this.gender,
        imageUrl = this.imageUrl,
        episodes = this.episodes.split(",") // Восстанавливаем список episodes
    )
}