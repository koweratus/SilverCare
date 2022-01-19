package com.example.silvercare.view.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.silvercare.R
import com.example.silvercare.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_settings
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val prefManager = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        prefManager.edit().clear().apply()
        showHomeFragmentButton()
    }

    private fun showHomeFragmentButton() {
        val prefManager = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        if (!prefManager.getBoolean("didShowPrompt", false))
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.navigation_home)
                .setPrimaryText("This your Home!")
                .setSecondaryText("This is the place where you can check on your Senior!")
                .setPromptBackground(RectanglePromptBackground())
                .setPromptFocal(RectanglePromptFocal())
                .setPromptStateChangeListener { prompt, state ->
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED
                        || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED
                    ) {
                        val prefEditor = prefManager.edit()
                        prefEditor.putBoolean("didShowPrompt", true)
                        prefEditor.apply()

                        showPillReminderPrompt()                    }
                }
                .show()
    }

    private fun showNotificationFragmentButton() {
        val prefManager = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        MaterialTapTargetPrompt.Builder(this)
            .setTarget(R.id.navigation_notifications)
            .setPrimaryText("Here you will find your Notifications!")
            .setSecondaryText("All of your received notification will be shown here!")
            .setPromptBackground(RectanglePromptBackground())
            .setPromptFocal(RectanglePromptFocal())
            .setPromptStateChangeListener { prompt, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED
                    || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED
                ) {
                    val prefEditor = prefManager.edit()
                    prefEditor.putBoolean("didShowPrompt", true)
                    prefEditor.apply()

                    showSettingsFragmentButton()
                }
            }
            .show()
    }

    private fun showSettingsFragmentButton() {
        val prefManager = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        MaterialTapTargetPrompt.Builder(this)
            .setTarget(R.id.navigation_settings)
            .setPrimaryText("Here you will find your Settings!")
            .setSecondaryText("Receive notification, edit profile and much more...")
            .setPromptBackground(RectanglePromptBackground())
            .setPromptFocal(RectanglePromptFocal())
            .setPromptStateChangeListener { prompt, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED
                    || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED
                ) {
                    val prefEditor = prefManager.edit()
                    prefEditor.putBoolean("didShowPrompt", true)
                    prefEditor.apply()
                }
            }
            .show()
    }

    private fun showPillReminderPrompt() {
        val prefManager = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        MaterialTapTargetPrompt.Builder(this)
            .setTarget(R.id.btn_reminder_pill)
            .setPrimaryText("This is Pill Reminding button!")
            .setSecondaryText("Clicking this button you will notify senior about their pill schedule.")
            .setBackButtonDismissEnabled(true)
            .setPromptBackground(RectanglePromptBackground())
            .setPromptFocal(RectanglePromptFocal())
            .setPromptStateChangeListener { prompt, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED
                    || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED
                ) {
                    val prefEditor = prefManager.edit()
                    prefEditor.putBoolean("didShowPrompt", true)
                    prefEditor.apply()
                    showNotificationFragmentButton()
                }
            }
            .show()
    }

}