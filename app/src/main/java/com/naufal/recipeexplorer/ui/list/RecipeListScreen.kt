package com.naufal.recipeexplorer.ui.list

import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naufal.recipeexplorer.data.model.Recipe
import com.naufal.recipeexplorer.ui.components.ErrorState
import com.naufal.recipeexplorer.ui.components.NetworkImage
import com.naufal.recipeexplorer.ui.theme.BrandGreen
import com.naufal.recipeexplorer.ui.theme.Cream
import com.naufal.recipeexplorer.ui.theme.SageGreen

@Composable
fun RecipeListScreen(viewModel: RecipeListViewModel = viewModel(factory = RecipeListViewModel.Factory)) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    RecipeListContent(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onRetry = viewModel::retry,
    )
}

@Composable
private fun RecipeListContent(
    state: RecipeListUiState,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
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
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        )

        // A new search is loading while the previous results are still shown.
        if (state.isLoading && state.recipes.isNotEmpty()) {
            LinearProgressIndicator(color = BrandGreen, modifier = Modifier.fillMaxWidth())
        }

        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            when {
                state.recipes.isNotEmpty() -> RecipeGrid(state.recipes)
                state.isLoading -> CircularProgressIndicator(color = BrandGreen)
                state.error != null -> ErrorState(message = state.error, onRetry = onRetry)
                else -> Text(
                    text = "No recipes found for \"${state.query.trim()}\".",
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp),
                )
            }
        }
    }
}

@Composable
private fun RecipeGrid(recipes: List<Recipe>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipeCard(recipe)
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
private fun RecipeCard(recipe: Recipe) {
    Card(
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

@Composable
private fun DifficultyLabel(difficulty: String, modifier: Modifier = Modifier) {
    val color = when (difficulty.lowercase()) {
        "easy" -> Color(0xFF2E7D32)
        "medium" -> Color(0xFFF27A1A)
        else -> Color(0xFFC62828)
    }
    Surface(color = color, contentColor = Color.White, shape = RoundedCornerShape(50), modifier = modifier) {
        Text(
            text = difficulty,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}
