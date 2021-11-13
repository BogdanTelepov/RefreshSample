package com.example.tokenapp.model

import com.google.gson.annotations.SerializedName

data class RefreshResponse(

    @SerializedName("access")
    val access: String
)
