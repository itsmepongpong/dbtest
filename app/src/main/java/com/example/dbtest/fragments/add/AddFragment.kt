package com.example.dbtest.fragments.add

import android.R.attr.text
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.dbtest.R
import com.example.dbtest.data.User
import com.example.dbtest.data.UserViewModel
import androidx.navigation.fragment.findNavController

class AddFragment : Fragment() {

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        rootView = view

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val fab = view.findViewById<Button>(R.id.addbutton)
        fab.setOnClickListener {
            insertDataToDatabase()
        }
        return view
    }

    private fun insertDataToDatabase() {
        val fullName = rootView.findViewById<EditText>(R.id.FullName).text.toString()
        val username = rootView.findViewById<EditText>(R.id.UserName).text.toString()
        val password = rootView.findViewById<EditText>(R.id.Password).text.toString()

        if(inputCheck(username, fullName, password)){
            val user = User(id = 0L, fullName, username, password)
            mUserViewModel.addUser(user)
            Toast.makeText(requireContext(), "naiyadded", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(), "kargaam amin", Toast.LENGTH_LONG).show()
        }
    }
    private fun inputCheck(UserName : String, FullName : String, Password : String): Boolean{
        return !(TextUtils.isEmpty(FullName)) && !(TextUtils.isEmpty(UserName)) && !(TextUtils.isEmpty(Password))
    }
}