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
import com.example.dbtest.data.User
import com.example.dbtest.data.UserDatabase
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var database: UserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        database = UserDatabase.getDatabase(this)

        val tilUsername = findViewById<TextInputLayout>(R.id.til_username)
        val etUsername = findViewById<EditText>(R.id.et_username)
        val tilPassword = findViewById<TextInputLayout>(R.id.til_password)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvSignup = findViewById<TextView>(R.id.tv_signup)

        // Make sure the admin account exists, without blocking the UI thread
        lifecycleScope.launch {
            val existingAdmin = database.userDao().getUserByUsername("admin")
            if (existingAdmin == null) {
                database.userDao().insertUser(
                    User(
                        fullName = "Administrator",
                        username = "admin",
                        password = "admin123",
                        isAdmin = true
                    )
                )
            }
        }

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

                    // Remember who's logged in so Settings/Profile screens know whose data to load
                    SessionManager.saveSession(this@LoginActivity, user.username)

                    if (user.isAdmin) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else {
                        startActivity(Intent(this@LoginActivity, ITBuilding::class.java))
                    }
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