package com.dheevvvv.taelecvis.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentSplashBinding
import com.dheevvvv.taelecvis.datastore_preferences.UserManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {
    private lateinit var binding:FragmentSplashBinding
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userManager = UserManager.getInstance(requireContext())
        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_splash)
        binding.ivLogoSplash.startAnimation(fadeInAnimation)

        Handler().postDelayed({
            lifecycleScope.launch {
                if (userManager.isLoggedIn().first()){
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                }else{
                    userManager.clearData()
                    findNavController().navigate(R.id.action_splashFragment_to_welcomePageFragment)
                }
            }
        }, 3000)
    }



}