package com.example.registrationapp

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tokenapp.Consts
import com.example.tokenapp.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityHomeBinding
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getDataAboutUser()
        viewModel.userInfo.observe(this){
            binding.name.text = it.first_name
            binding.number.text = it.number.toString()
        }
        binding.repeat.setOnClickListener{viewModel.getDataAboutUser()}

    }
}