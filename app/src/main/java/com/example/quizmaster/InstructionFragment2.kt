package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quizmaster.databinding.FragmentInstruction2Binding

class InstructionFragment2 : Fragment() {

    private lateinit var instruction2Binding: FragmentInstruction2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        instruction2Binding = FragmentInstruction2Binding.inflate(inflater, container, false)
        val view = instruction2Binding.root

        val stringBuilder = StringBuilder()

        stringBuilder.append("You get to choose from the following categories:<br/><br/>")
        stringBuilder.append("1) General Knowledge: Tests your overall awareness and understanding of various subjects.<br/>")
        stringBuilder.append("2) Science: Tests your knowledge of scientific principles, discoveries, and phenomena.<br/>")
        stringBuilder.append("3) History: Explores questions related to historical events, figures, and periods.<br/>")
        stringBuilder.append("4) Sports: Involves questions about different sports, athletes, teams, and sporting events.<br/>")


        instruction2Binding.instructionsTextView.text = Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_COMPACT)

        instruction2Binding.skipBtn.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }

        instruction2Binding.nextBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentView, InstructionFragment3()).commit()
        }

        return view
    }
}
