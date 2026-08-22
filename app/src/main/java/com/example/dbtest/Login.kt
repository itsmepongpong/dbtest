package com.example.dbtest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.dbtest.data.UserDatabase // ✅ Updated import
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var database: UserDatabase // ✅ Updated type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        database = UserDatabase.getDatabase(this) // ✅ Updated initialization

        val tilUsername = findViewById<TextInputLayout>(R.id.til_username)
        val etUsername = findViewById<EditText>(R.id.et_username)
        val tilPassword = findViewById<TextInputLayout>(R.id.til_password)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvSignup = findViewById<TextView>(R.id.tv_signup)

        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        etUsername.doOnTextChanged { _, _, _, _ -> tilUsername.error = null }
        etPassword.doOnTextChanged { _, _, _, _ -> tilPassword.error = null }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty()) {
                tilUsername.error = "Username is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                tilPassword.error = "Password is required"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = database.userDao().getUserByUsername(username)

                if (user != null && user.password == password) {
                    Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, ITBuilding::class.java))
                    finish()
                } else {
                    if (user == null) {
                        tilUsername.error = "Username not found"
                    } else {
                        tilPassword.error = "Incorrect password"
                    }
                }
            }
        }
    }
}