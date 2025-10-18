package com.example.rickandmortyapp.ui.home

import android.view.LayoutInflater
import com.example.rickandmortyapp.R
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmortyapp.api.CharacterItem
import com.example.rickandmortyapp.databinding.ItemCharacterBinding

class HomeAdapter : ListAdapter<CharacterItem, HomeAdapter.HomeViewHolder>(DIFF_CALLBACK) {

    var onItemClick: ((CharacterItem) -> Unit)? = null

    inner class HomeViewHolder(private val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(character: CharacterItem) {
            binding.nameCharacter.text = character.name
            binding.sexCharacter.text = character.gender
            binding.typeCharacter.text = character.species
            binding.statusCharacter.text = character.status

            binding.root.setOnClickListener {
                onItemClick?.invoke(character)
            }

            // Загрузка изображения - используем imageUrl
            character.imageUrl.let { imageUrl ->
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_notifications_black_24dp)
                    .into(binding.imageCharacter)
            }

        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeAdapter.HomeViewHolder {
        val binding = ItemCharacterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeAdapter.HomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CharacterItem>() {
            override fun areItemsTheSame(oldItem: CharacterItem, newItem: CharacterItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CharacterItem, newItem: CharacterItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}