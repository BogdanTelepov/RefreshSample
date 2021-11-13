package com.example.registrationapp.registration

import `in`.aabhasjindal.otptextview.OTPListener
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.registrationapp.HomeActivity
import com.example.tokenapp.Consts
import com.example.tokenapp.LocalDatabase
import com.example.tokenapp.R
import com.example.tokenapp.databinding.ActivityGetFirebaseMessageBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class GetFirebaseMessage : AppCompatActivity() {

    private lateinit var binding: ActivityGetFirebaseMessageBinding
    private var id: String = ""
    private val viewModel: RegistrationViewModel by viewModels()
    private val number by lazy { intent.getStringExtra("number") }
    private val name by lazy { intent.getStringExtra(Consts.USER_NAME) }
    private lateinit var firebaseCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private val localDatabase: LocalDatabase by lazy { LocalDatabase(this) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetFirebaseMessageBinding.inflate(layoutInflater)
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
                Toast.makeText(this@GetFirebaseMessage, p0.message, Toast.LENGTH_LONG).show()
                Log.i("TAG", p0.message!!)
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                id = p0
                Toast.makeText(this@GetFirebaseMessage, "Message sent", Toast.LENGTH_LONG).show()
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
                    this@GetFirebaseMessage,
                    R.drawable.button_disable_custom_item
                )
            }

            override fun onOTPComplete(otp: String) {
                binding.appCompatButton.background = ContextCompat.getDrawable(
                    this@GetFirebaseMessage,
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
                    createUser()

                }
            }
    }

    private fun createUser() {
        val uid = FirebaseAuth.getInstance().uid
        viewModel.postUserData(number!!.toInt(), uid!!, name!!)
        viewModel.userCreated.observe(this) {
            if (it) {
                getToken(uid)
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun getToken(uid: String) {
        viewModel.getToken(number!!.toInt(), uid)
        viewModel.token.observe(this) {
            localDatabase.saveAccessToken(it.access)
            localDatabase.saveRefreshToken(it.refresh)
        }
        startActivity(Intent(this, HomeActivity::class.java))
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
                with(binding) {
                    registrationButton.text = "Отправить повторно"
                    registrationButton.isEnabled = true
                    registrationButton.background = ContextCompat.getDrawable(
                        this@GetFirebaseMessage,
                        R.drawable.button_enable_custom_style
                    )
                }
            }
        }.start()
    }

    private fun signIn(phone: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phone).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                createUser()
            }
        }
    }
}