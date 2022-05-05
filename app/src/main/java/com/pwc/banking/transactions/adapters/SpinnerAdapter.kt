package com.pwc.banking.transactions.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pwc.banking.R
import com.pwc.banking.maskString
import com.pwc.banking.transactions.model.AccountsForSpinner

class SpinnerAdapter(val context: Context, var dataSource: List<AccountsForSpinner>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.transactions_view_spinner_adapter_view, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.productName?.text = dataSource[position].productName
        vh.accountNumber?.text = dataSource[position].accountNumber?.maskString()
        vh.accountAmount?.text = "$ "+dataSource[position].amount?.let {
            roundup(
                it.toDouble()
            )
        }

        return view
    }

    private fun roundup(value: Double):String{
        return String.format("%.2f", value)
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position];
    }

    override fun getCount(): Int {
        return dataSource.size;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    private class ItemHolder(row: View?) {
        val productName: TextView? = row?.findViewById(R.id.acount_name) as TextView
        val accountNumber: TextView? = row?.findViewById(R.id.acount_number) as TextView
        val accountAmount: TextView? = row?.findViewById(R.id.acount_amount) as TextView


    }

}