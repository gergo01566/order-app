import com.example.orderapp.common.utils.ValidationUtils
import com.example.orderapp.model.service.AuthService
import com.example.orderapp.screens.login.LoginViewModel


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // Mock your dependencies
    @Mock
    private lateinit var authService: AuthService

    // Create an instance of LoginViewModel
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
        loginViewModel = LoginViewModel(authService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun inputValidator_CorrectEmail_ReturnsTrue(){
        Assert.assertTrue(ValidationUtils.inputIsValidEmailFormat("test@example.com"))
    }

    @Test
    fun inputValidator_InvalidEmail_ReturnsFalse(){
        Assert.assertFalse(ValidationUtils.inputIsValidEmailFormat("testexample.com"))
    }

    @Test
    fun inputValidator_CorrectPasswordFormat_ReturnsTrue(){
        Assert.assertTrue(ValidationUtils.inputIsValidPassword("password123"))
    }

    @Test
    fun inputValidator_InvalidPasswordFormat_ReturnsFalse(){
        Assert.assertFalse(ValidationUtils.inputIsValidPassword("123456"))
    }



//    @Test
//    @DisplayName
//    suspend fun `onSignInClick with valid email and password should call onComplete and navigateFromTo`() {
//        // Arrange
//        loginViewModel.onEmailChange("test@example.com")
//        loginViewModel.onPasswordChange("password")
//
//        val onSuccess: () -> Unit = {}
//        val onFailure: () -> Unit = {}
//
//        // Mock the behavior of authService.signInWithEmailAndPassowrd
//        when(authService.signInWithEmailAndPassowrd(loginViewModel.uiState.value.email, loginViewModel.uiState.value.password, onSuccess, onFailure)).then {
//            // Capture the onSuccess lambda
//
//            onSuccess.invoke()
//            onSuccessCalled = true
//
//            // Return a Unit to match the signature of signInWithEmailAndPassowrd
//            Unit
//        }
//
//        // Mock the navigateFromTo function
//
//
//        // Act
//        loginViewModel.onSignInClick(
//            navigateFromTo = navigateFromTo,
//            onFailure = {},
//            onComplete = {}
//        )
//
//        // Assert
//        verify(navigateFromTo).invoke(DestinationLogin, DestinationOrderList)
//
//        // Check whether onSuccess was called
//        assert(onSuccessCalled)
//
//    }

    // Similar tests for other functions can be written
}
