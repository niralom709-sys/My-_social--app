package com.example.data.model

data class Story(
    val id: String,
    val username: String,
    val avatarUrl: String,
    val storyImageUrl: String,
    val caption: String = "",
    val isSeen: Boolean = false,
    val isUserStory: Boolean = false
)

data class UserProfile(
    val username: String = "my_profile",
    val fullName: String = "Alex Rivera",
    val avatarUrl: String = "",
    val bio: String = "✨ Visual storyteller & coffee enthusiast\n📍 Exploring urban aesthetics\n📸 Creating moments that inspire",
    val postsCount: Int = 12,
    val followersCount: Int = 1420,
    val followingCount: Int = 385,
    val isFollowing: Boolean = false
)

data class NotificationItem(
    val id: String,
    val username: String,
    val avatarUrl: String,
    val actionText: String,
    val timeAgo: String,
    val postThumbnailUrl: String? = null,
    val isFollowAction: Boolean = false,
    val isFollowing: Boolean = false
)
