package com.example.quizmaster

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import com.example.quizmaster.databinding.FragmentQuestionsBinding
import com.example.quizmaster.model.Quiz
import com.example.quizmaster.model.QuizQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val QUIZ_TYPE = "type"
private const val QUIZ_DIFFICULTY= "difficulty"
private const val QUIZ_CATEGORY= "category"
private const val NUMBER_OF_QUESTIONS= "amount"
class QuestionsFragment : Fragment() {
    private lateinit var quiz: Quiz;

    private var fragmentQuestionsBinding: FragmentQuestionsBinding? = null
    private var questionIndex = 0
    var correctAnswers:Int = 0
    private var type = ""
    private var difficulty = ""
    private var category = ""
    private var amount = ""
    private val binding get() = fragmentQuestionsBinding!!
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://opentdb.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val api = retrofit.create(Api::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString(QUIZ_TYPE).toString()
            difficulty = it.getString(QUIZ_DIFFICULTY).toString()
            category = it.getString(QUIZ_CATEGORY).toString()
            amount = it.getString(NUMBER_OF_QUESTIONS).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentQuestionsBinding = FragmentQuestionsBinding.inflate(inflater, container, false)
        val view = fragmentQuestionsBinding!!.root

        fetchQuizData()

        fragmentQuestionsBinding!!.submitBtn.setOnClickListener {
            var id = fragmentQuestionsBinding!!.optionRadioGroup.checkedRadioButtonId
            if (id==-1){
                Toast.makeText(context, "no option selected", Toast.LENGTH_SHORT).show()
            }else{
                val selectedRadioButton = fragmentQuestionsBinding!!.optionRadioGroup.findViewById<RadioButton>(id)
                val selectedOption = selectedRadioButton.text.toString()
                if (selectedOption == quiz.results[questionIndex].correctAnswer){
                    correctAnswers++;
                }
                Toast.makeText(context, "Option selected: $selectedOption $correctAnswers", Toast.LENGTH_SHORT).show()
            }
            questionIndex++
            if (questionIndex>=quiz.results.size){
                Toast.makeText(context, "No more questions", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, ResultsFragment.newInstance(correctAnswers,quiz.results.size))
                    .commit()
            }else{
                displayQuestion(quiz.results[questionIndex])
            }
        }

        return view
    }

    private fun fetchQuizData() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                quiz = api.getQuiz(amount = amount.toInt(), category = category.toInt(), difficulty = difficulty, type = type)
                displayQuestion(quiz.results[0])
            } catch (e: Exception) {
                Toast.makeText(context, "Error in fetching quiz", Toast.LENGTH_SHORT).show()
                Log.d("Fetch",e.toString())
            }
        }
    }

    private fun displayQuestion(
        quiz: QuizQuestion
    ) {
        val questionTextView = fragmentQuestionsBinding!!.questionTv
        val optionRadioGroup = fragmentQuestionsBinding!!.optionRadioGroup

        // Set the question text
        questionTextView.text = parseHtmlEntities(quiz.question)

        optionRadioGroup.removeAllViews()
        optionRadioGroup.clearCheck()

        var options:ArrayList<String> = quiz.incorrectAnswers
        options.add(quiz.correctAnswer)

        // Add options dynamically
        options.forEachIndexed{ idx,option ->
            val radioButton = RadioButton(requireContext())
            radioButton.text = parseHtmlEntities(option)
            radioButton.id = idx
            optionRadioGroup.addView(radioButton)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentQuestionsBinding = null
    }

    fun parseHtmlEntities(input: String): CharSequence {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY)
        } else {
            HtmlCompat.fromHtml(input, FROM_HTML_MODE_LEGACY)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(quizType: String, quizDifficulty: String, quizCategory:String,numOfQuestions:String) =
            QuestionsFragment().apply {
                arguments = Bundle().apply {
                    putString(QUIZ_TYPE, quizType)
                    putString(QUIZ_DIFFICULTY, quizDifficulty)
                    putString(QUIZ_CATEGORY,quizCategory)
                    putString(NUMBER_OF_QUESTIONS,numOfQuestions)
                }
            }
    }

}