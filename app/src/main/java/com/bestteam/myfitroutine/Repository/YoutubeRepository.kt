package com.bestteam.myfitroutine.Repository

import com.bestteam.myfitroutine.Contain.API_KEY
import com.bestteam.myfitroutine.Model.YoutubeApi
import com.bestteam.myfitroutine.retrofit.Youtube_interface
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface YoutubeRepository {
    suspend fun getVideoList(): Response<YoutubeApi>
}

@Singleton
class YoutubeRepositoryImpl @Inject constructor(
    private val api: Youtube_interface
) : YoutubeRepository {

    override suspend fun getVideoList(): Response<YoutubeApi> {
        return api.searchVideos("snippet", "fitness", 50, API_KEY)
    }
}