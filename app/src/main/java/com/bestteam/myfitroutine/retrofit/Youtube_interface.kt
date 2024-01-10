package com.bestteam.myfitroutine.retrofit

import com.bestteam.myfitroutine.Model.YoutubeApi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Youtube_interface {
    @GET("youtube/v3/search") // 엔드포인트를 "search"로 변경
    suspend fun searchVideos(
        @Query("part") part: String?,
        @Query("q") query: String?, // 검색어를 받는 매개변수 추가
        @Query("maxResults") maxResults: Int?,
        @Query("key") key: String?
    ): Response<YoutubeApi>
}