package ru.kostyadzyuba.exam

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class QuestionsActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var questionsAdapter: QuestionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        questionsAdapter = QuestionsAdapter(
            mutableListOf(
                Pair("What the cat says", "Meow"),
                Pair("What color is the sky", "Blue"),
                Pair("What falls in winter", "Snow"),
            )
        )
        findViewById<RecyclerView>(R.id.questions).adapter = questionsAdapter
        findViewById<FloatingActionButton>(R.id.add).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.add -> {
                questionsAdapter.questions.add(Pair("", ""))
                questionsAdapter.notifyItemInserted(questionsAdapter.itemCount - 1)
            }
            else -> throw IllegalArgumentException("View.id")
        }
    }
}