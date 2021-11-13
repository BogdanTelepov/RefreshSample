package com.example.registrationapp.authorization

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.tokenapp.LocalDatabase
import com.example.tokenapp.LocalDatabase.Companion.USER_NUMBER
import com.example.tokenapp.R
import com.example.tokenapp.databinding.ActivityAuthWithNumberBinding
import com.vicmikhailau.maskededittext.MaskedFormatter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthWithNumberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthWithNumberBinding
    private val viewModel by viewModels<AuthorizationViewModel>()

    private val localDatabase: LocalDatabase by lazy { LocalDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthWithNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.numberPhoneTextView.addTextChangedListener {
            if (it?.length == 11) {
                binding.nextButton.apply {
                    background = ContextCompat.getDrawable(
                        this@AuthWithNumberActivity,
                        R.drawable.button_enable_custom_style
                    )
                    isEnabled = true
                }
            } else {
                binding.nextButton.apply {
                    background = ContextCompat.getDrawable(
                        this@AuthWithNumberActivity,
                        R.drawable.button_disable_custom_item
                    )
                    isEnabled = false
                }
            }
        }

        binding.nextButton.setOnClickListener { sendMessage() }

    }

    private fun sendMessage() {
        val phoneNumber = MaskedFormatter("###-###-###").formatString(binding.numberPhoneTextView.text.toString())!!.unMaskedString
        viewModel.checkNumber(phoneNumber.toInt())
        viewModel.isNumberLocateInDB.observe(this){
            if (it){
                insertDataToSharedPreference(phoneNumber)
                val intent = Intent(this, AuthGetMessage::class.java).apply {
                    putExtra(USER_NUMBER, phoneNumber)
                }
                startActivity(intent)
            }else{
                Toast.makeText(this, "Неправильный номер",Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun insertDataToSharedPreference(number: String) {
        localDatabase.saveUserNumber(number.toInt())
    }
}