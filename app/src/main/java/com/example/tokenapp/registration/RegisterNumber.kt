package com.example.registrationapp.registration

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.tokenapp.Consts
import com.example.tokenapp.LocalDatabase
import com.example.tokenapp.LocalDatabase.Companion.USER_NUMBER
import com.example.tokenapp.R
import com.example.tokenapp.databinding.ActivityRegisterNumberBinding
import com.vicmikhailau.maskededittext.MaskedFormatter


class RegisterNumber : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterNumberBinding
    private val localDatabase: LocalDatabase by lazy { LocalDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editTextChangeListener()
        binding.nextButton.setOnClickListener { sendMessage() }
    }

    private fun editTextChangeListener() {
        binding.numberPhoneTextView.addTextChangedListener {
            if (it?.length == 11 && binding.name.text.isNotEmpty()) {
                binding.nextButton.apply {
                    background = ContextCompat.getDrawable(
                        this@RegisterNumber,
                        R.drawable.button_enable_custom_style
                    )
                    isEnabled = true
                }
            } else {
                binding.nextButton.apply {
                    background = ContextCompat.getDrawable(
                        this@RegisterNumber,
                        R.drawable.button_disable_custom_item
                    )
                    isEnabled = false
                }
            }
        }
    }

    private fun sendMessage() {
        val unMaskedNumber =
            MaskedFormatter("###-###-###").formatString(binding.numberPhoneTextView.text.toString())?.unMaskedString

        insertDataToSharedPreference(unMaskedNumber!!)

        val intent = Intent(this, GetFirebaseMessage::class.java).apply {
            putExtra(USER_NUMBER, unMaskedNumber)
            putExtra(Consts.USER_NAME, binding.name.text.toString())
        }
        startActivity(intent)
    }

    @SuppressLint("CommitPrefEdits")
    private fun insertDataToSharedPreference(number: String) {
        localDatabase.saveUserNumber(number.toInt())
    }
}