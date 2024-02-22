package com.dheevvvv.taelecvis.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentRecomendationBinding


class RecomendationFragment : Fragment() {
    private lateinit var binding: FragmentRecomendationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecomendationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item->
            when(item.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.action_recomendationFragment_to_homeFragment)
                    false
                }

                R.id.notification -> {
                    findNavController().navigate(R.id.action_recomendationFragment_to_notificationFragment)
                    false
                }

                R.id.report -> {
                    findNavController().navigate(R.id.action_recomendationFragment_to_reportFragment)
                    false
                }

                R.id.account -> {
                    findNavController().navigate(R.id.action_recomendationFragment_to_profileFragment)
                    false
                }

                else -> {
                    binding.bottomNavigation.menu.findItem(R.id.recommendation).isChecked = true
                    true
                }
            }
        }

    }

}