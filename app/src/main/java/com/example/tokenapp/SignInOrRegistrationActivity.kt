package com.example.registrationapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.registrationapp.authorization.AuthWithNumberActivity
import com.example.registrationapp.registration.RegisterNumber
import com.example.tokenapp.databinding.ActivitySignInOrRegistrationBinding

class SignInOrRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInOrRegistrationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInOrRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterNumber::class.java))
        }

        binding.signIn.setOnClickListener {
            startActivity(Intent(this, AuthWithNumberActivity::class.java))
        }
    }




}