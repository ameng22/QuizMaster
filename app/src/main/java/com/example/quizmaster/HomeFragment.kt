package com.example.quizmaster

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quizmaster.databinding.FragmentHomeBinding
import com.example.quizmaster.model.QuizCategory

class HomeFragment : Fragment(), QuizCategoryClickListener {

    private var fragmentHomeBinding:FragmentHomeBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        val view = fragmentHomeBinding!!.root

        setupRecyclerView()

        return view;
    }

    private fun setupRecyclerView() {
        val quizCategories: List<QuizCategory> = listOf(
            QuizCategory("Sports", R.drawable.sports,21),
            QuizCategory("Politics", R.drawable.politics,24),
            QuizCategory("Arts", R.drawable.art,25),
            QuizCategory("General", R.drawable.ideas,9),
            QuizCategory("Celebrity", R.drawable.fame,26),
            QuizCategory("Movies", R.drawable.tv,11),
            QuizCategory("History", R.drawable.history,23),
            QuizCategory("Computer", R.drawable.gaming,18),
            QuizCategory("Maths", R.drawable.tools,19),
        )

        val adapter = QuizAdapter(quizCategories, this)
        fragmentHomeBinding?.quizRecyclerView?.adapter = adapter

        fragmentHomeBinding?.quizRecyclerView?.layoutManager = GridLayoutManager(context, 4)
    }

    override fun onQuizCategoryClicked(quizCategory: QuizCategory, title: String) {

        val sharedPreferences = requireActivity().getSharedPreferences("QuizMasterPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selectedQuizTitle", title)
        editor.apply()


        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, QuestionSettingsFragment.newInstance(quizCategory.number))
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentHomeBinding = null
    }

}