package com.example.quizmaster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizmaster.databinding.FragmentPreviousResultBinding

class PreviousResultFragment : Fragment() {
    private var _binding: FragmentPreviousResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DBHelper
    private lateinit var resultAdapter: ResultsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviousResultBinding.inflate(inflater, container, false)
        val view = binding.root

        dbHelper = DBHelper(requireContext(), null)
        resultAdapter = ResultsAdapter(dbHelper.getName()!!)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = resultAdapter
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
