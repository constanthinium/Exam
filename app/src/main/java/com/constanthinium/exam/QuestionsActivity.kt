package com.constanthinium.exam

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val EXTRA_INDEX = "index"
private const val EXTRA_TEST = "test"

class QuestionsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var questionsAdapter: QuestionsAdapter
    private lateinit var save: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        questionsAdapter = QuestionsAdapter(intent.getSerializableExtra(EXTRA_TEST)
            ?.let { (it as Test).questions } ?: ArrayList())
        findViewById<RecyclerView>(R.id.questions).adapter = questionsAdapter
        findViewById<FloatingActionButton>(R.id.add).setOnClickListener(this)
        save = findViewById(R.id.save)
        save.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.add -> {
                questionsAdapter.questions.add(QuestionAndAnswer("", ""))
                questionsAdapter.notifyItemInserted(questionsAdapter.itemCount - 1)
                val oldPos = save.translationY + save.marginTop - save.height
                ObjectAnimator.ofFloat(save, "translationY", oldPos, 1f).start()
            }
            R.id.save -> {
                currentFocus?.clearFocus()
                if (questionsAdapter.questions.any { it.question.isBlank() || it.answer.isBlank() }) {
                    Toast.makeText(this, "All fields must be filled in.", Toast.LENGTH_SHORT).show()
                } else {
                    val resultIntent = Intent()
                        .putExtra(ResultExtras.QUESTIONS, questionsAdapter.questions)
                        .putExtra(ResultExtras.INDEX, intent.getIntExtra(ResultExtras.INDEX, -1))
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
            else -> throw IllegalArgumentException("View.id")
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, QuestionsActivity::class.java)
        }

        fun newIntent(context: Context, index: Int, test: Test): Intent {
            return newIntent(context)
                .putExtra(EXTRA_INDEX, index)
                .putExtra(EXTRA_TEST, test)
        }
    }
}