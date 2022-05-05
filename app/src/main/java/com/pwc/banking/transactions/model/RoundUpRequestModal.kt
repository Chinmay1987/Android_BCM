package com.pwc.banking.transactions.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoundUpRequestModal(
    val arrangementNumber:String? = null,
    val derivativeType:String? = null,
    val status:Boolean = false,
    val positionNumber:String? = null
)
