package com.example.braintrainer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ScoreActivity : AppCompatActivity() {
    private lateinit var textViewResult: TextView
    private lateinit var buttonStartNewGame: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)
        initViews()
        val intent = intent
        if (intent != null && intent.hasExtra("result")) {
            val result = intent.getIntExtra("result", 0)
            val preferences: SharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this)
            val max = preferences.getInt("max", 0)
            textViewResult.text = String
                .format("Ваш результат: %s\nМаксимальный результат: %s", result, max)
        }
    }

    fun onClickStartNewGame(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initViews() {
        textViewResult = findViewById(R.id.textViewResult)
        buttonStartNewGame = findViewById(R.id.buttonStartNewGame)
    }
}