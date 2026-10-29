package com.naufal.recipeexplorer

import android.app.Application
import com.naufal.recipeexplorer.data.RecipeRepository
import com.naufal.recipeexplorer.data.remote.RecipeApi

/** Holds app-wide objects. There is one repository, shared by every screen. */
class RecipeExplorerApplication : Application() {
    val repository by lazy { RecipeRepository(RecipeApi()) }
}
