package com.example.registrationapp.authorization

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrationapp.model.JWT_token
import com.example.registrationapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(private val repository: Repository) : ViewModel(){

    val isNumberLocateInDB = MutableLiveData<Boolean>()
    val jwtToken = MutableLiveData<JWT_token>()


    fun checkNumber(number:Int){
        viewModelScope.launch {
            val response = repository.checkNumber(number)
            if (response.isSuccessful){
                isNumberLocateInDB.postValue(response.body())
            }
        }
    }

    fun getToken(number:Int, uid:String){
        viewModelScope.launch {
            val response = repository.getToken(number, uid)
            if (response.isSuccessful){
                jwtToken.postValue(response.body())
            }
        }
    }

}