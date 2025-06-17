package com.wheatley.morph.update

import kotlinx.serialization.Serializable

@Serializable
data class UpdateInfo(
    val version: String,
    val apkUrl: String,
    val changelog: String
)