package com.pwc.banking.transactions.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScheduleTransfersRequestModel(
    val approvalId:String? = null
)
