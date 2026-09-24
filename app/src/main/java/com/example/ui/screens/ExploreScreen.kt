package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PostEntity
import com.example.ui.components.SocialImage
import com.example.ui.theme.BorderLight
import com.example.ui.theme.SocialBlue

@Composable
fun ExploreScreen(
    posts: List<PostEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = remember {
        listOf("Trending", "Photography", "Travel", "Coffee", "Architecture", "Art", "Portraits")
    }
    var selectedCategory by remember { mutableStateOf("Trending") }
    var previewPost by remember { mutableStateOf<PostEntity?>(null) }

    val explorePhotos = remember {
        listOf(
            "post_sample_1",
            "post_sample_2",
            "https://picsum.photos/600/600?random=11",
            "https://picsum.photos/600/600?random=12",
            "https://picsum.photos/600/600?random=13",
            "https://picsum.photos/600/600?random=14",
            "https://picsum.photos/600/600?random=15",
            "https://picsum.photos/600/600?random=16",
            "https://picsum.photos/600/600?random=17",
            "https://picsum.photos/600/600?random=18",
            "https://picsum.photos/600/600?random=19",
            "https://picsum.photos/600/600?random=20"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("explore_screen")
    ) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search accounts, tags, or places...", fontSize = 13.5.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("explore_search_input"),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SocialBlue,
                unfocusedBorderColor = BorderLight,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Category Pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 12.5.sp) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SocialBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Staggered / 3-column Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .testTag("explore_grid"),
            contentPadding = PaddingValues(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.5.dp),
            verticalArrangement = Arrangement.spacedBy(1.5.dp)
        ) {
            val displayList = if (searchQuery.isNotBlank()) {
                val matches = posts.filter {
                    it.caption.contains(searchQuery, ignoreCase = true) ||
                    it.authorUsername.contains(searchQuery, ignoreCase = true) ||
                    it.location.contains(searchQuery, ignoreCase = true)
                }.map { it.imageUrl }
                if (matches.isNotEmpty()) matches else explorePhotos.take(6)
            } else {
                explorePhotos
            }

            items(displayList) { imgUrl ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable {
                            val matched = posts.find { it.imageUrl == imgUrl }
                                ?: PostEntity(
                                    authorUsername = "explore_creator",
                                    authorAvatar = "https://i.pravatar.cc/100?img=16",
                                    imageUrl = imgUrl,
                                    caption = "Explore curated selection for #$selectedCategory",
                                    likesCount = 84,
                                    location = "Global Discovery"
                                )
                            previewPost = matched
                        }
                ) {
                    SocialImage(
                        imageUrl = imgUrl,
                        contentDescription = "Explore post",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Preview Dialog
        previewPost?.let { post ->
            Dialog(onDismissRequest = { previewPost = null }) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SocialImage(
                                imageUrl = post.authorAvatar,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                isAvatar = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = post.authorUsername,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        SocialImage(
                            imageUrl = post.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            contentScale = ContentScale.Crop
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFED4956),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${post.likesCount} likes",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }

                        if (post.caption.isNotBlank()) {
                            Text(
                                text = post.caption,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
