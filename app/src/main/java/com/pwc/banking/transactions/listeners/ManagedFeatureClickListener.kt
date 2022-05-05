package com.pwc.banking.transactions.listeners

import com.pwc.banking.accounts.model.DataModel
import com.pwc.banking.transactions.model.AdditionsModal

interface ManagedFeatureClickListener {
    fun onChangeCheckedStatus(dataModel: AdditionsModal, checkedStatus:Boolean)
}