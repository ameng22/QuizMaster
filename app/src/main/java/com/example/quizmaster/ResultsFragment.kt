package com.example.quizmaster

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.quizmaster.databinding.FragmentResultsBinding
import okhttp3.internal.notify
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


private const val ARG_CORRECT_ANSWERS = "correctAnswers"
private const val ARG_QUIZ_SIZE = "quizSize"
private const val CHANNEL_ID = "channel1"
private const val NOTIFICATION_ID = 1

class ResultsFragment : Fragment() {

    private var correctAnswers: Int = 0
    private var quizSize: Int = 0
    private var binding:FragmentResultsBinding?=null
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    private val channelId = "i.apps.notifications"
    private val description = "Download notification"
    lateinit var builder: Notification.Builder
    private lateinit var name:String
    private lateinit var dob:String
    private lateinit var gender:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            correctAnswers = it.getInt(ARG_CORRECT_ANSWERS)
            quizSize = it.getInt(ARG_QUIZ_SIZE)
        }

        sharedPreferences = requireActivity().getSharedPreferences("ParticipantDetailsSharedPref", Context.MODE_PRIVATE)
        name = sharedPreferences.getString("name", "").toString()
        dob = sharedPreferences.getString("dob", "").toString()
        gender = sharedPreferences.getString("gender", "").toString()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentResultsBinding.inflate(inflater, container, false)
        val view = binding!!.root

        displayResults()

        binding?.downloadBtn?.setOnClickListener {
            requestStoragePermission()
        }

        binding?.saveBtn?.setOnClickListener {
            saveResult()
        }

        view.findViewById<Button>(R.id.educationalDocumentsBtn)?.setOnClickListener {
            openEducationalDocuments()
        }

        return view
    }

    private fun saveResult(){
        val db = context?.let { DBHelper(it, null) }

        if (db != null) {
            if (correctAnswers.toString() != "" && quizSize.toString() != "") {
                db.addData(name, dob, gender, correctAnswers.toString(), quizSize.toString())

                Log.d("Data Insertion", "Attempting to insert data into the database")

                Log.d("Navigation", "Navigating to PreviousResultFragment")

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, PreviousResultFragment())
                    .commit()
            }
        } else {
            Toast.makeText(context, "No Database Found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayResults() {
        binding!!.score.text = correctAnswers.toString()
        binding!!.numOfQuestions.text = quizSize.toString()

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

    private fun requestStoragePermission() {
        val activity = requireActivity() as MainActivity
        activity.requestPermissionIfNeeded()
    }

    fun generatePdf() {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        paint.color = Color.BLACK

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        val contentPaint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
            textAlign = Paint.Align.LEFT
        }

        val lineHeight = 30f

        canvas.drawText("Quiz Results", pageInfo.pageWidth.toFloat() / 2, lineHeight, titlePaint)
        canvas.drawText("Score: ${correctAnswers}/${quizSize}", 20f, lineHeight * 3, contentPaint)
        val name = sharedPreferences.getString("name", "")
        val dob = sharedPreferences.getString("dob", "")
        val gender = sharedPreferences.getString("gender", "")
        canvas.drawText("Participant Name: $name", 20f, lineHeight * 5, contentPaint)
        canvas.drawText("Participant Age: $dob", 20f, lineHeight * 6, contentPaint)
        canvas.drawText("Participant Gender: $gender", 20f, lineHeight * 7, contentPaint)

        pdfDocument.finishPage(page)

        downloadPDF(pdfDocument)

        pdfDocument.close()
    }

    private fun downloadPDF(pdfDocument: PdfDocument){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "${System.currentTimeMillis()}_quiz_results.pdf")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requireContext().contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            } else {
                TODO("VERSION.SDK_INT < Q")
            }
            uri?.let { outputStream ->
                try {
                    requireContext().contentResolver.openOutputStream(outputStream)?.use { pdfDocument.writeTo(it) }
                    createNotificationChannel(true)
                    Toast.makeText(requireContext(), "PDF saved to Downloads the directory", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    createNotificationChannel(false)
                    Toast.makeText(requireContext(), "Failed to save PDF", Toast.LENGTH_SHORT).show()
                } finally {
                    pdfDocument.close()
                }
            }
        }else{
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, "${System.currentTimeMillis()}_quiz_results.pdf")
                pdfDocument.writeTo(FileOutputStream(file))
                createNotificationChannel(true)
                Toast.makeText(requireContext(), "PDF saved to Downloads directory", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                createNotificationChannel(false)
                Toast.makeText(requireContext(), "Failed to save PDF", Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun openEducationalDocuments() {

        val educationalDocumentsUrl = getEducationalUrlForQuiz()

        if (educationalDocumentsUrl.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(educationalDocumentsUrl))

            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "No URL found for this quiz", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getEducationalUrlForQuiz(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("QuizMasterPrefs", Context.MODE_PRIVATE)
        val selectedQuizTitle = sharedPreferences.getString("selectedQuizTitle", "") ?: ""

        return when (selectedQuizTitle) {
            "Sports" -> "https://archive.org/details/questionofsportq0000unse/mode/2up"
            "Politics" -> "https://kwizzbit.com/politics-quiz-questions-and-answers/"
            "Arts" -> "https://kwizzbit.com/art-quiz-questions-and-answers/"
            "General" -> "https://www.cosmopolitan.com/uk/worklife/a32388181/best-general-knowledge-quiz-questions/"
            "Celebrity" -> "https://thoughtcatalog.com/january-nelson/2021/10/celebrity-trivia/"
            "Movies" -> "https://parade.com/977752/samuelmurrian/movie-trivia/"
            "History" -> "https://www.rd.com/list/history-questions/"
            "Computer" -> "https://www.proprofs.com/quiz-school/topic/computer"
            "Maths" -> "https://www.proprofs.com/quiz-school/topic/math"
            else -> ""
        }
    }

    private fun createNotificationChannel(flag:Boolean){
        notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (flag == true){
            displayNotifications("Dowload Finished","Your download is Complete",R.drawable.download_success)
        }else{
            displayNotifications("Dowload Failed","Sorry, Your download could not be completed! ",R.drawable.download_fail)
        }
    }

    private fun displayNotifications(title:String,text:String,iconId:Int){



        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(context, channelId)
                .setSmallIcon(iconId)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, iconId))
                .setContentTitle(title)
                .setContentText(text)

        }else{
            builder = Notification.Builder(context)
                .setSmallIcon(iconId)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, iconId))
                .setContentTitle(title)
                .setContentText(text)
        }
        notificationManager.notify(NOTIFICATION_ID,builder.build())

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