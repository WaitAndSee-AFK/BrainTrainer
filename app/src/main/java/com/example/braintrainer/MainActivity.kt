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

    private lateinit var question: String
    private var rightAnswer: Int = 0
    private var rightAnswerPosition: Int = 0
    private var isPositive: Boolean = false
    private var minNumber = 5
    private var maxNumber = 50
    private var countOfQuestions = 0
    private var countOfRightAnswers = 0
    private var gameOver: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        createTimer()
    }

    private fun createTimer() {
        object : CountDownTimer(12000, 1000) {
            override fun onFinish() {
                gameOver = true
                val preferences: SharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(applicationContext)
                val max = preferences.getInt("max", 0)
                if (countOfRightAnswers >= max) {
                    preferences.edit().putInt("max", countOfRightAnswers).apply()
                }
                val intent = Intent(this@MainActivity, ScoreActivity::class.java)
                intent.putExtra("result", countOfRightAnswers)
                startActivity(intent)
            }

            override fun onTick(millisUntilFinished: Long) {
                textViewTimer.text = getTimer(millisUntilFinished)
                if (millisUntilFinished < 10_000) {
                    textViewTimer.setTextColor(resources.getColor(android.R.color.holo_red_light))
                }
            }
        }.start()
    }

    private fun getTimer(mills: Long): String {
        var seconds: Int = (mills / 1_000).toInt()
        val minutes: Int = seconds / 60
        seconds = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    fun onClickAnswer(view: View) {
        if (!gameOver) {
            val textView = view as TextView
            val answer: String = textView.text.toString()
            val chosenAnswer = answer.toInt()
            if (chosenAnswer == rightAnswer) {
                countOfRightAnswers++
                Toast.makeText(
                    this@MainActivity,
                    "Верно",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Неверно",
                    Toast.LENGTH_SHORT
                ).show()
            }
            countOfQuestions++
            playNext()
        }
    }

    private fun generateWrongAnswer(): Int {
        var result: Int = -1
        do {
            result = Random.nextInt((maxNumber * 2 + 1) + (maxNumber - minNumber))
        } while (result == rightAnswer)
        return result
    }

    private fun generateQuestion() {
        val a = Random.nextInt((maxNumber - minNumber + 1) + minNumber)
        val b = Random.nextInt((maxNumber - minNumber + 1) + minNumber)
        isPositive = Random.nextBoolean()
        if (isPositive) {
            rightAnswer = a + b
            question = String.format("%s + %s", a, b)
        } else {
            rightAnswer = a - b
            question = String.format("%s - %s", a, b)
        }
        textViewExample.text = question
        rightAnswerPosition = Random.nextInt(4)
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
       playNext()
    }

    private fun playNext() {
        generateQuestion()
        for (i in 0 until options.size) {
            if (i == rightAnswerPosition) {
                options.get(i).text = rightAnswer.toString()
            } else {
                options.get(i).text = generateWrongAnswer().toString()
            }
        }
        val score: String = String.format("%s / %s", countOfRightAnswers, countOfQuestions)
        textViewScore.text = score
    }
}