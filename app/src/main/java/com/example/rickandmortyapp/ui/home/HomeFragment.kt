package com.example.rickandmortyapp.ui.home

import android.graphics.Rect
import android.os.Bundle
import com.example.rickandmortyapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapp.FilterBottomSheetDialog
import com.example.rickandmortyapp.data.network.CharacterItem
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

    private var isFirstDataLoad = true

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
        setupFilterButton()
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

            setHasFixedSize(true)
            val spacing = (16 * resources.displayMetrics.density).toInt()
            addItemDecoration(SpacesItemDecoration(spacing))
        }

        characterAdapter.onItemClick = { character ->
            openCharacterDetail(character)
        }
    }

    private fun setupFilterButton() {
        // Добавьте кнопку фильтра в ваш layout (в toolbar или floating action button)
        binding.filterButton.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        val filterDialog = FilterBottomSheetDialog()
        filterDialog.setCurrentFilters(
            viewModel.currentStatusFilter.value,
            viewModel.currentGenderFilter.value,
            viewModel.currentSpeciesFilter.value
        )
        filterDialog.setOnFiltersAppliedListener { status, gender, species ->
            viewModel.setStatusFilter(status)
            viewModel.setGenderFilter(gender)
            viewModel.setSpeciesFilter(species)
        }
        filterDialog.show(parentFragmentManager, "FilterBottomSheet")
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

                        if (isFirstDataLoad) {
                            binding.characterRecycle.post {
                                binding.characterRecycle.scrollToPosition(0)
                            }
                            isFirstDataLoad = false
                        }

                        // Прокручиваем к началу только при первой загрузке
                        binding.characterRecycle.scrollToPosition(0)

                        binding.swipeRefreshLayout.isRefreshing = false

                        // Показываем количество результатов
                        updateResultsCount(characters.size)

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

    private fun updateResultsCount(count: Int) {
        // Можно показать количество результатов в Snackbar или TextView
        binding.resultsCountText.text = "Найдено: $count"
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