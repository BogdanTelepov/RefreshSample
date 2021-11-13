package com.example.registrationapp.repository

import com.example.registrationapp.model.JWT_token
import com.example.registrationapp.retrofit.RestApi
import com.example.tokenapp.model.User
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(private val restApi: RestApi) {

    suspend fun registerUser(number:Int, uid:String, name:String, ):Response<String>{
        return restApi.sendUserData(number, uid, name,"12/12/2005")
    }

    suspend fun getToken(number: Int, uid: String):Response<JWT_token>{
        return restApi.getJWTtoken(number, uid)
    }

    suspend fun checkNumber(number:Int):Response<Boolean>{
        return restApi.checkUserNumber(number)
    }

    suspend fun getUserData():Response<User>{
        return restApi.getUserData()
    }
}