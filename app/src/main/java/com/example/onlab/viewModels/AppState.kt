import android.content.Context
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.onlab.OrderApp
import com.example.onlab.PermissionRequester

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Stable
class AppState(
    val navController: NavHostController,
    val permissionRequester: PermissionRequester,
    val scaffoldState: ScaffoldState,
    var snackBarText: String,
    coroutineScope: CoroutineScope
) {
    init {
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(snackBarText)
        }

        fun navigate(route: String) {
            navController.navigate(route) { launchSingleTop = true }
        }

        fun navigateBack() {
            navController.popBackStack()
        }

        @Composable
        fun requestPermission(
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
}
