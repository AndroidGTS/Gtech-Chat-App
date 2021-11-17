package com.gtechapps.chatmessager.Adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.gtechapps.chatmessager.Model.Chat
import com.gtechapps.chatmessager.R
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val mContext: Context,
    private val mChat: ArrayList<Chat?>,
    private val imageurl: String
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    var MR: Typeface
    var MRR: Typeface
    var fuser: FirebaseUser? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MSG_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = mChat[position]
        holder.show_message.typeface = MRR
        holder.txt_seen.typeface = MRR
        holder.show_message.text = chat!!.message
        if (chat.time != null && chat.time!!.trim { it <= ' ' } != "") {
            holder.time_tv.text = holder.convertTime(chat.time)
        }
        if (imageurl == "default") {
            holder.profile_image.setImageResource(R.drawable.profile_img)
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image)
        }
        if (position == mChat.size - 1) {
            if (chat.isIsseen) {
                holder.txt_seen.text = "Seen"
            } else {
                holder.txt_seen.text = "Delivered"
            }
        } else {
            holder.txt_seen.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mChat.size
    }

    override fun getItemViewType(position: Int): Int {
        fuser = FirebaseAuth.getInstance().currentUser
        return if (mChat[position]!!.sender == fuser!!.uid) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var show_message: TextView
        var profile_image: ImageView
        var txt_seen: TextView
        var time_tv: TextView
        fun convertTime(time: String?): String {
            val formatter = SimpleDateFormat("h:mm a")
            return formatter.format(Date(time!!.toLong()))
        }

        init {
            show_message = itemView.findViewById(R.id.show_message)
            profile_image = itemView.findViewById(R.id.profile_image)
            txt_seen = itemView.findViewById(R.id.txt_seen)
            time_tv = itemView.findViewById(R.id.time_tv)
        }
    }

    companion object {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }

    init {
        MRR = Typeface.createFromAsset(mContext.assets, "fonts/myriadregular.ttf")
        MR = Typeface.createFromAsset(mContext.assets, "fonts/myriad.ttf")
    }
}