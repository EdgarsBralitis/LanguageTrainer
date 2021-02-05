package com.example.trainer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import kotlin.random.Random

private lateinit var buttonAnswer1: Button
private lateinit var buttonAnswer2: Button
private lateinit var buttonAnswer3: Button
private lateinit var buttonAnswer4: Button

private var button1Text = "1. opcija"
private var button2Text = "2. opcija"
private var button3Text = "3. opcija"
private var button4Text = "4. opcija"

private var rigthAnswerButtonNumber = 0 //

private const val TAG = "MainActivity"
public var countOfAnswerOptions: Int = 0

class InterfaceValues(val languages: List<String>, val thematics: List<String>, val score: List<String>, var interfaceIndex: Int) {
    override fun toString(): String {
        return """InterfaceIndex: $interfaceIndex
                    Language: ${languages[interfaceIndex]}
                    Thematics: ${thematics[interfaceIndex]}
                    Score: ${score[interfaceIndex]}""".trimIndent()
    }
}

class MainActivity : AppCompatActivity() {

    private val buttonNative by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.buttonNative) }
    private val buttonForeign by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.buttonForeign) }
    private val buttonSwitchNativeForeign by lazy(LazyThreadSafetyMode.NONE) {
        findViewById<Button>(
                R.id.buttonSwitchNativeForeign
        )
    }

    private val textViewThematics by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.textViewThematics) }
    private val buttonNextThematics by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.buttonNextThematics) }
    private val buttonPreviousThematics by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.buttonPreviousThematics) }

    private val textViewQuestion by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.textViewQuestion) }

    private val textViewScore by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.textViewScore) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        countOfAnswerOptions = getString(R.string.CountOfAnswerOptions).toInt()

        val interfaceValues = InterfaceValues(getString(R.string.Languages).split(","), getString(R.string.Thematics).split(","), getString(R.string.Score).split(","), 1)


        val questioner = Questioner(getString(R.string.VocabulariesLang1), getString(R.string.VocabulariesLang2), interfaceValues)

        Log.d(TAG, questioner.toString())

        for (i in 1..10) {
            Log.d(TAG, "********Test No. $i. :***************")
            Log.d(TAG, "${questioner.getQuestionAndAnswers()}")
            questioner.handleUserAnswer(Random.nextInt(0, countOfAnswerOptions))
        }



    }
}