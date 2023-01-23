package com.example.myapplication.words

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.words.model.Result
import com.example.myapplication.words.recycler_view.CustomAdapterFinish
import com.google.firebase.database.*
import java.lang.reflect.Array
import java.util.*
import kotlin.collections.ArrayList

class FinishActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        val language = intent.getStringExtra("Language")
        val category = intent.getStringExtra("category")
        val rightAnswer = intent.getIntExtra("rightAnswer", 0)
        val arrayListRightAnswerWords = intent.getStringArrayListExtra("arrayListRightAnswerWords")
        val wrongAnswer = intent.getIntExtra("wrongAnswer", 0)
        val arrayListWords = intent.getStringArrayListExtra("arrayListWords")
        val arrayListTranslate = intent.getStringArrayListExtra("arrayListTranslate")

        val tv_categoryName: TextView = findViewById(R.id.tv_categoryName)
        tv_categoryName.text = category.toString()

        val actionBar = supportActionBar
        actionBar?.title = "Результат"

        textToSpeech = TextToSpeech(this, this)

        if (arrayListWords != null) {
            showStars(rightAnswer, wrongAnswer, arrayListWords.size)
        }
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerViewFinish)
        recyclerview.layoutManager = LinearLayoutManager(this)

        val arrayListResult: ArrayList<Result> = arrayListOf()

        if (arrayListWords != null && arrayListTranslate != null) {
            for (i in 0 until arrayListWords.size) {
                val result =
                    Result(arrayListWords[i].toString(), arrayListWords[i].toString(), true)
                arrayListResult.add(result)
            }
        }

        val adapter = CustomAdapterFinish(arrayListResult)
        adapter.setOnItemClickListener(object : CustomAdapterFinish.OnItemClickListener {
            override fun onItemClick(pos: Int) {
                if (arrayListWords != null) {
                    speakOut(arrayListWords[pos].toString())
                }
            }
        })

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        val btn_proceed = findViewById<Button>(R.id.btn_proceed)
        btn_proceed.setOnClickListener {
            if (arrayListRightAnswerWords != null) {
                updateLearnedWord(language, arrayListRightAnswerWords)

                val intent = Intent(this, BottomNavigationActivity::class.java)
                intent.putExtra("Language", language)
                println("FinishACT : ${language}")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                val intent = Intent(this, BottomNavigationActivity::class.java)
                intent.putExtra("Language", language)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun speakOut(text: String) {
        textToSpeech!!.speak(text, TextToSpeech.SUCCESS, null, "")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language
            val result = textToSpeech!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                //voiceWord!!.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    override fun onDestroy() {
        // Shutdown textToSpeech
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        var sharedPrefLanguage: SharedPreferences =
            this.getSharedPreferences("Language", MODE_PRIVATE)

        val intent = Intent(this, BottomNavigationActivity::class.java)
        intent.putExtra("Language", sharedPrefLanguage.getString("Language", "").toString())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun updateLearnedWord(
        language: String?,
        arrayListRightWords: ArrayList<String>
    ) {
        val sharedPrefCurrentUserId: SharedPreferences? =
            this?.getSharedPreferences("CurrentUserId", MODE_PRIVATE)
        val currentId = sharedPrefCurrentUserId!!.getString("CurrentUserId", "")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("User/${currentId}/")
            .child("arrayListLanguage")
            .orderByChild("language")
            .equalTo(language)
        var index = 0

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (snap in snapshot.children) {
                    for (words in snap.child("words").children) {

                        // код для обновления изученного/не изученного слова на (true or false)

                        println("words.child(\"word\").value : ${words.child("word").value}")
                        println("arrayListRightWords[index] : " + arrayListRightWords[index])
                        if (arrayListRightWords.contains(words.child("word").value)) {
                            words.child("learned").ref.setValue(true)
                        }

                        if (index < arrayListRightWords.size - 1) {
                            index++
                        }
                        // код для обновления изученного слова
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    // подсчитываем результаты правильных ответов,
    // отрисовываем звездочки на экране результатов
    private fun showStars(rightAnswer: Int, wrongAnswer: Int, allCountWords: Int) {
        println("rightAnswer : ${rightAnswer}, wrongAnswer : ${wrongAnswer}, allCountWords : ${allCountWords}")

        val iv_firstStar = findViewById<ImageView>(R.id.iv_firstStar)
        val iv_secondStar = findViewById<ImageView>(R.id.iv_secondStar)
        val iv_thirdStar = findViewById<ImageView>(R.id.iv_thirdStar)

        val tv_comments = findViewById<TextView>(R.id.tv_comments)

        // все ответы правильные
        if (rightAnswer == allCountWords) {
            // отрисовываем все три звезды
            println("Три звезды")

            iv_firstStar.setImageResource(R.drawable.star)
            iv_secondStar.setImageResource(R.drawable.star)
            iv_thirdStar.setImageResource(R.drawable.star)

            tv_comments.text = "Отличный результат!"
        } else {
            // больше половины правильных ответов или ответы = ошибкам
            if ((wrongAnswer < (allCountWords / 2) + (allCountWords % 2)) || rightAnswer == wrongAnswer) {
                // отрисовываем две звезды
                println("Две звезды")

                iv_firstStar.setImageResource(R.drawable.star)
                iv_secondStar.setImageResource(R.drawable.star)
                iv_thirdStar.setImageResource(R.drawable.star_border)

                tv_comments.text = "Пойдет, но можешь лучше!"
            } else {
                if (wrongAnswer == allCountWords) {
                    // не отрисовываем звезды
                    println("не отрисовываем звезды")

                    iv_firstStar.setImageResource(R.drawable.star_border)
                    iv_secondStar.setImageResource(R.drawable.star_border)
                    iv_thirdStar.setImageResource(R.drawable.star_border)

                    tv_comments.text = "Очень плохо.. не забрасывай обучение!"
                } else {
                    // меньше половины правильных ответов
                    if (wrongAnswer > allCountWords / 2) {
                        // отрисовываем одну звезду
                        println("Одна звезда")

                        iv_firstStar.setImageResource(R.drawable.star)
                        iv_secondStar.setImageResource(R.drawable.star_border)
                        iv_thirdStar.setImageResource(R.drawable.star_border)

                        tv_comments.text = "Плохо.. попробуй еще!"
                    }
                }
            }
        }
    }
}