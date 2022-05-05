package com.pwc.banking.accounts.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.backbase.android.business.journey.common.extensions.exhaustive
import com.pwc.banking.R
import com.pwc.banking.accounts.constants.LayoutConstants
import com.pwc.banking.accounts.constants.MultiPositionAccountConstants
import com.pwc.banking.accounts.Listeners.ExpandableListClickListenerNew
import com.pwc.banking.accounts.model.AccountsData
import com.pwc.banking.accounts.model.DataModelForAccountsNew

class AccountsSummaryRecyclerAdapter (private val context: Context, private val list: MutableList<DataModelForAccountsNew>,
                                      private val cellClickListener: ExpandableListClickListenerNew
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType== LayoutConstants.PARENT){
            val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.accounts_iteam_header_section, parent,false)
            GroupViewHolder(rowView)
        } else {
            val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.accounts_item_child_section_sample, parent,false)
            ChildViewHolder(rowView)
        }
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val dataList = list[position]
        if (dataList.type == LayoutConstants.PARENT) {
            holder as GroupViewHolder
            holder.apply {
                accountNameHeader?.text = dataList.productName
                accountSubNameHeader?.text = dataList.accountNumber
                accountCurrencyHeader?.text = "Total available balance"
                accountBalanceHeader?.text = "$ "+dataList.aggregatedBalance
                viewAccountsHeader?.text = "Accounts breakdown("+dataList.noOfAccounts+")"
                downArrow?.setOnClickListener {
                    if(dataList.isExpanded){
                        downArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_down_24))
                    } else{
                        downArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_up_24))
                    }
                    expandOrCollapseParentItem(dataList,position)
                }
            }
        } else {
            holder as ChildViewHolder
            holder.apply {
                val singleService = dataList.accounts.first()
                accountName?.text =singleService.accountName
                accountSubName?.text = maskString(singleService.accountNumber)
                accountBalance?.text = "$ "+singleService.amount?.value.toString()
                accountCurrency?.text = "Available balance"
                accountRateOfIntr?.text = singleService.rateOfInterest+"%"
                when(singleService.iban){
                    MultiPositionAccountConstants.mspendAccount -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.mspendcolor))
                        groupLoanAdditions.visibility = View.GONE
                    }

                    MultiPositionAccountConstants.msaveAccount -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.msavecolor))
                        groupLoanAdditions.visibility = View.GONE
                    }

                    MultiPositionAccountConstants.mmortgageAccount -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.mmortgagecolor))
                        groupLoanAdditions.visibility = View.GONE

                    }
                    MultiPositionAccountConstants.mloanAccount -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.mloancolor))
                        groupLoanAdditions.visibility = View.VISIBLE
                        groupAdditions.visibility = View.VISIBLE
                        accountNextPaymentAmountLabel?.text = "Next payment amount:"
                        accountNextPaymentAmount?.text = "$ "+singleService.nextPaymentAmount
                    }

                    MultiPositionAccountConstants.mautosaveAccount -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.mautosavecolor))
                        accountCurrency.visibility = View.GONE
                        accountSubName.visibility = View.GONE
                        groupAdditions.visibility = View.GONE
                        groupLoanAdditions.visibility = View.GONE

                    }
                    MultiPositionAccountConstants.mcashbackAccount -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.mcashbackcolor))
                        accountCurrency.visibility = View.GONE
                        accountSubName.visibility = View.GONE
                        groupAdditions.visibility = View.GONE
                        groupLoanAdditions.visibility = View.GONE
                    }
                    MultiPositionAccountConstants.mloayltypointsAccount -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.mloayltypointscolor))
                        accountBalance?.text =
                            singleService.amount?.value?.let { String.format("%.0f", it.toDouble()) } + " PTS"
                        accountCurrency.visibility = View.GONE
                        accountSubName.visibility = View.GONE
                        groupAdditions.visibility = View.GONE
                        groupLoanAdditions.visibility = View.GONE
                    }

                    MultiPositionAccountConstants.mrentPocketAccount -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.mrentpocketcolor))
                        accountCurrency.visibility = View.GONE
                        accountSubName.visibility = View.GONE
                        groupAdditions.visibility = View.GONE
                        groupLoanAdditions.visibility = View.GONE
                    }
                    MultiPositionAccountConstants.mcarepocketAccount -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.mcarepocketcolor))
                        accountCurrency.visibility = View.GONE
                        accountSubName.visibility = View.GONE
                        groupAdditions.visibility = View.GONE
                        groupLoanAdditions.visibility = View.GONE
                    }
                    MultiPositionAccountConstants.mloanpocketAccount -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.mloanpocketcolor))
                        accountCurrency.visibility = View.GONE
                        accountSubName.visibility = View.GONE
                        groupAdditions.visibility = View.GONE
                        groupLoanAdditions.visibility = View.GONE
                    }
                    MultiPositionAccountConstants.mmusicpocketAccount -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.mmusicpocketcolor))
                        accountCurrency.visibility = View.GONE
                        accountSubName.visibility = View.GONE
                        groupAdditions.visibility = View.GONE
                        groupLoanAdditions.visibility = View.GONE
                    }

                    MultiPositionAccountConstants.singlePositionCurrentAccountItem -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorforsinglepositionloancurrent))
                        groupAdditions.visibility = View.VISIBLE
                        groupLoanAdditions.visibility = View.GONE
                    }
                    MultiPositionAccountConstants.singlePositionSavingAccountItem -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorforsinglepositionsavings))
                        groupAdditions.visibility = View.VISIBLE
                        groupLoanAdditions.visibility = View.GONE
                    }
                    MultiPositionAccountConstants.singlePositionLoanAccountItem -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorforsinglepositionloan))
                        groupAdditions.visibility = View.VISIBLE
                        groupLoanAdditions.visibility = View.VISIBLE

                        accountNextPaymentAmountLabel?.text = "Next payment amount:"
                        accountNextPaymentAmount?.text = "$ "+singleService.nextPaymentAmount
                    }
                    else -> {
                        coloredView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorforallaccounts))
                    }
                }.exhaustive

                root.setOnClickListener {
                    cellClickListener.onCellClickListener(singleService)
                }
            }
        }
    }


    private fun maskString(input: String?): String? {
        return input?.replace(".(?=.{4})".toRegex(), "x")
    }
    private fun expandOrCollapseParentItem(singleBoarding: DataModelForAccountsNew, position: Int) {

        if (singleBoarding.isExpanded) {
            collapseParentRow(position)
        } else {
            expandParentRow(position)
        }
    }

    private fun expandParentRow(position: Int){
        val currentTitleRow = list[position]
        val accountsRow = currentTitleRow.accounts
        currentTitleRow.isExpanded = true
        var nextPosition = position
        if(currentTitleRow.type== LayoutConstants.PARENT){
            accountsRow.forEach { service ->
                val parentModel =  DataModelForAccountsNew()
                parentModel.type = LayoutConstants.CHILD
                val subList : MutableList<AccountsData> = ArrayList()
                subList.add(service)
                parentModel.accounts=subList
                list.add(++nextPosition,parentModel)
            }
            notifyDataSetChanged()
        }
    }

    private fun collapseParentRow(position: Int){
        val currentTitleRow = list[position]
        val accountsRow = currentTitleRow.accounts
        list[position].isExpanded = false
        if(list[position].type== LayoutConstants.PARENT){
            accountsRow.forEach { _ ->
                list.removeAt(position + 1)
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int = list[position].type

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class GroupViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val viewAccountsHeader = row.findViewById(R.id.accountsAndTransactionsJourney_accountsScreen_header_viewaccounts) as TextView
        val accountNameHeader: TextView = row.findViewById(R.id.accountsAndTransactionsJourney_accountsScreen_header_title)
        val accountSubNameHeader: TextView = row.findViewById(R.id.accountsAndTransactionsJourney_accountsScreen_header_subtitle)
        val accountBalanceHeader: TextView = row.findViewById(R.id.accountsAndTransactionsJourney_accountsScreen_header_amount)
        val accountCurrencyHeader: TextView = row.findViewById(R.id.accountsAndTransactionsJourney_accountsScreen_header_currency)
        val downArrow: ImageView  = row.findViewById(R.id.down_arrow_1)

    }
    class ChildViewHolder(row: View) : RecyclerView.ViewHolder(row) {

        val coloredView: View = row.findViewById(R.id.view_vertical_color)
        val accountName: TextView = row.findViewById(R.id.account_name)
        val accountSubName: TextView = row.findViewById(R.id.account_sub_name)
        val accountBalance: TextView = row.findViewById(R.id.account_amount)
        val accountCurrency: TextView = row.findViewById(R.id.account_currency)
        val root: View = row.findViewById(R.id.account_item_root)


        val accountRateOfIntr: TextView = row.findViewById(R.id.account_rateofinterest_value)


        val groupAdditions:Group = row.findViewById(R.id.group_additions)


        val accountNextPaymentAmount: TextView = row.findViewById(R.id.account_nextpayment_amount_value)
        val accountNextPaymentAmountLabel: TextView = row.findViewById(R.id.account_nextpayment_amount)
        val groupLoanAdditions:Group = row.findViewById(R.id.group_loan_additions)


    }
}