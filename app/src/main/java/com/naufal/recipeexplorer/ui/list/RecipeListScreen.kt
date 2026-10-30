package com.naufal.recipeexplorer.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naufal.recipeexplorer.data.model.Recipe
import com.naufal.recipeexplorer.ui.components.DifficultyLabel
import com.naufal.recipeexplorer.ui.components.ErrorState
import com.naufal.recipeexplorer.ui.components.NetworkImage
import com.naufal.recipeexplorer.ui.theme.BrandGreen
import com.naufal.recipeexplorer.ui.theme.Cream
import com.naufal.recipeexplorer.ui.theme.SageGreen

@Composable
fun RecipeListScreen(
    onRecipeClick: (Int) -> Unit,
    viewModel: RecipeListViewModel = viewModel(factory = RecipeListViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    RecipeListContent(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onDifficultySelected = viewModel::onDifficultySelected,
        onRefresh = viewModel::refresh,
        onRetry = viewModel::retry,
        onRecipeClick = onRecipeClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeListContent(
    state: RecipeListUiState,
    onQueryChange: (String) -> Unit,
    onDifficultySelected: (String?) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onRecipeClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Cream, SageGreen)))
            .statusBarsPadding(),
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
            Text("Recipes", fontSize = 13.sp, color = Color.Black)
            Text("All Recipes", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        SearchBar(
            query = state.query,
            onQueryChange = onQueryChange,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 8.dp),
        )

        DifficultyFilter(
            difficulties = state.difficulties,
            selected = state.selectedDifficulty,
            onSelect = onDifficultySelected,
        )

        // A new search is loading while the previous results are still shown.
        if (state.isLoading && state.recipes.isNotEmpty()) {
            LinearProgressIndicator(color = BrandGreen, modifier = Modifier.fillMaxWidth())
        }

        // A refresh failed, but the recipes from before are still shown.
        if (state.error != null && state.recipes.isNotEmpty()) {
            Text(
                text = state.error,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFC62828))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                state.visibleRecipes.isNotEmpty() -> RecipeGrid(state.visibleRecipes, onRecipeClick)
                state.isLoading -> FullScreenMessage { CircularProgressIndicator(color = BrandGreen) }
                state.error != null -> FullScreenMessage { ErrorState(message = state.error, onRetry = onRetry) }
                else -> FullScreenMessage {
                    Text(
                        text = when {
                            state.recipes.isNotEmpty() -> "No ${state.selectedDifficulty} recipes here. Try another difficulty."
                            state.query.isNotBlank() -> "No recipes found for \"${state.query.trim()}\"."
                            else -> "No recipes found."
                        },
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp),
                    )
                }
            }
        }
    }
}

// Scrollable so pull-to-refresh also works when there is no list to pull.
@Composable
private fun FullScreenMessage(content: @Composable () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ) {
        content()
    }
}

@Composable
private fun DifficultyFilter(difficulties: List<String>, selected: String?, onSelect: (String?) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 4.dp),
    ) {
        DifficultyChip("All", selected = selected == null, onClick = { onSelect(null) })
        difficulties.forEach { difficulty ->
            DifficultyChip(
                label = difficulty,
                selected = selected == difficulty,
                // Tapping the selected chip again clears the filter.
                onClick = { onSelect(if (selected == difficulty) null else difficulty) },
            )
        }
    }
}

@Composable
private fun DifficultyChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontWeight = FontWeight.SemiBold) },
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            labelColor = BrandGreen,
            selectedContainerColor = BrandGreen,
            selectedLabelColor = Color.White,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = BrandGreen,
            selectedBorderColor = BrandGreen,
        ),
    )
}

@Composable
private fun RecipeGrid(recipes: List<Recipe>, onRecipeClick: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipeCard(recipe, onClick = { onRecipeClick(recipe.id) })
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val keyboard = LocalSoftwareKeyboardController.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp)),
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
        ) {
            if (query.isEmpty()) {
                Text("Search recipes...", fontSize = 14.sp, fontStyle = FontStyle.Italic, color = Color.Gray)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        IconButton(
            onClick = { keyboard?.hide() },
            modifier = Modifier
                .size(48.dp)
                .background(BrandGreen, RoundedCornerShape(8.dp)),
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
        }
    }
}

@Composable
private fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
            NetworkImage(
                url = recipe.image,
                contentDescription = recipe.name,
                modifier = Modifier.fillMaxSize(),
            )
            DifficultyLabel(
                difficulty = recipe.difficulty,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(BrandGreen, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .padding(10.dp),
        ) {
            Text(
                text = recipe.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = recipe.cuisine,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                maxLines = 1,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = " ${recipe.rating}  ·  ${recipe.totalTimeMinutes} min",
                    color = Color.White,
                    fontSize = 12.sp,
                )
            }
        }
    }
}
