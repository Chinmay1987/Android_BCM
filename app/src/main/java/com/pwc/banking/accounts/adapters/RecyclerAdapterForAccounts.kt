package com.pwc.banking.accounts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.pwc.banking.R
import com.pwc.banking.accounts.Listeners.CellClickListener
import com.pwc.banking.accounts.model.DataModel

class RecyclerAdapterForAccounts(
    private val values: List<DataModel>,
    private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<RecyclerAdapterForAccounts.DataAdapterViewHolder>() {

    companion object {
        private const val TYPE_Header = 0
        private const val TYPE_Account = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (values[position]) {
            is DataModel.Accounts -> TYPE_Account
            else -> TYPE_Header
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataAdapterViewHolder {
        val layout = when (viewType) {
            TYPE_Header -> R.layout.custom_account_item_header_section
            TYPE_Account -> R.layout.custom_account_item
            else -> throw IllegalArgumentException("Invalid view type")
        }

        val view = LayoutInflater
            .from(parent.context)
            .inflate(layout, parent, false)

        return DataAdapterViewHolder(view, cellClickListener)
    }

    override fun onBindViewHolder(holder: DataAdapterViewHolder, position: Int) {
        val data = values[position]
        holder.bind(data)

    }

    override fun getItemCount(): Int = values.size


    class DataAdapterViewHolder(itemView: View, private val cellClickListener: CellClickListener) : RecyclerView.ViewHolder(itemView){

        private fun bindAccount(item: DataModel.Accounts) {
            itemView.findViewById<AppCompatTextView>(R.id.accountsAndTransactionsJourney_accountsScreen_title)?.text = item.accountName
            itemView.findViewById<AppCompatTextView>(R.id.accountsAndTransactionsJourney_accountsScreen_subtitle)?.text = item.accountNumber
            itemView.findViewById<AppCompatTextView>(R.id.accountsAndTransactionsJourney_accountsScreen_rightAccessoryText)?.text = item.amount?.value.toString()//item.value.toString()
            itemView.findViewById<AppCompatTextView>(R.id.accountsAndTransactionsJourney_accountsScreen_rightAccessorySubtitle)?.text = item.amount?.currency.toString()//item.currency.toString()
            itemView.findViewById<CardView>(R.id.account_item_root).setOnClickListener {
                cellClickListener.onCellClickListener(item)
            }
        }
        private fun bindHeader(item: DataModel.Header) {
            itemView.findViewById<MaterialTextView>(R.id.accountsTransactionsJourney_listView_sectionHeader)?.text = item.title
        }

        fun bind(dataModel: DataModel) {
            when (dataModel) {
                is DataModel.Accounts -> bindAccount(dataModel)
                is DataModel.Header -> bindHeader(dataModel)
            }
        }
    }


}