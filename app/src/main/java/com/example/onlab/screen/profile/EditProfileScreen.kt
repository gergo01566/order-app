import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.onlab.screen.profile.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    navigateFromTo:(String, String) -> Unit = { it1, it2 -> Log.d("log", "EditProfileScreen: $")},
) {

    val uiState by viewModel.uiState

    val scaffoldState = rememberScaffoldState()

    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onImageChange(uri.toString())
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil szerkesztése") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onUpdateUser(navigateFromTo = navigateFromTo) }) {
                        Icon(Icons.Default.Done, contentDescription = "Save")
                    }
                }
            )
        }
    ) {
        it.calculateBottomPadding()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(it.calculateTopPadding()))
            ProfileImage(uiState.image.toUri()) {
                singlePhotoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
            Spacer(modifier = Modifier.height(it.calculateStartPadding(LayoutDirection.Ltr)))
            EditTextField(value = uiState.name, label = "Név") { viewModel.onNameChange(it) }
            EditTextField(value = uiState.address, label = "Cím") { viewModel.onAddressChange(it)  }
            EditTextField(value = uiState.email, label = "Email", readOnly = true) { }
        }
    }
}

@Composable
fun ProfileImage(imageUri: Uri,onClick:() -> Unit) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        AsyncImage(
            model = imageUri,
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,            // crop the image if it's not a square
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)                       // clip to the circle shape
                .border(2.dp, Color.Gray, CircleShape)   // add a border (optional)
                .padding(5.dp)
                .clickable {
                    onClick()
                }
        )
        Text(modifier = Modifier
            .clickable { onClick() }
            .padding(5.dp), text = "Profilkép módosítása")
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextField(value: String, label: String, readOnly: Boolean = false, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        readOnly = readOnly,
        onValueChange = { onValueChange(it) },
        label = { Text(label) },
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .height(56.dp)
    )
}



@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun PreviewEditProfileScreen() {
    //EditProfileScreen()
}
