package com.example

import com.example.data.model.PostEntity
import org.junit.Assert.*
import org.junit.Test

/**
 * Local unit tests for MySocial application logic.
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testPostBookmarkToggle() {
    val initialPost = PostEntity(
      id = 1L,
      authorUsername = "john_doe",
      authorAvatar = "https://example.com/avatar.jpg",
      location = "San Francisco, CA",
      imageUrl = "https://example.com/photo.jpg",
      caption = "Exploring the city! #goldenhour",
      likesCount = 42,
      isLiked = false,
      isSaved = false,
      timestamp = System.currentTimeMillis()
    )

    assertFalse(initialPost.isSaved)

    // Simulate save toggle
    val savedPost = initialPost.copy(isSaved = !initialPost.isSaved)
    assertTrue(savedPost.isSaved)

    // Simulate unsave toggle
    val unsavedPost = savedPost.copy(isSaved = !savedPost.isSaved)
    assertFalse(unsavedPost.isSaved)
  }
}
