package com.example.quizmaster

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quizmaster.databinding.FragmentResultsBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_CORRECT_ANSWERS = "correctAnswers"
private const val ARG_QUIZ_SIZE = "quizSize"

class ResultsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var correctAnswers: Int = 0
    private var quizSize: Int = 0
    private var binding:FragmentResultsBinding?=null
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            correctAnswers = it.getInt(ARG_CORRECT_ANSWERS)
            quizSize = it.getInt(ARG_QUIZ_SIZE)
        }

        sharedPreferences = requireActivity().getSharedPreferences("ParticipantDetailsSharedPref", Context.MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentResultsBinding.inflate(inflater, container, false)
        val view = binding!!.root

        displayResults()

        return view
    }

    private fun displayResults() {
        binding!!.score.text = correctAnswers.toString()
        binding!!.numOfQuestions.text = quizSize.toString()
        val name = sharedPreferences.getString("name", "")
        val dob = sharedPreferences.getString("dob", "")
        val gender = sharedPreferences.getString("gender", "")

        // Display the retrieved details wherever necessary in your layout
        binding!!.participantName.text = name
        binding!!.participantAge.text = dob
        binding!!.participantGender.text = gender

        val percentage:Int = (correctAnswers*100)/quizSize
        var feedback:String = ""
        when(percentage){
            in 80..100-> feedback="Welldone"
            in 60..80->feedback="Good job"
            in 40..60->feedback="Needs Improvement"
            else->feedback="Fail. Work Harder"
        }
        binding!!.feedback.text = feedback
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(correctAnswers: Int, quizSize: Int) =
            ResultsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CORRECT_ANSWERS, correctAnswers)
                    putInt(ARG_QUIZ_SIZE, quizSize)
                }
            }
    }
}