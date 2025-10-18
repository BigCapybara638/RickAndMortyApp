package com.example.rickandmortyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.api.CharacterItem
import com.example.rickandmortyapp.api.RickRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val characterRepository = RickRepository()

    // Добавьте LiveData или StateFlow для наблюдения за данными
    private val _characters = MutableStateFlow<List<CharacterItem>>(emptyList())
    val characters: StateFlow<List<CharacterItem>> = _characters.asStateFlow()

    // Или с LiveData:
    // private val _characters = MutableLiveData<List<CharacterItem>>()
    // val characters: LiveData<List<CharacterItem>> = _characters

    fun loadData() {
        viewModelScope.launch {
            loadCharactersList()
        }
    }

    private suspend fun loadCharactersList() {
        try {
            val characters = characterRepository.getCharacterList(20)
            _characters.value = characters // Обновляем StateFlow
            println("✅ Загружено ${characters.size} героев")
        } catch (e: Exception) {
            println("❌ Ошибка загрузки героев: ${e.message}")
            _characters.value = emptyList()
        }
    }
}