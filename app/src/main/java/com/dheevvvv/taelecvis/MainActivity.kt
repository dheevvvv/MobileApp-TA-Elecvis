package com.dheevvvv.taelecvis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.postDelayed
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.dheevvvv.taelecvis.datastore_preferences.UserManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var userManager: UserManager
    private var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userManager = UserManager.getInstance(this)

    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        // Cek apakah halaman saat ini adalah halaman detail atau halaman lain yang tidak memiliki bottom menu
        if (isDetailPage()) {
            // Navigasikan kembali ke halaman sebelumnya
            super.onBackPressed()
        } else {
            // Halaman saat ini adalah halaman home atau halaman dengan bottom menu
            if (doubleBackToExitPressedOnce) {
                finishAffinity()
                super.onBackPressed()
            } else {
                showExitConfirmationDialog()
            }
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

    private fun isDetailPage(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.currentDestination?.id == R.id.detailFragment
    }
}