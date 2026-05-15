package com.example.shelterspot

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.shelterspot.databinding.ActivityHotelHomeDetUploadBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class HotelHomeDetUpload : AppCompatActivity() {

    private lateinit var binding: ActivityHotelHomeDetUploadBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    
    private var mainUri: Uri? = null
    private var washroomUri: Uri? = null
    private var sceneryUri: Uri? = null
    
    private var activePicker: String = "main"
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelHomeDetUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val password = intent.getStringExtra("password") ?: ""
        email = intent.getStringExtra("email") ?: ""

        binding.map.setOnClickListener {
            val intent = Intent(this, HotelMapLocSetActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("password", password)
            startActivity(intent)
        }

        binding.selectImage.setOnClickListener {
            activePicker = "main"
            resultLauncher.launch("image/*")
        }
        
        binding.selectWashroomImage.setOnClickListener {
            activePicker = "washroom"
            resultLauncher.launch("image/*")
        }
        
        binding.selectSceneryImage.setOnClickListener {
            activePicker = "scenery"
            resultLauncher.launch("image/*")
        }

        database = FirebaseDatabase.getInstance().getReference("hotels")
        auth = FirebaseAuth.getInstance()

        binding.city.setText(intent.getStringExtra("city") ?: "")
        binding.area.setText(intent.getStringExtra("area") ?: "")
        binding.state.setText(intent.getStringExtra("state") ?: "")

        binding.signup.setOnClickListener {
            if (validateInputs()) {
                binding.signup.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.VISIBLE
                
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            uploadAllDetails()
                        } else {
                            binding.signup.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (mainUri == null) {
            Toast.makeText(this, "Please select a main photo", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.hotelName.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter homestay name", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            when (activePicker) {
                "main" -> {
                    mainUri = it
                    binding.image.setImageURI(it)
                }
                "washroom" -> {
                    washroomUri = it
                    Toast.makeText(this, "Washroom photo selected", Toast.LENGTH_SHORT).show()
                }
                "scenery" -> {
                    sceneryUri = it
                    Toast.makeText(this, "Scenery photo selected", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadAllDetails() {
        val userId = auth.currentUser!!.uid
        val storage = FirebaseStorage.getInstance().reference.child("hotels").child(userId)
        
        val uploads = mutableListOf<Task<Uri>>()
        
        // Prepare main image upload
        val mainRef = storage.child("main.jpg")
        uploads.add(mainRef.putFile(mainUri!!).continueWithTask { mainRef.downloadUrl })

        // Prepare optional washroom image upload
        var washroomIndex = -1
        if (washroomUri != null) {
            washroomIndex = uploads.size
            val washroomRef = storage.child("washroom.jpg")
            uploads.add(washroomRef.putFile(washroomUri!!).continueWithTask { washroomRef.downloadUrl })
        }

        // Prepare optional scenery image upload
        var sceneryIndex = -1
        if (sceneryUri != null) {
            sceneryIndex = uploads.size
            val sceneryRef = storage.child("scenery.jpg")
            uploads.add(sceneryRef.putFile(sceneryUri!!).continueWithTask { sceneryRef.downloadUrl })
        }

        Tasks.whenAllSuccess<Uri>(uploads).addOnSuccessListener { urls ->
            val mainUrl = urls[0].toString()
            val washroomUrl = if (washroomIndex != -1) urls[washroomIndex].toString() else null
            val sceneryUrl = if (sceneryIndex != -1) urls[sceneryIndex].toString() else null

            val hotelData = HotelDetDataClass(
                ownername = binding.ownerName.text.toString(),
                hotelname = binding.hotelName.text.toString(),
                state = binding.state.text.toString(),
                city = binding.city.text.toString(),
                area = binding.area.text.toString(),
                pincode = "000000",
                mobile = binding.mobile.text.toString(),
                email = email,
                price = binding.price.text.toString(),
                rooms = binding.rooms.text.toString(),
                description = binding.description.text.toString(),
                url = mainUrl,
                userId = userId,
                personperroom = "2",
                latitude = intent.getStringExtra("latitude") ?: "0.0",
                longitude = intent.getStringExtra("longitude") ?: "0.0",
                dailyMenu = binding.dailyMenu.text.toString(),
                localGuide = binding.localGuide.text.toString(),
                washroomPhotosUrl = washroomUrl,
                sceneryPhotosUrl = sceneryUrl
            )
            
            database.child(userId).setValue(hotelData).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Homestay Registered Successfully!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, HotelSignin::class.java))
                    finish()
                }
            }
        }.addOnFailureListener {
            binding.signup.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}