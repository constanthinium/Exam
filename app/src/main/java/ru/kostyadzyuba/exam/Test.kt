package ru.kostyadzyuba.exam

import java.io.Serializable

class Test(
    val name: String,
    var questions: ArrayList<QuestionAndAnswer>
) : Serializable

class QuestionAndAnswer(
    var question: String,
    var answer: String
) : Serializable {
    override fun toString() = "$question - $answer"
}