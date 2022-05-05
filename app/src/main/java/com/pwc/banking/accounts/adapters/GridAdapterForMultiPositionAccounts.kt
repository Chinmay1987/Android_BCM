package com.pwc.banking.accounts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.backbase.android.retail.journey.accounts_and_transactions.accounts.Account
import com.pwc.banking.R

class GridAdapterForMultiPositionAccounts(
    private val values: List<Account>,
    private val listener: (Account) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private const val LAYOUT_HEADER = 0
        private const val LAYOUT_CHILD = 1
        private const val ACCOUNT_NAME = "All-in-One Account"
    }

    override fun getItemViewType(position: Int): Int {
        return if (
            values[position].accountId == ACCOUNT_NAME
        ) {
            LAYOUT_HEADER
        } else {
            LAYOUT_CHILD
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var holder: RecyclerView.ViewHolder = if(viewType== LAYOUT_HEADER){
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_account_item_header_multiposition_gridview, parent, false)
            HeaderViewHolder(view);
        }else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_account_item_multiposition, parent, false)
            ChildViewHolder(view);
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = values[position]
        if (holder.itemViewType === LAYOUT_HEADER) {
            val vaultItemHolder: HeaderViewHolder = holder as HeaderViewHolder
            vaultItemHolder.sectionHaderTextview.text = item.accountId
        } else {
            val vaultItemHolder: ChildViewHolder = holder as ChildViewHolder
            vaultItemHolder.apply {
                accountName.text = item.accountName
                accountSubName.text = item.accountNumber
                accountBalance.text = item.amount?.value.toString()
                accountCurrency.text = item.amount?.currency.toString()
                root.setOnClickListener { listener(item) }
            }
        }

    }

    override fun getItemCount(): Int = values.size

    inner class ChildViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val accountName: TextView = view.findViewById(R.id.accountsAndTransactionsJourney_accountsScreen_title)
        val accountSubName: TextView = view.findViewById(R.id.accountsAndTransactionsJourney_accountsScreen_subtitle)
        val accountBalance: TextView = view.findViewById(R.id.accountsAndTransactionsJourney_accountsScreen_rightAccessoryText)
        val accountCurrency: TextView = view.findViewById(R.id.accountsAndTransactionsJourney_accountsScreen_rightAccessorySubtitle)
        val root: View = view.findViewById(R.id.account_item_root)
    }
    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sectionHaderTextview: TextView = view.findViewById(R.id.multiposition_list_sectionHeader)

    }
}