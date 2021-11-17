package com.gtechapps.chatmessager

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.gtechapps.chatmessager.Utils.hideKeyboard
import com.gtechapps.chatmessager.Utils.showLoader

class LoginActivity : AppCompatActivity() {
    var email: EditText? = null
    var password: EditText? = null
    var btn_login: Button? = null
    var MR: Typeface? = null
    var MRR: Typeface? = null
    var dialog: ProgressDialog? = null
    var auth: FirebaseAuth? = null
    var forgot_password: TextView? = null
    var login_tv: TextView? = null
    var msg_tv: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        MRR = Typeface.createFromAsset(assets, "fonts/myriadregular.ttf")
        MR = Typeface.createFromAsset(assets, "fonts/myriad.ttf")
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()
        login_tv = findViewById(R.id.login_tv)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        btn_login = findViewById(R.id.btn_login)
        forgot_password = findViewById(R.id.forgot_password)
        msg_tv = findViewById(R.id.msg_tv)
        msg_tv!!.typeface = MRR
        login_tv!!.typeface = MR
        email!!.typeface = MRR
        password!!.typeface = MRR
        btn_login!!.typeface = MRR
        forgot_password!!.typeface = MRR
        forgot_password!!.setOnClickListener { view: View? ->
            startActivity(
                Intent(this@LoginActivity, ResetPasswordActivity::class.java)
            )
        }
        btn_login!!.setOnClickListener { view: View? ->
            val txt_email = email!!.text.toString()
            val txt_password = password!!.text.toString()
            hideKeyboard(this@LoginActivity)
            if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                Toast.makeText(this@LoginActivity, "All fields are required", Toast.LENGTH_SHORT)
                    .show()
            } else {
                dialog = showLoader(this@LoginActivity)
                auth!!.signInWithEmailAndPassword(txt_email, txt_password)
                    .addOnCompleteListener { task: Task<AuthResult?> ->
                        if (task.isSuccessful) {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            if (dialog != null) {
                                dialog!!.dismiss()
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            if (dialog != null) {
                                dialog!!.dismiss()
                            }
                            Toast.makeText(
                                this@LoginActivity,
                                "Authentication failed!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}