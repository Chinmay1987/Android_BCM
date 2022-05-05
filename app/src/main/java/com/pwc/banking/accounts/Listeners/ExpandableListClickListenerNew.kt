package com.pwc.banking.accounts.Listeners

import com.backbase.android.retail.journey.accounts_and_transactions.accounts.Account
import com.pwc.banking.accounts.model.AccountsData

interface ExpandableListClickListenerNew {
    fun onCellClickListener(dataModel: AccountsData)
}