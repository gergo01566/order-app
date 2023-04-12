package com.example.onlab.model

enum class Category {
    Italok,
    Sütemények,
    Tészták,
    Csokoládék,
}

fun <T : Enum<T>> getCategoryTypes(enumClass: Class<T>): List<T> {
    return enumClass.enumConstants.toList()
}