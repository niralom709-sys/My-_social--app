package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CommentEntity
import com.example.data.model.NotificationItem
import com.example.data.model.PostEntity
import com.example.data.model.Story
import com.example.data.model.UserProfile
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    HOME,
    EXPLORE,
    CREATE,
    ACTIVITY,
    PROFILE
}

class SocialViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SocialRepository

    val allPosts: StateFlow<List<PostEntity>>
    val savedPosts: StateFlow<List<PostEntity>>
    val allComments: StateFlow<List<CommentEntity>>

    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _profileSelectedTab = MutableStateFlow(0)
    val profileSelectedTab: StateFlow<Int> = _profileSelectedTab.asStateFlow()

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    private val _activeStory = MutableStateFlow<Story?>(null)
    val activeStory: StateFlow<Story?> = _activeStory.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isDirectMessagesOpen = MutableStateFlow(false)
    val isDirectMessagesOpen: StateFlow<Boolean> = _isDirectMessagesOpen.asStateFlow()

    private val _showCreatePostSheet = MutableStateFlow(false)
    val showCreatePostSheet: StateFlow<Boolean> = _showCreatePostSheet.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = SocialRepository(database.postDao(), database.commentDao())

        allPosts = repository.allPosts
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        savedPosts = repository.savedPosts
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allComments = repository.allComments
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        _stories.value = repository.getInitialStories()
        _notifications.value = repository.getInitialNotifications()

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun selectTab(tab: ScreenTab) {
        if (tab == ScreenTab.CREATE) {
            _showCreatePostSheet.value = true
        } else {
            _currentTab.value = tab
        }
    }

    fun openSavedPosts() {
        _profileSelectedTab.value = 1
        _currentTab.value = ScreenTab.PROFILE
    }

    fun setProfileSelectedTab(tab: Int) {
        _profileSelectedTab.value = tab
    }

    fun setCreatePostSheetVisible(visible: Boolean) {
        _showCreatePostSheet.value = visible
    }

    fun refreshFeed() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            // Realistic delay for smooth refresh animation
            kotlinx.coroutines.delay(1000)
            repository.fetchNewPostsOnRefresh()
            _isRefreshing.value = false
        }
    }

    fun openStory(story: Story) {
        _activeStory.value = story
        // Mark as seen
        _stories.value = _stories.value.map {
            if (it.id == story.id) it.copy(isSeen = true) else it
        }
    }

    fun closeStory() {
        _activeStory.value = null
    }

    fun toggleLike(post: PostEntity) {
        viewModelScope.launch {
            repository.toggleLike(post)
        }
    }

    fun toggleSave(post: PostEntity) {
        viewModelScope.launch {
            repository.toggleSave(post)
        }
    }

    fun addComment(postId: Long, text: String) {
        viewModelScope.launch {
            repository.addComment(
                postId = postId,
                authorUsername = _userProfile.value.username,
                authorAvatar = "https://i.pravatar.cc/100?img=12",
                text = text
            )
        }
    }

    fun createNewPost(caption: String, imageUrl: String, location: String) {
        viewModelScope.launch {
            repository.createPost(
                authorUsername = _userProfile.value.username,
                authorAvatar = "https://i.pravatar.cc/100?img=12",
                imageUrl = imageUrl,
                caption = caption,
                location = location
            )
            _userProfile.value = _userProfile.value.copy(
                postsCount = _userProfile.value.postsCount + 1
            )
            _showCreatePostSheet.value = false
            _currentTab.value = ScreenTab.HOME
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFollowProfile() {
        val current = _userProfile.value
        val newFollowState = !current.isFollowing
        val newFollowerCount = if (newFollowState) current.followersCount + 1 else (current.followersCount - 1).coerceAtLeast(0)
        _userProfile.value = current.copy(
            isFollowing = newFollowState,
            followersCount = newFollowerCount
        )
    }

    fun toggleNotificationFollow(notifId: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == notifId) it.copy(isFollowing = !it.isFollowing) else it
        }
    }

    fun setDirectMessagesOpen(isOpen: Boolean) {
        _isDirectMessagesOpen.value = isOpen
    }

    fun updateProfileBio(newBio: String, newName: String) {
        _userProfile.value = _userProfile.value.copy(
            bio = newBio,
            fullName = newName
        )
    }
}
