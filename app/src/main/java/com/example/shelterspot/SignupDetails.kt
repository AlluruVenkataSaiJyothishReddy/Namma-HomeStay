package com.example.shelterspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.shelterspot.databinding.ActivitySignupDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupDetails : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("customer")

        val email = intent.getStringExtra("email") ?: ""
        val password = intent.getStringExtra("password") ?: ""

        binding.back.setOnClickListener {
            finish()
        }

        binding.signup.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val mobile = binding.mobile.text.toString().trim()

            if (name.isEmpty() || mobile.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.signup.isEnabled = false
            
            // 1. Create User
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // User is automatically signed in on success
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            val userProfile = CstmrDetData(name, mobile, email)
                            
                            // 2. Save Profile Details to Database
                            database.child(userId).setValue(userProfile).addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Welcome to Namma HomeStay!", Toast.LENGTH_SHORT).show()
                                    // 3. Go directly to Home
                                    val intent = Intent(this, CHome::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                } else {
                                    binding.signup.isEnabled = true
                                    Toast.makeText(this, "Failed to save profile. Try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        binding.signup.isEnabled = true
                        val errorMessage = task.exception?.message ?: "Registration Failed"
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}