package com.example.myapplication.words

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private var sharedPrefFirstEntered: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPrefFirstEntered = this?.getSharedPreferences("FirstEntered", MODE_PRIVATE)

        runAnimation()
        auth = Firebase.auth
    }

    private fun runAnimation() {
        val a: Animation =
            AnimationUtils.loadAnimation(this, R.anim.animation_text_splash_screen)
        val b: Animation =
            AnimationUtils.loadAnimation(this, R.anim.animation_text_splash_screen)
        val tv = findViewById<View>(R.id.tv_titleSplash) as TextView
        tv.clearAnimation()
        tv.text = "С нами легко изучать иностранные языки"
        tv.startAnimation(a)

        a.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                println("anim start")
            }

            override fun onAnimationEnd(p0: Animation?) {
                println("anim end")
                tv.text = "Английский, Немецкий, Французкий, Китайский и Японский языки"
                tv.clearAnimation()
                tv.startAnimation(b)
            }

            override fun onAnimationRepeat(p0: Animation?) {
                println("anim repeat")
                if (p0 != null) {
                    println("p0 : ${p0.repeatCount}")
                }
            }
        })

        b.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                println("anim start")
            }

            override fun onAnimationEnd(p0: Animation?) {
                println("anim end")
                tv.text = ""
                tv.clearAnimation()

                val currentUser = auth.currentUser
                if (currentUser == null) {
                    println("currentUser = null")
                    val intent = Intent(applicationContext, AuthorizationActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    println("currentUser != null")
                    val intent = Intent(applicationContext, SelectLanguageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }

            override fun onAnimationRepeat(p0: Animation?) {
                println("anim repeat")
                if (p0 != null) {
                    println("p0 : ${p0.repeatCount}")
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()

        finish()
    }
}