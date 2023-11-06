package com.dheevvvv.taelecvis.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentViewPagerBinding


class ViewPagerFragment : Fragment() {
    private lateinit var binding:FragmentViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewPagerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.takeIf { it.containsKey(INTRO_STRING_OBJECT) }?.apply {
            binding.viewPagerHeader.text = getStringArray(INTRO_STRING_OBJECT)!![0]
            binding.viewPagerSubText.text = getStringArray(INTRO_STRING_OBJECT)!![1]
            change(getStringArray(INTRO_STRING_OBJECT)!![2])
        }

        binding.skipText.setOnClickListener {
            findNavController().navigate(R.id.action_welcomePageFragment_to_loginFragment)
        }


    }

    fun change(color:String){
        when(color)
        {
            "R" -> {
                binding.baseLinear.setBackgroundResource(R.drawable.welcome_background)
                binding.ivWelcome.setImageResource(R.drawable.visualization)
            }
            "G" -> {
                binding.baseLinear.setBackgroundResource(R.drawable.welcome_background)
                binding.ivWelcome.setImageResource(R.drawable.graph)
            }

            "B" -> {
                binding.baseLinear.setBackgroundResource(R.drawable.welcome_background)
                binding.ivWelcome.setImageResource(R.drawable.notif)
            }

            "D" -> {
                binding.baseLinear.setBackgroundResource(R.drawable.welcome_background)
                binding.ivWelcome.setImageResource(R.drawable.report)
            }
        }
    }


}