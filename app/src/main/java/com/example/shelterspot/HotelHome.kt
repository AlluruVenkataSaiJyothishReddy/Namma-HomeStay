package com.example.shelterspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.shelterspot.databinding.ActivityHotelHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HotelHome : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityHotelHomeBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("hotels")

        val userId = auth.currentUser?.uid ?: return
        val email = intent.getStringExtra("email") ?: auth.currentUser?.email ?: ""

        fetchData(userId)

        binding.back.setOnClickListener {
            finish()
        }

        binding.edit.setOnClickListener {
            val intent = Intent(this, EditHotelDetails::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        binding.signout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun fetchData(userId: String) {
        database.child(userId).get().addOnSuccessListener { snapshot ->
            val hotel = snapshot.getValue(HotelDetDataClass::class.java)
            hotel?.let {
                binding.hotelName.text = it.hotelname
                binding.city.text = "${it.city},"
                binding.state.text = it.state
                binding.description.text = it.description
                binding.price.text = it.price
                binding.ownerName.text = it.ownername
                binding.mobile.text = it.mobile
                binding.room.text = it.rooms
                
                // New Fields
                binding.dailyMenuDisplay.text = it.dailyMenu ?: "No menu set today."
                binding.localGuideDisplay.text = it.localGuide ?: "No local tips added yet."
                
                Glide.with(this).load(it.url).centerCrop().placeholder(R.drawable.hotelplaceholder).into(binding.image)
            }
        }
    }
}