package com.example.tokenapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.registrationapp.HomeActivity
import com.example.registrationapp.HomeViewModel
import com.example.registrationapp.SignInOrRegistrationActivity
import com.example.tokenapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val uid by lazy { FirebaseAuth.getInstance().uid }
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var binding: ActivityMainBinding

    private val localDatabase: LocalDatabase by lazy { LocalDatabase(this) }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (uid == null) {
            startActivity(Intent(this, SignInOrRegistrationActivity::class.java))
        } else {
            viewModel.getToken(localDatabase.fetchUserNumber(), uid!!)
            viewModel.jwtToken.observe(this) {
                localDatabase.saveAccessToken(it.access)
                localDatabase.saveRefreshToken(it.refresh)
            }
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}
