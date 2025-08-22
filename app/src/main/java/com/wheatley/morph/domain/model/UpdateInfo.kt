package com.wheatley.morph.domain.model

data class UpdateInfo(
    val version: String,
    val apkUrl: String,
    val changelog: String
)