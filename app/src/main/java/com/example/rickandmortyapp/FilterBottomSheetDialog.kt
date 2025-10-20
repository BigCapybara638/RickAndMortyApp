package com.example.rickandmortyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.core.view.isVisible
import com.example.rickandmortyapp.databinding.FilterBottomSheetBinding
import com.google.android.material.chip.Chip
import com.example.rickandmortyapp.CharacterStatus
import com.example.rickandmortyapp.CharacterGender

class FilterBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: FilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var onFiltersApplied: ((CharacterStatus, CharacterGender, String) -> Unit)? = null
    private var currentStatus = CharacterStatus.ALL
    private var currentGender = CharacterGender.ALL
    private var currentSpecies = ""

    // Фабричный метод для создания диалога с текущими фильтрами
    companion object {
        fun newInstance(
            currentStatus: CharacterStatus,
            currentGender: CharacterGender,
            currentSpecies: String
        ): FilterBottomSheetDialog {
            return FilterBottomSheetDialog().apply {
                this.currentStatus = currentStatus
                this.currentGender = currentGender
                this.currentSpecies = currentSpecies
            }
        }
    }

    fun setOnFiltersAppliedListener(listener: (CharacterStatus, CharacterGender, String) -> Unit) {
        onFiltersApplied = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChips()
        setupButtons()
        setupSpeciesFilter()
    }

    fun setCurrentFilters(status: CharacterStatus, gender: CharacterGender, species: String) {
        this.currentStatus = status
        this.currentGender = gender
        this.currentSpecies = species
    }
    private fun setupChips() {
        // Статус фильтры
        binding.chipStatusAll.isChecked = currentStatus == CharacterStatus.ALL
        binding.chipStatusAlive.isChecked = currentStatus == CharacterStatus.ALIVE
        binding.chipStatusDead.isChecked = currentStatus == CharacterStatus.DEAD
        binding.chipStatusUnknown.isChecked = currentStatus == CharacterStatus.UNKNOWN

        // Гендер фильтры
        binding.chipGenderAll.isChecked = currentGender == CharacterGender.ALL
        binding.chipGenderMale.isChecked = currentGender == CharacterGender.MALE
        binding.chipGenderFemale.isChecked = currentGender == CharacterGender.FEMALE
        binding.chipGenderGenderless.isChecked = currentGender == CharacterGender.GENDERLESS
        binding.chipGenderUnknown.isChecked = currentGender == CharacterGender.UNKNOWN

        // Устанавливаем текст для вида
        binding.speciesEditText.setText(currentSpecies)
    }

    private fun setupButtons() {
        binding.btnApplyFilters.setOnClickListener {
            println("🎯 Кнопка Применить нажата")
            applyFilters()
        }

        binding.btnClearFilters.setOnClickListener {
            println("🎯 Кнопка Сбросить нажата")
            clearFilters()
        }
    }

    private fun setupSpeciesFilter() {
        // Можно добавить логику для реального времени, если нужно
        binding.speciesEditText.setOnEditorActionListener { _, _, _ ->
            applyFilters()
            dismiss()
            true
        }
    }

    private fun applyFilters() {
        val status = getSelectedStatus()
        val gender = getSelectedGender()
        val species = binding.speciesEditText.text?.toString()?.trim() ?: ""

        println("🔍 Применяем фильтры: status=$status, gender=$gender, species='$species'")

        onFiltersApplied?.invoke(status, gender, species)
        dismiss()
    }

    private fun getSelectedStatus(): CharacterStatus {
        return when {
            binding.chipStatusAlive.isChecked -> CharacterStatus.ALIVE
            binding.chipStatusDead.isChecked -> CharacterStatus.DEAD
            binding.chipStatusUnknown.isChecked -> CharacterStatus.UNKNOWN
            else -> CharacterStatus.ALL
        }
    }

    private fun getSelectedGender(): CharacterGender {
        return when {
            binding.chipGenderMale.isChecked -> CharacterGender.MALE
            binding.chipGenderFemale.isChecked -> CharacterGender.FEMALE
            binding.chipGenderGenderless.isChecked -> CharacterGender.GENDERLESS
            binding.chipGenderUnknown.isChecked -> CharacterGender.UNKNOWN
            else -> CharacterGender.ALL
        }
    }

    private fun clearFilters() {
        // Сбрасываем все чекбоксы
        binding.chipStatusAll.isChecked = true
        binding.chipGenderAll.isChecked = true
        binding.speciesEditText.setText("")

        onFiltersApplied?.invoke(CharacterStatus.ALL, CharacterGender.ALL, "")
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}