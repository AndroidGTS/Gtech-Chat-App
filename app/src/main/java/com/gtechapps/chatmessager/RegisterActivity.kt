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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.gtechapps.chatmessager.Utils.hideKeyboard
import com.gtechapps.chatmessager.Utils.showLoader
import java.util.*

class RegisterActivity : AppCompatActivity() {
    var username: EditText? = null
    var email: EditText? = null
    var password: EditText? = null
    var register_tv: TextView? = null
    var msg_reg_tv: TextView? = null
    var btn_register: Button? = null
    var MR: Typeface? = null
    var MRR: Typeface? = null
    var auth: FirebaseAuth? = null
    var reference: DatabaseReference? = null
    var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        MRR = Typeface.createFromAsset(assets, "fonts/myriadregular.ttf")
        MR = Typeface.createFromAsset(assets, "fonts/myriad.ttf")
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        username = findViewById(R.id.username)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        btn_register = findViewById(R.id.btn_register)
        register_tv = findViewById(R.id.register_tv)
        msg_reg_tv = findViewById(R.id.msg_reg_tv)
        msg_reg_tv!!.typeface = MRR
        username!!.typeface = MRR
        email!!.typeface = MRR
        password!!.typeface = MRR
        btn_register!!.typeface = MR
        register_tv!!.typeface = MR
        auth = FirebaseAuth.getInstance()
        btn_register!!.setOnClickListener { view: View? ->
            val txt_username = username!!.text.toString()
            val txt_email = email!!.text.toString()
            val txt_password = password!!.text.toString()
            hideKeyboard(this@RegisterActivity)
            if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(
                    txt_password
                )
            ) {
                Toast.makeText(this@RegisterActivity, "All fileds are required", Toast.LENGTH_SHORT)
                    .show()
            } else if (txt_password.length < 6) {
                Toast.makeText(
                    this@RegisterActivity,
                    "password must be at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                register(txt_username, txt_email, txt_password)
            }
        }
    }

    private fun register(username: String, email: String, password: String) {
        dialog = showLoader(this@RegisterActivity)
        auth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val firebaseUser = auth!!.currentUser!!
                    val userid = firebaseUser.uid
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid)
                    val hashMap = HashMap<String, String>()
                    hashMap["id"] = userid
                    hashMap["username"] = username
                    hashMap["imageURL"] = "default"
                    hashMap["status"] = "offline"
                    hashMap["bio"] = ""
                    hashMap["search"] = username.lowercase(Locale.getDefault())
                    if (dialog != null) {
                        dialog!!.dismiss()
                    }
                    reference!!.setValue(hashMap).addOnCompleteListener { task1: Task<Void?> ->
                        if (task1.isSuccessful) {
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "You can't register woth this email or password",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (dialog != null) {
                        dialog!!.dismiss()
                    }
                }
            }
    }
}