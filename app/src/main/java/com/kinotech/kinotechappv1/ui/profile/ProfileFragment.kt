package com.kinotech.kinotechappv1.ui.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.kinotech.kinotechappv1.AuthActivity
import com.kinotech.kinotechappv1.R
import java.lang.reflect.TypeVariable
import com.kinotech.kinotechappv1.databinding.FragmentProfileBinding
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var photoAcc: ImageView
    private lateinit var nickName: TextView
    private lateinit var signOut: Button
    private lateinit var mSignInClient: GoogleSignInClient
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var profileId: String
    private lateinit var fullName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        signOut = root.findViewById(R.id.imageExit)
        nickName = root.findViewById(R.id.textProfile)
        photoAcc = root.findViewById(R.id.profile_photo)
        userInfo()
        val button = root.findViewById<Button>(R.id.changeProfileButton)
        button.setOnClickListener {
            loadfragment()
        }
        return root
    }

    private fun loadfragment() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        if (transaction != null) {
            transaction.replace(R.id.container, ChangeProfileFragment())
            transaction.disallowAddToBackStack()
            transaction.commit()
        }

    }

    override fun onResume() {
        super.onResume()
        val buffAcc = GoogleSignIn.getLastSignedInAccount(context)
        //bind(buffAcc)
        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }!!
        signOut.setOnClickListener {
            mSignInClient.signOut()
            val intent = Intent(context, AuthActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }


//    private fun bind(acc: GoogleSignInAccount?) {
//        if (acc == null) {
//            Log.d("check", "null")
//        } else {
//            nickName.text = acc.displayName
//            Glide
//                .with(this)
//                .load(acc.photoUrl)
//                .error(R.drawable.ic_like_40dp)
//                .into(photoAcc)
//        }
//    }
//    private fun userInfo(){
//        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
//        usersRef.addValueEventListener(object : ValueEventListener
//        {
//            override fun onDataChange(p0: DataSnapshot){
//                val user = p0.getValue<User>(User::class.java)
//                //Picasso.get().load(user!!.getPhoto()).placeholder(R.drawable.ic_like_40dp).into(photoAcc)
//                view?. = user!!.getFullName()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//    }
    private fun userInfo()
    {
        nickName.text = firebaseUser.displayName
        Glide
            .with(this)
            .load(firebaseUser.photoUrl)
            .error(R.drawable.ic_like_40dp)
            .into(photoAcc)
    }
//private fun userInfo(){
//    val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
//    usersRef.addValueEventListener(object : ValueEventListener
//    {
//        override fun onDataChange(p0: DataSnapshot){
//            fullName = p0.child("fullName").value.toString()
//            nickName.text = fullName
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//            TODO("Not yet implemented")
//        }
//    })
//}

}
