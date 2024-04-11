package com.example.quizmaster

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import com.example.quizmaster.databinding.FragmentQuizSettingsBinding
import java.util.Calendar
import java.util.Locale

class QuestionSettingsFragment : Fragment() {

    private var fragmentQuizSettingsBinding:FragmentQuizSettingsBinding?=null
    private var selectedType:String = "multiple"
    private var selectedDifficulty:String = "easy"
    private var numOfQuestions:String = "4"
    private var category: Int = 0
    private var timerDuration: Long = 20
    private lateinit var name: String
    private lateinit var dob: String
    private lateinit var gender: String
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentQuizSettingsBinding =  FragmentQuizSettingsBinding.inflate(inflater, container, false)
        val view = fragmentQuizSettingsBinding!!.root

        arguments?.let {
            category = it.getInt(CATEGORY_KEY, 0)
        }

        fragmentQuizSettingsBinding!!.editTextDob.setOnClickListener {
            showDatePicker()
        }

        sharedPreferences = requireActivity().getSharedPreferences("ParticipantDetailsSharedPref", Context.MODE_PRIVATE)

        //Added settings for the app. This was already implemented earlier
        fragmentQuizSettingsBinding!!.subBtn.setOnClickListener {
            val typeRadioGroup = fragmentQuizSettingsBinding!!.typeRadioGp
            val selectedTypeRadioButtonId = typeRadioGroup.checkedRadioButtonId
            val selectedTypeRadioButton = view.findViewById<RadioButton>(selectedTypeRadioButtonId)
            name = fragmentQuizSettingsBinding!!.editTextName.text.toString().trim()
            dob = fragmentQuizSettingsBinding!!.editTextDob.text.toString().trim()
            val genderRadioGroup = fragmentQuizSettingsBinding!!.radioGroupGender
            val selectedGenderRadioButtonId = genderRadioGroup.checkedRadioButtonId
            val selectedGenderRadioButton = view.findViewById<RadioButton>(selectedGenderRadioButtonId)
            val text = fragmentQuizSettingsBinding?.timerDuration?.text?.toString()
            timerDuration = if (text?.isNotEmpty() == true) {
                text.toLong()
            } else {
                20
            }
            gender = selectedGenderRadioButton.text.toString()

            numOfQuestions = fragmentQuizSettingsBinding!!.noOfQuestions.text.toString()

            if (name.isEmpty() || dob.isEmpty()||numOfQuestions.isEmpty() || numOfQuestions == "0" || timerDuration == 0.toLong()) {
                Toast.makeText(requireContext(), "Please enter all fields or valid input is required", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            timerDuration *= 1000

            with(sharedPreferences.edit()) {
                putString("name", name)
                putString("dob", dob)
                putString("gender",gender)
                apply()
            }

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

            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.main_fragment,QuestionsFragment.newInstance(selectedType,selectedDifficulty.lowercase(),category.toString(),numOfQuestions,timerDuration)).commit()
        }
        return view
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate =
                    String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                fragmentQuizSettingsBinding!!.editTextDob.setText(formattedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    companion object {
        private const val CATEGORY_KEY = "category"

        fun newInstance(category: Int): QuestionSettingsFragment {
            val fragment = QuestionSettingsFragment()
            val args = Bundle()
            args.putInt(CATEGORY_KEY, category)
            fragment.arguments = args
            return fragment
        }
    }

}