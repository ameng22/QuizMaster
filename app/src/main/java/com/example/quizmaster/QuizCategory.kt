package com.example.quizmaster

import com.example.quizmaster.model.QuizCategory

interface QuizCategoryClickListener {
    fun onQuizCategoryClicked(quizCategory: QuizCategory, title: String)
}