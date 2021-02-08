package com.example.trainer

import android.util.Log
import kotlin.random.Random

class QuestionAndAnswers(val question: String, val answerOptions: List<String>, val correctAnswerOptionIndex: Int, val themticsName: String, val percentageOfSuccesses: Int, val interfaceValues: InterfaceValues) {

    override fun toString(): String {
        return """\n
                  Interface: $interfaceValues
                  PercentageOfSuccesses: $percentageOfSuccesses
                  Thematics: $themticsName
                  Question: $question
                  AnswerOptions: $answerOptions
                  correctAnswerOptionIndex: $correctAnswerOptionIndex"""
    }
}

private const val TAG = "Questioner"

class Questioner(inputStringLang0: String, inputStringLang1: String, interfaceValues: InterfaceValues) {

    class Vocabulary(thematicsLang0: String, thematicsLang1: String, listOfWords0: List<String>, listOfWords1: List<String>) {

        class WordPair(wordLang0: String, wordLang1: String) {
            operator fun get(index: Int): String {
                return word[index]
            }

            private val word: MutableList<String> = mutableListOf()
            var value = 0 //shows the need to remember this word pair. correct answer with first try decreases value by 1. wrong answer increases value by 1

            init {
                word.add(wordLang0)
                word.add(wordLang1)
            }
            //word[0] is translation of word[1] and vice versa
        }

        var listOfWordPairs: MutableList<WordPair> = mutableListOf()//question and correct answer pairs which belong to thematics

        init {
            if (listOfWords0.lastIndex != listOfWords1.lastIndex)
                throw Exception("Vocabulary Init (Count of phrases: in $thematicsLang0 ${listOfWords0.count()}, in $thematicsLang1 ${listOfWords1.count()})")

            if (listOfWords0.distinct().count() < countOfAnswerOptions)
                throw Exception("Vocabulary Init listOfWords1.distinct().count() < $countOfAnswerOptions")

            if (listOfWords1.distinct().count() < countOfAnswerOptions)
                throw Exception("Vocabulary Init listOfWords2.distinct().count() < $countOfAnswerOptions")

            for (i in 0..listOfWords0.lastIndex) {
                listOfWordPairs.add(WordPair(listOfWords0[i], listOfWords1[i]))
            }
        }



       val thematics: MutableList<String> = mutableListOf()//thematics name in first language and second language

        init {
            thematics.add(thematicsLang0)
            thematics.add(thematicsLang1)
        }


    }

    private val interfaceValues = interfaceValues

    private val inputStringListLang0: List<String> = inputStringLang0.split(",")//data to recreate vocabulary from is stored here
    private val inputStringListLang1: List<String> = inputStringLang1.split(",")//data to recreate vocabulary from is stored here
    private var vocabularies: MutableList<Vocabulary> = mutableListOf()

    private var currentVocabularyIndex = 0
    private var currentQuestionIndex = 0

    private var currentCorrectAnswerOption = -1
    private var countOfCurrentQuestionAnsweringTries = 0

    private var countOfQuestionsAnswered = 0
    private var countOfSuccessfulFirstTryAnswers = 0

    private var indexPrimaryLang = interfaceValues.interfaceIndex
    private var indexSecondaryLang = (indexPrimaryLang + 1) % 2

    override fun toString(): String {
        var result = ""
        result += "${vocabularies.count()} vocabularies:\n"

        for (i in 0..vocabularies.lastIndex) {
            result += " *** ${vocabularies[i].thematics[indexPrimaryLang]} / ${vocabularies[i].thematics[indexSecondaryLang]} (${vocabularies[i].listOfWordPairs.count()})"
        }

        return result
    }

    init {
        if (inputStringListLang0.count() != inputStringListLang1.count()) {
            throw Exception("inputStringListLang0.count() = ${inputStringListLang0.count()} and inputStringListLang1.count() = ${inputStringListLang1.count()}")
        }
        initializeVocabularies()
    }

    private fun initializeVocabularies() {
        var openingTagPosition = 0
        var closingTagPosition: Int
        var thematicsNameLang0 = ""
        var thematicsNameLang1 = ""
        var openingTagOccurred = false

        Log.d(TAG, "initializeVocabularies()")
        for (i in 0..inputStringListLang0.lastIndex) {
            if ((inputStringListLang0[i][0] == '/' && inputStringListLang1[i][0] != '/') || (inputStringListLang0[i][0] != '/' && inputStringListLang1[i][0] == '/')) {
                throw Exception("initializeVocabularies(): inputStringLang1[$i][0] = ${inputStringListLang0[i][0]} and inputStringLang2[$i][0] = ${inputStringListLang1[i][0]}");
            }
            if (inputStringListLang0[i][0] == '/') {
                if (!openingTagOccurred) {
                    thematicsNameLang0 = inputStringListLang0[i].drop(1)
                    thematicsNameLang1 = inputStringListLang1[i].drop(1)
                    openingTagPosition = i
                    openingTagOccurred = true
                } else {
                    closingTagPosition = i
                    openingTagOccurred = false
                    val vocabulary = Vocabulary(
                            thematicsLang0 = thematicsNameLang0,
                            thematicsLang1 = thematicsNameLang1,
                            listOfWords0 = inputStringListLang0.subList(
                                    openingTagPosition + 1,
                                    closingTagPosition
                            ),
                            listOfWords1 = inputStringListLang1.subList(
                                    openingTagPosition + 1,
                                    closingTagPosition
                            )
                    )
                    if (inputStringListLang0[openingTagPosition] != inputStringListLang0[closingTagPosition])
                        throw Exception("initializeVocabularies(): ${inputStringListLang0[openingTagPosition]} != ${inputStringListLang0[closingTagPosition]}")
                    if (inputStringListLang1[openingTagPosition] != inputStringListLang1[closingTagPosition])
                        throw Exception("initializeVocabularies(): ${inputStringListLang1[openingTagPosition]} != ${inputStringListLang1[closingTagPosition]}")

                    vocabularies.add(vocabulary)
                }
            }
        }
        currentVocabularyIndex = Random.nextInt(vocabularies.count())
    }

    private fun wrongAnswersDiffersFromRight(incorrectAnswerIndex: Int, indexOfSelectedAnswers: List<Int>): Boolean {
        for (index in indexOfSelectedAnswers) {
            if (vocabularies[currentVocabularyIndex].listOfWordPairs[index][indexSecondaryLang] == vocabularies[currentVocabularyIndex].listOfWordPairs[incorrectAnswerIndex][indexSecondaryLang]) return false
            if (vocabularies[currentVocabularyIndex].listOfWordPairs[index][indexPrimaryLang] == vocabularies[currentVocabularyIndex].listOfWordPairs[incorrectAnswerIndex][indexPrimaryLang]) return false
        }
        return true
    }

    private fun shuffleCorrectAnswer(countOfAnswerOptions: Int, correctAnswerIndex: Int): List<String> {
        //returns List<String> of answer options. One option is right answer, the rest are wrong answers. Correct answer is randomly placed in list.
        var correctAnswerOptionNumber = Random.nextInt(0, countOfAnswerOptions)

        var indexOfSelectedAnswers: MutableList<Int> = mutableListOf()
        var selectedAnswers: MutableList<String> = mutableListOf()

        for (i in 0 until countOfAnswerOptions) {
            if (i == correctAnswerOptionNumber) {
                indexOfSelectedAnswers.add(correctAnswerIndex)
                selectedAnswers.add(vocabularies[currentVocabularyIndex].listOfWordPairs[correctAnswerIndex][indexSecondaryLang])
            }
            else {
                var incorrectAnswerIndex: Int

                do {
                    incorrectAnswerIndex = Random.nextInt(0, vocabularies[currentVocabularyIndex].listOfWordPairs.count())
                } while (indexOfSelectedAnswers.contains(incorrectAnswerIndex) || incorrectAnswerIndex == correctAnswerIndex || !wrongAnswersDiffersFromRight(incorrectAnswerIndex, indexOfSelectedAnswers))

                indexOfSelectedAnswers.add(incorrectAnswerIndex)
                selectedAnswers.add(vocabularies[currentVocabularyIndex].listOfWordPairs[incorrectAnswerIndex][indexSecondaryLang])
            }
        }


        return selectedAnswers
    }

    fun getQuestionAndAnswers(): QuestionAndAnswers {
        Log.d(TAG, "getQuestionAndAnswers()")
        currentQuestionIndex = Random.nextInt(0, vocabularies[currentVocabularyIndex].listOfWordPairs.count())

        val question = vocabularies[currentVocabularyIndex].listOfWordPairs[currentQuestionIndex][indexPrimaryLang]
        val correctAnswer = vocabularies[currentVocabularyIndex].listOfWordPairs[currentQuestionIndex][indexSecondaryLang]

        val answerOptions = shuffleCorrectAnswer(countOfAnswerOptions, currentQuestionIndex)

        //Log.d(TAG, "getQuestionAndAnswers(): currentQuestionIndex= $currentQuestionIndex *** $question *** correctAnswer= $correctAnswer (${answerOptions.indexOf(correctAnswer)}) *** answerOptions: $answerOptions")

        currentCorrectAnswerOption = answerOptions.indexOf(correctAnswer)

        countOfCurrentQuestionAnsweringTries = 0

        val percentageOfSuccesses = if (countOfQuestionsAnswered == 0) 0 else (countOfSuccessfulFirstTryAnswers.toFloat()/countOfQuestionsAnswered.toFloat() * 100).toInt()

        //Log.d(TAG, "getQuestionAndAnswers(): $countOfSuccessfulFirstTryAnswers/$countOfQuestionsAsked * 100 = ${(countOfSuccessfulFirstTryAnswers.toFloat()/countOfQuestionsAsked.toFloat() * 100).toInt()}")

        return QuestionAndAnswers(question, answerOptions, currentCorrectAnswerOption, vocabularies[currentVocabularyIndex].thematics[indexPrimaryLang], percentageOfSuccesses, interfaceValues)
    }

    private fun currentVocabularyIsOK(): Boolean {
        //returns true when both languages in current vocabulary have at least counOfAnswerOptions different phrases
        Log.d(TAG, "currentVocabularyIsOK()")
        val temp0: MutableList<String> = mutableListOf()
        val temp1: MutableList<String> = mutableListOf()
        for (i in 0 until vocabularies[currentVocabularyIndex].listOfWordPairs.count()) {
            temp0.add(vocabularies[currentVocabularyIndex].listOfWordPairs[i][0])
            temp1.add(vocabularies[currentVocabularyIndex].listOfWordPairs[i][0])
            if (temp0.distinct().count() >= countOfAnswerOptions && temp1.distinct().count() >= countOfAnswerOptions) return true
        }
        return false
    }

    fun handleUserAnswer(answerOptionIndex: Int) {
        Log.d(TAG, "handleUserAnswer($answerOptionIndex):")

        if (countOfCurrentQuestionAnsweringTries == 0) countOfQuestionsAnswered += 1

        countOfCurrentQuestionAnsweringTries += 1
        if (answerOptionIndex == currentCorrectAnswerOption) {
            Log.d(TAG, "Answer $answerOptionIndex is correct!")
            if (countOfCurrentQuestionAnsweringTries == 1) {
                vocabularies[currentVocabularyIndex].listOfWordPairs[currentQuestionIndex].value -= 1
                countOfSuccessfulFirstTryAnswers += 1
            }
        } else {
            Log.d(TAG, "Answer $answerOptionIndex is wrong!")
            if (countOfCurrentQuestionAnsweringTries == 1) vocabularies[currentVocabularyIndex].listOfWordPairs[currentQuestionIndex].value += 1
        }


        if (vocabularies[currentVocabularyIndex].listOfWordPairs[currentQuestionIndex].value < 0) {
            forgetCurrentQuestionAnswerPair()
        }

    }

    private fun forgetCurrentQuestionAnswerPair() {
        Log.d(TAG, "forgetCurrentQuestionAnswerPair()")
        vocabularies[currentVocabularyIndex].listOfWordPairs.removeAt(currentQuestionIndex)
        //if (vocabularies[currentVocabularyIndex].listOfWordPairs.lastIndex < currentQuestionIndex)

        if (!currentVocabularyIsOK()) {
            forgetCurrentVocabulary()
        }
    }

    private fun forgetCurrentVocabulary() {
        Log.d(TAG, "forgetCurrentVocabulary()")
        vocabularies.removeAt(currentVocabularyIndex)
        if (vocabularies.count() == 0) restoreAllVocabularies()
        if (currentVocabularyIndex > vocabularies.lastIndex) currentVocabularyIndex = vocabularies.lastIndex
    }

    fun switchLanguages() {
        indexPrimaryLang = (indexPrimaryLang + 1) % 2
        indexSecondaryLang = (indexSecondaryLang + 1) % 2
        interfaceValues.interfaceIndex = (interfaceValues.interfaceIndex + 1) % 2
        Log.d(TAG, "switchLagnuages(): indexPrimaryLang=$indexPrimaryLang and indexSecondaryLang=$indexSecondaryLang; interfaceIndex=${interfaceValues.interfaceIndex}")
    }

    fun nextVocabulary() {
        Log.d(TAG, "nextVocabulary(): currentVocabularyIndex was $currentVocabularyIndex")
        currentVocabularyIndex = (currentVocabularyIndex + 1) % vocabularies.count()
        Log.d(TAG, "nextVocabulary(): currentVocabularyIndex is $currentVocabularyIndex")
    }

    fun previousVocabulary() {
        Log.d(TAG, "previousVocabulary(): currentVocabularyIndex was $currentVocabularyIndex")
        currentVocabularyIndex -= 1
        if (currentVocabularyIndex < 0) currentVocabularyIndex = vocabularies.lastIndex
        Log.d(TAG, "previousVocabulary(): currentVocabularyIndex is $currentVocabularyIndex")
    }

    private fun restoreAllVocabularies() {
        Log.d(TAG, "restoreAllVocabularies()")
        vocabularies = mutableListOf()
        initializeVocabularies()
    }
}