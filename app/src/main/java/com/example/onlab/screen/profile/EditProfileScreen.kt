import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlab.R
import com.example.onlab.navigation.DestinationCustomerDetails
import com.example.onlab.screen.product.BasicField
import com.example.onlab.screen.profile.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    navigateFromTo:(String, String) -> Unit = { it1, it2 -> Log.d("log", "EditProfileScreen: $")},
) {

    val uiState by viewModel.uiState

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil szerkesztése") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle navigation back */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle save action */ }) {
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
            ProfileImage({})
            Spacer(modifier = Modifier.height(it.calculateStartPadding(LayoutDirection.Ltr)))
            EditTextField(value = uiState.name, label = "Név") {  }
            EditTextField(value = uiState.address, label = "Cím") {  }
            EditTextField(value = uiState.email, label = "Email") {  }
        }
    }
}

@Composable
fun ProfileImage(onClick:() -> Unit) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(R.drawable.ic_launcher_round),
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
fun EditTextField(value: String, label: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
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
