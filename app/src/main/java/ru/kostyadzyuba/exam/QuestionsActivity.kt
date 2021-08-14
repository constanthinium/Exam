package ru.kostyadzyuba.exam

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class QuestionsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var questionsAdapter: QuestionsAdapter
    private lateinit var save: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        questionsAdapter = QuestionsAdapter(mutableListOf("" to ""))
        findViewById<RecyclerView>(R.id.questions).adapter = questionsAdapter
        findViewById<FloatingActionButton>(R.id.add).setOnClickListener(this)
        save = findViewById(R.id.save)
        save.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.add -> {
                questionsAdapter.questions.add("" to "")
                questionsAdapter.notifyItemInserted(questionsAdapter.itemCount - 1)
                val oldPos = save.translationY + save.marginTop - save.height
                ObjectAnimator.ofFloat(save, "translationY", oldPos, 1f).start()
            }
            R.id.save -> {
                setResult(RESULT_OK)
                finish()
            }
            else -> throw IllegalArgumentException("View.id")
        }
    }
}