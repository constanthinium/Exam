package ru.kostyadzyuba.exam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TestsAdapter(val testList: MutableList<String>) :
    RecyclerView.Adapter<TestsAdapter.TestViewHolder>() {
    class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(test: String) {
            (itemView as TextView).text = test
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_selectable_list_item, parent, false)
        return TestViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(testList[position])
    }

    override fun getItemCount(): Int {
        return testList.size
    }
}