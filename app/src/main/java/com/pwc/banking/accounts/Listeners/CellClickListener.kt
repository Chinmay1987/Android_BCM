package com.pwc.banking.accounts.Listeners

import com.pwc.banking.accounts.model.DataModel

interface CellClickListener {
    fun onCellClickListener(dataModel: DataModel.Accounts)
}