package com.example.myapplication.words.fragments

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.words.*
import com.example.myapplication.words.model.Result
import com.example.myapplication.words.recycler_view.CustomAdapterResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class ResultsFragment : Fragment(), TextToSpeech.OnInitListener {

    private var sharedPrefLanguage: SharedPreferences? = null
    private lateinit var textToSpeech: TextToSpeech

    var arrayListResult: ArrayList<Result> = arrayListOf()
    private lateinit var recyclerview: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_results, container, false)

        textToSpeech = TextToSpeech(context, this)
        sharedPrefLanguage = context?.getSharedPreferences("Language", MODE_PRIVATE)

        recyclerview = view.findViewById<RecyclerView>(R.id.recyclerViewResult)

        var arrayListCategory: ArrayList<String> = arrayListOf()

        val progressBarAddResul: ProgressBar = view.findViewById(R.id.progressBarAddResult)
        progressBarAddResul.visibility = View.VISIBLE
        val hor_scrollViewCateg: HorizontalScrollView = view.findViewById(R.id.hor_scrollViewRes)
        hor_scrollViewCateg.visibility = View.GONE

        getCategoriesByLanguage(object : CallBackCategories {
            override fun getCategoriesByLanguage(arrayListCategories: ArrayList<String>) {
                for (i in arrayListCategories) {
                    arrayListCategory.add(i)
                }

                showButtonsCategories(arrayListCategories, view)

                progressBarAddResul.visibility = View.GONE
                hor_scrollViewCateg.visibility = View.VISIBLE
            }
        })

        onBackPressedButton()

        // Inflate the layout for this fragment
        return view
    }

    private fun onBackPressedButton() {
        // This callback will only be called when MyFragment is at least Started.
        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val intent = Intent(context, SelectLanguageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
            startActivity(intent)
        }
    }

    private fun getCategoriesByLanguage(callBackCategories: CallBackCategories): ArrayList<String> {
        val sharedPrefLanguage: SharedPreferences? =
            context?.getSharedPreferences("Language", MODE_PRIVATE)
        val sharedPrefCurrentUserId: SharedPreferences? =
            context?.getSharedPreferences("CurrentUserId", AppCompatActivity.MODE_PRIVATE)
        val currentId = sharedPrefCurrentUserId!!.getString("CurrentUserId", "")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("User/${currentId}/")
            .child("arrayListLanguage")
            .orderByChild("language")
            .equalTo(sharedPrefLanguage?.getString("Language", ""))

        val arrayListCategory: ArrayList<String> = arrayListOf()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    arrayListCategory.add(snap.child("category").value.toString())
                }

                callBackCategories.getCategoriesByLanguage(arrayListCategory)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return arrayListCategory
    }

    private fun speakOut(text: String) {
        textToSpeech!!.speak(text, TextToSpeech.SUCCESS, null, "")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
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

    private fun showButtonsCategories(arrayListCategory: ArrayList<String>, view: View) {
        val linearLayoutCategory = view.findViewById<LinearLayout>(R.id.linearLayoutCategory)
        var index = 0
        var firstClicked = false

        for (i in arrayListCategory.indices) {
            var btnCategory = Button(context)
            val params = LinearLayout.LayoutParams(
                305,
                115
            )
            params.setMargins(15, 5, 15, 5)
            btnCategory.id = i
            btnCategory.layoutParams = params
            btnCategory.isAllCaps = false
            btnCategory.setBackgroundResource(R.drawable.roundedbutton)
            btnCategory.text = arrayListCategory[i]
            linearLayoutCategory.addView(btnCategory)

            btnCategory.setOnClickListener {
                val tv_listWordEmpty: TextView = view.findViewById(R.id.tv_listWordEmpty)
                tv_listWordEmpty.visibility = View.GONE

                val linearLayResult: LinearLayout = view.findViewById(R.id.linearLayResult)
                linearLayResult.visibility = View.VISIBLE

                val recyclerViewResult: RecyclerView = view.findViewById(R.id.recyclerViewResult)
                recyclerViewResult.visibility = View.GONE

                val sharedPrefLanguage: SharedPreferences? =
                    context?.getSharedPreferences("Language", MODE_PRIVATE)

                arrayListResult.clear()

                if (sharedPrefLanguage != null) {
                    showListLearningWords(
                        view,
                        sharedPrefLanguage.getString("Language", "").toString(),
                        arrayListCategory[i]
                    )
                }
                println("Кнопка : ${btnCategory.text}")

                if (btnCategory.id == i) {
                    btnCategory.setTextColor(R.color.dark_brown)


                    if (firstClicked) {
                        firstClicked = false

                        val btn = view.findViewById<Button>(index)
                        btn.setTextColor(Color.BLACK)
                    }
                }

                firstClicked = true
                index = i
            }
        }
    }

    fun resetListWords(
        language: String?,
        category: String?
    ) {
        val sharedPrefCurrentUserId: SharedPreferences? =
            context?.getSharedPreferences("CurrentUserId", MODE_PRIVATE)
        val currentId = sharedPrefCurrentUserId!!.getString("CurrentUserId", "")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("User/${currentId}/")
            .child("arrayListLanguage")
            .orderByChild("language")
            .equalTo(language)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    if (snap.child("category").value == category) {
                        for (words in snap.child("words").children) {
                            words.child("learned").ref.setValue(false)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun showListLearningWords(view: View, language: String, category: String) {
        val tv_listWordEmpty: TextView = view.findViewById(R.id.tv_listWordEmpty)

        val tv_clear_result: TextView = view.findViewById(R.id.tv_clear_result)
        tv_clear_result.visibility = View.GONE

        val sharedPrefCurrentUserId: SharedPreferences? =
            context?.getSharedPreferences("CurrentUserId", MODE_PRIVATE)
        val currentId = sharedPrefCurrentUserId!!.getString("CurrentUserId", "")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("User/${currentId}/")
            .child("arrayListLanguage")
            .orderByChild("language")
            .equalTo(language)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var countLearnWords = 0
                for (snap in snapshot.children) {
                    println("language : " + snap.child("language").value)
                    println("category : " + snap.child("category").value)

                    if (category == snap.child("category").value) {
                        for (words in snap.child("words").children) {
                            if (words.child("learned").value as Boolean) {
                                countLearnWords++

                                var result = Result(
                                    words.child("word").value.toString(),
                                    words.child("translate").value.toString(),
                                    words.child("learned").value as Boolean
                                )
                                arrayListResult.add(result)
                            }
                        }
                    }

                    var sharedPrefLanguage: SharedPreferences? =
                        context?.getSharedPreferences("Language", MODE_PRIVATE)

                    recyclerview.layoutManager = LinearLayoutManager(context)

                    val myAdapter = CustomAdapterResult(arrayListResult)
                    myAdapter.setOnItemClickListener(object :
                        CustomAdapterResult.MyOnItemClickListener {
                        override fun myOnItemClick(pos: Int) {
                            speakOut(arrayListResult[pos].word)
                        }
                    })

                    myAdapter.setOnItemLongClickListener(object :
                        CustomAdapterResult.MyOnItemLongClickListener {
                        override fun myOnLongClick(pos: Int): Boolean {
                            deleteWordsFromDB(
                                arrayListResult[pos].word,
                                sharedPrefLanguage?.getString("Language", "").toString(),
                                category,
                                view
                            )

                            return true
                        }
                    })

                    // Setting the Adapter with the recyclerview
                    recyclerview.adapter = myAdapter

                    val linearLayResult: LinearLayout = view.findViewById(R.id.linearLayResult)
                    linearLayResult.visibility = View.GONE
                    val recyclerViewResult: RecyclerView =
                        view.findViewById(R.id.recyclerViewResult)
                    recyclerViewResult.visibility = View.VISIBLE

                    if (arrayListResult.isEmpty()) {
                        val linearLayResult: LinearLayout = view.findViewById(R.id.linearLayResult)
                        linearLayResult.visibility = View.GONE

                        tv_listWordEmpty.visibility = View.VISIBLE

                        val tv_count_learning: TextView = view.findViewById(R.id.tv_count_learning)
                        tv_count_learning.text = "Изучено 0 слов "
                    } else {
                        val recyclerViewResult: RecyclerView =
                            view.findViewById(R.id.recyclerViewResult)
                        recyclerViewResult.visibility = View.VISIBLE

                        val tv_count_learning: TextView = view.findViewById(R.id.tv_count_learning)
                        tv_count_learning.text = "Изучено ${countLearnWords} слов "

                        tv_listWordEmpty.visibility = View.GONE

                        val tv_clear_result: TextView = view.findViewById(R.id.tv_clear_result)
                        tv_clear_result.visibility = View.VISIBLE
                        tv_clear_result.setOnClickListener {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Вы действительно хотите очистить список достижений?")
                            builder.setTitle("Сбросить результаты")

                            builder.setPositiveButton("Да") { dialogInterface, which ->
                                resetListWords(language, category)

                                arrayListResult.clear()
                                myAdapter.notifyDataSetChanged()
                                recyclerview.adapter = myAdapter
                                val tv_count_learning: TextView =
                                    view.findViewById(R.id.tv_count_learning)
                                tv_count_learning.text = "Изучено 0 слов "

                                val tv_clear_result: TextView =
                                    view.findViewById(R.id.tv_clear_result)
                                tv_clear_result.visibility = View.GONE

                                val tv_listWordEmpty: TextView =
                                    view.findViewById(R.id.tv_listWordEmpty)
                                tv_listWordEmpty.visibility = View.VISIBLE
                            }

                            builder.setNegativeButton("Нет") { dialogInterface, which ->
                            }
                            val alertDialog: AlertDialog = builder.create()
                            alertDialog.setCancelable(false)
                            alertDialog.show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun deleteWordsFromDB(word: String, language: String, category: String, view: View) {
        var sharedPrefCurrentUserId: SharedPreferences? =
            context?.getSharedPreferences("CurrentUserId", MODE_PRIVATE)
        val currentId = sharedPrefCurrentUserId!!.getString("CurrentUserId", "")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("User/${currentId}/")
            .child("arrayListLanguage")
            .orderByChild("language")
            .equalTo(language)

        val progressBar: ProgressBar = view.findViewById(R.id.prBarLoadingResult)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val builder = AlertDialog.Builder(context)
                builder.setMessage("Подтвердите действие")
                builder.setTitle("Вы действительно хотите удалить слово?")

                builder.setPositiveButton("Да") { dialogInterface, which ->
                    progressBar.visibility = View.VISIBLE
                    recyclerview.visibility = View.GONE

                    for (snap in snapshot.children) {
                        if (language == snap.child("language").value) {
                            if (snap.child("category").value.toString() == category) {
                                for (words in snap.child("words").children) {
                                    if (word == words.child("word").value) {
                                        countTheNumberOfWords(
                                            snap.child("category").value.toString(),
                                            object : CallBackCountOfWords {
                                                override fun countOfWordsByCategory(
                                                    countWordsByCategory: Int
                                                ) {

                                                    if (countWordsByCategory > 4) {
                                                        words.ref.removeValue()

                                                        arrayListResult.clear()
                                                        showListLearningWords(
                                                            view,
                                                            sharedPrefLanguage?.getString(
                                                                "Language",
                                                                ""
                                                            ).toString(),
                                                            category
                                                        )

                                                        progressBar.visibility = View.GONE
                                                        recyclerview.visibility = View.VISIBLE

                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "По данной категории осталось ${countWordsByCategory} слова." +
                                                                    " Для правильной работы приложения, нельзя удалять слова, при их остатке меннее чем ${countWordsByCategory}-х слов",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        progressBar.visibility = View.GONE
                                                        recyclerview.visibility = View.VISIBLE

                                                        val tv_clear_result: TextView = view.findViewById(R.id.tv_clear_result)
                                                        tv_clear_result.visibility = View.GONE
                                                    }
                                                }
                                            })
                                    }
                                }
                            }
                        }
                    }
                }

                builder.setNegativeButton("Нет") { dialogInterface, which ->
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun countTheNumberOfWords(
        category: String,
        callBackCountOfWords: CallBackCountOfWords
    ) {
        var sharedPrefCurrentUserId: SharedPreferences? =
            context?.getSharedPreferences("CurrentUserId", MODE_PRIVATE)
        var sharedPrefLanguage: SharedPreferences? =
            context?.getSharedPreferences("Language", MODE_PRIVATE)
        val currentId = sharedPrefCurrentUserId!!.getString("CurrentUserId", "")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("User/${currentId}/")
            .child("arrayListLanguage")
            .orderByChild("language")
            .equalTo(sharedPrefLanguage?.getString("Language", ""))

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (snap in snapshot.children) {
                    if (category == snap.child("category").value) {
                        var countWordsByCategory = 0
                        for (words in snap.child("words").children) {
                            countWordsByCategory++
                        }

                        callBackCountOfWords.countOfWordsByCategory(countWordsByCategory)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}