package com.example.rickandmortyapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.api.CharacterItem
import com.example.rickandmortyapp.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val character = arguments?.getParcelable<CharacterItem>("character")

        // настройка ActionBar
        (requireContext() as AppCompatActivity).supportActionBar?.title = character?.name ?: "Страница героя"

        character?.imageUrl.let { imageUrl ->
            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_notifications_black_24dp)
                .into(binding.imageCharacter)
        }

        binding.nameCharacter.text = character?.name ?: "Нет данных"
        binding.species.text = character?.species ?: "Нет данных"
        binding.sex.text = character?.gender ?: "Нет данных"
        binding.status.text = character?.status ?: "Нет данных"

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}