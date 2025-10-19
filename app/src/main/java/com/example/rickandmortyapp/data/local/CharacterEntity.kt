package com.example.rickandmortyapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val imageUrl: String,
    val episodes: String, // Сохраняем как JSON строку
    val createdAt: Long = System.currentTimeMillis()
)