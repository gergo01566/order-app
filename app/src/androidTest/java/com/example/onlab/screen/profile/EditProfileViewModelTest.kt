package com.example.onlab.screen.profile

import com.example.onlab.data.ValueOrException
import com.example.onlab.model.User
import com.example.onlab.service.AuthService
import com.example.onlab.service.UserStorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

internal class EditProfileViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var authService: AuthService

    @Mock
    private lateinit var userStorageService: UserStorageService

    // Create an instance of LoginViewModel
    private lateinit var editProfileViewModel: EditProfileViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)  // Initialize annotated mocks
        Dispatchers.setMain(testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun initTest() = runBlocking {
        `when`(authService.currentUserId).thenReturn("user123")
        val user = User(
            userId = "user123",
            displayName = "John Doe",
            address = "123 Main Street",
            email = "john.doe@example.com",
            image = "https://example.com/image.jpg"
        )
        Mockito.`when`(userStorageService.getUser(authService.currentUserId)).thenReturn(
            ValueOrException.Success<User>(user))
        editProfileViewModel = EditProfileViewModel(authService, userStorageService)

        Assert.assertEquals(editProfileViewModel.uiState.value.address, user.address)
    }

}