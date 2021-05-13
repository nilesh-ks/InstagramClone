package com.android.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.Adapter.UserAdapter
import com.android.Model.User
import com.android.instagram.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.view.*
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

       mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, mUser as ArrayList<User>, true) }
        recyclerView?.adapter = userAdapter

        view.search_edit_text.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
            {

            }
            override fun afterTextChanged(p0: Editable?)
            {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int)
            {
                if(view?.search_edit_text?.text.toString()=="")
                {

                }
                else
                {
                    recyclerView?.visibility=View.VISIBLE
                    retrieveUsers()
                    searchUser(s.toString().toLowerCase(Locale.getDefault()))
                }
            }
 })

        return view
    }

    private fun searchUser(input: String) {
        Log.d("searching","user1")
        val query = FirebaseDatabase.getInstance().reference
                .child("Users")
                .orderByChild("fullname")
                .startAt(input)
                .endAt( input+"\uf8ff")

        query.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                mUser?.clear()
                Log.d("searching","user2")
                val c = dataSnapshot.childrenCount
                Log.i("countSearch","$c")
                for (i in dataSnapshot.children)
                {
                    Log.d("searching","1")
                    val user = i.getValue(User::class.java)
                    Log.d("msg", "$user")
                    if(user!=null)
                    {
                        Log.d("searching","user3")
                        mUser?.add(user)
                    }
                    Log.d("searching","user4")
                }
                Log.d("searching","out of data change")
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("searching","user5")
            }


        })
    }

    private fun retrieveUsers() {
        Log.d("retrieving","user1")
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
        usersRef.addValueEventListener(object: ValueEventListener
        {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(view?.search_edit_text?.text.toString()=="")
                {
                    Log.d("retrieving","user2")
                    mUser?.clear()
                    for (snapshot in dataSnapshot.children)
                    {
                        Log.d("retrieving","user3")
                        val user = snapshot.getValue(User::class.java)
                        if(user!=null)
                        {
                            mUser?.add(user)
                            Log.d("retrieving","user4")

                        }
                    }
                    userAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("retrieving","user5")
            }

        })
    }

}


