package com.pwc.banking.accounts.model

import com.backbase.android.retail.journey.accounts_and_transactions.Amount
import com.backbase.android.retail.journey.accounts_and_transactions.accounts.AccountType
import java.util.*

data class AccountsData(
    val accountId: String?= null,
    val accountName: String?= null,
    val accountNumber: String? = null,
    val accountType: AccountType? = null,
    val accountTypeName: String?= null,
    val amount: Amount? = null,
    val bban: String? = null,
    val iban: String?= null,
    val accruedInterest: String?= null,
    val accountOpenDate: Date? = null,
    val rateOfInterest : String? = null,
    val aggregatedBalance: String?=  null,
    val nextPaymentDate: String?= null,
    val nextPaymentAmount: String? = null,
    val maturityDate: String?= null
)
