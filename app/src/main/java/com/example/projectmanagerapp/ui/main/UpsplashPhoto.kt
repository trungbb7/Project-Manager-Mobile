package com.example.projectmanagerapp.ui.main

data class UnsplashPhoto(
    val id: String,
    val urls: PhotoUrls,
    val user: UnsplashUser
)

data class PhotoUrls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)

data class UnsplashUser(
    val name: String
)