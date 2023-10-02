package com.example.onlab.viewModels

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlab.model.MUser
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class LoginScreenViewModel: ViewModel() {
    //val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun signInWithEmailAndPassword(email: String, password: String, orders: () -> Unit, onFailure: () -> Unit) = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        orders()
                        Log.d("IGEN", "LoginScreen: ")

                    } else {
                        Log.d("NEMjj", "LoginScreen: ")
                        onFailure()
                    }
                }
        } catch (ex: Exception) {
            Log.d("FB", "signInWithEmailAndPassword: ${ex.message}")
        }
    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid
        val user = MUser(userId = userId.toString(), displayName = displayName.toString(), address = "Budapest", id = null).toMap()

        FirebaseFirestore.getInstance().collection("users")
            .add(user)
    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        orders: () -> Unit
    ){
        if(loading.value == false){
            _loading.value = true
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{task->
                    if(task.isSuccessful){
                        val displayName = task.result?.user?.email?.split('@')?.get(0)
                        createUser(displayName)
                        orders()
                    } else {
                        Log.d("FB", "createUserWithEmailAndPassword: ${task.result.toString()}")
                    }
                    _loading.value = false
                }
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }


}