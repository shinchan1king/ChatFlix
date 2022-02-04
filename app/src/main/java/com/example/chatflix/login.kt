package com.example.chatflix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.chatflix.screens.BottomNavActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class login : AppCompatActivity() {
    private var mAuth: FirebaseAuth?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
    }
    fun btLogin(view:View)
    {
        //val userEmail = etEmail.text.toString()
       // val userPassword = etPassword.text.toString()
      /*  val userEmail="bhavymiglani@gmail.com"
        val userPassword="bhavy123"
        if (userEmail == "" || userPassword == "") {
            Toast.makeText(
                applicationContext,
                "Please enter both email and password  to login",
                Toast.LENGTH_LONG
            ).show()

        } else {
            mAuth?.signInWithEmailAndPassword(userEmail, userPassword)
                ?.addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Signin Successful", Toast.LENGTH_SHORT)
                            .show()
                        Loadmain()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Please provide valid email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

        }*/
        var bottomSheetLoginFragment=BottomSheetLoginFragment()

            bottomSheetLoginFragment.show(supportFragmentManager,bottomSheetLoginFragment.tag)


    }
    fun btSignup(view: View)
    {

        var bottomSheetSignupFragment=BottomSheetSignupFragment()

        bottomSheetSignupFragment.show(supportFragmentManager,bottomSheetSignupFragment.tag)

    }
    override fun onStart() {
        super.onStart()
        Loadmain()
    }
    fun Loadmain()
    {

        var CurrenntUser=mAuth!!.currentUser
        if(CurrenntUser!=null){
            val intent= Intent(this,BottomNavActivity::class.java)
            intent.putExtra("Email",CurrenntUser.email)
            intent.putExtra("uid",CurrenntUser.uid)
            startActivity(intent)
        }
    }
}