package com.pwc.banking.transactions.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScheduleTransfersResponseModel(
    val approvalId:String? = null
)
