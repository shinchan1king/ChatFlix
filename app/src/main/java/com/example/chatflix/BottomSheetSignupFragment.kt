package com.example.chatflix

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chatflix.screens.BottomNavActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_bottom_sheet_login.view.*
import kotlinx.android.synthetic.main.fragment_bottom_sheet_login.view.button
import kotlinx.android.synthetic.main.fragment_bottom_sheet_login.view.etEmailText
import kotlinx.android.synthetic.main.fragment_bottom_sheet_login.view.etPass
import kotlinx.android.synthetic.main.fragment_bottom_sheet_signup.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BottomSheetSignupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BottomSheetSignupFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val rootView:View=inflater.inflate(R.layout.fragment_bottom_sheet_signup,container,false)
        var mAuth: FirebaseAuth?=null
        mAuth = FirebaseAuth.getInstance()

        fun signuptoFirebase(Email:String,Password:String)
        {
            try {
                mAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "SignUp Successful", Toast.LENGTH_LONG).show()
                            var database = FirebaseDatabase.getInstance()
                            var coins=0;
                            val user: HashMap<String, Int> = hashMapOf(
                                "coin" to coins)
                            val myRef = database.reference
                            mAuth.currentUser?.let { myRef.child("users").child(it.uid).setValue(user) }
                            val intent = Intent(activity, BottomNavActivity::class.java)
                            intent.putExtra("email",Email)
                            startActivity(intent)
                        } else {
                            Toast.makeText(context, "Signup Failed", Toast.LENGTH_LONG).show()
                        }
                    }
            } catch (e: Exception) {

            }
        }
        rootView.button.setOnClickListener {
            val email=rootView.etEmailText.text.toString()
            val Password1=rootView.etPass.text.toString()
            val Password2=rootView.etConfirmPass.text.toString()
            if (Password1 == "") {
                Toast.makeText(context, "Please Enter Some Password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (Password1 == Password2) {
                    if (email == "") {
                        Toast.makeText(context,
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
                        context, "Please Make Sure confirm password and password are same",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Inflate the layout for this fragment
        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BottomSheetSignupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BottomSheetSignupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}