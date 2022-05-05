package com.pwc.banking.accounts.model


import com.pwc.banking.accounts.constants.LayoutConstants

data class DataModelForAccountsNew(
    val productName: String?= null,
    val accountNumber: String? = null,
    val noOfAccounts:String?= null,
    val aggregatedBalance: String?= null,
    var accounts: MutableList<AccountsData> = ArrayList(),
    var isExpanded:Boolean = false,
    var type:Int = LayoutConstants.PARENT
)
