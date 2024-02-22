package com.dheevvvv.taelecvis.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentNotificationBinding


class NotificationFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item->
            when(item.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.action_notificationFragment_to_homeFragment)
                    false
                }

                R.id.recommendation -> {
                    findNavController().navigate(R.id.action_notificationFragment_to_recomendationFragment)
                    false
                }

                R.id.report -> {
                    findNavController().navigate(R.id.action_notificationFragment_to_reportFragment)
                    false
                }

                R.id.account -> {
                    findNavController().navigate(R.id.action_notificationFragment_to_profileFragment)
                    false
                }

                else -> {
                    binding.bottomNavigation.menu.findItem(R.id.notification).isChecked = true
                    true
                }
            }
        }


    }


}