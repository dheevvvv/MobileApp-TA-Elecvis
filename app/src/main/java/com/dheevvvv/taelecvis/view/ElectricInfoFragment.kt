package com.dheevvvv.taelecvis.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentElectricInfoBinding


class ElectricInfoFragment : Fragment() {
    private lateinit var binding : FragmentElectricInfoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentElectricInfoBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }


}