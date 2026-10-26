package com.naufal.recipeexplorer.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naufal.recipeexplorer.R
import com.naufal.recipeexplorer.ui.theme.BrandGreen
import com.naufal.recipeexplorer.ui.theme.Cream
import com.naufal.recipeexplorer.ui.theme.SageGreen

@Composable
fun WelcomeScreen(onEnter: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Cream, SageGreen)))
            .systemBarsPadding()
            .padding(horizontal = 32.dp),
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.dishdiscover_logo),
            contentDescription = "DishDiscover logo",
            modifier = Modifier.width(240.dp),
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Welcome",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Text(
                text = "Discover delicious recipes from around the world, all in one place.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = Color.Black,
            )
        }

        Spacer(modifier = Modifier.weight(1.2f))

        Button(
            onClick = onEnter,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Text("Explore Recipes", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WelcomeScreenPreview() {
    WelcomeScreen(onEnter = {})
}
