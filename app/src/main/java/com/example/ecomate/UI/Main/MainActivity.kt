package com.example.ecomate.UI.Main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.ecomate.DashboardFragment
import com.example.ecomate.ProfileFragment
import com.example.ecomate.R
import com.example.ecomate.UI.Login.LoginViewModel
import com.example.ecomate.UI.ResultPredict.ResultPredictActivity
import com.example.ecomate.UI.TrashHub.TrashHubFragment
import com.example.ecomate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setSupportActionBar(binding.layoutAppbar) todo
        binding.bottomNav.background = null
        binding.bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.nav_home -> openFragment(DashboardFragment())
                R.id.nav_profile -> openFragment(ProfileFragment())
                R.id.nav_trashHub -> openFragment(TrashHubFragment())
            }
            true
        }
        fragmentManager = supportFragmentManager
        openFragment(DashboardFragment())
        binding.fabScan.setOnClickListener {
            val intent = Intent(this@MainActivity, ResultPredictActivity::class.java)
            startActivity(intent)
        }

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

    override fun onBackPressed() {
        super.onBackPressed()
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else{
            super.onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_wrapper, fragment)
        fragmentTransaction.commit()
    }
}