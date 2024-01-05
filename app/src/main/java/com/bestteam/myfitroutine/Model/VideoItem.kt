package com.bestteam.myfitroutine.Model

import java.io.Serializable

data class VideoItem(
    val thumbnails: String?,
    val title: String?,
    val categoryId: String?,
    val videoId : String?,
    val description : String?
): Serializable
