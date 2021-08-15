package ru.kostyadzyuba.exam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuestionsAdapter : RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {
    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnFocusChangeListener {

        private val questionEditText = itemView.findViewById<EditText>(R.id.question)
        private val answerEditText = itemView.findViewById<EditText>(R.id.answer)

        init {
            questionEditText.onFocusChangeListener = this
            answerEditText.onFocusChangeListener = this
        }

        fun bind(questionAndAnswer: QuestionAndAnswer) {
            questionEditText.setText(questionAndAnswer.question)
            answerEditText.setText(questionAndAnswer.answer)
        }

        override fun onFocusChange(view: View, hasFocus: Boolean) {
            if (!hasFocus) {
                val text = (view as TextView).text.toString()
                when (view.id) {
                    R.id.question -> questions[adapterPosition].question = text
                    R.id.answer -> questions[adapterPosition].answer = text
                }
            }
        }
    }

    val questions = ArrayList<QuestionAndAnswer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.question_list_item, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position])
    }

    override fun getItemCount(): Int {
        return questions.size
    }
}