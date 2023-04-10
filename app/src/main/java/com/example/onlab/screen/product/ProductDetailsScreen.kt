package com.example.onlab.screen.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onlab.R

@Preview
@Composable
fun ProductDetailsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "product.name") },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            Column(modifier = Modifier.padding(it)) {
                Image(
                    painter = painterResource(id = R.drawable.picture_placeholder),
                    contentDescription = "product.name",
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "product.name",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "yy",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "HUF $",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    )
}
