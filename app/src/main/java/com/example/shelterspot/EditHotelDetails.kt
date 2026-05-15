package com.example.shelterspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.shelterspot.databinding.ActivityEditHotelDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditHotelDetails : AppCompatActivity() {
    private lateinit var binding: ActivityEditHotelDetailsBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var url: String? = null
    private var washroomUrl: String? = null
    private var sceneryUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditHotelDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("hotels")
        val userId = auth.currentUser!!.uid

        fetchData(userId)

        binding.save.setOnClickListener {
            binding.save.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE

            val hotelData = HotelDetDataClass(
                ownername = binding.ownerName.text.toString(),
                hotelname = binding.hotelName.text.toString(),
                state = binding.state.text.toString(),
                city = binding.city.text.toString(),
                area = binding.area.text.toString(),
                pincode = binding.pincode.text.toString(),
                mobile = binding.mobile.text.toString(),
                email = auth.currentUser?.email,
                price = binding.price.text.toString(),
                rooms = binding.rooms.text.toString(),
                description = binding.description.text.toString(),
                url = url,
                userId = userId,
                dailyMenu = binding.dailyMenu.text.toString(),
                localGuide = binding.localGuide.text.toString(),
                washroomPhotosUrl = washroomUrl,
                sceneryPhotosUrl = sceneryUrl
            )

            database.child(userId).setValue(hotelData).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Details Updated Successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update details", Toast.LENGTH_SHORT).show()
                    binding.save.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun fetchData(userId: String) {
        database.child(userId).get().addOnSuccessListener { snapshot ->
            val hotel = snapshot.getValue(HotelDetDataClass::class.java)
            hotel?.let {
                binding.ownerName.setText(it.ownername)
                binding.hotelName.setText(it.hotelname)
                binding.state.setText(it.state)
                binding.city.setText(it.city)
                binding.area.setText(it.area)
                binding.pincode.setText(it.pincode)
                binding.mobile.setText(it.mobile)
                binding.price.setText(it.price)
                binding.rooms.setText(it.rooms)
                binding.description.setText(it.description)
                binding.dailyMenu.setText(it.dailyMenu)
                binding.localGuide.setText(it.localGuide)
                url = it.url
                washroomUrl = it.washroomPhotosUrl
                sceneryUrl = it.sceneryPhotosUrl
            }
        }
    }
}