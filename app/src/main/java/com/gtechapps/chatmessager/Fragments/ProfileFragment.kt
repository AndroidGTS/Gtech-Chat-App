package com.gtechapps.chatmessager.Fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.gtechapps.chatmessager.Model.User
import com.gtechapps.chatmessager.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "PropertyName")
class ProfileFragment : Fragment() {
    var image_profile: CircleImageView? = null
    var profile_tv: TextView? = null
    var username: EditText? = null
    var bio_et: EditText? = null
    var edit_img: ImageView? = null
    var save: Button? = null
    var reference: DatabaseReference? = null
    var fuser: FirebaseUser? = null
    var MR: Typeface? = null
    var MRR: Typeface? = null
    var storageReference: StorageReference? = null
    private var imageUri: Uri? = null
    private var uploadTask: StorageTask<*>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        MRR = Typeface.createFromAsset(requireContext().assets, "fonts/myriadregular.ttf")
        MR = Typeface.createFromAsset(requireContext().assets, "fonts/myriad.ttf")
        image_profile = view.findViewById(R.id.profile_image)
        username = view.findViewById(R.id.username)
        profile_tv = view.findViewById(R.id.profile_tv)
        bio_et = view.findViewById(R.id.bio_et)
        edit_img = view.findViewById(R.id.edit_image)
        save = view.findViewById(R.id.save_btn)
        username!!.typeface = MR
        profile_tv!!.typeface = MR
        bio_et!!.typeface = MRR
        save!!.typeface = MR
        storageReference = FirebaseStorage.getInstance().getReference("uploads")
        fuser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser!!.uid)
        edit_img!!.setOnClickListener {
            save!!.visibility = View.VISIBLE
            username!!.isEnabled = true
            bio_et!!.isEnabled = true
            username!!.setSelection(username!!.text.length)
        }
        save!!.setOnClickListener {
            username!!.isEnabled = false
            bio_et!!.isEnabled = false
            reference!!.child("bio").setValue(bio_et!!.text.toString().trim { it <= ' ' })
                .addOnCompleteListener { task ->
                }
            reference!!.child("username")
                .setValue(username!!.text.toString().trim { it <= ' ' })
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Profile Updated...", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Unable to Save...", Toast.LENGTH_SHORT).show()
                    }
                }
            save!!.visibility = View.GONE
        }
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (isAdded) {
                    val user = dataSnapshot.getValue(
                        User::class.java
                    )
                    if (user != null) {
                        if (user.bio != null) {
                            bio_et!!.setText(user.bio)
                        }
                        if (user.username != null) {
                            username!!.setText(user.username)
                        }
                        if (user.imageURL != null) {
                            if ((user.imageURL == "default")) {
                                image_profile!!.setImageResource(R.drawable.profile_img)
                            } else {
                                //change this
                                Glide.with((activity)!!).load(user.imageURL).into(image_profile!!)
                            }
                        } else {
                            image_profile!!.setImageResource(R.drawable.profile_img)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        image_profile!!.setOnClickListener { view1: View? -> openImage() }
        return view
    }

    private fun openImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_REQUEST)
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = requireContext().contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun uploadImage() {
        val pd = ProgressDialog(context)
        pd.setIndeterminateDrawable(resources.getDrawable(R.drawable.ic_picture))
        pd.setMessage("Uploading...")
        pd.show()
        if (imageUri != null) {
            val fileReference = storageReference!!.child(
                (System.currentTimeMillis()
                    .toString() + "." + getFileExtension(imageUri!!))
            )
            uploadTask = fileReference.putFile(imageUri!!)
            (uploadTask as UploadTask).continueWithTask(Continuation { task: Task<UploadTask.TaskSnapshot?> ->
                if (!task.isSuccessful) {
                    throw (task.exception)!!
                }
                fileReference.downloadUrl
            } as Continuation<UploadTask.TaskSnapshot?, Task<Uri>>)
                .addOnCompleteListener { task: Task<Uri?> ->
                    if (task.isSuccessful) {
                        val downloadUri: Uri? = task.result
                        val mUri: String = downloadUri.toString()
                        reference = FirebaseDatabase.getInstance().getReference("Users")
                            .child(fuser!!.uid)
                        val map: HashMap<String, Any> = HashMap()
                        map.put("imageURL", "" + mUri)
                        reference!!.updateChildren(map)
                        pd.dismiss()
                    } else {
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                        pd.dismiss()
                    }
                }.addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                pd.dismiss()
            }
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == IMAGE_REQUEST) && (resultCode == Activity.RESULT_OK
                    ) && (data != null) && (data.data != null)
        ) {
            imageUri = data.data
            if (uploadTask != null && uploadTask!!.isInProgress) {
                Toast.makeText(context, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else {
                uploadImage()
            }
        }
    }

    companion object {
        private val IMAGE_REQUEST = 1
    }
}