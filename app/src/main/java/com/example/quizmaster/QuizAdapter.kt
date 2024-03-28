package com.example.quizmaster

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.databinding.QuizCardViewBinding
import com.example.quizmaster.model.QuizCategory

class QuizAdapter(private val mList: List<QuizCategory>,private val clickListener: QuizCategoryClickListener) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {
    inner class QuizViewHolder(private val binding:QuizCardViewBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(quizCategory: QuizCategory) {
            binding.quizCardTitle.text = quizCategory.title
            binding.quizCardImg.setImageResource(quizCategory.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding = QuizCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quizCategory = mList[position]
        holder.bind(quizCategory)

        holder.itemView.setOnClickListener {
            clickListener.onQuizCategoryClicked(quizCategory,quizCategory.title)
        }
    }
}