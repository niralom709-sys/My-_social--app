package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CreatePostDialog
import com.example.ui.components.DirectMessagesDialog
import com.example.ui.components.SocialImage
import com.example.ui.components.StoryViewerDialog
import com.example.ui.screens.ActivityScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.BorderLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SocialBlue
import com.example.ui.viewmodel.ScreenTab
import com.example.ui.viewmodel.SocialViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MySocialApp()
            }
        }
    }
}

@Composable
fun MySocialApp(
    viewModel: SocialViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val allPosts by viewModel.allPosts.collectAsStateWithLifecycle()
    val savedPosts by viewModel.savedPosts.collectAsStateWithLifecycle()
    val allComments by viewModel.allComments.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val activeStory by viewModel.activeStory.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isDirectMessagesOpen by viewModel.isDirectMessagesOpen.collectAsStateWithLifecycle()
    val showCreatePostSheet by viewModel.showCreatePostSheet.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val profileSelectedTab by viewModel.profileSelectedTab.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                // Home Tab
                NavigationBarItem(
                    selected = currentTab == ScreenTab.HOME,
                    onClick = { viewModel.selectTab(ScreenTab.HOME) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.testTag("nav_home_button")
                )

                // Explore Tab
                NavigationBarItem(
                    selected = currentTab == ScreenTab.EXPLORE,
                    onClick = { viewModel.selectTab(ScreenTab.EXPLORE) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Explore"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.testTag("nav_explore_button")
                )

                // Create Post Tab
                NavigationBarItem(
                    selected = false,
                    onClick = { viewModel.selectTab(ScreenTab.CREATE) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.AddBox,
                            contentDescription = "Create Post",
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.testTag("nav_create_button")
                )

                // Activity / Likes Tab
                NavigationBarItem(
                    selected = currentTab == ScreenTab.ACTIVITY,
                    onClick = { viewModel.selectTab(ScreenTab.ACTIVITY) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.ACTIVITY) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Activity"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.testTag("nav_activity_button")
                )

                // Profile Tab
                NavigationBarItem(
                    selected = currentTab == ScreenTab.PROFILE,
                    onClick = { viewModel.selectTab(ScreenTab.PROFILE) },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (currentTab == ScreenTab.PROFILE) 2.dp else 1.dp,
                                    color = if (currentTab == ScreenTab.PROFILE) MaterialTheme.colorScheme.onSurface else BorderLight,
                                    shape = CircleShape
                                )
                        ) {
                            SocialImage(
                                imageUrl = userProfile.avatarUrl.ifEmpty { "https://i.pravatar.cc/100?img=12" },
                                contentDescription = "Profile",
                                modifier = Modifier.fillMaxSize(),
                                isAvatar = true
                            )
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.testTag("nav_profile_button")
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.padding(innerPadding),
            label = "tab_switch"
        ) { tab ->
            when (tab) {
                ScreenTab.HOME -> {
                    HomeScreen(
                        posts = allPosts,
                        comments = allComments,
                        stories = stories,
                        savedPostsCount = savedPosts.size,
                        isRefreshing = isRefreshing,
                        onRefresh = { viewModel.refreshFeed() },
                        onStoryClick = { story -> viewModel.openStory(story) },
                        onLikeToggle = { post -> viewModel.toggleLike(post) },
                        onSaveToggle = { post -> viewModel.toggleSave(post) },
                        onAddComment = { postId, text -> viewModel.addComment(postId, text) },
                        onActivityClick = { viewModel.selectTab(ScreenTab.ACTIVITY) },
                        onMessagesClick = { viewModel.setDirectMessagesOpen(true) },
                        onBookmarksClick = { viewModel.openSavedPosts() }
                    )
                }
                ScreenTab.EXPLORE -> {
                    ExploreScreen(
                        posts = allPosts,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) }
                    )
                }
                ScreenTab.ACTIVITY -> {
                    ActivityScreen(
                        notifications = notifications,
                        onFollowToggle = { notifId -> viewModel.toggleNotificationFollow(notifId) }
                    )
                }
                ScreenTab.PROFILE -> {
                    val userPosts = allPosts.filter { it.authorUsername == userProfile.username }
                    ProfileScreen(
                        userProfile = userProfile,
                        userPosts = userPosts,
                        savedPosts = savedPosts,
                        selectedTab = profileSelectedTab,
                        onTabSelected = { viewModel.setProfileSelectedTab(it) },
                        onFollowToggle = { viewModel.toggleFollowProfile() },
                        onUpdateProfile = { name, bio -> viewModel.updateProfileBio(bio, name) },
                        onLikeToggle = { post -> viewModel.toggleLike(post) },
                        onSaveToggle = { post -> viewModel.toggleSave(post) },
                        onAddComment = { postId, text -> viewModel.addComment(postId, text) },
                        allComments = allComments
                    )
                }
                ScreenTab.CREATE -> {
                    // Create is opened as a bottom sheet / dialog, fallback to Home
                    HomeScreen(
                        posts = allPosts,
                        comments = allComments,
                        stories = stories,
                        savedPostsCount = savedPosts.size,
                        isRefreshing = isRefreshing,
                        onRefresh = { viewModel.refreshFeed() },
                        onStoryClick = { story -> viewModel.openStory(story) },
                        onLikeToggle = { post -> viewModel.toggleLike(post) },
                        onSaveToggle = { post -> viewModel.toggleSave(post) },
                        onAddComment = { postId, text -> viewModel.addComment(postId, text) },
                        onActivityClick = { viewModel.selectTab(ScreenTab.ACTIVITY) },
                        onMessagesClick = { viewModel.setDirectMessagesOpen(true) },
                        onBookmarksClick = { viewModel.openSavedPosts() }
                    )
                }
            }
        }
    }

    // Story Viewer Modal
    activeStory?.let { story ->
        StoryViewerDialog(
            story = story,
            onDismiss = { viewModel.closeStory() }
        )
    }

    // Direct Messages Modal
    if (isDirectMessagesOpen) {
        DirectMessagesDialog(
            onDismiss = { viewModel.setDirectMessagesOpen(false) }
        )
    }

    // Create Post Sheet / Dialog
    if (showCreatePostSheet) {
        CreatePostDialog(
            onDismiss = { viewModel.setCreatePostSheetVisible(false) },
            onSubmitPost = { caption, imageUrl, location ->
                viewModel.createNewPost(caption, imageUrl, location)
            }
        )
    }
}

// Kept for screenshot test backward compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
