package com.android

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.android.instagram.R
import com.android.instagram.R.id.fullname_signup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.Context
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {
  //  lateinit var btnSignIn: Button
  //  lateinit var btnSignUp: Button
 /*lateinit var context: SignUpActivity
 lateinit var fullname_Signup:EditText
    lateinit var username_Signup:EditText
    lateinit var email_Signup:EditText
    lateinit var password_Signup:EditText*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
     //   btnSignIn=findViewById(R.id.signinLink_btn)
    //    btnSignUp=findViewById(R.id.signupLink_btn)

        //fullname_Signup=findViewById(R.id.fullname_signup)
       /* username_Signup=findViewById(R.id.username_signup)
        email_Signup=findViewById(R.id.email_signup)
        password_Signup=findViewById(R.id.password_signup)*/
        signinLink_btn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        btn_signup.setOnClickListener {
            CreateAccount()
        }
    }

    private fun CreateAccount() {
        val fullname= fullname_signup.text.toString()
        val username=username_signup.text.toString()
        val email=email_signup.text.toString()
        val password=password_signup.text.toString()

        when{
            TextUtils.isEmpty(fullname) -> Toast.makeText(this, "Full name is required!",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(username) -> Toast.makeText(this, "Username is required!",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email address is required!",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is required!",Toast.LENGTH_LONG).show()

            else ->{

                val progressDialog= ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please wait, this may take a while..")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth= FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {task->
                        if(task.isSuccessful)
                        {
                            Log.e("success", "saving info")
                            saveUserInfo(fullname,username,email, progressDialog)
                        }else
                        {
                            val message=task.exception!!.toString()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                            mAuth.signOut()

                        }
                    }
            }
        }
    }

    private fun saveUserInfo(fullname: String, username: String, email: String, progressDialog: ProgressDialog)
    {
        val currentUserID= FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap= HashMap<String, Any>()
        userMap["uid"]= currentUserID
        userMap["fullname"]=currentUserID
        userMap["username"]=currentUserID
        userMap["email"]=currentUserID
        userMap["bio"]="Hey there! I'm using Instagram."
    //In order to get the default profile image, we need to copy the profile image from the drawable folder
    // and upload it in the Firebase storage.
        userMap["image"]="https://firebasestorage.googleapis.com/v0/b/instagram-7d7c6.appspot.com/o/Default%20images%2Fprofile.png?alt=media&token=dfd70ef3-166b-4f78-aa3d-dfbcd6a62ae6"
        usersRef.child(currentUserID).push().setValue(userMap)
            .addOnCompleteListener { task->
                if(task.isSuccessful)
                {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Account has been created successfully!!", Toast.LENGTH_LONG).show()

                    val intent=Intent(this@SignUpActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    Log.d("success", "successful")
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Log.d("failed", "not successful")
                    val message=task.exception!!.toString()
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }

    }
}