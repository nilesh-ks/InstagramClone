package com.android

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.android.Model.User
import com.android.instagram.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUrl: Uri?= null
    private var storageProfilePicRef: StorageReference? = null // reference for firebase storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")
        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent=Intent(this@AccountSettingsActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        change_image_text_btn.setOnClickListener {
            checker = "clicked"

            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@AccountSettingsActivity)
        }

        save_profile_btn.setOnClickListener{
            if (checker=="clicked")
            {
                uploadImageAndUpdateInfo()
            }else
            {
                updateUserInfoOnly()
            }
        }

        userInfo()

    }

    private fun uploadImageAndUpdateInfo() {
        when {
            imageUrl == null -> Toast.makeText(
                this,
                "Please select an image first!!",
                Toast.LENGTH_LONG
            ).show()

            TextUtils.isEmpty(full_name_profile_frag.text.toString()) -> Toast.makeText(
                this,
                "Full name is required!",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(username_profile_frag.text.toString()) -> Toast.makeText(
                this,
                "Username is required!",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(bio_profile_frag.text.toString()) -> Toast.makeText(
                this,
                "Please write your bio first",
                Toast.LENGTH_LONG
            ).show()

            else -> {
                val progressDialog= ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Please wait, we are updating your profile..")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid+ ".jpg")

                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUrl!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {task ->
                    if (!task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl

                }).addOnCompleteListener(OnCompleteListener<Uri> {task ->
                    if(task.isSuccessful)
                    {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref= FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()

                        userMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                        userMap["username"] = username_profile_frag.text.toString().toLowerCase()
                        userMap["bio"] = bio_profile_frag.text.toString()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)
                        Toast.makeText(this, "Account Information has been updated successfully!!", Toast.LENGTH_LONG).show()

                        val intent=Intent(this@AccountSettingsActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        Log.d("success", "successful")
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }
                    else{
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data!=null)
        {
            val result= CropImage.getActivityResult(data)
            imageUrl = result.uri
            profile_image_view_profile_frag.setImageURI(imageUrl)
        }
    }

    private fun updateUserInfoOnly() {

        when {
            TextUtils.isEmpty(full_name_profile_frag.text.toString()) -> Toast.makeText(
                this,
                "Full name is required!",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(username_profile_frag.text.toString()) -> Toast.makeText(
                this,
                "Username is required!",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(bio_profile_frag.text.toString()) -> Toast.makeText(
                this,
                "Please write your bio first",
                Toast.LENGTH_LONG
            ).show()

            else -> {
                val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

                val userMap = HashMap<String, Any>()

                userMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                userMap["username"] = username_profile_frag.text.toString().toLowerCase()
                userMap["bio"] = bio_profile_frag.text.toString()

                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Account information has been updated successfully!!", Toast.LENGTH_LONG).show()

                val intent=Intent(this@AccountSettingsActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                Log.d("success", "successful")
                startActivity(intent)
                finish()
            }
        }
    }

    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.image).placeholder(R.drawable.profile).into(profile_image_view_profile_frag)
                    username_profile_frag.setText(user!!.username)
                    full_name_profile_frag.setText(user!!.fullname)
                    bio_profile_frag.setText(user!!.bio)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}