package com.dheevvvv.taelecvis.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentProfileBinding
import com.dheevvvv.taelecvis.datastore_preferences.UserManager
import com.dheevvvv.taelecvis.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userManager: UserManager
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var username:String = ""
    private var email:String = ""

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userManager = UserManager.getInstance(requireContext())
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item->
            when(item.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
                    true
                }


                R.id.energy -> {
                    findNavController().navigate(R.id.action_profileFragment_to_consumptionTransactionFragment)
                    true
                }

                R.id.report -> {
                    findNavController().navigate(R.id.action_profileFragment_to_reportFragment)
                    true
                }

                R.id.notification -> {
                    findNavController().navigate(R.id.action_profileFragment_to_notificationFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.clLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        userViewModel.getUsername()
        userViewModel.getEmail()

        userViewModel.username.observe(viewLifecycleOwner, Observer {
            username = it
        })
        userViewModel.email.observe(viewLifecycleOwner, Observer {
            email = it
        })

        binding.tvUsername.setText(username)
        binding.tvEmail.setText(email)

    }

    private fun showLogoutConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Konfirmasi Logout")
        alertDialogBuilder.setMessage("Apakah Anda yakin ingin keluar?")
        alertDialogBuilder.setPositiveButton("Ya") { _, _ ->
            GlobalScope.async {
                userManager.clearData()
            }
            mGoogleSignInClient.signOut()
            Toast.makeText(requireContext(), "Logout Berhasil, Anda telah Logout", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_profileFragment_to_splashFragment)
        }
        alertDialogBuilder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }



}