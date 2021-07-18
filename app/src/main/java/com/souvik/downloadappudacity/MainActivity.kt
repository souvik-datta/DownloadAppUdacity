package com.souvik.downloadappudacity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = findNavController(R.id.nav_host_fragment)
        intent?.let {
            if (it.getStringExtra("destination") != null && it.getStringExtra("destination")
                    .equals("DetailsFragment", true)
            ) {
                var bundle = Bundle()
                bundle.putString("status", it.getStringExtra("status"))
                bundle.putString("title", it.getStringExtra("title"))
                navController.navigate(R.id.detailsFragment, bundle)
            }
        }

    }
}