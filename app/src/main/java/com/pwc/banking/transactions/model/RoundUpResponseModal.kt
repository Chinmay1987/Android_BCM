package com.pwc.banking.transactions.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoundUpResponseModal(
    val arrangementNumber:String? = null,
    val status:Boolean = false,
    val message:String? = null,
    val derivativeType:String? = null
)
