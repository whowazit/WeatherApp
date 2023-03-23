package com.jayjohn.app.feature.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jayjohn.app.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.main_container, WeatherFragment()).commit()
    }
}