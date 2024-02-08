package com.dheevvvv.taelecvis.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentRegisterBinding
import com.dheevvvv.taelecvis.viewmodel.UserViewModel
import dagger.hilt.android.internal.Contexts.getApplication


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.tvLinkMasuk.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.btnRegister.setOnClickListener {
            register()
        }




        binding.etNoHandphoneRegist.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                val phoneNumber = p0.toString()
                val isPhoneNumberValid = validatePhoneNumber(phoneNumber)
                if (isPhoneNumberValid){
                    binding.outlinedTextField3.error = null
                    binding.outlinedTextField3.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)
                } else{
                    binding.outlinedTextField3.error = "Phone Number tidak sesuai"
                    binding.outlinedTextField3.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error)
                }
            }

        })

        binding.etPasswordRegist.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak diperlukan pada tahap ini
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Tidak diperlukan pada tahap ini
            }

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                val isPasswordValid = validatePassword(password)
                if (isPasswordValid) {
                    binding.outlinedTextField4.error = null
                    binding.outlinedTextField4.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)
                    binding.outlinedTextField4.requestFocus()
                } else {
                    binding.outlinedTextField4.error = "Password harus mengandung setidaknya 1 huruf besar, 1 angka, dan 1 simbol"
                    binding.outlinedTextField4.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error)
                }
            }
        })
    }

    fun validatePhoneNumber(phoneNumber: String): Boolean {
        val isLengthValid = phoneNumber.length >= 11

        return isLengthValid
    }

    fun validatePassword(password: String) : Boolean {
        val uppercasePattern = Regex("[A-Z]")
        val digitPattern = Regex("[0-9]")
        val symbolPattern = Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]")

        val isUppercasePresent = uppercasePattern.containsMatchIn(password)
        val isDigitPresent = digitPattern.containsMatchIn(password)
        val isSymbolPresent = symbolPattern.containsMatchIn(password)
        val isLengthValid = password.length >= 8

        return isUppercasePresent && isDigitPresent && isSymbolPresent && isLengthValid

    }

    private fun register() {
        val inputName = binding.etNameRegist.text.toString()
        val inputEmail = binding.etEmailRegist.text.toString()
        val inputUsername = binding.etUsernameRegist.text.toString()
        val phoneNumber = binding.etNoHandphoneRegist.text.toString()
        val password = binding.etPasswordRegist.text.toString()
        val confirmPassword = binding.etConfirmPasswordRegist.text.toString()

        if (inputName.isEmpty() || inputEmail.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show()
        } else if (password != confirmPassword) {
            binding.outlinedTextField5.error = "Confirm password tidak sama dengan password"
            binding.outlinedTextField5.requestFocus()
        } else if (!validatePassword(password)) {
            Toast.makeText(requireContext(), "Invalid password", Toast.LENGTH_SHORT).show()
        } else {

            userViewModel.callApiUserPostRegister(inputEmail, password, inputName, inputUsername ,phoneNumber)
            userViewModel.registerUser.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Register Success", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                } else {
                    Toast.makeText(requireContext(), "Register Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


}