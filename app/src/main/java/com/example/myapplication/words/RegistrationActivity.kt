package com.example.myapplication.words

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.words.new_model.Language
import com.example.myapplication.words.new_model.User
import com.example.myapplication.words.new_model.Words
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.lang.Exception
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity() {
    private var et_nickName: TextInputLayout? = null
    private var et_login: TextInputLayout? = null
    private var et_password: TextInputLayout? = null
    private var btn_registration: Button? = null
    private var tv_toEnter: TextView? = null

    private var sharedPrefNick: SharedPreferences? = null
    private var sharedPrefEmail: SharedPreferences? = null
    private var sharedPrefPassword: SharedPreferences? = null

    private var sharedPrefUserData: SharedPreferences? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        title = "Регистрация"

        auth = Firebase.auth

        sharedPrefNick = this?.getSharedPreferences("Nick", MODE_PRIVATE)
        sharedPrefEmail = this?.getSharedPreferences("Email", MODE_PRIVATE)
        sharedPrefPassword = this?.getSharedPreferences("Password", MODE_PRIVATE)
        sharedPrefUserData = this?.getSharedPreferences("UserData", MODE_PRIVATE)

        et_nickName = findViewById(R.id.textInpLayRegNik)
        et_login = findViewById(R.id.textInpLayRegLog)
        et_password = findViewById(R.id.textInpLayRegPass)
        btn_registration = findViewById(R.id.btn_registration)

        tv_toEnter = findViewById(R.id.tv_toEnter)
        tv_toEnter!!.setOnClickListener {
            val intent = Intent(baseContext, AuthorizationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_registration!!.setOnClickListener {
            confirmInput()
        }
    }

    private fun validateNickName(): Boolean {
        val nickNameInput = et_nickName!!.editText!!.text.toString().trim { it <= ' ' }

        return if (nickNameInput.isEmpty()) {
            et_nickName!!.error = "Поле не может быть пустым"
            false
        } else {
            et_nickName!!.error = null
            true
        }
    }

    private fun validateEmail(): Boolean {
        val emailInput = et_login!!.editText!!.text.toString().trim { it <= ' ' }

        return if (emailInput.isEmpty()) {
            et_login!!.error = "Поле не может быть пустым"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            et_login!!.error = "Пожалуйста, введите действительный адрес электронной почты"
            false
        } else {
            et_login!!.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val passwordInput = et_password!!.editText!!.text.toString().trim { it <= ' ' }
        return if (passwordInput.isEmpty()) {
            et_password!!.error = "Поле не может быть пустым"
            false
        }
//        else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
//            et_password!!.error = "Пароль слишком слабый"
//            false
//        }
        else {
            et_password!!.error = null
            true
        }
    }

    private fun confirmInput() {
        if (!validateEmail() or !validatePassword() or !validateNickName()) {
            return
        }

        val const_layout_views: ConstraintLayout = findViewById(R.id.const_layout_views)
        const_layout_views.visibility = View.GONE

        val constLayoutProgressBar: LinearLayout = findViewById(R.id.constLayoutProgressBar)
        constLayoutProgressBar.visibility = View.VISIBLE

        val nick = et_nickName!!.editText!!.text.toString()
        val login = et_login!!.editText!!.text.toString()
        val password = et_password!!.editText!!.text.toString()

        auth.createUserWithEmailAndPassword(login, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val shPrefNick = sharedPrefNick?.edit()
                    shPrefNick?.putString("Nick", nick)
                    shPrefNick?.commit()

                    val shPrefLogin = sharedPrefEmail?.edit()
                    shPrefLogin?.putString("Email", login)
                    shPrefLogin?.commit()

                    val shPrefPassword = sharedPrefPassword?.edit()
                    shPrefPassword?.putString("Password", password)
                    shPrefPassword?.commit()

                    val database = FirebaseDatabase.getInstance()
                    val myRef = database.getReference("/CommonWords")

                    var arrayListWords: ArrayList<Words> = arrayListOf()
                    var arrayListLanguage: ArrayList<Language> = arrayListOf()

                    myRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (snapFirst in snapshot.children) {
                                snapFirst.child("Category").children.forEach { categories ->
                                    categories.child("ListWords").children.forEach { words ->
                                        val word = words.key.toString()
                                        val translate = words.value.toString()
                                        val learned = false

                                        val words = Words(word, translate, learned)
                                        arrayListWords.add(words)
                                    }

                                    val language = Language(
                                        snapFirst.child("Language").value.toString(),
                                        categories.child("Name").value.toString(),
                                        arrayListWords
                                    )

                                    arrayListLanguage.add(language)
                                    arrayListWords = arrayListOf()
                                }
                            }

                            val user = User(nick, login, password, arrayListLanguage)
                            println(user)

                            val shPrefUserData = sharedPrefUserData?.edit()
                            val gson = Gson()
                            val join = gson.toJson(user)
                            shPrefUserData?.putString("UserData", join)
                            shPrefUserData?.commit()

                            FirebaseDatabase.getInstance().getReference("/User")
                                .child(auth.currentUser!!.uid)
                                .setValue(user).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        println("Пользователь успешно зарегистрирован!")
                                        val intent =
                                            Intent(
                                                applicationContext,
                                                AuthorizationActivity::class.java
                                            )
                                        startActivity(intent)
                                    } else {
                                        println("Регистрация провалена! ${task.exception?.message.toString()}")
                                    }
                                }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            }.addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()

                    constLayoutProgressBar.visibility = View.GONE
                    const_layout_views.visibility = View.VISIBLE
                }
            })
    }

    companion object {
        private val PASSWORD_PATTERN = Pattern.compile(
            "^" +
                    "(?=.*[@#$%^&+=])" +  // 1 специальный символ
                    "(?=\\S+$)" +  // нет пробелов
                    ".{4,}" +  // минимум 4 символа
                    "$"
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()

        return true
    }
}