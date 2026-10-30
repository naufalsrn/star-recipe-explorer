@file:OptIn(ExperimentalLayoutApi::class)

package com.naufal.recipeexplorer.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naufal.recipeexplorer.data.model.Recipe
import com.naufal.recipeexplorer.ui.components.DifficultyLabel
import com.naufal.recipeexplorer.ui.components.ErrorState
import com.naufal.recipeexplorer.ui.components.NetworkImage
import com.naufal.recipeexplorer.ui.theme.BrandGreen
import com.naufal.recipeexplorer.ui.theme.BrandOrange
import com.naufal.recipeexplorer.ui.theme.Cream
import com.naufal.recipeexplorer.ui.theme.SageGreen

@Composable
fun RecipeDetailScreen(
    onBack: () -> Unit,
    viewModel: RecipeDetailViewModel = viewModel(factory = RecipeDetailViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val recipe = state.recipe

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Cream, SageGreen))),
    ) {
        when {
            recipe != null -> RecipeDetailContent(
                recipe = recipe,
                checkedIngredients = state.checkedIngredients,
                onToggleIngredient = viewModel::toggleIngredient,
            )
            state.error != null -> ErrorState(message = state.error!!, onRetry = viewModel::retry)
            else -> CircularProgressIndicator(color = BrandGreen)
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(8.dp)
                .background(Color.White, CircleShape),
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BrandGreen)
        }
    }
}

@Composable
private fun RecipeDetailContent(
    recipe: Recipe,
    checkedIngredients: Set<Int>,
    onToggleIngredient: (Int) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 32.dp),
        modifier = Modifier.fillMaxSize().navigationBarsPadding(),
    ) {
        item {
            NetworkImage(
                url = recipe.image,
                contentDescription = recipe.name,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            )
        }
        item { Header(recipe) }
        item { Stats(recipe) }

        item {
            SectionTitle(
                title = "Ingredients",
                subtitle = "${checkedIngredients.size} of ${recipe.ingredients.size} ready · tap to tick off",
            )
        }
        itemsIndexed(recipe.ingredients) { index, ingredient ->
            val checked = index in checkedIngredients
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleIngredient(index) }
                    .padding(horizontal = 4.dp),
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { onToggleIngredient(index) },
                    colors = CheckboxDefaults.colors(checkedColor = BrandGreen),
                )
                Text(
                    text = ingredient,
                    fontSize = 15.sp,
                    textDecoration = if (checked) TextDecoration.LineThrough else null,
                    color = if (checked) Color.DarkGray else Color.Black,
                )
            }
        }

        item { SectionTitle(title = "Instructions", subtitle = "${recipe.instructions.size} steps") }
        itemsIndexed(recipe.instructions) { index, step ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                StepNumber(index + 1)
                Text(text = step, fontSize = 15.sp, color = Color.Black, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Header(recipe: Recipe) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-16).dp)
            .background(BrandGreen, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(16.dp),
    ) {
        Text(recipe.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
            Text(
                text = " %.1f  ·  %d reviews".format(recipe.rating, recipe.reviewCount),
                color = Color.White,
                fontSize = 14.sp,
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DifficultyLabel(recipe.difficulty)
            if (recipe.cuisine.isNotBlank()) Pill(recipe.cuisine)
            recipe.mealType.forEach { Pill(it) }
        }
    }
}

@Composable
private fun Stats(recipe: Recipe) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    ) {
        StatTile("Prep", "${recipe.prepTimeMinutes} min", Modifier.weight(1f))
        StatTile("Cook", "${recipe.cookTimeMinutes} min", Modifier.weight(1f))
        StatTile("Serves", "${recipe.servings}", Modifier.weight(1f))
        StatTile("Calories", "${recipe.caloriesPerServing}", Modifier.weight(1f))
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
        ) {
            Text(value, color = BrandGreen, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(label, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)) {
        Text(title, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        if (subtitle != null) {
            Text(subtitle, color = Color.DarkGray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun StepNumber(number: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(28.dp).background(BrandOrange, CircleShape),
    ) {
        Text("$number", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Pill(text: String) {
    Surface(color = Color.White.copy(alpha = 0.9f), contentColor = BrandGreen, shape = RoundedCornerShape(50)) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}
