package com.example.rickandmortyapp.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.network.CharacterItem
import com.example.rickandmortyapp.data.RickRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val characterRepository = RickRepository(application.applicationContext)

    private val _characters = MutableStateFlow<List<CharacterItem>>(emptyList())
    val characters: StateFlow<List<CharacterItem>> = _characters.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadData()
        observeDatabase()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val characters = characterRepository.getCharacterList(40)
                _characters.value = characters
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки данных"
                println("❌ Ошибка загрузки героев: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun observeDatabase() {
        viewModelScope.launch {
            characterRepository.getCharactersFlow().collect { characters ->
                if (_characters.value.isEmpty() && characters.isNotEmpty()) {
                    _characters.value = characters
                    println("🔄 Данные обновлены из Flow: ${characters.size} персонажей")
                }
            }
        }
    }

    fun refreshData() {
        loadData()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

class HomeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}