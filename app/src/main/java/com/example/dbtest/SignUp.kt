package com.example.dbtest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.dbtest.data.User // ✅ Keep this
import com.example.dbtest.data.UserDatabase // ✅ Updated import
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var database: UserDatabase // ✅ Updated type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        database = UserDatabase.getDatabase(this) // ✅ Updated initialization

        val tilName = findViewById<TextInputLayout>(R.id.til_name)
        val etName = findViewById<EditText>(R.id.et_name)
        val tilUsername = findViewById<TextInputLayout>(R.id.til_username)
        val etUsername = findViewById<EditText>(R.id.et_username)
        val tilPassword = findViewById<TextInputLayout>(R.id.til_password)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val cbTerms = findViewById<CheckBox>(R.id.cb_terms)
        val btnSignup = findViewById<Button>(R.id.btn_signup)
        val tvLogin = findViewById<TextView>(R.id.tv_login)

        tvLogin.setOnClickListener { finish() }

        cbTerms.setOnCheckedChangeListener { _, isChecked ->
            btnSignup.isEnabled = isChecked
        }
        btnSignup.isEnabled = false

        etName.doOnTextChanged { _, _, _, _ -> tilName.error = null }
        etUsername.doOnTextChanged { _, _, _, _ -> tilUsername.error = null }
        etPassword.doOnTextChanged { _, _, _, _ -> tilPassword.error = null }

        btnSignup.setOnClickListener {
            val name = etName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty()) { tilName.error = "Full Name is required"; return@setOnClickListener }
            if (username.isEmpty()) { tilUsername.error = "Username is required"; return@setOnClickListener }
            if (password.isEmpty()) { tilPassword.error = "Password is required"; return@setOnClickListener }
            if (password.length < 6) { tilPassword.error = "Minimum 6 characters"; return@setOnClickListener }

            lifecycleScope.launch {
                val newUser = User(fullName = name, username = username, password = password)
                database.userDao().insertUser(newUser)

                Toast.makeText(this@SignUpActivity, "Account Created!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}