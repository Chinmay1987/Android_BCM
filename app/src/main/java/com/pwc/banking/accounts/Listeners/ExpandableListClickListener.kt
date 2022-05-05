package com.pwc.banking.accounts.Listeners

import com.backbase.android.retail.journey.accounts_and_transactions.accounts.Account

interface ExpandableListClickListener {

    fun onCellClickListener(dataModel: Account)
}