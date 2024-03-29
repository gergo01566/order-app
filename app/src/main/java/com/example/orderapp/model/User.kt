package com.example.orderapp.model

data class User(
    val id: String?,
    val userId: String,
    val displayName: String,
    val address: String,
    val email: String,
    val image: String
){
    constructor(userId: String, displayName: String, address: String, email: String, image: String) : this(null, "", "", "", "", "")
    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "user_id" to this.userId,
            "display_name" to this.displayName,
            "address" to this.address,
            "email" to this.email,
            "image" to this.image
        )
    }
}
