package com.example.quizmaster

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.example.quizmaster.databinding.FragmentQuizSettingsBinding

class QuestionSettingsFragment : Fragment() {

    private var fragmentQuizSettingsBinding:FragmentQuizSettingsBinding?=null
    private var selectedType:String = "multiple"
    private var selectedDifficulty:String = "easy"
    private var numOfQuestions:String = "4"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentQuizSettingsBinding =  FragmentQuizSettingsBinding.inflate(inflater, container, false)
        val view = fragmentQuizSettingsBinding!!.root


        fragmentQuizSettingsBinding!!.subBtn.setOnClickListener {
            val typeRadioGroup = fragmentQuizSettingsBinding!!.typeRadioGp
            val selectedTypeRadioButtonId = typeRadioGroup.checkedRadioButtonId
            val selectedTypeRadioButton = view.findViewById<RadioButton>(selectedTypeRadioButtonId)
            selectedType = selectedTypeRadioButton.text.toString()

            if (selectedType == "MCQ"){
                selectedType = "multiple"
            }else{
                selectedType = "boolean"
            }

            val difficultyRadioGroup = fragmentQuizSettingsBinding!!.difficultyRadioGp
            val selectedDifficultyRadioButtonId = difficultyRadioGroup.checkedRadioButtonId
            val selectedDifficultyRadioButton = view.findViewById<RadioButton>(selectedDifficultyRadioButtonId)
            selectedDifficulty = selectedDifficultyRadioButton.text.toString()
            if (selectedDifficulty=="Med"){
                selectedDifficulty = "medium"
            }
            numOfQuestions = fragmentQuizSettingsBinding!!.noOfQuestions.text.toString()
            if (numOfQuestions == ""){
                numOfQuestions = "4"
            }


            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.main_fragment,QuestionsFragment.newInstance(selectedType,selectedDifficulty.lowercase(),"9",numOfQuestions)).commit()
        }

        return view

    }

}