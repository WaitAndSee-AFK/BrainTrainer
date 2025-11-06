package com.example.braintrainer

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Locale
import kotlin.random.Random

class MainViewModel : ViewModel() {
    private val _timer = MutableLiveData<String>()
    val timer: LiveData<String>
        get() = _timer

    private val _timerColor = MutableLiveData<Int>()
    val timerColor: LiveData<Int>
        get() = _timerColor

    private val _score = MutableLiveData<String>()
    val score: LiveData<String>
        get() = _score

    private val _question = MutableLiveData<String>()
    val question: LiveData<String>
        get() = _question

    private val _answer = MutableLiveData<List<Int>>()
    val answer: LiveData<List<Int>>
        get() = _answer

    private val _toastM = MutableLiveData<String>()
    val toastM: LiveData<String>
        get() = _toastM

    private val _navigateToScore = MutableLiveData<Int>()
    val navigateToScore: LiveData<Int>
        get() = _navigateToScore

    private var rightAnswer: Int = 0
    private var rightAnswerPosition: Int = 0
    private var isPositive: Boolean = false
    private var minNumber = 5
    private var maxNumber = 50
    private var countOfQuestions = 0
    private var countOfRightAnswers = 0
    private var gameOver: Boolean = false

    fun startGame() {
        countOfQuestions = 0
        countOfRightAnswers = 0
        _timerColor.value = android.R.color.holo_green_light
        createTimer()
        generateQuestion()
    }

    private fun createTimer() {
        object : CountDownTimer(12000, 1000) {
            override fun onFinish() {
                _navigateToScore.value = countOfRightAnswers
            }

            override fun onTick(millisUntilFinished: Long) {
                _timer.value = getTimer(millisUntilFinished)
                if (millisUntilFinished < 10_000) {
                    _timerColor.value = android.R.color.holo_red_light
                }
            }
        }.start()
    }

    private fun generateWrongAnswer(): Int {
        var result: Int = -1
        do {
            result = Random.nextInt((maxNumber * 2 + 1) + (maxNumber - minNumber))
        } while (result == rightAnswer)
        return result
    }

    private fun generateAnswer() {
        val answersList = mutableListOf<Int>()
        for (i in 0 until 4) {
            if (i == rightAnswerPosition) {
                answersList.add(rightAnswer)
            } else {
                answersList.add(generateWrongAnswer())
            }
        }
        _answer.value = answersList
    }

    private fun updateScore() {
        _score.value = String.format("%s / %s", countOfRightAnswers, countOfQuestions)
    }

    private fun generateQuestion() {
        val a = Random.nextInt((maxNumber - minNumber + 1) + minNumber)
        val b = Random.nextInt((maxNumber - minNumber + 1) + minNumber)
        isPositive = Random.nextBoolean()
        if (isPositive) {
            rightAnswer = a + b
            _question.value = String.format("%s + %s", a, b)
        } else {
            rightAnswer = a - b
            _question.value = String.format("%s - %s", a, b)
        }
        rightAnswerPosition = Random.nextInt(4)
        generateAnswer()
        updateScore()
    }

    private fun getTimer(mills: Long): String {
        var seconds: Int = (mills / 1_000).toInt()
        val minutes: Int = seconds / 60
        seconds = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    fun onAnswerSelected(chosenAnswer: Int) {
        if (chosenAnswer == rightAnswer) {
            countOfRightAnswers++
            _toastM.value = "Верно"
        } else {
            _toastM.value = "Неверно"
        }
        countOfQuestions++
        generateQuestion()
    }
}