package com.example.trainer

import android.util.Log
import kotlin.random.Random

class QuestionAndAnswers(question: String, answer: List<String>, correctAnswerIndex: Int) {
    val question = question
    var answer: List<String> = answer
    val correctAnswerIndex = correctAnswerIndex
}

private const val TAG = "Questioner"

class Questioner(inputStringLang1: String, inputStringLang2: String) {

    class Vocabulary(thematicsLang1: String, thematicsLang2: String, listOfWords1: List<String>, listOfWords2: List<String>) {
        class WordPair(wordLang1: String, wordLang2: String) {
            operator fun get(index: Int): String {
                return word[index]
            }

            var word: MutableList<String> = mutableListOf()
            var value = 0 //shows the need to remember this word pair. correct answer with first try decreases value by 1. wrong answer increases value by 1

            init {
                word.add(wordLang1)
                word.add(wordLang2)
            }
            //word[0] is translation of word[1] and vice versa
        }

        val thematicsLang1: String = thematicsLang1 //thematics in first language
        val thematicsLang2: String = thematicsLang2 //thematics in second language
        var listOfWordPairs: MutableList<WordPair> = mutableListOf()//question and correct answer pairs which belong to thematics

        init {
            if (listOfWords1.lastIndex != listOfWords2.lastIndex)
                throw Exception("Vocabulary Init (Count of phrases: in $thematicsLang1 ${listOfWords1.count()}, in $thematicsLang2 ${listOfWords2.count()})")

            if (listOfWords1.distinct().count() < countOfAnswerOptions)
                throw Exception("Vocabulary Init listOfWords1.distinct().count() < $countOfAnswerOptions")

            if (listOfWords2.distinct().count() < countOfAnswerOptions)
                throw Exception("Vocabulary Init listOfWords2.distinct().count() < $countOfAnswerOptions")

            for (i in 0..listOfWords1.lastIndex) {
                listOfWordPairs.add(WordPair(listOfWords1[i], listOfWords2[i]))
            }
        }
    }

    private val inputStringListLang1: List<String> = inputStringLang1.split(",")//data to recreate vocabulary from is stored here
    private val inputStringListLang2: List<String> = inputStringLang2.split(",")//data to recreate vocabulary from is stored here
    private var vocabularies: MutableList<Vocabulary> = mutableListOf()

    private var currentVocabularyIndex = 0
    private var currentQuestionIndex = 0

    private var indexPrimaryLang = Random.nextInt(0, 2)
    private var indexSecondaryLang = (indexPrimaryLang + 1) % 2

    override fun toString(): String {
        var result = ""
        result += "${vocabularies.count()} vocabularies:\n"

        for (i in 0..vocabularies.lastIndex) {
            result += " *** ${vocabularies[i].thematicsLang1} / ${vocabularies[i].thematicsLang2} (${vocabularies[i].listOfWordPairs.count()})"
        }

        return result
    }

    init {
        if (inputStringListLang1.count() != inputStringListLang2.count()) {
            throw Exception("inputStringListLang1.count() = ${inputStringListLang1.count()} and inputStringListLang2.count() = ${inputStringListLang2.count()}")
        }
        initializeVocabularies()
    }

    private fun initializeVocabularies() {
        var openingTagPosition = 0
        var closingTagPosition: Int
        var thematicsNameLang1 = ""
        var thematicsNameLang2 = ""
        var openingTagOccurred = false

        for (i in 0..inputStringListLang1.lastIndex) {
            if ((inputStringListLang1[i][0] == '/' && inputStringListLang2[i][0] != '/') || (inputStringListLang1[i][0] != '/' && inputStringListLang2[i][0] == '/')) {
                throw Exception("initializeVocabularies(): inputStringLang1[$i][0] = ${inputStringListLang1[i][0]} and inputStringLang2[$i][0] = ${inputStringListLang2[i][0]}");
            }
            if (inputStringListLang1[i][0] == '/') {
                if (!openingTagOccurred) {
                    thematicsNameLang1 = inputStringListLang1[i].drop(1)
                    thematicsNameLang2 = inputStringListLang2[i].drop(1)
                    openingTagPosition = i
                    openingTagOccurred = true
                } else {
                    closingTagPosition = i
                    openingTagOccurred = false
                    val vocabulary = Vocabulary(
                            thematicsLang1 = thematicsNameLang1,
                            thematicsLang2 = thematicsNameLang2,
                            listOfWords1 = inputStringListLang1.subList(
                                    openingTagPosition + 1,
                                    closingTagPosition
                            ),
                            listOfWords2 = inputStringListLang2.subList(
                                    openingTagPosition + 1,
                                    closingTagPosition
                            )
                    )
                    if (inputStringListLang1[openingTagPosition] != inputStringListLang1[closingTagPosition])
                        throw Exception("initializeVocabularies(): ${inputStringListLang1[openingTagPosition]} != ${inputStringListLang1[closingTagPosition]}")
                    if (inputStringListLang2[openingTagPosition] != inputStringListLang2[closingTagPosition])
                        throw Exception("initializeVocabularies(): ${inputStringListLang2[openingTagPosition]} != ${inputStringListLang2[closingTagPosition]}")

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

    fun giveQuestionAndAnswers(): QuestionAndAnswers {
        currentQuestionIndex = Random.nextInt(0, vocabularies[currentVocabularyIndex].listOfWordPairs.count())

        var question = vocabularies[currentVocabularyIndex].listOfWordPairs[currentQuestionIndex][indexPrimaryLang]
        var correctAnswer = vocabularies[currentVocabularyIndex].listOfWordPairs[currentQuestionIndex][indexSecondaryLang]

        var answerOptions = shuffleCorrectAnswer(countOfAnswerOptions, currentQuestionIndex)

        Log.d(TAG, "giveQuestionAndAnswers(): currentQuestionIndex= $currentQuestionIndex *** $question *** correctAnswer= $correctAnswer (${answerOptions.indexOf(correctAnswer)}) *** answerOptions: $answerOptions")
        return QuestionAndAnswers(question, answerOptions, answerOptions.indexOf(correctAnswer))
    }

    fun handleUserAnswer(answer: Int) {
        //if answerCorrect wordpair value-=1 else wordpair value+=1
        //if wordpair value == -1 forgetQuestion
    }

    fun forgetQuestion(questionAndAnswers: QuestionAndAnswers) {
        //delete wordpair
        //if unique words in lang1 or lang2 less than countOfAnswerOptions then forgetcurrentVocabulary
    }

    fun forgetVocabulary() {

    }

    fun switchLanguages() {
        indexPrimaryLang = (indexPrimaryLang + 1) % 2
        indexSecondaryLang = (indexSecondaryLang + 1) % 2
        Log.d(TAG, "switchLagnuages(): indexPrimaryLang=$indexPrimaryLang and indexSecondarylang=$indexSecondaryLang")
    }

    fun restoreAllQuestions() {
        vocabularies = mutableListOf()
        initializeVocabularies()
    }
}