package com.example.quizmaster

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quizmaster.databinding.FragmentInstruction1Binding
import com.example.quizmaster.databinding.FragmentQuizSettingsBinding

class InstructionFragment1 : Fragment() {

    private lateinit var instruction1Binding: FragmentInstruction1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        instruction1Binding = FragmentInstruction1Binding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        instruction1Binding =  FragmentInstruction1Binding.inflate(inflater, container, false)
        val view = instruction1Binding.root

        instruction1Binding.skipBtn.setOnClickListener {

            val intent = Intent(context,MainActivity::class.java)
            startActivity(intent)

        }

        instruction1Binding.nextBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentView,InstructionFragment2()).commit()
        }

        return view
    }

}