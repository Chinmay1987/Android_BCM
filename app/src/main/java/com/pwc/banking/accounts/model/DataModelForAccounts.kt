package com.pwc.banking.accounts.model

import com.backbase.android.retail.journey.accounts_and_transactions.accounts.Account
import com.pwc.banking.accounts.constants.LayoutConstants

data class DataModelForAccounts(
    val productName: String?= null,
    val accountNumber: String? = null,
    val noOfAccounts:String?= null,
    var accounts: MutableList<Account> = ArrayList(),
    var isExpanded:Boolean = false,
    var type:Int = LayoutConstants.PARENT


)
