package com.example.onlab.model

data class MUser(
    val id: String?,
    val userId: String,
    val displayName: String,
    val address: String,
){
    constructor(): this(null, "", "", "")
    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "user_id" to this.userId,
            "display_name" to this.displayName,
            "address" to this.address
        )
    }
}
