package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quizmaster.databinding.FragmentInstruction3Binding

class InstructionFragment3 : Fragment() {

    private lateinit var instruction3Binding: FragmentInstruction3Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        instruction3Binding = FragmentInstruction3Binding.inflate(inflater, container, false)
        val view = instruction3Binding.root

        // Create a SpannableStringBuilder to format the text
        val instructions = SpannableStringBuilder()
        instructions.append("Register with the following details:<br/><br/>")
        instructions.append("- Name<br/>")
        instructions.append("- Date of Birth<br/>")
        instructions.append("- Gender<br/>")
        instructions.append("- Type of Quiz (MCQ, True/False)<br/>")
        instructions.append("- Difficulty Level<br/>")
        instructions.append("- Number of Questions<br/><br/>")

        instructions.append(" Enjoy the Quiz<br/><br/>")

        // Set the formatted text to the TextView
        instruction3Binding.instructionsTextView.text = Html.fromHtml(instructions.toString(), Html.FROM_HTML_MODE_COMPACT)

        instruction3Binding.skipBtn.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
