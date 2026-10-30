package com.naufal.recipeexplorer

import android.app.Application
import com.naufal.recipeexplorer.data.RecipeRepository
import com.naufal.recipeexplorer.data.remote.RecipeApi

class RecipeExplorerApplication : Application() {
    val repository by lazy { RecipeRepository(RecipeApi()) }
}
