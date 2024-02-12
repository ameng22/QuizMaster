package com.example.quizmaster.model

import com.google.gson.annotations.SerializedName

data class Quiz(
    @SerializedName("response_code")
    val responseCode: Int,
    val results: List<QuizQuestion>
)

data class QuizQuestion(
    val type: String,
    val difficulty: String,
    val category: String,
    val question: String,
    @SerializedName("correct_answer")
    val correctAnswer: String,
    @SerializedName("incorrect_answers")
    val incorrectAnswers: ArrayList<String>
)