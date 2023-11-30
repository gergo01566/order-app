import android.content.Context
import android.content.res.Resources
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import com.example.onlab.PermissionRequester
import com.example.onlab.components.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Stable
class AppState(
    val navController: NavHostController,
    val permissionRequester: PermissionRequester,
    val scaffoldState: ScaffoldState,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {
    init {
        coroutineScope.launch {
            snackbarManager.snackbarMessage.filterNotNull().collect { message ->
                val text = resources.getString(message)
                scaffoldState.snackbarHostState.showSnackbar(text)
                snackbarManager.clearSnackbarState()
            }
        }
    }

    fun navigate(route: String) {
        navController.navigate(route) { launchSingleTop = true }
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    @Composable
    fun RequestPermission(
        context: Context,
        permission: String,
        showRationale: () -> Unit,
        onPermissionDenied: () -> Unit,
        onPermissionGranted: () -> Unit
    ) {
        permissionRequester.requestPermission(
            context = context,
            permission = permission,
            showRationale = showRationale,
            onPermissionDenied = onPermissionDenied,
            onPermissionGranted = onPermissionGranted
        )
    }
}

