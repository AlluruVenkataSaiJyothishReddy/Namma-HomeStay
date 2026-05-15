package fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shelterspot.Adapters.HomeFragLocAdapter
import com.example.shelterspot.Adapters.onHotelClicked
import com.example.shelterspot.CHotelDetails
import com.example.shelterspot.HotelDetDataClass
import com.example.shelterspot.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : androidx.fragment.app.Fragment(), onHotelClicked {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var arraylist = ArrayList<HotelDetDataClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("customer")
            userRef.child(userId).get().addOnSuccessListener {
                binding.name.text = it.child("name").value?.toString() ?: "Traveler"
            }
        }

        database = FirebaseDatabase.getInstance().getReference("hotels")
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        
        fetchLocationRecyclerData()

        return binding.root
    }

    private fun fetchLocationRecyclerData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    arraylist.clear()
                    for (datasnapshot in snapshot.children) {
                        val singledata = datasnapshot.getValue(HotelDetDataClass::class.java)
                        singledata?.let { arraylist.add(it) }
                    }
                    binding.recyclerView.adapter =
                        HomeFragLocAdapter(arraylist, requireContext(), this@HomeFragment)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Log error
            }
        })
    }

    override fun onItemClicked(position: Int) {
        val intent = Intent(context, CHotelDetails::class.java)
        intent.putExtra("userId", arraylist[position].userId)
        startActivity(intent)
    }
}