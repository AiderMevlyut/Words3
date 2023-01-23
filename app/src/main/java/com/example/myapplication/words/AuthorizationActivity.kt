package com.example.myapplication.words

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthorizationActivity : AppCompatActivity() {

    private lateinit var button_entered: Button
    private lateinit var button_registration: Button
    private lateinit var et_login: TextInputLayout
    private lateinit var et_password: TextInputLayout
    private lateinit var checkBoxSaveUser: CheckBox
    private lateinit var tv_reset_password: TextView

    private var sharedPrefCheckBox: SharedPreferences? = null
    private var sharedPrefEmail: SharedPreferences? = null

    private var sharedPrefCurrentUserId: SharedPreferences? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        title = "Авторизация"

        auth = Firebase.auth

        sharedPrefCheckBox = this?.getSharedPreferences("SaveUser", MODE_PRIVATE)
        sharedPrefEmail = this?.getSharedPreferences("Email", MODE_PRIVATE)
        sharedPrefCurrentUserId = this?.getSharedPreferences("CurrentUserId", MODE_PRIVATE)

        et_login = findViewById(R.id.textInputLogin)
        et_password = findViewById(R.id.textInpLayoutPass)

        button_entered = findViewById(R.id.button_entered)
        button_entered.visibility = View.VISIBLE
        button_entered.setOnClickListener {
            checkUserAuthorization(
                et_login.editText?.text.toString(),
                et_password.editText?.text.toString()
            )
        }

        tv_reset_password = findViewById(R.id.tv_reset_password)
        tv_reset_password.visibility = View.VISIBLE

        checkBoxSaveUser = findViewById(R.id.checkBoxSaveUser)
        checkBoxSaveUser.visibility = View.VISIBLE

        button_registration = findViewById(R.id.button_registration)
        button_registration.visibility = View.VISIBLE
        button_registration.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        tv_reset_password.setOnClickListener {
            val intent = Intent(this, RestorePasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkUserAuthorization(login: String, password: String) {
        if (!validateEmail(et_login) or !validatePassword()) {
            return
        }

        val constLayoutProgressBar: ConstraintLayout = findViewById(R.id.constLayoutProgressBar)
        constLayoutProgressBar.visibility = View.VISIBLE

        checkBoxSaveUser.visibility = View.GONE
        tv_reset_password.visibility = View.GONE
        button_entered.visibility = View.GONE
        button_registration.visibility = View.GONE

        auth.signInWithEmailAndPassword(login, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    val shPrefUserID = sharedPrefCurrentUserId?.edit()
                    shPrefUserID?.putString("CurrentUserId", auth.currentUser!!.uid)
                    shPrefUserID?.commit()

                    println("Пользователь успешно авторизовался!!!")

                    val saveEmailShPref = sharedPrefEmail!!.edit()
                    saveEmailShPref.putString("Email", login)
                    saveEmailShPref.apply()

                    if (!checkBoxSaveUser.isChecked) {
                        auth.signOut()
                    }

                    val intent = Intent(applicationContext, SelectLanguageActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this,
                        "Ошибка авторизации! ${it.exception?.message.toString()}",
                        Toast.LENGTH_LONG
                    ).show()

                    println("Ошибка авторизации! ${it.exception?.message.toString()}")
                }

                constLayoutProgressBar.visibility = View.GONE

                checkBoxSaveUser.visibility = View.VISIBLE
                tv_reset_password.visibility = View.VISIBLE
                button_entered.visibility = View.VISIBLE
                button_registration.visibility = View.VISIBLE
            }
    }

    private fun validateEmail(textInputLayout: TextInputLayout): Boolean {
        val emailInput = textInputLayout!!.editText!!.text.toString().trim { it <= ' ' }

        return if (emailInput.isEmpty()) {
            textInputLayout!!.error = "Поле не может быть пустым"
            false
        } else {
            textInputLayout!!.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val passwordInput = et_password!!.editText!!.text.toString().trim { it <= ' ' }
        // если поле пароля пустое будет отображаться сообщение об ошибке "Поле не может быть пустым"
        return if (passwordInput.isEmpty()) {
            et_password!!.error = "Поле не может быть пустым"
            false
        } else {
            et_password!!.error = null
            true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        finish()
    }
}