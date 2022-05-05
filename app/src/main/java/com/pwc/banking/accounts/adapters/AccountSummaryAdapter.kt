package com.pwc.banking.accounts.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.backbase.android.business.journey.common.extensions.exhaustive
import com.pwc.banking.R
import com.pwc.banking.StringObjectConstants
import com.pwc.banking.accounts.Listeners.ExpandableListClickListenerNew
import com.pwc.banking.accounts.constants.LayoutConstants
import com.pwc.banking.accounts.constants.MultiPositionAccountConstants
import com.pwc.banking.accounts.model.AccountsData
import com.pwc.banking.accounts.model.DataModelForAccountsNew
import com.pwc.banking.maskString

class AccountSummaryAdapter (private val context: Context, private val list: MutableList<DataModelForAccountsNew>,
                             private val cellClickListener: ExpandableListClickListenerNew
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType== LayoutConstants.PARENT){
            val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.accounts_iteam_header_section, parent,false)
            GroupViewHolder(rowView)
        } else {
            val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.account_item_child_with_icons, parent,false)
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
                        downArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.blue_arrow_down))
                    } else{
                        downArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.blue_arrow_up))
                    }
                    expandOrCollapseParentItem(dataList,position)
                }
            }
        } else {
            holder as ChildViewHolder
            holder.apply {
                val singleService = dataList.accounts.first()
                accountName?.text =singleService.accountName
                accountSubName?.text = singleService.accountNumber?.maskString()
                accountBalance?.text = "$ "+singleService.amount?.value.toString()
                accountCurrency?.text = "Current APY: "+ singleService.rateOfInterest+"%"

                icon_title.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.depositicon))
                icon_arrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.blue_arrow_right))
                textviewOptions.visibility = View.GONE

                when(singleService.iban){
                    MultiPositionAccountConstants.mspendAccount -> {
                        icon_title.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.checking))
                        groupAddtions.visibility =  View.VISIBLE
                        icon_arrow.visibility = View.VISIBLE
                        textviewOptions.visibility = View.GONE
                        accountCurrency?.text = "Current APY: "+ singleService.rateOfInterest+"%"
                    }

                    MultiPositionAccountConstants.msaveAccount -> {
                        icon_title.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.savings))
                        groupAddtions.visibility =  View.VISIBLE
                        icon_arrow.visibility = View.VISIBLE
                        textviewOptions.visibility = View.GONE
                        accountCurrency?.text = "Current APY: "+ singleService.rateOfInterest+"%"
                    }

                    MultiPositionAccountConstants.mmortgageAccount -> {

                        icon_title.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.loan))
                        groupAddtions.visibility =  View.VISIBLE
                        icon_arrow.visibility = View.VISIBLE
                        textviewOptions.visibility = View.GONE
                        accountCurrency?.text = "Current APY: "+ singleService.rateOfInterest+"%"
                    }
                    MultiPositionAccountConstants.mloanAccount -> {
                        icon_title.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.loan))
                        groupAddtions.visibility =  View.VISIBLE
                        icon_arrow.visibility = View.VISIBLE
                        textviewOptions.visibility = View.GONE
                        accountCurrency?.text = "Current APY: "+ singleService.rateOfInterest+"%"
                    }

                    MultiPositionAccountConstants.mautosaveAccount -> {
                        icon_title.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.autosave))
                        groupAddtions.visibility =  View.GONE
                        icon_arrow.visibility = View.GONE
                        textviewOptions.visibility = View.VISIBLE
                        accountCurrency?.text = "Available balance"

                    }
                    MultiPositionAccountConstants.mcashbackAccount -> {
                        icon_title.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cashback))
                        groupAddtions.visibility =  View.GONE
                        icon_arrow.visibility = View.GONE
                        textviewOptions.visibility = View.VISIBLE
                        accountCurrency?.text = "Available balance"
                    }
                    MultiPositionAccountConstants.mloayltypointsAccount -> {
                        icon_title.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.loayalty))
                        accountBalance?.text =
                            singleService.amount?.value?.let { String.format("%.0f", it.toDouble()) } + " PTS"
                        groupAddtions.visibility =  View.GONE
                        icon_arrow.visibility = View.GONE
                        textviewOptions.visibility = View.VISIBLE
                        accountCurrency?.text = "Available balance"
                    }

                    MultiPositionAccountConstants.mrentPocketAccount -> {
                        groupAddtions.visibility =  View.GONE
                        icon_arrow.visibility = View.GONE
                        textviewOptions.visibility = View.VISIBLE
                        accountCurrency?.text = "Available balance"
                    }
                    MultiPositionAccountConstants.mcarepocketAccount -> {
                        groupAddtions.visibility =  View.GONE
                        icon_arrow.visibility = View.GONE
                        textviewOptions.visibility = View.VISIBLE
                        accountCurrency?.text = "Available balance"
                    }
                    MultiPositionAccountConstants.mloanpocketAccount -> {
                        groupAddtions.visibility =  View.GONE
                        icon_arrow.visibility = View.GONE
                        textviewOptions.visibility = View.VISIBLE
                        accountCurrency?.text = "Available balance"

                    }
                    MultiPositionAccountConstants.mmusicpocketAccount -> {
                        groupAddtions.visibility =  View.GONE
                        icon_arrow.visibility = View.GONE
                        textviewOptions.visibility = View.VISIBLE
                        accountCurrency?.text = "Available balance"
                    }

                    MultiPositionAccountConstants.singlePositionCurrentAccountItem -> {
                        icon_title.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.checking))
                        groupAddtions.visibility =  View.VISIBLE
                        icon_arrow.visibility = View.VISIBLE
                        textviewOptions.visibility = View.GONE
                        accountCurrency?.text = "Current APY: "+ singleService.rateOfInterest+"%"
                    }
                    MultiPositionAccountConstants.singlePositionSavingAccountItem -> {
                        icon_title.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.savings))
                        groupAddtions.visibility =  View.VISIBLE
                        icon_arrow.visibility = View.VISIBLE
                        textviewOptions.visibility = View.GONE
                        accountCurrency?.text = "Current APY: "+ singleService.rateOfInterest+"%"
                    }
                    MultiPositionAccountConstants.singlePositionLoanAccountItem -> {
                        icon_title.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.loan))
                        groupAddtions.visibility =  View.VISIBLE
                        icon_arrow.visibility = View.VISIBLE
                        textviewOptions.visibility = View.GONE
                        accountCurrency?.text = "Current APY: "+ singleService.rateOfInterest+"%"
                    }
                    else -> {
                        icon_title.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.autosave))
                    }
                }.exhaustive

                root.setOnClickListener {
                    StringObjectConstants.arrangementId = singleService.accountId
                    cellClickListener.onCellClickListener(singleService)
                }
            }
        }
    }


//    private fun maskString(input: String?): String? {
//        return input?.replace(".(?=.{4})".toRegex(), "\u00b7")
//    }
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
        val downArrow: ImageView = row.findViewById(R.id.down_arrow_1)

    }
    class ChildViewHolder(row: View) : RecyclerView.ViewHolder(row) {


        val accountName: TextView = row.findViewById(R.id.accountsAndTransactionsJourney_accountsScreen_title)
        val accountSubName: TextView = row.findViewById(R.id.accountsAndTransactionsJourney_accountsScreen_subtitle)
        val accountBalance: TextView = row.findViewById(R.id.accountsAndTransactionsJourney_accountsScreen_rightAccessoryText)
        val accountCurrency: TextView = row.findViewById(R.id.accountsAndTransactionsJourney_accountsScreen_rightAccessorySubtitle)
        val root: View = row.findViewById(R.id.account_item_root)

        val icon_title: AppCompatImageView = row.findViewById(R.id.account_child_icon)
        val icon_arrow: AppCompatImageView = row.findViewById(R.id.account_child_arrow)
        val textviewOptions: AppCompatTextView = row.findViewById(R.id.textview_options)
        val groupAddtions:Group = row.findViewById(R.id.group_additions)

    }
}