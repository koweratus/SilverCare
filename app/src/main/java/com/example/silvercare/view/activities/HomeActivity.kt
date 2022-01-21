package com.example.silvercare.view.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.silvercare.R
import com.example.silvercare.databinding.ActivityHomeBinding
import com.example.silvercare.model.NotificationData
import com.example.silvercare.model.PushNotification
import com.example.silvercare.service.FirebaseService
import com.example.silvercare.utils.Constants
import com.example.silvercare.utils.RetrofitInstance
import com.example.silvercare.view.fragments.TOPIC
import com.example.silvercare.viewmodel.HomeViewModel
import com.example.silvercare.viewmodel.LoginViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navView: BottomNavigationView
    val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        navView = binding.navView
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
        val sharedPreferences =
            this.getSharedPreferences(
                Constants.SILVERCARE_PREFERENCES,
                Context.MODE_PRIVATE
            )
        val type = sharedPreferences.getString(Constants.USER_TYPE, "")
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
                        if (type == "Caretaker"){
                            showPillReminderPrompt()
                        }else{
                            showNotificationFragmentButton()
                        }

                    }
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