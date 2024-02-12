package com.example.quizmaster

import com.example.quizmaster.model.Quiz
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("api.php")
    suspend fun getQuiz(
        @Query("amount") amount: Int,
        @Query("category") category: Int,
        @Query("difficulty") difficulty: String,
        @Query("type") type: String
    ): Quiz

}