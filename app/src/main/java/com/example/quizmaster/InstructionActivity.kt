package com.example.quizmaster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quizmaster.databinding.ActivityInstructionBinding

class InstructionActivity : AppCompatActivity() {
    private lateinit var instructionBinding:ActivityInstructionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instructionBinding = ActivityInstructionBinding.inflate(layoutInflater)
        setContentView(instructionBinding.root)

        val instructionFragment1 = InstructionFragment1()

        supportFragmentManager.beginTransaction().apply {
            replace(instructionBinding.fragmentView.id,instructionFragment1).commit()
        }
    }
}