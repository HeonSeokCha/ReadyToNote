package com.chs.readytonote.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.chs.readytonote.Constants
import com.chs.readytonote.DataStoreModule
import com.chs.readytonote.R
import com.chs.readytonote.adapter.NoteAdapter
import com.chs.readytonote.dataStore
import com.chs.readytonote.databinding.ActivityMainBinding
import com.chs.readytonote.entities.Note
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel by viewModels<MainViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainViewModel(application) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkTheme()
        initNav()
    }

    private fun checkTheme() {
        runBlocking { // todo 과연 이게 맞을까?
            when (this@MainActivity.dataStore.data.first()
                    [stringPreferencesKey(Constants.UI_STATUS)]
            ) {
                Constants.WHITE_MODE -> {
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                    )
                    viewModel.selectUI = Constants.WHITE_MODE
                }
                Constants.DARK_MODE -> {
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                    )
                    viewModel.selectUI = Constants.DARK_MODE
                }
                Constants.DEFAULT_MODE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        )
                    } else {
                        AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                        )
                    }
                    viewModel.selectUI = Constants.DEFAULT_MODE
                }
            }
        }
    }

    private fun initNav() {
        val navHostFragment: NavHostFragment = binding.navHostFragment.getFragment()!!
        navController = navHostFragment.navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}