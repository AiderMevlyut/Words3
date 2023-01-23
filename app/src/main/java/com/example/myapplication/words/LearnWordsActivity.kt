package com.example.myapplication.words

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.words.model.CategoryItem

class LearnWordsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_words)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val arrayListCategoryItem = intent.getSerializableExtra("arrayListCategoryItem") as CategoryItem
        val language = intent.getStringExtra("language")

        actionBar?.title = arrayListCategoryItem.categoryName

        if (arrayListCategoryItem != null && language != null) {
            setTranslateOnButtons(
                arrayListCategoryItem,
                language
            )
        }
    }

    private fun setTranslateOnButtons(categoryItem: CategoryItem, language: String) {
        val textViewProgress = findViewById<TextView>(R.id.tv_show_progress)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val textViewWord = findViewById<TextView>(R.id.tv_word)

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val radioButtonFirst = findViewById<RadioButton>(R.id.rb_first)
        val radioButtonSecond = findViewById<RadioButton>(R.id.rb_second)
        val radioButtonThird = findViewById<RadioButton>(R.id.rb_third)
        val radioButtonFourth = findViewById<RadioButton>(R.id.rb_fourth)

        val buttonSkip = findViewById<Button>(R.id.btn_skip)
        val buttonNext = findViewById<Button>(R.id.btn_next)

        var rightAnswer = 0
        var wrongAnswer = 0

        // рандомные индексы ↓
        val arrayListRandom = arrayListOf<Int>()
        var random = true
        while (random) {
            val randomNumber = (Math.random() * categoryItem.arrayListWords.size).toInt()
            if (arrayListRandom.size < categoryItem.arrayListWords.size) {
                if (!arrayListRandom.contains(randomNumber)) {
                    arrayListRandom.add(randomNumber)
                }
            } else {
                random = false
            }
        }

        val arrayListRightAnswerWords: ArrayList<String> = arrayListOf()

        val arrayListWords: ArrayList<String> = arrayListOf()
        val arrayListTranslate: ArrayList<String> = arrayListOf()
        for (i in 0 until categoryItem.arrayListWords.size) {
            arrayListWords.add(categoryItem.arrayListWords[i].word)
            arrayListTranslate.add(categoryItem.arrayListWords[i].translate)
        }

        textViewProgress.text = "Прогресс 1 из ${arrayListWords.size}"
        textViewWord.text = categoryItem.arrayListWords[arrayListRandom[0]].translate

        val arrayListRandomWord = randomTitlesOnButton(
            categoryItem.arrayListWords[arrayListRandom[0]].word,
            arrayListWords
        )

        radioButtonFirst.text = arrayListRandomWord[0]
        radioButtonSecond.text = arrayListRandomWord[1]
        radioButtonThird.text = arrayListRandomWord[2]
        radioButtonFourth.text = arrayListRandomWord[3]

        var index = 1
        buttonNext.setOnClickListener {
            var selectedId = radioGroup.checkedRadioButtonId
            var radioButton = findViewById<RadioButton>(selectedId)

            if (radioButton != null) {
                if (index < categoryItem.arrayListWords.size) {
                    textViewProgress.text = "Прогресс ${index + 1} из ${arrayListWords.size}"
                    progressBar.progress += setValueForProgressBar(arrayListWords.size)

                    if (index % 2 == 0) {// randomTitlesOnButton()
                        if (radioButton.text.toString() == arrayListTranslate[arrayListRandom[index - 1]]) {
                            rightAnswer++

                            arrayListRightAnswerWords.add(arrayListWords[arrayListRandom[index - 1]])
                        } else {
                            wrongAnswer++
                        }

                        val arrayListRandomWord = randomTitlesOnButton(
                            categoryItem.arrayListWords[arrayListRandom[index]].word,
                            arrayListWords
                        )

                        textViewWord.text =
                            categoryItem.arrayListWords[arrayListRandom[index]].translate

                        radioButtonFirst.text = arrayListRandomWord[0]
                        radioButtonSecond.text = arrayListRandomWord[1]
                        radioButtonThird.text = arrayListRandomWord[2]
                        radioButtonFourth.text = arrayListRandomWord[3]
                    } else {
                        if (radioButton.text.toString() == arrayListWords[arrayListRandom[index - 1]]) {
                            rightAnswer++

                            arrayListRightAnswerWords.add(arrayListWords[arrayListRandom[index - 1]])
                        } else {
                            wrongAnswer++
                        }

                        val arrayListRandomTranslate = randomTitlesOnButton(
                            categoryItem.arrayListWords[arrayListRandom[index]].translate,
                            arrayListTranslate
                        )

                        textViewWord.text = categoryItem.arrayListWords[arrayListRandom[index]].word

                        radioButtonFirst.text = arrayListRandomTranslate[0]
                        radioButtonSecond.text = arrayListRandomTranslate[1]
                        radioButtonThird.text = arrayListRandomTranslate[2]
                        radioButtonFourth.text = arrayListRandomTranslate[3]
                    }

                    index++
                } else {
                    if (index % 2 == 0) {
                        textViewProgress.text = "Прогресс ${index + 1} из ${arrayListWords.size}"
                        progressBar.progress += setValueForProgressBar(arrayListWords.size)

                        if (radioButton.text.toString() == arrayListTranslate[arrayListRandom[index - 1]]) {
                            rightAnswer++

                            arrayListRightAnswerWords.add(arrayListWords[arrayListRandom[index - 1]])
                        } else {
                            wrongAnswer++
                        }
                    } else {
                        if (radioButton.text.toString() == arrayListWords[arrayListRandom[index - 1]]) {
                            rightAnswer++

                            arrayListRightAnswerWords.add(arrayListWords[arrayListRandom[index - 1]])
                        } else {
                            wrongAnswer++
                        }
                    }

                    val intent = Intent(applicationContext, FinishActivity::class.java)
                    intent.putExtra("Language", language)
                    intent.putExtra("category", categoryItem.categoryName)
                    intent.putExtra("rightAnswer", rightAnswer)
                    intent.putExtra("arrayListRightAnswerWords", arrayListRightAnswerWords)
                    intent.putExtra("wrongAnswer", wrongAnswer)
                    intent.putStringArrayListExtra("arrayListWords", arrayListWords)
                    intent.putStringArrayListExtra("arrayListTranslate", arrayListTranslate)
                    startActivity(intent)
                }
                radioGroup.clearCheck()
            }
        }

        buttonSkip.setOnClickListener {
            if (index < categoryItem.arrayListWords.size) {
                textViewProgress.text = "Прогресс ${index + 1} из ${arrayListWords.size}"
                progressBar.progress += setValueForProgressBar(arrayListWords.size)

                if (index % 2 == 0) {// randomTitlesOnButton()

                    val arrayListRandomWord = randomTitlesOnButton(
                        categoryItem.arrayListWords[arrayListRandom[index]].word,
                        arrayListWords
                    )

                    textViewWord.text =
                        categoryItem.arrayListWords[arrayListRandom[index]].translate

                    radioButtonFirst.text = arrayListRandomWord[0]
                    radioButtonSecond.text = arrayListRandomWord[1]
                    radioButtonThird.text = arrayListRandomWord[2]
                    radioButtonFourth.text = arrayListRandomWord[3]
                } else {

                    val arrayListRandomTranslate = randomTitlesOnButton(
                        categoryItem.arrayListWords[arrayListRandom[index]].translate,
                        arrayListTranslate
                    )

                    textViewWord.text = categoryItem.arrayListWords[arrayListRandom[index]].word

                    radioButtonFirst.text = arrayListRandomTranslate[0]
                    radioButtonSecond.text = arrayListRandomTranslate[1]
                    radioButtonThird.text = arrayListRandomTranslate[2]
                    radioButtonFourth.text = arrayListRandomTranslate[3]
                }

                index++
                wrongAnswer++
            } else {
                progressBar.progress += setValueForProgressBar(arrayListWords.size)
                wrongAnswer++

                val intent = Intent(applicationContext, FinishActivity::class.java)
                intent.putExtra("Language", language)
                intent.putExtra("category", categoryItem.categoryName)
                intent.putExtra("rightAnswer", rightAnswer)
                intent.putExtra("wrongAnswer", wrongAnswer)
                intent.putStringArrayListExtra("arrayListWords", arrayListWords)
                intent.putStringArrayListExtra("arrayListTranslate", arrayListTranslate)
                startActivity(intent)
            }
        }
    }

    private fun setValueForProgressBar(sizeWord: Int): Int {
        if (100 % sizeWord == 0) {
            return 100 / sizeWord
        } else {
            return (100 / sizeWord) + (100 % sizeWord)
        }
    }

    // рандомные названия на кнопках
    private fun randomTitlesOnButton(
        rightWord: String,
        listTranslateWords: MutableList<String>
    ): MutableList<String> {
        var fullListWords = mutableListOf<String>()
        fullListWords.addAll(listTranslateWords)

        var deleteResult = fullListWords.remove(rightWord)
        var listRandomElements = mutableListOf<String>()

        if (deleteResult) {
            for (i in 0 until 3) {
                while (true) {
                    var randomNum = (0 until fullListWords.size).random()
                    if (!(listRandomElements.contains(fullListWords[randomNum]))) {
                        listRandomElements.add(fullListWords[randomNum])
                        break
                    }
                }
            }
            listRandomElements.add(rightWord)
            listRandomElements.shuffle()

        }
        return listRandomElements
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Отменить задание?")
        builder.setMessage("Текущий прогресс будет потерян")

        builder.setPositiveButton("Да") { dialogInterface, which ->
            super.onBackPressed()
        }

        builder.setNegativeButton("Нет") { dialogInterface, which ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onBackPressed() {
        showDialog()
    }

    override fun onSupportNavigateUp(): Boolean {
        showDialog()

        return true
    }
}