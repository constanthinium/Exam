package ru.kostyadzyuba.exam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

class QuestionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        findViewById<RecyclerView>(R.id.questions).adapter = QuestionsAdapter(mutableListOf(
            Pair("What the cat says", "Meow"),
            Pair("What color is the sky", "Blue"),
            Pair("What falls in winter", "Snow"),
        ))
    }
}