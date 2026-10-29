package com.naufal.recipeexplorer.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.naufal.recipeexplorer.RecipeExplorerApplication
import com.naufal.recipeexplorer.data.RecipeRepository
import com.naufal.recipeexplorer.data.model.Recipe
import com.naufal.recipeexplorer.ui.components.toUserMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    /** Indexes of the ingredients the user has ticked off. */
    val checkedIngredients: Set<Int> = emptySet(),
)

/** Loads one recipe and remembers which ingredients the user has ticked off. */
class RecipeDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: RecipeRepository,
) : ViewModel() {

    // The recipe id comes from the navigation route, e.g. "recipes/12".
    private val recipeId: Int = checkNotNull(savedStateHandle[RECIPE_ID_ARG])

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    fun toggleIngredient(index: Int) {
        _uiState.update {
            val checked = if (index in it.checkedIngredients) it.checkedIngredients - index
            else it.checkedIngredients + index
            it.copy(checkedIngredients = checked)
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val recipe = repository.getRecipe(recipeId)
                _uiState.update { it.copy(recipe = recipe, isLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.toUserMessage()) }
            }
        }
    }

    companion object {
        const val RECIPE_ID_ARG = "recipeId"

        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as RecipeExplorerApplication
                RecipeDetailViewModel(createSavedStateHandle(), app.repository)
            }
        }
    }
}
