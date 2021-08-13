package ru.kostyadzyuba.exam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView

class QuestionsAdapter(val questionsList: MutableList<Pair<String, String>>) :
    RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

    class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val questionEditText = itemView.findViewById<EditText>(R.id.question)
        private val answerEditText = itemView.findViewById<EditText>(R.id.answer)

        fun bind(question: Pair<String, String>) {
            questionEditText.setText(question.first)
            answerEditText.setText(question.second)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.question_list_item, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questionsList[position])
    }

    override fun getItemCount(): Int {
        return questionsList.size
    }
}