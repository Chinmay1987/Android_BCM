package com.pwc.banking.transactions.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.backbase.android.design.icon.IconView
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.models.TransactionItem
import com.google.android.material.imageview.ShapeableImageView
import com.pwc.banking.R
import java.util.*
import kotlin.collections.ArrayList


class RecyclerAdapterForTransactions(
    private val context: Context,
    private val values: List<TransactionItem>
) : RecyclerView.Adapter<RecyclerAdapterForTransactions.ViewHolder>(), Filterable {

    private val deposit: String = "Deposit"
    private val withdrawal: String = "Withdrawal"

    var countryFilterList : MutableList<TransactionItem> = ArrayList()

    init {
        countryFilterList = values as MutableList<TransactionItem>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.accounts_transactions_journey_transactions_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = countryFilterList[position]
        holder.transactionName.text = item.description
        holder.transactionSubName.text = item.type

        when(item.type) {
            deposit -> {
                holder.transactionBalance.text =
                    "+$" + item.transactionAmountCurrency?.value.toString()
                holder.transactionBalance.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green_color
                    )
                )
                holder.iconImageView.setBackgroundResource(R.drawable.depositicon)
            }
            withdrawal -> {
                holder.transactionBalance.text =
                    "-$" + item.transactionAmountCurrency?.value.toString()
                holder.transactionBalance.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black_color
                    )
                )
                holder.iconImageView.setBackgroundResource(R.drawable.cashicon)
            }
            else -> {
                holder.transactionBalance.text =
                    "$" + item.transactionAmountCurrency?.value.toString()
                holder.transactionBalance.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black_color
                    )
                )
                holder.iconImageView.setBackgroundResource(R.drawable.dollarsign)
            }
        }
    }

    override fun getItemCount(): Int = countryFilterList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val transactionName: TextView = view.findViewById(R.id.backbaseRetail_transactionView_name)
        val transactionSubName: TextView =
            view.findViewById(R.id.backbaseRetail_transactionView_subtitle)
        val transactionBalance: TextView =
            view.findViewById(R.id.backbaseRetail_transactionView_amount)
        val iconImageView: IconView = view.findViewById(R.id.backbaseRetail_transactionView_icon)
        val transactonRunningBalance: TextView =
            view.findViewById(R.id.backbaseRetail_transactionView_runningBalance)
        val root: View = view.findViewById(R.id.accounts_transactions_journey_rootid)
    }

    override fun getFilter(): Filter? {
       return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    countryFilterList = values as MutableList<TransactionItem>
                } else {
                    val resultList = ArrayList<TransactionItem>()
                    for (row in values) {
                        if (row.description?.lowercase(Locale.ROOT)
                                ?.contains(charSearch.lowercase(Locale.ROOT)) == true
                        ) {
                            resultList.add(row)
                        }
                    }
                    countryFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = countryFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                countryFilterList = results?.values as MutableList<TransactionItem>
                notifyDataSetChanged()
            }

        }
    }

}