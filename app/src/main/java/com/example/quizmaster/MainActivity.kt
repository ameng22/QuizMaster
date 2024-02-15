package com.example.quizmaster

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.quizmaster.databinding.ActivityMainBinding

private const val REQUEST_STORAGE_PERMISSION_CODE = 200
class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val homeFragment = HomeFragment()

        setFragment(homeFragment)
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(activityMainBinding.mainFragment.id,fragment)
            commit()
        }
    }

    fun requestPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQUEST_STORAGE_PERMISSION_CODE
                )
            } else {
                val fragment =
                    supportFragmentManager.findFragmentById(R.id.main_fragment) as? ResultsFragment
                try {
                    fragment?.generatePdf()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            // Permission is not needed for Android 11 (API level 30) or higher
            val fragment =
                supportFragmentManager.findFragmentById(R.id.main_fragment) as? ResultsFragment
            try {
                fragment?.generatePdf()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}