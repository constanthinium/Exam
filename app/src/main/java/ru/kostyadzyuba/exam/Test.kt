package ru.kostyadzyuba.exam

import java.io.Serializable

class Test(
    val name: String,
    val questions: ArrayList<QuestionAndAnswer>
)

class QuestionAndAnswer(
    var question: String,
    var answer: String
) : Serializable