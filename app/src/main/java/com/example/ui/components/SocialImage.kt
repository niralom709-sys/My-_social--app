package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R

@Composable
fun SocialImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    isAvatar: Boolean = false
) {
    val context = LocalContext.current

    // If imageUrl points to one of our bundled drawable identifiers
    when (imageUrl) {
        "avatar_profile" -> {
            Image(
                painter = painterResource(id = R.drawable.avatar_profile),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        "post_sample_1" -> {
            Image(
                painter = painterResource(id = R.drawable.post_sample_1),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        "post_sample_2" -> {
            Image(
                painter = painterResource(id = R.drawable.post_sample_2),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        else -> {
            val fallbackRes = if (isAvatar) R.drawable.avatar_profile else R.drawable.post_sample_1

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(if (imageUrl.isBlank()) fallbackRes else imageUrl)
                    .crossfade(true)
                    .error(fallbackRes)
                    .placeholder(fallbackRes)
                    .build(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
    }
}
