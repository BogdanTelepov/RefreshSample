package com.example.registrationapp.registration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrationapp.model.JWT_token
import com.example.registrationapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val userCreated = MutableLiveData<Boolean>()
    val token = MutableLiveData<JWT_token>()


    fun postUserData(number:Int, uid:String, name:String){
        viewModelScope.launch {
            val response = repository.registerUser(number, uid, name)
            if (response.isSuccessful){
                if (response.body() == "User is created"){
                    userCreated.postValue(true)
                }
            }
        }
    }

    fun getToken(number:Int, uid:String){
        viewModelScope.launch {
            val response = repository.getToken(number, uid)
            if (response.isSuccessful){
                token.postValue(response.body())
            }
        }
    }

}