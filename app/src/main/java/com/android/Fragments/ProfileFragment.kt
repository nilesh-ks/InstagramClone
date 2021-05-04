package com.android.Fragments

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
//import com.android.instagram.R.id.edit_account_settings_btn


class ProfileFragment : Fragment() {
lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      val view=inflater.inflate(R.layout.fragment_profile,container, false)
        button=view.findViewById(R.id.edit_account_settings_btn)
        button.setOnClickListener {
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }
        return  view
    }


}