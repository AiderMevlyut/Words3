package com.example.myapplication.words

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class SelectLanguageActivity : AppCompatActivity() {

    private lateinit var cv_Eng: CardView
    private lateinit var cv_Ger: CardView
    private lateinit var cv_Fran: CardView
    private lateinit var cv_Chin: CardView
    private lateinit var cv_Jap: CardView

    private var sharedPrefLanguage: SharedPreferences? = null
    private var sharedPrefCurrentUserId: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_language)

        cv_Eng = findViewById(R.id.cv_Eng)
        onClickListener(cv_Eng)
        cv_Ger = findViewById(R.id.cv_Ger)
        onClickListener(cv_Ger)
        cv_Fran = findViewById(R.id.cv_Fran)
        onClickListener(cv_Fran)
        cv_Chin = findViewById(R.id.cv_Chin)
        onClickListener(cv_Chin)
        cv_Jap = findViewById(R.id.cv_Jap)
        onClickListener(cv_Jap)

        sharedPrefLanguage = this?.getSharedPreferences("Language", MODE_PRIVATE)
        sharedPrefCurrentUserId = this?.getSharedPreferences("CurrentUserId", MODE_PRIVATE)

        title = "Выберите язык"
    }

    private fun onClickListener(cardView: CardView) {
        cardView.setOnClickListener {
                when (cardView.id) {
                    R.id.cv_Eng -> {
                        val myCount = sharedPrefLanguage!!.edit()
                        myCount.putString("Language", "Английский")
                        myCount.apply()

                        val intent = Intent(this, BottomNavigationActivity::class.java)
                        intent.putExtra("Language", "Английский")
                        startActivity(intent)
                    }
                R.id.cv_Ger -> {
                    val myCount = sharedPrefLanguage!!.edit()
                    myCount.putString("Language", "Немецкий")
                    myCount.apply()

                    val intent = Intent(this, BottomNavigationActivity::class.java)
                    intent.putExtra("Language", "Немецкий")
                    startActivity(intent)
                }
                R.id.cv_Fran -> {
                    val myCount = sharedPrefLanguage!!.edit()
                    myCount.putString("Language", "Французкий")
                    myCount.apply()

                    val intent = Intent(this, BottomNavigationActivity::class.java)
                    intent.putExtra("Language", "Французкий")
                    startActivity(intent)
                }
                R.id.cv_Chin -> {
                    Toast.makeText(this, "В разработке..", Toast.LENGTH_SHORT).show()
                }
                R.id.cv_Jap -> {
                    Toast.makeText(this, "В разработке..", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.action_bar_exit, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.btn_exit) {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Выход из приложения")
            builder.setMessage("Вы действительно хотите выйти?")

            //performing positive action
            builder.setPositiveButton("Да") { dialogInterface, which ->
                val auth: FirebaseAuth = Firebase.auth
                auth.signOut()

                val intent = Intent(this, AuthorizationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            //performing negative action
            builder.setNegativeButton("Нет") { dialogInterface, which ->

            }

            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}