package com.example.rickandmortyapp.ui.home

import android.app.Application
import android.graphics.Rect
import android.os.Bundle
import com.example.rickandmortyapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapp.api.CharacterItem
import com.example.rickandmortyapp.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val characterAdapter = HomeAdapter()

    // Используем by viewModels() с фабрикой для Application
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupSwipeRefresh()

        // Данные уже загружаются в init ViewModel
    }

    private fun setupRecyclerView() {
        binding.characterRecycle.apply {
            adapter = characterAdapter
            layoutManager = GridLayoutManager(
                requireContext(),
                2,
                GridLayoutManager.VERTICAL,
                false
            )
            val spacing = (16 * resources.displayMetrics.density).toInt()
            addItemDecoration(SpacesItemDecoration(spacing))
        }

        characterAdapter.onItemClick = { character ->
            openCharacterDetail(character)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.characters.collect { characters ->
                characterAdapter.submitList(characters) {
                    // Этот callback вызывается после обновления списка
                    if (characters.isNotEmpty()) {
                        // Прокручиваем к началу только при первой загрузке
                        binding.characterRecycle.scrollToPosition(0)
                    }
                }
                binding.swipeRefreshLayout.isRefreshing = false
                println("🔄 Обновление адаптера с ${characters.size} элементами")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                        .setAction("Повторить") { viewModel.refreshData() }
                        .show()
                    viewModel.clearError()
                }
            }
        }
    }

    private fun openCharacterDetail(character: CharacterItem) {
        val bundle = Bundle().apply {
            putParcelable("character", character)
        }
        findNavController().navigate(R.id.action_first_to_second, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = space / 2
        outRect.right = space / 2
        outRect.top = space / 4
        outRect.bottom = space / 2
    }
}