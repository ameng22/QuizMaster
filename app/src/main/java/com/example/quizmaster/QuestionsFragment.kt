package com.example.quizmaster

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import com.example.quizmaster.databinding.FragmentQuestionsBinding
import com.example.quizmaster.model.Quiz
import com.example.quizmaster.model.QuizQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration

private const val QUIZ_TYPE = "type"
private const val QUIZ_DIFFICULTY= "difficulty"
private const val QUIZ_CATEGORY= "category"
private const val NUMBER_OF_QUESTIONS= "amount"
private const val TIMER_DURATION = "timer"

class QuestionsFragment : Fragment() {
    private lateinit var quiz: Quiz;

    private var fragmentQuestionsBinding: FragmentQuestionsBinding? = null
    private var questionIndex = 0
    var correctAnswers:Int = 0
    private var type = ""
    private var difficulty = ""
    private var category = ""
    private var amount = ""
    private var timerDuration:Long = 0
    private lateinit var timer:CountDownTimer
    private lateinit var correctAnswerSound: MediaPlayer
    private lateinit var wrongAnswerSound: MediaPlayer

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
            timerDuration = it.getLong(TIMER_DURATION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentQuestionsBinding = FragmentQuestionsBinding.inflate(inflater, container, false)
        val view = fragmentQuestionsBinding!!.root

        val db = context?.let { DBHelper(it, null) }

        correctAnswerSound = MediaPlayer.create(context, R.raw.correctanswer)
        wrongAnswerSound = MediaPlayer.create(context, R.raw.wronganswer)
        
        startTimer(timerDuration)

        fetchQuizData()


        fragmentQuestionsBinding!!.submitBtn.setOnClickListener {
            val id = fragmentQuestionsBinding!!.optionRadioGroup.checkedRadioButtonId
            if (id == -1) {
                Toast.makeText(context, "No option selected", Toast.LENGTH_SHORT).show()
            } else {
                val selectedRadioButton = fragmentQuestionsBinding!!.optionRadioGroup.findViewById<RadioButton>(id)
                val selectedOption = selectedRadioButton.text.toString()
                try {
                    if (selectedOption == quiz.results[questionIndex].correctAnswer) {
                        selectedRadioButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.correct_color))
                        correctAnswerSound.start()
                        correctAnswers++
                    } else {
                        selectedRadioButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.wrong_color))
                        wrongAnswerSound.start()
                    }
                } catch (e: Exception) {
                    Log.e("SoundError", "Error playing sound: ${e.message}")
                }
                Toast.makeText(context, "Option selected: $selectedOption $correctAnswers", Toast.LENGTH_SHORT).show()
            }
            questionIndex++
            updateProgress(questionIndex)
            if (questionIndex >= quiz.results.size) {
                timer.cancel()
                Toast.makeText(context, "No more questions", Toast.LENGTH_SHORT).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, ResultsFragment.newInstance(correctAnswers, quiz.results.size))
                        .commit()
                }, 2000)
            } else {
                // For other questions, display the next question
                Handler(Looper.getMainLooper()).postDelayed({
                    displayQuestion(quiz.results[questionIndex])
                    timer.cancel()
                    timer.start()
                }, 3000)

            }
        }

        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        fragmentQuestionsBinding = null
        timer.cancel()
        correctAnswerSound.release()
        wrongAnswerSound.release()
    }

    companion object {
        @JvmStatic
        fun newInstance(quizType: String, quizDifficulty: String, quizCategory:String,numOfQuestions:String,timerDuration: Long) =
            QuestionsFragment().apply {
                arguments = Bundle().apply {
                    putString(QUIZ_TYPE, quizType)
                    putString(QUIZ_DIFFICULTY, quizDifficulty)
                    putString(QUIZ_CATEGORY,quizCategory)
                    putString(NUMBER_OF_QUESTIONS,numOfQuestions)
                    putLong(TIMER_DURATION,timerDuration)
                }
            }
    }

    private fun fetchQuizData() {

        GlobalScope.launch(Dispatchers.Main) {
            try {
                quiz = api.getQuiz(amount = amount.toInt(), category = category.toInt(), difficulty = difficulty, type = type)
                displayQuestion(quiz.results[0])
                binding.questionProgressBar.max = quiz.results.size
            } catch (e: Exception) {
                Toast.makeText(context, "Error in fetching quiz", Toast.LENGTH_SHORT).show()
                Log.d("Fetch",e.toString())
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun displayQuestion(
        quiz: QuizQuestion
    ) {
        val questionTextView = fragmentQuestionsBinding!!.questionTv
        val optionRadioGroup = fragmentQuestionsBinding!!.optionRadioGroup

        questionTextView.text = parseHtmlEntities(quiz.question)

        optionRadioGroup.removeAllViews()
        optionRadioGroup.clearCheck()

        val options:ArrayList<String> = quiz.incorrectAnswers
        options.add(quiz.correctAnswer)

        options.forEachIndexed{ idx,option ->
            val radioButton = RadioButton(requireContext())
            radioButton.text = parseHtmlEntities(option)
            radioButton.id = idx
            radioButton.background = context?.let { ContextCompat.getDrawable(it, R.drawable.quiz_options_selector) }
            radioButton.setPadding(16)
            radioButton.gravity = Gravity.CENTER_HORIZONTAL
            radioButton.buttonDrawable = ContextCompat.getDrawable(requireContext(), android.R.color.transparent)
            radioButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.radio_text_selector))
            val params = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.MATCH_PARENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
            )
            params.setMargins(30, 0, 30, 20) // Set your desired margin here
            radioButton.layoutParams = params
            optionRadioGroup.addView(radioButton)
        }
        timer.cancel()
        timer.start()
    }

    @SuppressLint("ObsoleteSdkInt")
    fun parseHtmlEntities(input: String): CharSequence {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY)
        } else {
            HtmlCompat.fromHtml(input, FROM_HTML_MODE_LEGACY)
        }
    }

    private fun updateProgress(currentQuestionIndex:Int) {
        binding.questionProgressBar.progress = currentQuestionIndex
    }
    
    private fun startTimer(timerDuration: Long) {
        timer = object : CountDownTimer(timerDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                fragmentQuestionsBinding!!.counterTV.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                fragmentQuestionsBinding!!.submitBtn.performClick()
            }
        }

        timer.start()
    }

}