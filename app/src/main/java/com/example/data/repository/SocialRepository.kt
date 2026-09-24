package com.example.data.repository

import com.example.data.local.CommentDao
import com.example.data.local.PostDao
import com.example.data.model.CommentEntity
import com.example.data.model.NotificationItem
import com.example.data.model.PostEntity
import com.example.data.model.Story
import com.example.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SocialRepository(
    private val postDao: PostDao,
    private val commentDao: CommentDao
) {
    val allPosts: Flow<List<PostEntity>> = postDao.getAllPosts()
    val savedPosts: Flow<List<PostEntity>> = postDao.getSavedPosts()
    val allComments: Flow<List<CommentEntity>> = commentDao.getAllComments()

    fun getCommentsForPost(postId: Long): Flow<List<CommentEntity>> {
        return commentDao.getCommentsForPost(postId)
    }

    suspend fun toggleLike(post: PostEntity) = withContext(Dispatchers.IO) {
        val newLiked = !post.isLiked
        val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
        postDao.updateLike(post.id, newLiked, newCount)
    }

    suspend fun toggleSave(post: PostEntity) = withContext(Dispatchers.IO) {
        postDao.updateSaved(post.id, !post.isSaved)
    }

    suspend fun addComment(
        postId: Long,
        authorUsername: String,
        authorAvatar: String,
        text: String
    ) = withContext(Dispatchers.IO) {
        if (text.isNotBlank()) {
            val comment = CommentEntity(
                postId = postId,
                authorUsername = authorUsername,
                authorAvatar = authorAvatar,
                text = text.trim(),
                timestamp = System.currentTimeMillis()
            )
            commentDao.insertComment(comment)
        }
    }

    suspend fun createPost(
        authorUsername: String = "my_profile",
        authorAvatar: String = "avatar_profile",
        imageUrl: String,
        caption: String,
        location: String = ""
    ): Long = withContext(Dispatchers.IO) {
        val post = PostEntity(
            authorUsername = authorUsername,
            authorAvatar = authorAvatar,
            imageUrl = imageUrl,
            caption = caption,
            location = location,
            likesCount = 0,
            isLiked = false,
            isSaved = false,
            timestamp = System.currentTimeMillis()
        )
        postDao.insertPost(post)
    }

    suspend fun deletePost(postId: Long) = withContext(Dispatchers.IO) {
        postDao.deletePost(postId)
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val currentPosts = postDao.getAllPosts().first()
        if (currentPosts.isEmpty()) {
            val defaultPosts = listOf(
                PostEntity(
                    id = 1,
                    authorUsername = "my_profile",
                    authorAvatar = "https://i.pravatar.cc/100?img=12",
                    location = "San Francisco, CA",
                    imageUrl = "https://picsum.photos/600/600?random=10",
                    caption = "Welcome to MySocial! ❤️ Enjoying the sunset breeze and sharing moments with friends.",
                    likesCount = 0,
                    isLiked = false,
                    isSaved = false,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 30
                ),
                PostEntity(
                    id = 2,
                    authorUsername = "priya",
                    authorAvatar = "https://i.pravatar.cc/100?img=9",
                    location = "Kyoto, Japan",
                    imageUrl = "https://picsum.photos/600/600?random=24",
                    caption = "Morning matcha and serene garden walks 🍵🌸 Pure tranquility.",
                    likesCount = 42,
                    isLiked = true,
                    isSaved = true,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 180
                ),
                PostEntity(
                    id = 3,
                    authorUsername = "rahul",
                    authorAvatar = "https://i.pravatar.cc/100?img=5",
                    location = "Alps, Switzerland",
                    imageUrl = "https://picsum.photos/600/600?random=31",
                    caption = "Above the clouds! 🏔️ Early summit hike was totally worth the steep climb.",
                    likesCount = 128,
                    isLiked = false,
                    isSaved = false,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 420
                ),
                PostEntity(
                    id = 4,
                    authorUsername = "junaid",
                    authorAvatar = "https://i.pravatar.cc/100?img=3",
                    location = "Artisan Bakery",
                    imageUrl = "https://picsum.photos/600/600?random=45",
                    caption = "Freshly baked sourdough and golden croissants fresh out of the oven! 🥐🥖",
                    likesCount = 89,
                    isLiked = false,
                    isSaved = false,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 960
                )
            )
            postDao.insertPosts(defaultPosts)

            val defaultComments = listOf(
                CommentEntity(
                    postId = 1,
                    authorUsername = "priya",
                    authorAvatar = "https://i.pravatar.cc/100?img=9",
                    text = "Welcome Alex! Loving this new app design ✨",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 20
                ),
                CommentEntity(
                    postId = 1,
                    authorUsername = "rahul",
                    authorAvatar = "https://i.pravatar.cc/100?img=5",
                    text = "Awesome shot! Can't wait to see more posts 📸",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 10
                ),
                CommentEntity(
                    postId = 2,
                    authorUsername = "my_profile",
                    authorAvatar = "https://i.pravatar.cc/100?img=12",
                    text = "Kyoto looks so magical! Enjoy your trip! 🌸",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 120
                )
            )
            commentDao.insertComments(defaultComments)
        }
    }

    fun getInitialStories(): List<Story> {
        return listOf(
            Story(
                id = "user_story",
                username = "Your Story",
                avatarUrl = "https://i.pravatar.cc/100?img=12",
                storyImageUrl = "https://picsum.photos/800/1200?random=101",
                caption = "Sunset vibes from my balcony today 🌇",
                isSeen = false,
                isUserStory = true
            ),
            Story(
                id = "story_rahul",
                username = "Rahul",
                avatarUrl = "https://i.pravatar.cc/100?img=5",
                storyImageUrl = "https://picsum.photos/800/1200?random=102",
                caption = "Alpine mountain trails 🏔️",
                isSeen = false
            ),
            Story(
                id = "story_priya",
                username = "Priya",
                avatarUrl = "https://i.pravatar.cc/100?img=9",
                storyImageUrl = "https://picsum.photos/800/1200?random=103",
                caption = "Exploring bamboo groves 🎋",
                isSeen = false
            ),
            Story(
                id = "story_junaid",
                username = "Junaid",
                avatarUrl = "https://i.pravatar.cc/100?img=3",
                storyImageUrl = "https://picsum.photos/800/1200?random=104",
                caption = "Bakery tasting session! 🧁",
                isSeen = false
            ),
            Story(
                id = "story_hima",
                username = "Hima",
                avatarUrl = "https://i.pravatar.cc/100?img=7",
                storyImageUrl = "https://picsum.photos/800/1200?random=105",
                caption = "Weekend ceramics workshop 🏺",
                isSeen = false
            )
        )
    }

    fun getInitialNotifications(): List<NotificationItem> {
        return listOf(
            NotificationItem(
                id = "notif_1",
                username = "priya",
                avatarUrl = "https://i.pravatar.cc/100?img=9",
                actionText = "liked your post: \"Welcome to MySocial! ❤️\"",
                timeAgo = "15m",
                postThumbnailUrl = "https://picsum.photos/200/200?random=10"
            ),
            NotificationItem(
                id = "notif_2",
                username = "rahul",
                avatarUrl = "https://i.pravatar.cc/100?img=5",
                actionText = "started following you",
                timeAgo = "1h",
                isFollowAction = true,
                isFollowing = true
            ),
            NotificationItem(
                id = "notif_3",
                username = "junaid",
                avatarUrl = "https://i.pravatar.cc/100?img=3",
                actionText = "commented: \"Awesome shot! Can't wait to see more\"",
                timeAgo = "3h",
                postThumbnailUrl = "https://picsum.photos/200/200?random=10"
            ),
            NotificationItem(
                id = "notif_4",
                username = "hima",
                avatarUrl = "https://i.pravatar.cc/100?img=7",
                actionText = "started following you",
                timeAgo = "1d",
                isFollowAction = true,
                isFollowing = false
            )
        )
    }

    private var refreshCounter = 0

    private val refreshPool = listOf(
        Pair(
            PostEntity(
                authorUsername = "elena_travels",
                authorAvatar = "https://i.pravatar.cc/100?img=28",
                location = "Santorini, Greece",
                imageUrl = "https://picsum.photos/600/600?random=51",
                caption = "Sunset in Oia with the Aegean breeze 🌅 Greek summer dreams.",
                likesCount = 154,
                isLiked = false,
                isSaved = false,
                timestamp = 0L
            ),
            "Breathtaking view Elena! Take me there 🇬🇷"
        ),
        Pair(
            PostEntity(
                authorUsername = "marcus_design",
                authorAvatar = "https://i.pravatar.cc/100?img=33",
                location = "Studio Nord",
                imageUrl = "https://picsum.photos/600/600?random=52",
                caption = "Minimalist workspace setup complete! Natural light makes all the difference 🖥️🌿",
                likesCount = 96,
                isLiked = false,
                isSaved = false,
                timestamp = 0L
            ),
            "What monitor arm is that? Super clean setup!"
        ),
        Pair(
            PostEntity(
                authorUsername = "chef_antonio",
                authorAvatar = "https://i.pravatar.cc/100?img=60",
                location = "Little Italy",
                imageUrl = "https://picsum.photos/600/600?random=54",
                caption = "Handmade tagliatelle with slow-cooked ragù alla Bolognese 🍝 Buon appetito!",
                likesCount = 178,
                isLiked = false,
                isSaved = false,
                timestamp = 0L
            ),
            "Recipe please! Looks mouthwatering 🤤"
        ),
        Pair(
            PostEntity(
                authorUsername = "maya_lens",
                authorAvatar = "https://i.pravatar.cc/100?img=44",
                location = "Kyoto Bamboo Grove",
                imageUrl = "https://picsum.photos/600/600?random=53",
                caption = "Whispering bamboo forest at sunrise. Nature's quiet symphony 🎋✨",
                likesCount = 210,
                isLiked = false,
                isSaved = false,
                timestamp = 0L
            ),
            "The lighting in this capture is stunning 👏"
        ),
        Pair(
            PostEntity(
                authorUsername = "clara_adventures",
                authorAvatar = "https://i.pravatar.cc/100?img=47",
                location = "Lake Louise, Banff",
                imageUrl = "https://picsum.photos/600/600?random=55",
                caption = "Turquoise waters of Lake Louise. Cold morning but unforgettable view! 🛶🏔️",
                likesCount = 312,
                isLiked = false,
                isSaved = false,
                timestamp = 0L
            ),
            "Canadian Rockies never disappoint! 🇨🇦"
        )
    )

    suspend fun fetchNewPostsOnRefresh(): Int = withContext(Dispatchers.IO) {
        val poolItem = refreshPool[refreshCounter % refreshPool.size]
        refreshCounter++

        val newTimestamp = System.currentTimeMillis()
        val postToInsert = poolItem.first.copy(
            id = 0, // Auto-generate new primary key
            timestamp = newTimestamp
        )
        val insertedId = postDao.insertPost(postToInsert)

        // Add a sample reaction comment for freshness
        val comment = CommentEntity(
            postId = insertedId,
            authorUsername = "rahul",
            authorAvatar = "https://i.pravatar.cc/100?img=5",
            text = poolItem.second,
            timestamp = newTimestamp - 1000 * 60 * 2
        )
        commentDao.insertComment(comment)
        1
    }
}
