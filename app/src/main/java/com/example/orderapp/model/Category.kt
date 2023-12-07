package com.example.orderapp.model

enum class Category {
    Összes,
    Italok,
    Sütemények,
    Tészták,
    Csokoládék,
}

fun <T : Enum<T>> getCategoryTypes(enumClass: Class<T>): List<T> {
    return enumClass.enumConstants!!.toList()
}