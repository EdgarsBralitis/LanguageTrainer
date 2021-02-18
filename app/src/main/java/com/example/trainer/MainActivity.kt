package com.example.trainer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
//import kotlinx.coroutines.flow.flow

private lateinit var buttonAnswer1: Button
private lateinit var buttonAnswer2: Button
private lateinit var buttonAnswer3: Button
private lateinit var buttonAnswer4: Button

//onscreen values (start of declaration)
private var textViewScoreText = "%"
private var textViewQuestionText = "?"

private var button1Text = "1"
private var button2Text = "2"
private var button3Text = "3"
private var button4Text = "4"

private var textViewLang1Text = ""
private var buttonSwitchLanguagesText = "<->"
private var textViewlang2 = ""

private var buttonPreviousThematicsText = "<"
private var textViewThematicsText = ""
private var buttonNextThematicsText = ">"
//onscreen values (end of declaration)

private var rigthAnswerButtonNumber = 0 //

private const val TAG = "MainActivity"
var countOfAnswerOptions: Int = 0

class MainActivity : AppCompatActivity() {

    private val textViewLang1 by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.textViewLang1) }
    private val textViewLang2 by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.textViewLang2) }
    private val buttonSwitchNativeForeign by lazy(LazyThreadSafetyMode.NONE) {
        findViewById<Button>(
                R.id.buttonSwitchLanguages
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




//        val flowTest = flow<QuestionAndAnswers> {
//            val questioner = Questioner(getString(R.string.VocabulariesLang1), getString(R.string.VocabulariesLang2), interfaceValues)
//
//            for (i in 1 .. 10) {
//                emit(questioner.getQuestionAndAnswers())
//            }
//        }
//
//        launch {}

//        Log.d(TAG, questioner.toString())

//        for (i in 1..200) {
//            Log.d(TAG, "********Test No. $i. :***************")
//
//            var questionAndAnswers = questioner.getQuestionAndAnswers()
//            Log.d(TAG, "$questionAndAnswers")
//            questioner.handleUserAnswer(if (i%7 == 0) 0 else questionAndAnswers.correctAnswerOptionIndex)
//            questioner.switchLanguages()
//            questioner.nextVocabulary()
//        }



    }
}