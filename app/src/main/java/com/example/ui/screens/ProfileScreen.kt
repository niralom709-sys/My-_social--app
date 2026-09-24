package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CommentEntity
import com.example.data.model.PostEntity
import com.example.data.model.UserProfile
import com.example.ui.components.SocialImage
import com.example.ui.theme.BorderLight
import com.example.ui.theme.SocialBlue

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    userPosts: List<PostEntity>,
    savedPosts: List<PostEntity>,
    selectedTab: Int = 0,
    onTabSelected: (Int) -> Unit = {},
    onFollowToggle: () -> Unit,
    onUpdateProfile: (name: String, bio: String) -> Unit,
    onLikeToggle: (PostEntity) -> Unit = {},
    onSaveToggle: (PostEntity) -> Unit = {},
    onAddComment: (postId: Long, text: String) -> Unit = { _, _ -> },
    allComments: List<CommentEntity> = emptyList(),
    modifier: Modifier = Modifier
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var selectedPostIdForDetail by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("profile_screen")
    ) {
        // Profile Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = userProfile.username,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("profile_username")
            )

            Row {
                IconButton(onClick = { showEditProfileDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Edit Profile Settings",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        HorizontalDivider(color = BorderLight, thickness = 0.8.dp)

        // Profile Details (matching HTML: 100px profile-img + my_profile + Welcome to my profile 👋)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile image (100dp matching HTML's 100px)
            SocialImage(
                imageUrl = if (userProfile.avatarUrl.isNotBlank()) userProfile.avatarUrl else "https://i.pravatar.cc/200?img=12",
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, BorderLight, CircleShape)
                    .testTag("profile_image"),
                isAvatar = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = userProfile.username,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = userProfile.fullName,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = userProfile.bio,
                fontSize = 13.5.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("profile_bio")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats row (Posts, Followers, Following)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem(label = "Posts", value = userPosts.size.toString())
                ProfileStatItem(label = "Followers", value = userProfile.followersCount.toString())
                ProfileStatItem(label = "Following", value = userProfile.followingCount.toString())
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Follow button (exact HTML replica with followMe toggle) + Edit profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (userProfile.isFollowing) {
                    OutlinedButton(
                        onClick = onFollowToggle,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("follow_me_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Following",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    Button(
                        onClick = onFollowToggle,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("follow_me_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SocialBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Follow",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = { showEditProfileDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("edit_profile_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Edit profile",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Tabs: Posts Grid vs Saved Posts
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = SocialBlue
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = "My Posts",
                        tint = if (selectedTab == 0) SocialBlue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.testTag("profile_tab_posts")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                icon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Saved Posts",
                            tint = if (selectedTab == 1) SocialBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (savedPosts.isNotEmpty()) {
                            Text(
                                text = "(${savedPosts.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 1) SocialBlue else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                modifier = Modifier.testTag("profile_tab_saved")
            )
        }

        // Posts Grid
        val currentList = if (selectedTab == 0) userPosts else savedPosts

        if (currentList.isEmpty()) {
            if (selectedTab == 1) {
                // Informative Empty State for Saved Posts
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Save Posts to Revisit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the bookmark icon 🔖 on any post in your feed to save it here. You can easily revisit all your bookmarked posts anytime.",
                        textAlign = TextAlign.Center,
                        fontSize = 13.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 19.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No posts uploaded yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("profile_posts_grid"),
                contentPadding = PaddingValues(1.dp),
                horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                verticalArrangement = Arrangement.spacedBy(1.5.dp)
            ) {
                items(currentList, key = { it.id }) { post ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { selectedPostIdForDetail = post.id }
                            .testTag("post_grid_item_${post.id}")
                    ) {
                        SocialImage(
                            imageUrl = post.imageUrl,
                            contentDescription = "Post photo",
                            modifier = Modifier.fillMaxSize()
                        )
                        // If viewing saved tab, show small bookmark badge overlay
                        if (selectedTab == 1) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Bookmark,
                                    contentDescription = "Saved post",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(userProfile.fullName) }
        var editBio by remember { mutableStateOf(userProfile.bio) }

        Dialog(onDismissRequest = { showEditProfileDialog = false }) {
            androidx.compose.material3.Card(
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Edit Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        androidx.compose.material3.TextButton(
                            onClick = { showEditProfileDialog = false }
                        ) {
                            Text("Cancel")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                onUpdateProfile(editName, editBio)
                                showEditProfileDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SocialBlue)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }

    // Post Detail Modal Dialog for inspecting clicked saved or uploaded posts
    selectedPostIdForDetail?.let { postId ->
        val post = (savedPosts + userPosts).firstOrNull { it.id == postId }
        if (post != null) {
            val postComments = allComments.filter { it.postId == post.id }
            var detailCommentText by remember { mutableStateOf("") }

            Dialog(onDismissRequest = { selectedPostIdForDetail = null }) {
                androidx.compose.material3.Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 24.dp)
                        .testTag("post_detail_dialog")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                SocialImage(
                                    imageUrl = post.authorAvatar,
                                    contentDescription = "${post.authorUsername}'s avatar",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape),
                                    isAvatar = true
                                )
                                Column {
                                    Text(
                                        text = post.authorUsername,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (post.location.isNotBlank()) {
                                        Text(
                                            text = post.location,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = { selectedPostIdForDetail = null },
                                modifier = Modifier.testTag("close_detail_dialog")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Post Image
                        SocialImage(
                            imageUrl = post.imageUrl,
                            contentDescription = "Post photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        )

                        // Action Buttons: Like and Save/Bookmark
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                IconButton(onClick = { onLikeToggle(post) }) {
                                    Icon(
                                        imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Like",
                                        tint = if (post.isLiked) com.example.ui.theme.SocialHeartRed else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Text(
                                    text = "${post.likesCount} likes",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Bookmark Toggle inside detail
                            IconButton(
                                onClick = { onSaveToggle(post) },
                                modifier = Modifier.testTag("detail_save_toggle")
                            ) {
                                Icon(
                                    imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = if (post.isSaved) "Remove from saved bookmarks" else "Save to bookmarks",
                                    tint = if (post.isSaved) SocialBlue else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // Caption
                        if (post.caption.isNotBlank()) {
                            Text(
                                text = "${post.authorUsername} ${post.caption}",
                                fontSize = 13.5.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = BorderLight
                        )

                        // Comments Section
                        Text(
                            text = "Comments (${postComments.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        postComments.forEach { comment ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = comment.authorUsername,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                                Text(
                                    text = comment.text,
                                    fontSize = 12.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Add Comment Input
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = detailCommentText,
                                onValueChange = { detailCommentText = it },
                                placeholder = { Text("Add a comment...", fontSize = 13.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (detailCommentText.isNotBlank()) {
                                        onAddComment(post.id, detailCommentText)
                                        detailCommentText = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SocialBlue),
                                enabled = detailCommentText.isNotBlank()
                            ) {
                                Text("Post", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatItem(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 12.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
