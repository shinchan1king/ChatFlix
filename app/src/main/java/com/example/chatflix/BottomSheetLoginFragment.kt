package com.example.chatflix

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_bottom_sheet_login.view.*
import android.widget.Toast
import com.example.chatflix.screens.BottomNavActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BottomSheetLoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BottomSheetLoginFragment : BottomSheetDialogFragment() {
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
    ): View? {
        val rootView:View=inflater.inflate(R.layout.fragment_bottom_sheet_login,container,false)
         var mAuth: FirebaseAuth?=null
        mAuth = FirebaseAuth.getInstance()
        var userEmail=rootView.etEmailText.text.toString()
        var userPassword=rootView.etPass.text.toString()
        rootView.button.setOnClickListener {
            var userEmail=rootView.etEmailText.text.toString()
            var userPassword=rootView.etPass.text.toString()
            if (userEmail == "" || userPassword == "") {
                Toast.makeText(
                    context,
                    "Please enter both email and password  to login",
                    Toast.LENGTH_LONG
                ).show()

            } else {
                mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            Toast.makeText(context, "Signin Successful", Toast.LENGTH_SHORT)
                                .show()
                            var CurrenntUser= mAuth.currentUser

                            if (CurrenntUser != null) {
                                val intent= Intent(activity,BottomNavActivity::class.java)
                                intent.putExtra("Email",CurrenntUser.email)
                                intent.putExtra("uid",CurrenntUser.uid)
                                activity?.startActivity(intent)

                            }
                            dismiss()
                        } else {
                            Toast.makeText(
                                context,
                                "Please provide valid email or password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

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
         * @return A new instance of fragment BottomSheetLoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BottomSheetLoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }
}