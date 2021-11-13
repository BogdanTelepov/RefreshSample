package com.example.registrationapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrationapp.model.JWT_token
import com.example.registrationapp.repository.Repository
import com.example.tokenapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val jwtToken = MutableLiveData<JWT_token>()
    val userInfo = MutableLiveData<User>()

    fun getToken(number:Int, uid:String){
        viewModelScope.launch {
            val response = repository.getToken(number, uid)
            if (response.isSuccessful){
                jwtToken.postValue(response.body())
            }
        }
    }

    fun getDataAboutUser(){
        viewModelScope.launch {
            val response = repository.getUserData()
            if (response.isSuccessful){
                userInfo.postValue(response.body())
            }
        }
    }

}