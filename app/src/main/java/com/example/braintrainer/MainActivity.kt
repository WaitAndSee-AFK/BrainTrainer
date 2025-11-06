package com.example.braintrainer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.preference.PreferenceManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.util.Locale
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var textViewTimer: TextView
    private lateinit var textViewScore: TextView
    private lateinit var textViewExample: TextView
    private lateinit var textViewOpinionFirst: TextView
    private lateinit var textViewOpinionSecond: TextView
    private lateinit var textViewOpinionThird: TextView
    private lateinit var textViewOpinionFourth: TextView

    private val options = mutableListOf<TextView>()
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setupObservers()
        viewModel.startGame()
    }

    private fun setupObservers() {
        viewModel.timer.observe(this, Observer { text ->
            textViewTimer.text = text
        })

        viewModel.timerColor.observe(this, Observer { colorRes ->
            textViewTimer.setTextColor(ContextCompat.getColor(this, colorRes))
        })

        viewModel.score.observe(this, Observer { score ->
            textViewScore.text = score
        })

        viewModel.question.observe(this, Observer { question ->
            textViewExample.text = question
        })

        viewModel.answer.observe(this, Observer { answers ->
            for (i in options.indices) {
                options[i].text = answers.getOrNull(i)?.toString() ?: ""
            }
        })

        viewModel.toastM.observe(this, Observer { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.navigateToScore.observe(this, Observer { result ->
            if (result != -1) {
                val preferences: SharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(applicationContext)
                val max = preferences.getInt("max", 0)
                if (result >= max) {
                    preferences.edit().putInt("max", result).apply()
                }
                val intent = Intent(this, ScoreActivity::class.java)
                intent.putExtra("result", result)
                startActivity(intent)
            }
        })
    }

    private fun initViews() {
        textViewTimer = findViewById(R.id.textViewTimer)
        textViewScore = findViewById(R.id.textViewScore)
        textViewExample = findViewById(R.id.textViewExample)
        textViewOpinionFirst = findViewById(R.id.textViewOpinionFirst)
        textViewOpinionSecond = findViewById(R.id.textViewOpinionSecond)
        textViewOpinionThird = findViewById(R.id.textViewOpinionThird)
        textViewOpinionFourth = findViewById(R.id.textViewOpinionFourth)
        options.apply {
            add(textViewOpinionFirst)
            add(textViewOpinionSecond)
            add(textViewOpinionThird)
            add(textViewOpinionFourth)
        }
    }

    fun onClickAnswer(view: View) {
        val textView = view as TextView
        val answer: String = textView.text.toString()
        val chosenAnswer = answer.toIntOrNull() ?: return
        viewModel.onAnswerSelected(chosenAnswer)
    }
}