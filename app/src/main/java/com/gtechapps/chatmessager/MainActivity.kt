package com.gtechapps.chatmessager

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.gtechapps.chatmessager.Adapter.OnItemClick
import com.gtechapps.chatmessager.Fragments.ChatsFragment
import com.gtechapps.chatmessager.Fragments.ProfileFragment
import com.gtechapps.chatmessager.Fragments.UsersFragment
import com.gtechapps.chatmessager.Model.Chat
import com.gtechapps.chatmessager.Model.User
import com.gtechapps.chatmessager.Utils.showLoader
import com.gtechapps.chatmessager.ViewProfileActivity.Companion.newInstance
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class MainActivity : AppCompatActivity(), OnItemClick {
    var doubleBackToExitPressedOnce = false
    var profile_image: CircleImageView? = null
    var username: TextView? = null
    var dialog: ProgressDialog? = null
    var MR: Typeface? = null
    var MRR: Typeface? = null
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var onItemClick: OnItemClick? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onItemClick = this
        MRR = Typeface.createFromAsset(assets, "fonts/myriadregular.ttf")
        MR = Typeface.createFromAsset(assets, "fonts/myriad.ttf")
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        profile_image = findViewById(R.id.profile_image)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        profile_image!!.setOnClickListener(View.OnClickListener { view: View? ->
            val tab = tabLayout.getTabAt(2)
            tab?.select()
        })
        username = findViewById(R.id.username)
        username!!.typeface = MR
        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(
                    User::class.java
                )
                if (user != null) {
                    if (user.username != null) {
                        username!!.text = user.username
                    }
                    if (user.imageURL != null) {
                        if (user.imageURL == "default") {
                            profile_image!!.setImageResource(R.drawable.profile_img)
                        } else {
                            //change this
                            Glide.with(applicationContext).load(user.imageURL).into(profile_image!!)
                        }
                    } else {
                        profile_image!!.setImageResource(R.drawable.profile_img)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        dialog = showLoader(this@MainActivity)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var unread = 0
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)!!
                    if (chat.receiver == firebaseUser!!.uid && !chat.isIsseen) {
                        unread++
                    }
                }
                if (unread == 0) {
                    viewPagerAdapter.addFragment(ChatsFragment.newInstance(onItemClick), "Chats")
                } else {
                    viewPagerAdapter.addFragment(
                        ChatsFragment.newInstance(onItemClick),
                        "($unread) Chats"
                    )
                }
                viewPagerAdapter.addFragment(UsersFragment.newInstance(onItemClick), "Users")
                viewPagerAdapter.addFragment(ProfileFragment(), "Profile")
                viewPager.adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)
                if (dialog != null) {
                    dialog!!.dismiss()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut()
            // change this code beacuse your app will crash
            startActivity(
                Intent(
                    this@MainActivity,
                    StartActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            return true
        }
        return false
    }

    override fun onItemCLick(uid: String?, view: View?) {
        val viewProfileActivity = newInstance(uid, this)
        viewProfileActivity.show(
            supportFragmentManager,
            "view_profile"
        )
    }

    private fun status(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        reference!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click Back again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    internal class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(
        fm!!
    ) {
        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

        init {
            fragments = ArrayList()
            titles = ArrayList()
        }
    }
}