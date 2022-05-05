package com.pwc.banking.transactions.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.list.TransactionsScreenArgs
import com.google.android.material.tabs.TabLayout
import com.pwc.banking.R
import com.pwc.banking.SharedPreferencesDataUtil
import com.pwc.banking.StringConstantUtils
import com.pwc.banking.StringObjectConstants
import com.pwc.banking.accounts.model.DataModelForAccountsNew
import com.pwc.banking.transactions.adapters.SpinnerAdapter
import com.pwc.banking.transactions.adapters.ViewPagerAdapter
import com.pwc.banking.transactions.model.AccountsForSpinner
import java.math.BigDecimal

class CustomTransactionsView(): Fragment() {
    var accountLists : MutableList<DataModelForAccountsNew> = ArrayList()
    var listSpinner : ArrayList<AccountsForSpinner> = ArrayList()

    var mTablalayout:TabLayout? = null
    var mViewPager : ViewPager2? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_layout_for_verification, container, false)
        val spinner = view.findViewById<Spinner>(com.backbase.android.clients.common.R.id.spinner)
         mTablalayout = view.findViewById(R.id.tablalayout_id)
         mViewPager = view.findViewById(R.id.viewpager)


        accountLists = SharedPreferencesDataUtil(context).getList() as MutableList<DataModelForAccountsNew>
        if(accountLists.size >= 0){
            accountLists.forEach {accounts ->
                if(accounts.productName != null) {

                    accounts.accounts.forEach{
                        if(it.accountId == StringObjectConstants.arrangementId){
                            listSpinner.add(0,
                                AccountsForSpinner(
                                    productName = it.accountName,
                                    accountNumber = it.accountNumber,
                                    amount = it.amount?.value.toString(),
                                    id = it.accountId)
                            )
                        }else {
                            it.accountName?.let { it1 ->
                                listSpinner.add(
                                    AccountsForSpinner(
                                        productName = it.accountName,
                                        accountNumber = it.accountNumber,
                                        amount = it.amount?.value.toString(),
                                        id = it.accountId
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated{
            with(spinner) {
                val customDropDownAdapter = context?.let { SpinnerAdapter(it, listSpinner) }
                adapter = customDropDownAdapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedItem = parent?.getItemAtPosition(position) as AccountsForSpinner
                        val id = selectedItem.id
                        StringObjectConstants.arrangementId = selectedItem.id
                        if (id != null) {
                            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                                val fm: FragmentManager = parentFragmentManager
                                val adapters = ViewPagerAdapter(fm, lifecycle)
                                mViewPager?.adapter = adapters
                            }
                        }
                    }
                }
            }
        }


        mTablalayout?.newTab()?.let { mTablalayout?.addTab(it.setText(R.string.tab_transaction_history)) }
        mTablalayout?.newTab()?.let { mTablalayout?.addTab(it.setText(R.string.tab_details)) }
        mTablalayout?.newTab()?.let { mTablalayout?.addTab(it.setText(R.string.tab_schedule_transfers)) }
        mTablalayout?.tabGravity = TabLayout.GRAVITY_FILL


        mViewPager?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                mTablalayout?.selectTab(mTablalayout?.getTabAt(position))
            }
        })
        mTablalayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                mViewPager?.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        return view
    }

}