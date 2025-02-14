package com.kinotech.kinotechappv1.ui.profile.subs

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kinotech.kinotechappv1.R
import com.kinotech.kinotechappv1.databinding.SubscriptionItemBinding
import com.kinotech.kinotechappv1.ui.profile.FriendProfileFragment
import com.kinotech.kinotechappv1.ui.profile.SubsInfo
import java.util.Locale

class SubscriptionsAdapter(
    private val subscription: ArrayList<SubsInfo>
) :
    RecyclerView.Adapter<SubscriptionsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionsViewHolder {
        val binding = SubscriptionItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubscriptionsViewHolder(binding, subscription)
    }

    override fun getItemCount(): Int {
        return subscription.size
    }

    override fun onBindViewHolder(holder: SubscriptionsViewHolder, position: Int) {
        return holder.bind(subscription[position])
    }
}

class SubscriptionsViewHolder(
    private val binding: SubscriptionItemBinding,
    private val subscription: ArrayList<SubsInfo>
) : RecyclerView.ViewHolder(binding.root) {
    private val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    fun bind(subsInfo: SubsInfo) {
        binding.apply {
            profileName.text = subsInfo.fullName.split(" ")
                .joinToString(" ") { it.capitalize(Locale.getDefault()) }
            Glide
                .with(binding.root)
                .load(subsInfo.photo)
                .error(R.drawable.ic_add)
                .into(binding.profilePic)
            root.setOnClickListener {
                val activity: AppCompatActivity = root.context as AppCompatActivity
                val transaction = activity.supportFragmentManager.beginTransaction()
                transaction.replace(
                    R.id.container,
                    FriendProfileFragment(subsInfo)
                )
                transaction.addToBackStack(null)
                transaction.commit()
            }

            checkFollowingStatus(subsInfo.uid, likeProfile)
            likeProfile.setOnClickListener {
                if (likeProfile.tag == "not liked") {
                    setSubscribe(subsInfo)
                } else {
                    setUnsubscribe(subsInfo)
                }
            }
        }
    }

    private fun setSubscribe(subsInfo: SubsInfo) {
        firebaseUser?.uid.let { uid ->
            FirebaseDatabase.getInstance().reference
                .child("Follow")
                .child(uid.toString())
                .child("Following")
                .child(subsInfo.uid)
                .setValue(subsInfo.uid)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseUser?.uid.let { uid ->
                            FirebaseDatabase.getInstance().reference
                                .child("Follow")
                                .child(subsInfo.uid)
                                .child("Followers")
                                .child(uid.toString())
                                .setValue(uid.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.i("follow", "Подписан")
                                    }
                                }
                        }
                    }
                }
        }
    }

    private fun setUnsubscribe(subsInfo: SubsInfo) {
        firebaseUser?.uid.let { uid ->
            FirebaseDatabase.getInstance().reference
                .child("Follow")
                .child(uid.toString())
                .child("Following")
                .child(subsInfo.uid)
                .removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseUser?.uid.let { uid ->
                            FirebaseDatabase.getInstance().reference
                                .child("Follow").child(subsInfo.uid)
                                .child("Followers").child(uid.toString())
                                .removeValue().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.i("follow", "Отписан")
                                    }
                                }
                        }
                    }
                }
        }
    }

    private fun checkFollowingStatus(uid: String, likeProfile: ImageButton) {
        val followingRef = firebaseUser?.uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Follow")
                .child(it.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(uid).exists()) {
                        likeProfile.setImageResource(R.drawable.ic_liked_40)
                        likeProfile.tag = "liked"
                    } else {
                        likeProfile.setImageResource(R.drawable.ic_like_40dp)
                        likeProfile.tag = "not liked"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("dbError", "$error")
                }
            }
        )
    }
}
