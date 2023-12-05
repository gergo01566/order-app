package com.example.onlab.screen.profile

import com.example.onlab.data.ValueOrException
import com.example.onlab.model.User
import com.example.onlab.service.AuthService
import com.example.onlab.service.UserStorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class ProfileViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var authService: AuthService

    @InjectMocks
    private lateinit var userStorageService: UserStorageService

    private lateinit var profileViewModel: ProfileViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)  // Initialize annotated mocks
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `testOnLogout`() = runBlocking {

        val viewModel = ProfileViewModel(authService, userStorageService)

        // When
        viewModel.onLogout()

        delay(1000)

        // Then
        Mockito.verify(authService).signOut()
    }
}