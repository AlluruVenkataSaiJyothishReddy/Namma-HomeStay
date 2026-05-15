package com.example.shelterspot

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.shelterspot.databinding.ActivityChotelDetailsBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CHotelDetails : AppCompatActivity() {

    private lateinit var binding: ActivityChotelDetailsBinding
    private lateinit var database: DatabaseReference
    private var hostPhone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChotelDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("userId").toString()

        var latitude = 0.0
        var longitude = 0.0

        database = FirebaseDatabase.getInstance().getReference("hotels")
        database.child(userId).get().addOnSuccessListener { snapshot ->
            val hotel = snapshot.getValue(HotelDetDataClass::class.java)
            hotel?.let {
                binding.hotelName.text = it.hotelname
                binding.locationText.text = "${it.city}, ${it.state}"
                binding.description.text = it.description
                binding.price.text = "₹ ${it.price}"
                
                hostPhone = it.mobile
                
                // New sections
                binding.dailyMenuText.text = it.dailyMenu ?: "Ask host for today's specials!"
                binding.localGuideText.text = it.localGuide ?: "Explore the beautiful surroundings."
                
                // Load images
                Glide.with(this).load(it.url).placeholder(R.drawable.hotelplaceholder).into(binding.image)
                
                // Load gallery images if they exist
                if (!it.washroomPhotosUrl.isNullOrEmpty()) {
                    Glide.with(this).load(it.washroomPhotosUrl).placeholder(R.drawable.hotelplaceholder).into(binding.washroomImage)
                }
                if (!it.sceneryPhotosUrl.isNullOrEmpty()) {
                    Glide.with(this).load(it.sceneryPhotosUrl).placeholder(R.drawable.hotelplaceholder).into(binding.sceneryImage)
                }
                
                latitude = it.latitude?.toDoubleOrNull() ?: 0.0
                longitude = it.longitude?.toDoubleOrNull() ?: 0.0
            }
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        // Inquiry Box: Call
        binding.btnCall.setOnClickListener {
            hostPhone?.let { phone ->
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)
            }
        }

        // Inquiry Box: Message (WhatsApp or SMS)
        binding.btnMessage.setOnClickListener {
            hostPhone?.let { phone ->
                val uri = Uri.parse("https://api.whatsapp.com/send?phone=91$phone&text=Hi, I am interested in your homestay!")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }

        binding.mapCard.setOnClickListener {
            val intent = Intent(this, CMapView::class.java)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            startActivity(intent)
        }

        binding.book.setOnClickListener {
            val intent = Intent(this, HotelBookingDetails::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
    }
}