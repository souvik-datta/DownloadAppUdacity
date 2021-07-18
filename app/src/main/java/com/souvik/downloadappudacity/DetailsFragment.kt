package com.souvik.downloadappudacity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.souvik.downloadappudacity.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            Log.d(
                "TAG",
                "data from mainfragment: ${it.getString("status")}\t ${it.getString("title")}"
            )
            binding.statusDetails = "Status: ${it.getString("status")}"
            binding.fileNameDetails = "File Name : ${it.getString("title")}"
        }
        binding.okay.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}