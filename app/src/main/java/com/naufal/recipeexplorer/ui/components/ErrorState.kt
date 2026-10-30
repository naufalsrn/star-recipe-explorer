package com.naufal.recipeexplorer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.naufal.recipeexplorer.data.remote.HttpException
import com.naufal.recipeexplorer.ui.theme.BrandGreen
import org.json.JSONException
import java.io.IOException
import java.net.SocketTimeoutException

@Composable
fun ErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp),
        )
        Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Text("Retry", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

fun Throwable.toUserMessage(): String = when (this) {
    is SocketTimeoutException -> "The server took too long to respond. Please try again."
    is IOException -> "No internet connection. Check your connection and try again."
    is HttpException -> "The server returned an error ($code). Please try again later."
    is JSONException -> "Received unexpected data from the server."
    else -> "Something went wrong. Please try again."
}
