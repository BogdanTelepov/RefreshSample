package com.example.registrationapp.authorization

import `in`.aabhasjindal.otptextview.OTPListener
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.registrationapp.HomeActivity
import com.example.tokenapp.Consts
import com.example.tokenapp.LocalDatabase
import com.example.tokenapp.R
import com.example.tokenapp.databinding.ActivityAuthGetMessageBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AuthGetMessage : AppCompatActivity() {

    private lateinit var binding: ActivityAuthGetMessageBinding
    private var id: String = ""
    private val viewModel: AuthorizationViewModel by viewModels()
    private val number by lazy { intent.getStringExtra("number") }
    private lateinit var firebaseCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private val localDatabase: LocalDatabase by lazy { LocalDatabase(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthGetMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.registrationButton.setOnClickListener {  // Это кнопка отправить обратоно он отправляет сообщение и заноно запускает таймер
            sendMessage()
            startTimer()
        }

        binding.phoneNumber.text = "Код был отправлен на номер $number"
        startTimer()
        firebaseCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                signIn(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@AuthGetMessage, p0.message, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                id = p0
                Toast.makeText(this@AuthGetMessage, "Message sent", Toast.LENGTH_LONG).show()
            }
        }
        sendMessage()
        listenOTR()
        binding.appCompatButton.setOnClickListener {
            binding.registrationButton.isEnabled = false
            binding.registrationButton.background =
                ContextCompat.getDrawable(this, R.drawable.button_disable_custom_item)
        }
    }


    private fun sendMessage() {
        PhoneAuthOptions.newBuilder()
            .setActivity(this)
            .setPhoneNumber("+996$number")
            .setTimeout(30L, TimeUnit.SECONDS)
            .setCallbacks(firebaseCallback)
            .build()
            .apply {
                PhoneAuthProvider.verifyPhoneNumber(this)
            }
    }

    private fun listenOTR() {
        binding.otpTextView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                binding.appCompatButton.background = ContextCompat.getDrawable(
                    this@AuthGetMessage,
                    R.drawable.button_disable_custom_item
                )
            }

            override fun onOTPComplete(otp: String) {
                binding.appCompatButton.background = ContextCompat.getDrawable(
                    this@AuthGetMessage,
                    R.drawable.button_enable_custom_style
                )
                binding.appCompatButton.isEnabled = true
                signInWithCredential()
            }
        }
    }

    private fun signInWithCredential() {
        val phoneAuthCredential = PhoneAuthProvider.getCredential(id, binding.otpTextView.otp!!)
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    getToken()
                }
            }
    }

    @SuppressLint("CommitPrefEdits")
    private fun getToken() {
        binding.progress.visibility = View.VISIBLE
        val uid = FirebaseAuth.getInstance().uid
        viewModel.getToken(number!!.toInt(), uid!!)
        viewModel.jwtToken.observe(this) {
            localDatabase.saveAccessToken(it.access)
            localDatabase.saveRefreshToken(it.refresh)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(
                Intent(
                    this,
                    HomeActivity::class.java
                )
            )
        }, 7000)
    }

    private fun startTimer() {
        object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.registrationButton.isEnabled = false
                binding.registrationButton.text =
                    "Отправить повторно ${millisUntilFinished / 1000}"
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.registrationButton.text = "Отправить повторно"
                binding.registrationButton.isEnabled = true
                binding.registrationButton.background = ContextCompat.getDrawable(
                    this@AuthGetMessage,
                    R.drawable.button_enable_custom_style
                )
            }
        }.start()
    }

    private fun signIn(phone: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phone).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                getToken()
            }
        }
    }
}