package com.example.myapplication.words.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.words.CallBackCountOfWords
import com.example.myapplication.words.R
import com.example.myapplication.words.SelectLanguageActivity
import com.example.myapplication.words.model.ListWordsItem
import com.example.myapplication.words.recycler_view.CustomAdapterListWords
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class ListWordsFragment : Fragment(), TextToSpeech.OnInitListener{
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_list_words, container, false)

        textToSpeech = TextToSpeech(context, this)

        showListWords(view)

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

    private fun showListWords(view: View){
        val prBarLoadingListWord: ProgressBar = view.findViewById(R.id.prBarLoadingListWord)
        prBarLoadingListWord.visibility = View.VISIBLE

        var arrayListResult: ArrayList<ListWordsItem> = arrayListOf()
        var recyclerViewListWords: RecyclerView = view.findViewById(R.id.recyclerViewListWords)
        recyclerViewListWords.visibility = View.GONE

        val sharedPrefLanguage: SharedPreferences? = context?.getSharedPreferences("Language", Context.MODE_PRIVATE)
        val sharedPrefCurrentUserId: SharedPreferences? = context?.getSharedPreferences("CurrentUserId", Context.MODE_PRIVATE)
        val currentId = sharedPrefCurrentUserId!!.getString("CurrentUserId", "")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("User/${currentId}/")
            .child("arrayListLanguage")
            .orderByChild("language")
            .equalTo(sharedPrefLanguage?.getString("Language", ""))

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    var category: String? = snap.child("category").value.toString()

                    for (words in snap.child("words").children) {
                        var result = ListWordsItem(
                            category,
                            snap.child("category").value.toString(),
                            words.child("word").value.toString(),
                            words.child("translate").value.toString()
                        )
                        category = null

                        arrayListResult.add(result)
                    }

                    recyclerViewListWords.layoutManager = LinearLayoutManager(context)

                    val myAdapter = CustomAdapterListWords(arrayListResult)
                    myAdapter.onValueClickListener(object :
                        CustomAdapterListWords.ValueClickListener {
                        override fun valueClickListener(pos: Int) {
                            speakOut(arrayListResult[pos].word)
                        }
                    })

                    myAdapter.deleteItemClickListener(object :
                        CustomAdapterListWords.DeleteItemClickListener {
                        override fun myOnItemDelete(pos: Int) {
                            deleteWords(arrayListResult[pos].categoryDelete, arrayListResult[pos].word, view)
                        }
                    })

                    // Setting the Adapter with the recyclerview
                    recyclerViewListWords.adapter = myAdapter

                    prBarLoadingListWord.visibility = View.GONE
                    recyclerViewListWords.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun deleteWords(category: String, word: String, view: View){
        val sharedPrefCurrentUserId: SharedPreferences? = context?.getSharedPreferences("CurrentUserId", Context.MODE_PRIVATE)
        val sharedPrefLanguage: SharedPreferences? = context?.getSharedPreferences("Language", Context.MODE_PRIVATE)
        val currentId = sharedPrefCurrentUserId!!.getString("CurrentUserId", "")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("User/${currentId}/")
            .child("arrayListLanguage")
            .orderByChild("language")
            .equalTo(sharedPrefLanguage?.getString("Language", ""))

        myRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Подтвердите действие")
                builder.setTitle("Вы действительно хотите удалить слово?")

                builder.setPositiveButton("Да") { dialogInterface, which ->

                    for (snap in snapshot.children) {
                        if (sharedPrefLanguage?.getString("Language", "") == snap.child("language").value) {
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

                                                        showListWords(view)
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "По данной категории осталось ${countWordsByCategory} слова." +
                                                                    " Для правильной работы приложения, нельзя удалять слова, при их остатке меннее чем ${countWordsByCategory}-х слов",
                                                            Toast.LENGTH_LONG
                                                        ).show()
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
        var sharedPrefCurrentUserId: SharedPreferences? = context?.getSharedPreferences("CurrentUserId", Context.MODE_PRIVATE)
        var sharedPrefLanguage: SharedPreferences? = context?.getSharedPreferences("Language", Context.MODE_PRIVATE)
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