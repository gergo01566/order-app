package com.example.onlab.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlab.model.MUser
import com.example.onlab.screen.login.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginScreenViewModel: ViewModel() {
    //val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun signInWithEmailAndPassword(email: String, password: String, orders: () -> Unit) = viewModelScope.launch{
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{ task->
                    if(task.isSuccessful){
                        orders()
                    } else {
                        Log.d("FB", "signInWithEmailAndPassword: ${task.result.toString()}")
                    }
                }
        } catch (ex: java.lang.Exception){
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


}