package com.gtechapps.chatmessager.Fragments

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.gtechapps.chatmessager.Adapter.OnItemClick
import com.gtechapps.chatmessager.Adapter.UserAdapter
import com.gtechapps.chatmessager.Model.Chatlist
import com.gtechapps.chatmessager.Model.User
import com.gtechapps.chatmessager.Notifications.Token
import com.gtechapps.chatmessager.R
import java.util.*

class ChatsFragment : Fragment() {
    var MR: Typeface? = null
    var MRR: Typeface? = null
    var frameLayout: FrameLayout? = null
    var es_descp: TextView? = null
    var es_title: TextView? = null
    var fuser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUsers: ArrayList<User?>? = null
    private var usersList: ArrayList<Chatlist?>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        MRR = Typeface.createFromAsset(requireContext().assets, "fonts/myriadregular.ttf")
        MR = Typeface.createFromAsset(requireContext().assets, "fonts/myriad.ttf")
        recyclerView = view.findViewById(R.id.recycler_view)
        frameLayout = view.findViewById(R.id.es_layout)
        es_descp = view.findViewById(R.id.es_descp)
        es_title = view.findViewById(R.id.es_title)
        es_descp!!.typeface = MR
        es_title!!.typeface = MRR
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView!!.context, DividerItemDecoration.VERTICAL)
        recyclerView!!.addItemDecoration(dividerItemDecoration)
        fuser = FirebaseAuth.getInstance().currentUser
        usersList = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser!!.uid)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val chatlist = snapshot.getValue(Chatlist::class.java)
                    usersList!!.add(chatlist)
                }
                if (usersList!!.size == 0) {
                    frameLayout!!.visibility = View.VISIBLE
                } else {
                    frameLayout!!.visibility = View.GONE
                }
                chatList()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String?> ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                updateToken(token)
            }
        return view
    }

    private fun updateToken(token: String?) {
        val reference = FirebaseDatabase.getInstance().getReference("Tokens")
        val token1 = Token(token)
        reference.child(fuser!!.uid).setValue(token1)
    }

    private fun chatList() {
        mUsers = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(
                        User::class.java
                    )
                    for (chatlist in usersList!!) {
                        if (user?.id != null && chatlist != null && chatlist.id != null && user.id == chatlist.id) {
                            mUsers!!.add(user)
                        }
                    }
                }
                userAdapter = UserAdapter(context, onItemClick!!, mUsers!!, true)
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    companion object {
        var onItemClick: OnItemClick? = null
        fun newInstance(click: OnItemClick?): ChatsFragment {
            onItemClick = click
            val args = Bundle()
            val fragment = ChatsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}