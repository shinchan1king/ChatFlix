package com.example.chatflix.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.chatflix.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

const val POSTER_IMAGE = "https://i.ibb.co/12fHwfg/netflix-downloads.png"

class UserFragment : BottomNavFragment() {
    private lateinit var binding: FragmentUserBinding
    private var mAuth: FirebaseAuth?=null
    var database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(layoutInflater, container, false)
        val myRef = database.reference
        mAuth = FirebaseAuth.getInstance()
        var coins=0
        fun readData(Rslt: Query)
        {


            Rslt.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {


                }

                 override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {


                        for (data in snapshot.children) {


                             var coins1 = data.getValue().toString()

                           binding.userCoins.text="User Coins"+" "+coins1



                        }


                    }
                }
            })


        }
        val myRslt= mAuth!!.currentUser?.let { myRef.child("users").child(it.uid) }
        if (myRslt != null) {
            readData(myRslt)
        };
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        Glide.with(binding.posterImage).load(POSTER_IMAGE).into(binding.posterImage)
    }

    override fun onFirstDisplay() {
    }
}