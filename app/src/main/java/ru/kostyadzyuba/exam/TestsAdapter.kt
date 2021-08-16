package ru.kostyadzyuba.exam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TestsAdapter(
    val tests: MutableList<Test>,
    private val longClickListener: View.OnLongClickListener
) :
    RecyclerView.Adapter<TestsAdapter.TestViewHolder>() {
    class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(test: Test) {
            (itemView as TextView).text = test.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_selectable_list_item, parent, false)
        view.setOnLongClickListener(longClickListener)
        return TestViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(tests[position])
    }

    override fun getItemCount(): Int {
        return tests.size
    }
}