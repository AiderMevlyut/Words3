package com.example.myapplication.words

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.myapplication.words.fragments.AddNewWordsFragment
import com.example.myapplication.words.fragments.CategoryFragment
import com.example.myapplication.words.fragments.ListWordsFragment
import com.example.myapplication.words.fragments.ResultsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_navigation_activity)

        var bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        title = "Выберите категорию"

        supportFragmentManager.commit {
            replace<CategoryFragment>(R.id.fragmentContainerView)
            setReorderingAllowed(true)
            addToBackStack(null) // name can be null
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_lessons -> {
                    title = "Выберите категорию"

                    supportFragmentManager.commit {
                        replace<CategoryFragment>(R.id.fragmentContainerView)
                        setReorderingAllowed(true)
                        addToBackStack("category") // name can be null
                    }
                }

                R.id.item_competitions -> {
                    title = "Достижения"

                    supportFragmentManager.commit {
                        replace<ResultsFragment>(R.id.fragmentContainerView)
                        setReorderingAllowed(true)
                        addToBackStack("result") // name can be null
                    }
                }

                R.id.item_profile -> {
                    title = "Добавить новое слово"

                    supportFragmentManager.commit {
                        replace<AddNewWordsFragment>(R.id.fragmentContainerView)
                        setReorderingAllowed(true)
                        addToBackStack("newWords") // name can be null
                    }
                }

                R.id.item_abc -> {
                    title = "Список слов"

                    supportFragmentManager.commit {
                        replace<ListWordsFragment>(R.id.fragmentContainerView)
                        setReorderingAllowed(true)
                        addToBackStack("listWords") // name can be null
                    }
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this, SelectLanguageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}