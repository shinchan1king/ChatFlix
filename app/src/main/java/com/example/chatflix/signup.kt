package com.example.chatflix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.chatflix.screens.BottomNavActivity
import com.google.firebase.auth.FirebaseAuth

class signup : AppCompatActivity() {
    private var mAuth: FirebaseAuth?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        mAuth = FirebaseAuth.getInstance()
    }
    fun buSignup(view: View) {
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword1 = findViewById<EditText>(R.id.etPassword1)
        val etPassword2 = findViewById<EditText>(R.id.etPassword2)
        val email = etEmail.text.toString()
        val Password1 = etPassword1.text.toString()
        val Password2 = etPassword2.text.toString()
        if (Password1 == "") {
            Toast.makeText(applicationContext, "Please Enter Some Password", Toast.LENGTH_SHORT)
                .show()
        } else {
            if (Password1 == Password2) {
                if (email == "") {
                    Toast.makeText(applicationContext,
                        "Please Enter email first",
                        Toast.LENGTH_SHORT).show()
                } else {
                    signuptoFirebase(
                        email,
                        Password1
                    )
                }
            } else {
                Toast.makeText(
                    applicationContext, "Please Make Sure confirm password and password are same",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    fun signuptoFirebase(Email:String,Password:String)
    {
        try {
            mAuth!!.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "SignUp Successful", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, BottomNavActivity::class.java)
                        intent.putExtra("email",Email)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "Signup Failed", Toast.LENGTH_LONG).show()
                    }
                }
        } catch (e: Exception) {

        }
    }
}