package com.example.quizmaster

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.quizmaster.databinding.ActivityMainBinding

private const val REQUEST_STORAGE_PERMISSION_CODE = 200
class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding:ActivityMainBinding
    private lateinit var toolbar:Toolbar
    private lateinit var sharedPreferences: SharedPreferences
    private var isDarkMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        sharedPreferences = this.getSharedPreferences("DarkModeSharedPref", Context.MODE_PRIVATE)
        isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)

        toolbar = activityMainBinding.toolbar
        setSupportActionBar(toolbar)

        activityMainBinding.lightDarkBtn.setOnClickListener {
            toggleTheme()
        }

        activityMainBinding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.home->{setFragment(HomeFragment())
                true}

                R.id.results->{setFragment(PreviousResultFragment())
                    true}

                else -> false
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_STORAGE_PERMISSION_CODE)
            }
        }

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

    private fun toggleTheme() {
        val editor = sharedPreferences.edit()

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            toolbar.contentDescription = getString(R.string.toggle_to_light_mode_description)
            editor.putBoolean("isDarkMode", false)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            toolbar.contentDescription = getString(R.string.toggle_to_dark_mode_description)
            editor.putBoolean("isDarkMode", true)
        }

        editor.apply()

        isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)


    }
}