package com.example.quizmaster

import android.annotation.SuppressLint
import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmaster.databinding.ResultItemLayoutBinding

class ResultsAdapter(private val cursor: Cursor) :
    RecyclerView.Adapter<ResultsAdapter.ResultViewHolder>() {

    inner class ResultViewHolder(private val binding: ResultItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("Range")
        fun bind(cursor: Cursor) {
            binding.apply {
                nameTextView.text = cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl))
                ageTextView.text = cursor.getString(cursor.getColumnIndex(DBHelper.AGE_COL))
                genderTextView.text = cursor.getString(cursor.getColumnIndex(DBHelper.GENDER))
                correctAnswersTextView.text =
                    cursor.getString(cursor.getColumnIndex(DBHelper.CORRECT_ANS))
                totalTextView.text = cursor.getString(cursor.getColumnIndex(DBHelper.TOTAL))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ResultItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        cursor.moveToPosition(position)
        holder.bind(cursor)
    }

    override fun getItemCount(): Int {
        return cursor.count
    }

    fun swapCursor(newCursor: Cursor) {
        cursor.close()
        notifyDataSetChanged()
    }
}