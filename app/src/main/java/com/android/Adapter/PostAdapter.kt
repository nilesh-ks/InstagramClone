package com.android.Adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.android.MainActivity
import com.android.Model.Post
import com.android.Model.User
import com.android.instagram.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_account_settings.*

class PostAdapter(private val mContext: Context,
                  private val mPost: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = null

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var profileimage: CircleImageView
        var postimage: ImageView
        var likeButton: ImageView
        var commentButton: ImageView
        var saveButton: ImageView
        var userName: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView

        init {
            profileimage=itemView.findViewById(R.id.user_profile_image_search)
            postimage=itemView.findViewById(R.id.post_image_home)
            likeButton=itemView.findViewById(R.id.post_image_like_btn)
            commentButton=itemView.findViewById(R.id.post_image_comment_btn)
            saveButton=itemView.findViewById(R.id.post_save_comment_btn)
            userName=itemView.findViewById(R.id.user_name_post)
            likes=itemView.findViewById(R.id.likes)
            publisher=itemView.findViewById(R.id.publisher)
            description=itemView.findViewById(R.id.description)
            comments=itemView.findViewById(R.id.comments)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post= mPost[position]

        Picasso.get().load(post.postimage).into(holder.postimage)

        publisherInfo(holder.profileimage, holder.userName, holder.publisher, post.publisher)

        holder.likeButton.setOnClickListener{
            if(holder.likeButton.tag=="Like")
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.postid.toString())
                    .child(firebaseUser!!.uid)
                    .setValue(true)
            }else
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.postid.toString())
                    .child(firebaseUser!!.uid)
                    .removeValue()

                val intent = Intent(mContext, MainActivity::class.java)
                mContext.startActivity(intent)
            }
        }

    }

    private fun publisherInfo(profileimage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String?) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID.toString())

        usersRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.image).placeholder(R.drawable.profile).into(profileimage)
                    userName.text= user!!.username
                   publisher.text = user!!.fullname

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun getItemCount(): Int {
       return mPost.size
    }


}