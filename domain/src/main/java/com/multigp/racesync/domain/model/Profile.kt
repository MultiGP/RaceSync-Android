package com.multigp.racesync.domain.model

data class Profile(
    val addressOne: String,
    val addressTwo: String,
    val authType: String,
    val chapterCount: String,
    val chapterIds: List<String>,
    val city: String,
    val country: String,
    val dateAdded: String,
    val dateModified: String,
    val displayName: String,
    val firstName: String,
    val homeChapterId: String,
    val id: String,
    val isPublic: Boolean,
    val language: String,
    val lastName: String,
    val latitude: String,
    val longitude: String,
    val phoneNumber: String,
    val profileBackgroundUrl: String,
    val profilePictureUrl: String,
    val raceCount: String,
    val state: String,
    val userName: String,
    val zip: String
)