package com.example.ecomate.UI.Dashboard

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ecomate.R
import com.example.ecomate.UI.Login.LoginViewModel
import com.example.ecomate.databinding.ActivityDashboard2Binding

class DashboardActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityDashboard2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboard2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.layoutAppbar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutAppbar) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.setPadding(0, statusBarInsets.top, 0, 0)
            WindowInsetsCompat.CONSUMED
        }
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_profile, menu)
        return true
    }
}