package com.example.myapplication.words

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RestorePasswordActivity : AppCompatActivity() {
    private lateinit var btn_reset_password: Button
    private lateinit var textInputLoginResetPassword: TextInputLayout

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore_password)

        title = "Восстановление пароля"

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        auth = Firebase.auth

        btn_reset_password = findViewById(R.id.btn_reset_password)
        textInputLoginResetPassword = findViewById(R.id.textInputLoginResetPassword)

        btn_reset_password.setOnClickListener {
            if (validateEmail(textInputLoginResetPassword)) {
                val enteredEmail = textInputLoginResetPassword.editText?.text.toString()
                if (enteredEmail != null && enteredEmail.isNotEmpty()) {
                    auth.sendPasswordResetEmail(enteredEmail)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                textInputLoginResetPassword.editText?.clearComposingText()

                                Toast.makeText(
                                    this,
                                    "Пароль успешно сброшен! Проверьте Вашу почту и следуйте указанным инструкциям!",
                                    Toast.LENGTH_LONG
                                ).show()

                                val intent = Intent(this, AuthorizationActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this,
                                    "Ошибка сброса пароля! Убедитесь, в правильности введенного логина!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }
}