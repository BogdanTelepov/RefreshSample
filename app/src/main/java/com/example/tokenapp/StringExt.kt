package com.example.tokenapp

fun String.convertToBearerToken() = "Bearer $this"