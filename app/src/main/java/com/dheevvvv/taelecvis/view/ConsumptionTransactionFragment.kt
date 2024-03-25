package com.dheevvvv.taelecvis.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item->
            when(item.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.action_consumptionTransactionFragment_to_homeFragment)
                    true
                }

                R.id.notification -> {
                    findNavController().navigate(R.id.action_consumptionTransactionFragment_to_notificationFragment)
                    true
                }

                R.id.report -> {
                    findNavController().navigate(R.id.action_consumptionTransactionFragment_to_reportFragment)
                    true
                }

                R.id.account -> {
                    findNavController().navigate(R.id.action_consumptionTransactionFragment_to_profileFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }
    }



}