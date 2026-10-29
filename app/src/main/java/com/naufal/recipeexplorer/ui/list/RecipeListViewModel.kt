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
    val isLoading: Boolean = false,
    val error: String? = null,
)

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

    fun retry() = load()

    private fun load(delayMs: Long = 0) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            delay(delayMs)
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val recipes = repository.getRecipes(_uiState.value.query.trim())
                _uiState.update { it.copy(recipes = recipes, isLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(recipes = emptyList(), isLoading = false, error = e.toUserMessage()) }
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
