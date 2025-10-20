package com.example.rickandmortyapp.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.CharacterGender
import com.example.rickandmortyapp.CharacterStatus
import com.example.rickandmortyapp.data.network.CharacterItem
import com.example.rickandmortyapp.data.RickRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val characterRepository = RickRepository(application.applicationContext)

    private val _allCharacters = MutableStateFlow<List<CharacterItem>>(emptyList())

    private val _characters = MutableStateFlow<List<CharacterItem>>(emptyList())
    val characters: StateFlow<List<CharacterItem>> = _characters.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Фильтры
    private val _currentStatusFilter = MutableStateFlow(CharacterStatus.ALL)
    private val _currentGenderFilter = MutableStateFlow(CharacterGender.ALL)
    private val _currentSpeciesFilter = MutableStateFlow("")

    val currentStatusFilter: StateFlow<CharacterStatus> = _currentStatusFilter.asStateFlow()
    val currentGenderFilter: StateFlow<CharacterGender> = _currentGenderFilter.asStateFlow()
    val currentSpeciesFilter: StateFlow<String> = _currentSpeciesFilter.asStateFlow()

    private var isFirstLoad = true

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
                _allCharacters.value = characters
                applyFilters()
                isFirstLoad = false
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки данных"
                println("❌ Ошибка загрузки героев: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Методы для установки фильтров
    fun setStatusFilter(status: CharacterStatus) {
        _currentStatusFilter.value = status
        applyFilters()
    }

    fun setGenderFilter(gender: CharacterGender) {
        _currentGenderFilter.value = gender
        applyFilters()
    }

    fun setSpeciesFilter(species: String) {
        _currentSpeciesFilter.value = species
        applyFilters()
    }

    fun clearFilters() {
        _currentStatusFilter.value = CharacterStatus.ALL
        _currentGenderFilter.value = CharacterGender.ALL
        _currentSpeciesFilter.value = ""
        applyFilters()
    }

    private fun applyFilters() {
        val filteredCharacters = _allCharacters.value.filter { character ->
            val statusMatch = when (_currentStatusFilter.value) {
                CharacterStatus.ALL -> true
                CharacterStatus.ALIVE -> character.status.equals("Alive", ignoreCase = true)
                CharacterStatus.DEAD -> character.status.equals("Dead", ignoreCase = true)
                CharacterStatus.UNKNOWN -> character.status.equals("unknown", ignoreCase = true)
            }

            val genderMatch = when (_currentGenderFilter.value) {
                CharacterGender.ALL -> true
                CharacterGender.MALE -> character.gender.equals("Male", ignoreCase = true)
                CharacterGender.FEMALE -> character.gender.equals("Female", ignoreCase = true)
                CharacterGender.GENDERLESS -> character.gender.equals("Genderless", ignoreCase = true)
                CharacterGender.UNKNOWN -> character.gender.equals("unknown", ignoreCase = true)
            }

            val speciesMatch = _currentSpeciesFilter.value.isEmpty() ||
                    character.species.contains(_currentSpeciesFilter.value, ignoreCase = true)

            statusMatch && genderMatch && speciesMatch
        }

        _characters.value = filteredCharacters
        println("🔍 Применены фильтры. Результатов: ${filteredCharacters.size}")
    }


    private fun observeDatabase() {
        viewModelScope.launch {
            characterRepository.getCharactersFlow().collect { characters ->
                if (_allCharacters.value.isEmpty() && characters.isNotEmpty()) {
                    _allCharacters.value = characters
                    applyFilters()
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

    fun isFirstLoad(): Boolean = isFirstLoad
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