package com.gtechapps.chatmessager

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.gtechapps.chatmessager.Adapter.MessageAdapter
import com.gtechapps.chatmessager.Fragments.APIService
import com.gtechapps.chatmessager.Model.Chat
import com.gtechapps.chatmessager.Model.User
import com.gtechapps.chatmessager.Notifications.*
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MessageActivity : AppCompatActivity() {
    var profile_image: CircleImageView? = null
    var username: TextView? = null
    var MR: Typeface? = null
    var MRR: Typeface? = null
    var fuser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var btn_send: ImageButton? = null
    var text_send: EditText? = null
    var messageAdapter: MessageAdapter? = null
    var mchat: ArrayList<Chat?>? = null
    var recyclerView: RecyclerView? = null

    var seenListener: ValueEventListener? = null
    var userid: String? = null
    var apiService: APIService? = null
    var notify = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        MRR = Typeface.createFromAsset(assets, "fonts/myriadregular.ttf")
        MR = Typeface.createFromAsset(assets, "fonts/myriad.ttf")
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { // and this
            startActivity(
                Intent(
                    this@MessageActivity,
                    MainActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
        apiService = Client.getClient("https://fcm.googleapis.com/")!!.create(
            APIService::class.java
        )
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
        profile_image = findViewById(R.id.profile_image)
        username = findViewById(R.id.username)
        btn_send = findViewById(R.id.btn_send)
        text_send = findViewById(R.id.text_send)
        username!!.typeface = MR
        text_send!!.setTypeface(MRR)
        val intent = intent
        userid = intent!!.getStringExtra("userid")
        fuser = FirebaseAuth.getInstance().currentUser
        btn_send!!.setOnClickListener { view: View? ->
            notify = true
            val msg = text_send!!.text.toString()
            val time = System.currentTimeMillis().toString()
            if (msg != "") {
                sendMessage(fuser!!.uid, userid, msg, time)
            } else {
                Toast.makeText(
                    this@MessageActivity,
                    "You can't send empty message",
                    Toast.LENGTH_SHORT
                ).show()
            }
            text_send!!.setText("")
        }
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid!!)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(
                    User::class.java
                )
                username!!.text = user!!.username
                if (user.imageURL == "default") {
                    profile_image!!.setImageResource(R.drawable.profile_img)
                } else {
                    //and this
                    Glide.with(applicationContext).load(user.imageURL).into(profile_image!!)
                }
                readMessages(fuser!!.uid, userid, user.imageURL!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        seenMessage(userid)
    }

    private fun seenMessage(userid: String?) {
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        seenListener = reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.receiver == fuser!!.uid && chat.sender == userid) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        snapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun sendMessage(sender: String, receiver: String?, message: String, time: String) {
        var reference = FirebaseDatabase.getInstance().reference
        val hashMap = HashMap<String, Any?>()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["message"] = message
        hashMap["isseen"] = false
        hashMap["time"] = time
        reference.child("Chats").push().setValue(hashMap)


        // add user to chat fragment
        val chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
            .child(fuser!!.uid)
            .child(userid!!)
        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        val chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
            .child(userid!!)
            .child(fuser!!.uid)
        chatRefReceiver.child("id").setValue(fuser!!.uid)
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(
                    User::class.java
                )
                if (notify) {
                    sendNotifiaction(receiver, user!!.username!!, message)
                }
                notify = false
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun sendNotifiaction(receiver: String?, username: String, message: String) {
        val tokens = FirebaseDatabase.getInstance().getReference("Tokens")
        val query = tokens.orderByKey().equalTo(receiver)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val token = snapshot.getValue(
                        Token::class.java
                    )
                    val data = Data(
                        fuser!!.uid, R.drawable.profile_img, "$username: $message", "New Message",
                        userid
                    )
                    val sender = Sender(data, token!!.token!!)
                    apiService!!.sendNotification(sender)!!.enqueue(object : Callback<MyResponse?> {
                            override fun onResponse(
                                call: Call<MyResponse?>,
                                response: Response<MyResponse?>
                            ) {
                                if (response.code() == 200) {
                                    if (response.body()!!.success != 1) {
                                        //Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse?>, t: Throwable) {}
                        })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun readMessages(myid: String, userid: String?, imageurl: String) {
        mchat = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mchat!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.receiver == myid && chat.sender == userid ||
                        chat.receiver == userid && chat.sender == myid
                    ) {
                        mchat!!.add(chat)
                    }
                    messageAdapter = MessageAdapter(this@MessageActivity, mchat!!, imageurl)
                    recyclerView!!.adapter = messageAdapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun currentUser(userid: String?) {
        val editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit()
        editor.putString("currentuser", userid)
        editor.apply()
    }

    private fun status(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        reference!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        status("online")
        currentUser(userid)
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
        status("offline")
        currentUser("none")
    }
}