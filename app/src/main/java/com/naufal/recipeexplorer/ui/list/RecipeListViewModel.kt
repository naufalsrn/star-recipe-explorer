package com.naufal.recipeexplorer.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.naufal.recipeexplorer.RecipeExplorerApplication
import com.naufal.recipeexplorer.data.RecipeRepository
import com.naufal.recipeexplorer.data.model.Recipe
import com.naufal.recipeexplorer.ui.components.toUserMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeListUiState(
    val recipes: List<Recipe> = emptyList(),
    val query: String = "",
    // null means "show all difficulties".
    val selectedDifficulty: String? = null,
    val isLoading: Boolean = false,
    // Loading started by pull-to-refresh.
    val isRefreshing: Boolean = false,
    val error: String? = null,
) {
    // Difficulty chips, built from the fetched recipes. The selected one is always
    // kept, so it can still be cleared after a search that has none of it.
    val difficulties: List<String>
        get() = (recipes.map { it.difficulty } + listOfNotNull(selectedDifficulty))
            .filter { it.isNotBlank() }
            .distinct()
            .sortedBy { DIFFICULTY_ORDER.indexOf(it.lowercase()).takeIf { i -> i >= 0 } ?: Int.MAX_VALUE }

    // The difficulty filter runs on the device, on recipes already fetched.
    val visibleRecipes: List<Recipe>
        get() = if (selectedDifficulty == null) recipes
        else recipes.filter { it.difficulty.equals(selectedDifficulty, ignoreCase = true) }

    private companion object {
        val DIFFICULTY_ORDER = listOf("easy", "medium", "hard")
    }
}

class RecipeListViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        load(delayMs = SEARCH_DEBOUNCE_MS)
    }

    fun onDifficultySelected(difficulty: String?) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
    }

    fun retry() = load()

    fun refresh() = load(isRefresh = true)

    private fun load(delayMs: Long = 0, isRefresh: Boolean = false) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            delay(delayMs)
            _uiState.update { it.copy(isLoading = !isRefresh, isRefreshing = isRefresh, error = null) }
            try {
                val recipes = repository.getRecipes(_uiState.value.query.trim())
                _uiState.update { it.copy(recipes = recipes, isLoading = false, isRefreshing = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // A failed refresh keeps the recipes already on screen; a failed search clears them.
                _uiState.update {
                    it.copy(
                        recipes = if (isRefresh) it.recipes else emptyList(),
                        isLoading = false,
                        isRefreshing = false,
                        error = e.toUserMessage(),
                    )
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 400L

        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as RecipeExplorerApplication
                RecipeListViewModel(app.repository)
            }
        }
    }
}
