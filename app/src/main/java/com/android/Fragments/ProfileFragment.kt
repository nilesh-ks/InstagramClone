package com.android.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.contentValuesOf
import com.android.AccountSettingsActivity
import com.android.instagram.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.view.*

//import com.android.instagram.R.id.edit_account_settings_btn


class ProfileFragment : Fragment() {
lateinit var button: Button
private lateinit var profileId: String
private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      val view=inflater.inflate(R.layout.fragment_profile,container, false)

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref!=null)
        {
            this.profileId = pref.getString("profileId", "none").toString()
        }
        if(profileId == firebaseUser.uid)
        {
            view.edit_account_settings_btn.text = "Edit Profile"
        }else if(profileId != firebaseUser.uid)
        {
            checkFollowingAndFollowingButtonStatus()
        }

        button=view.findViewById(R.id.edit_account_settings_btn)
        button.setOnClickListener {
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }
        return  view
    }

    private fun checkFollowingAndFollowingButtonStatus() {
        val followingRef= FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")

        if(followingRef != null)
        {
            followingRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child(profileId).exists())
                    view?.edit_account_settings_btn?.text="Following"
                    else
                        view?.edit_account_settings_btn?.text="Follow"
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    private fun getFollowers()
    {
        val follwersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        follwersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    view?.total_followers?.text= snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}