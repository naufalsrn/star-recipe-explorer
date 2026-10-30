package com.naufal.recipeexplorer.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.naufal.recipeexplorer.RecipeExplorerApplication
import com.naufal.recipeexplorer.data.RecipeRepository
import com.naufal.recipeexplorer.data.model.Recipe
import com.naufal.recipeexplorer.data.model.RecipePage
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
    val selectedDifficulty: String? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null,
    val loadMoreError: String? = null,
) {
    val difficulties: List<String>
        get() = (recipes.map { it.difficulty } + listOfNotNull(selectedDifficulty))
            .filter { it.isNotBlank() }
            .distinct()
            .sortedBy { DIFFICULTY_ORDER.indexOf(it.lowercase()).takeIf { i -> i >= 0 } ?: Int.MAX_VALUE }

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

    fun loadNextPage() {
        val state = _uiState.value
        if (loadJob?.isActive == true || state.endReached || state.recipes.isEmpty() || state.loadMoreError != null) return

        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            try {
                val page = repository.getRecipes(state.query.trim(), PAGE_SIZE, skip = state.recipes.size)
                _uiState.update {
                    val recipes = (it.recipes + page.recipes).distinctBy { recipe -> recipe.id }
                    it.copy(
                        recipes = recipes,
                        isLoadingMore = false,
                        endReached = isLastPage(page, recipes.size),
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingMore = false, loadMoreError = e.toUserMessage()) }
            }
        }
    }

    fun retryLoadMore() {
        _uiState.update { it.copy(loadMoreError = null) }
        loadNextPage()
    }

    private fun load(delayMs: Long = 0, isRefresh: Boolean = false) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            delay(delayMs)
            _uiState.update {
                it.copy(isLoading = !isRefresh, isRefreshing = isRefresh, isLoadingMore = false, error = null, loadMoreError = null)
            }
            try {
                val page = repository.getRecipes(_uiState.value.query.trim(), PAGE_SIZE, skip = 0)
                _uiState.update {
                    it.copy(
                        recipes = page.recipes,
                        isLoading = false,
                        isRefreshing = false,
                        endReached = isLastPage(page, page.recipes.size),
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
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

    private fun isLastPage(page: RecipePage, loadedCount: Int) =
        page.recipes.size < PAGE_SIZE || loadedCount >= page.total

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 400L
        const val PAGE_SIZE = 10

        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as RecipeExplorerApplication
                RecipeListViewModel(app.repository)
            }
        }
    }
}
