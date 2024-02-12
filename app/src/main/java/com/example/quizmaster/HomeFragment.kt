package com.example.quizmaster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quizmaster.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var fragmentHomeBinding:FragmentHomeBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        val view = fragmentHomeBinding!!.root

        fragmentHomeBinding!!.generalCard.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.main_fragment,QuizSettingsFragment()).commit()
        }

        return view;
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentHomeBinding = null
    }

}