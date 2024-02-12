package com.example.quizmaster
import com.example.quizmaster.model.Quiz
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson
import java.io.IOException

fun main() {
    val url = "https://opentdb.com/api.php?amount=10&category=9&difficulty=easy&type=multiple"

    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val responseBody = response.body?.string()
        val gson = Gson()
        val quiz: Quiz = gson.fromJson(responseBody, Quiz::class.java)

        println("Response Code: ${quiz.responseCode}")
        quiz.results.forEachIndexed { index, question ->
            println("Question ${index + 1}: ${question.question}")
            println("Correct Answer: ${question.correctAnswer}")
            println("Incorrect Answers: ${question.incorrectAnswers}")
            println()
        }
    }
}