package com.pwc.banking.transactions.model

data class AdditionsModal(
    val featureName:String?=null,
    val status:Boolean = false,
    val displayName:String?=null,
    val targetPositionNumber:String? = null

)
