package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommentEntity
import com.example.data.model.PostEntity
import com.example.data.model.Story
import com.example.ui.components.PostCard
import com.example.ui.components.StoriesRow
import com.example.ui.components.TopHeader
import com.example.ui.theme.BorderLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    posts: List<PostEntity>,
    comments: List<CommentEntity>,
    stories: List<Story>,
    savedPostsCount: Int = 0,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onStoryClick: (Story) -> Unit,
    onLikeToggle: (PostEntity) -> Unit,
    onSaveToggle: (PostEntity) -> Unit,
    onAddComment: (postId: Long, text: String) -> Unit,
    onActivityClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onBookmarksClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen")
    ) {
        TopHeader(
            onActivityClick = onActivityClick,
            onMessagesClick = onMessagesClick,
            onBookmarksClick = onBookmarksClick,
            savedCount = savedPostsCount
        )

        HorizontalDivider(color = BorderLight, thickness = 0.8.dp)

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_pull_to_refresh_box")
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("feed_lazy_column")
            ) {
            // Stories Tray
            item {
                StoriesRow(
                    stories = stories,
                    onStoryClick = onStoryClick
                )
                HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
            }

            // Empty state if no posts
            if (posts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No posts yet. Tap + to share your first post!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                items(posts, key = { it.id }) { post ->
                    val postComments = comments.filter { it.postId == post.id }
                    PostCard(
                        post = post,
                        comments = postComments,
                        onLikeToggle = { onLikeToggle(post) },
                        onSaveToggle = {
                            val willBeSaved = !post.isSaved
                            onSaveToggle(post)
                            val message = if (willBeSaved) "Saved to your bookmarks 🔖" else "Removed from bookmarks"
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        },
                        onAddComment = { text -> onAddComment(post.id, text) },
                        onShareClick = {
                            Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}
}
