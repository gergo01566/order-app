package com.example.onlab.common.utils

object ValidationUtils {

    fun inputContainsOnlyLetter(input: String):Boolean {
        if (!input.all { it.isLetter() } || input.isBlank()) {
            return false
        }
        return true
    }

    fun inputContaintsOnlyNumbers(input: String):Boolean {
        if (!input.all { it.isDigit() } || input.isBlank()) {
            return false
        }
        return true
    }

    fun inputIsNotEmpty(input: String):Boolean {
        if (input.isBlank()) {
            return false
        }
        return true
    }

    fun inputIsValidEmailFormat(input: String):Boolean {
        val emailRegex = Regex("[A-Za-z\\d._%+-]+@[A-Za-z\\d.-]+\\.[A-Z|a-z]{2,}")
        if (!emailRegex.matches(input) && input.isBlank()) {
            return false
        }
        return true
    }

    fun inputIsValidPrice(input: String):Boolean {
        if (input == "0" || input.isBlank()) {
            return false
        }
        return true
    }

    fun inputIsValidPassword(input: String): Boolean {
        val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")
        return passwordRegex.matches(input) && input.isNotBlank()
    }

}