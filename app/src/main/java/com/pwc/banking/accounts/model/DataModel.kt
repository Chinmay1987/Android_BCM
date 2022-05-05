package com.pwc.banking.accounts.model

import com.backbase.android.retail.journey.accounts_and_transactions.accounts.AccountType
import java.util.Currency

sealed class DataModel{
    data class Header(
        val title: String?
    ) : DataModel()

    data class Accounts(
        val accountId: String,

        val accountName: String?,

        val accountNumber: String?,

        val accountType: AccountType,
        val amount: com.backbase.android.retail.journey.accounts_and_transactions.Amount?,
    ) : DataModel()

    data class MultipositionAccounts(
        val accountId: String,

        val accountName: String?,

        val accountNumber: String?,

        val accountType: AccountType,

        var currency: Currency?,
        var value: Number?
    ) : DataModel()
}
