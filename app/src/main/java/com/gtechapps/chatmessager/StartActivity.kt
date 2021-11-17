package com.gtechapps.chatmessager

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class StartActivity : AppCompatActivity() {
    var login: Button? = null
    var register: Button? = null
    var chat_title_tv: TextView? = null
    var MR: Typeface? = null
    var MRR: Typeface? = null
    var firebaseUser: FirebaseUser? = null
    override fun onStart() {
        super.onStart()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        //check if user is null
        if (firebaseUser != null) {
            val intent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        MRR = Typeface.createFromAsset(assets, "fonts/myriadregular.ttf")
        MR = Typeface.createFromAsset(assets, "fonts/myriad.ttf")
        login = findViewById(R.id.login)
        register = findViewById(R.id.register)
        chat_title_tv = findViewById(R.id.chat_title_tv)
        login!!.typeface = MR
        register!!.typeface = MR
        chat_title_tv!!.typeface = MR
        login!!.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@StartActivity,
                    LoginActivity::class.java
                )
            )
        }
        register!!.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@StartActivity,
                    RegisterActivity::class.java
                )
            )
        }
    }
}