package com.dheevvvv.taelecvis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AlertDialog
import androidx.core.os.postDelayed
import androidx.lifecycle.lifecycleScope
import com.dheevvvv.taelecvis.datastore_preferences.UserManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var userManager: UserManager
    private var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userManager = UserManager.getInstance(this)

    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
        } else {
            showExitConfirmationDialog()
        }
    }

    private fun showExitConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Tekan kembali lagi untuk keluar")
        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()

        Handler(Looper.getMainLooper()).postDelayed(2000) {
            alertDialog.dismiss()
            doubleBackToExitPressedOnce = false
        }

        doubleBackToExitPressedOnce = true
    }
}