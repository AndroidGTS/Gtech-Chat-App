package com.gtechapps.chatmessager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.*
import com.gtechapps.chatmessager.Model.User

class ViewProfileActivity : BottomSheetDialogFragment() {
    var uid: String? = null
    var reference: DatabaseReference? = null
    var username: TextView? = null
    var bio_et: TextView? = null
    var profile_img: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_view_profile, container, false)
        if (arguments != null) {
            uid = requireArguments().getString("uid")
            profile_img = view.findViewById(R.id.view_profile_image)
            username = view.findViewById(R.id.view_username)
            bio_et = view.findViewById(R.id.view_bio_et)
            reference = FirebaseDatabase.getInstance().getReference("Users").child(uid!!)
            reference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(
                        User::class.java
                    )!!
                    username!!.text = user.username
                    bio_et!!.text = user.bio
                    if (user.imageURL == "default") {
                        profile_img!!.setImageResource(R.drawable.profile_img)
                    } else {
                        //change this
                        Glide.with(mContext!!.applicationContext).load(
                            user.imageURL
                        ).into(profile_img!!)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
        return view
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mContext: Context? = null

        @JvmStatic
        fun newInstance(uid: String?, context: Context?): ViewProfileActivity {
            val args = Bundle()
            args.putString("uid", uid)
            mContext = context
            val fragment = ViewProfileActivity()
            fragment.arguments = args
            return fragment
        }
    }
}