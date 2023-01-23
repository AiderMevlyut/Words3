package com.example.myapplication.words.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.words.*
import com.example.myapplication.words.model.Words
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AddNewWordsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewInflater = inflater.inflate(R.layout.fragment_add_new_words, container, false)

        getListCategories(viewInflater)

        onBackPressedButton()

        // Inflate the layout for this fragment
        return viewInflater
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
        val sharedPrefLanguage: SharedPreferences? = context?.getSharedPreferences("Language", MODE_PRIVATE)
        val sharedPrefCurrentUserId: SharedPreferences? = context?.getSharedPreferences("CurrentUserId", AppCompatActivity.MODE_PRIVATE)
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

    private fun showButtonsCategories(arrayListCategory: ArrayList<String>, view: View) {
        val linearLayoutCategory = view.findViewById<LinearLayout>(R.id.linearLayoutLangAdd)
        var index = 0
        var firstClicked = false

        linearLayoutCategory.removeAllViews()

        for (i in arrayListCategory.indices) {
            var btnCategory = Button(context)
            val params = LinearLayout.LayoutParams(
                340,
                140
            )
            params.setMargins(15, 5, 15, 5)
            btnCategory.id = i
            btnCategory.layoutParams = params
            btnCategory.isAllCaps = false
            btnCategory.setBackgroundResource(R.drawable.roundedbutton)
            btnCategory.text = arrayListCategory[i]
            linearLayoutCategory.addView(btnCategory)

            val et_enteredWord: EditText = view.findViewById(R.id.et_enteredWord)
            val et_translateWord: EditText = view.findViewById(R.id.et_translateWord)

            var categoryFromButton = ""
            btnCategory.setOnClickListener {
                println("Кнопка : ${btnCategory.text}")

                categoryFromButton = btnCategory.text.toString()

                val btn_add_new_word: Button = view.findViewById(R.id.btn_add_new_word)
                btn_add_new_word.setOnClickListener{

                    val enteredWord = et_enteredWord.text.toString()
                    val enteredTranslate = et_translateWord.text.toString()

                    println("et_enteredWord : ${enteredWord} AND et_translateWord : ${enteredTranslate}")

                    if(categoryFromButton != "" && enteredWord != null && enteredTranslate != null) {

                        parseReference(categoryFromButton, object : CallBackReference {
                            override fun getReferenceByCategory(reference: String) {
                                val database = FirebaseDatabase.getInstance()
                                val myRef2 = database.getReference(reference)
                                val newChildRef = myRef2.push()
                                val newWords = com.example.myapplication.words.new_model.Words(
                                    enteredWord,
                                    enteredTranslate,
                                    false
                                )
                                newChildRef.setValue(newWords)
                            }
                        })

                        val btn = view.findViewById<Button>(index)
                        btn.setTextColor(Color.BLACK)
                        categoryFromButton = ""

                        et_enteredWord.setText("")
                        et_translateWord.setText("")
                    }
                }

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

    private fun getListCategories(view: View) {
        val progressBarAddCateg: ProgressBar = view.findViewById(R.id.progressBarAddCateg)
        progressBarAddCateg.visibility = View.VISIBLE
        val hor_scrollViewCateg: HorizontalScrollView = view.findViewById(R.id.hor_scrollViewCateg)
        hor_scrollViewCateg.visibility = View.GONE

        val arrayListCategory: ArrayList<String> = arrayListOf()

        getCategoriesByLanguage(object : CallBackCategories {
            override fun getCategoriesByLanguage(arrayListCategories: ArrayList<String>) {
                for (i in arrayListCategories) {
                    arrayListCategory.add(i)
                }

                showButtonsCategories(arrayListCategories, view)

                progressBarAddCateg.visibility = View.GONE
                hor_scrollViewCateg.visibility = View.VISIBLE
            }
        })
    }

    private fun parseReference(category: String, callBackReference: CallBackReference) {
        val sharedPrefCurrentUserId: SharedPreferences? =
            context?.getSharedPreferences("CurrentUserId", AppCompatActivity.MODE_PRIVATE)
        val sharedPrefLanguage: SharedPreferences? =
            context?.getSharedPreferences("Language", AppCompatActivity.MODE_PRIVATE)
        val currentId = sharedPrefCurrentUserId!!.getString("CurrentUserId", "")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("User/${currentId}/")
            .child("arrayListLanguage")
            .orderByChild("language")
            .equalTo(sharedPrefLanguage?.getString("Language", ""))

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    if (snap.child("category").value == category) {
                        val patternRemoveStart = Regex(".app")
                        val resultSecond = patternRemoveStart.split(snap.ref.toString())
                        callBackReference.getReferenceByCategory(resultSecond[1]+"/words/")
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}