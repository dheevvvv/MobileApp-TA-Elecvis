package com.dheevvvv.taelecvis.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentConsumptionTransactionBinding


class ConsumptionTransactionFragment : Fragment() {
    private lateinit var binding: FragmentConsumptionTransactionBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentConsumptionTransactionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }



}