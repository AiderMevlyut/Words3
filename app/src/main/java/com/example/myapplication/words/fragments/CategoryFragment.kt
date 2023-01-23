package com.example.myapplication.words.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myapplication.words.*
import com.example.myapplication.words.model.CategoryItem
import com.example.myapplication.words.new_model.Words
import com.example.myapplication.words.recycler_view.CustomAdapterCategory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList


class CategoryFragment : Fragment() {

    private var sharedPrefEmail: SharedPreferences? = null
    private var sharedPrefLanguage: SharedPreferences? = null
    private var sharedPrefUserData: SharedPreferences? = null
    private var sharedPrefCurrentUserId: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_category, container, false)

        val pullToRefresh = view.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener(object : SwipeRefreshLayout(requireContext()),
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                defineLearnLanguage(view)
                pullToRefresh.isRefreshing = false
            }
        })

        sharedPrefEmail = context?.getSharedPreferences("Email", Context.MODE_PRIVATE)
        sharedPrefLanguage = context?.getSharedPreferences("Language", Context.MODE_PRIVATE)
        sharedPrefUserData = context?.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        sharedPrefCurrentUserId = context?.getSharedPreferences("CurrentUserId", AppCompatActivity.MODE_PRIVATE)

//        // определяем ин. язык для изучения
        defineLearnLanguage(view)

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

    private fun defineLearnLanguage(view: View) {
        when (requireActivity().intent.extras?.getString("Language")) {
            "Английский" -> {
                getDataFromDbToRecyclerView("Английский", view)
            }
            "Немецкий" -> {
                getDataFromDbToRecyclerView("Немецкий", view)
            }

            "Французкий" -> {
                getDataFromDbToRecyclerView("Французкий", view)
            }
        }
    }

    private fun getDataFromDbToRecyclerView(
        language: String,
        view: View
    ) {
        val layoutLoading = view.findViewById<LinearLayout>(R.id.linLaLoading)
        layoutLoading.visibility = View.VISIBLE

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView?.visibility = View.GONE

        var arrayListCategoryItem: ArrayList<CategoryItem> = arrayListOf()
        var arrayListWords: ArrayList<Words> = arrayListOf()

        val currentId = sharedPrefCurrentUserId!!.getString("CurrentUserId", "")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("User/${currentId}/")
            .child("arrayListLanguage")
            .orderByChild("language")
            .equalTo(language)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    var learnedWordsByCategory = 0
                    var allWordsByCategory = 0
                    for (words in snap.child("words").children) {
                        if (words.child("learned").value == true) {
                            learnedWordsByCategory++
                        }

                        val words = Words(
                            words.child("word").value.toString(),
                            words.child("translate").value.toString(),
                            words.child("learned").value as Boolean
                        )
                        arrayListWords.add(words)

                        allWordsByCategory++
                    }

                    val categoryItem = CategoryItem(
                        snap.child("category").value.toString(),
                        learnedWordsByCategory,
                        allWordsByCategory,
                        arrayListWords
                    )

                    arrayListCategoryItem.add(categoryItem)
                    arrayListWords = arrayListOf()
                }

                val adapter = CustomAdapterCategory(arrayListCategoryItem)
                adapter.setOnItemClickListener(object :
                    CustomAdapterCategory.OnItemClickListener {
                    override fun onItemClick(pos: Int) {
                        val intent = Intent(context, LearnWordsActivity::class.java)
                        intent.putExtra("arrayListCategoryItem", arrayListCategoryItem[pos])
                        intent.putExtra("language", language)
                        startActivity(intent)
                    }
                })
                recyclerView.adapter = adapter
                recyclerView.visibility = View.VISIBLE

                layoutLoading.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}