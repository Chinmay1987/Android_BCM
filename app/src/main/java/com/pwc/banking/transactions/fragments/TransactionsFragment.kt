package com.pwc.banking.transactions.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.backbase.android.retail.journey.accounts_and_transactions.Amount
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.CallState
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.TransactionsUseCase
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.models.CreditDebitIndicatorItem
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.models.TransactionItem
import com.pwc.banking.R
import com.pwc.banking.StringObjectConstants
import com.pwc.banking.transactions.adapters.RecyclerAdapterForTransactions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.lang.Exception
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog


class TransactionsFragment : Fragment() {
    private val transactionsUseCase: TransactionsUseCase by inject()
    var transactionsList : MutableList<TransactionItem> = ArrayList()
    private lateinit var adapters: RecyclerAdapterForTransactions

    var mTransactionsList:RecyclerView? = null
    var mLoader:ProgressBar? = null
    var mEmptyView:TextView? = null
    var searchView: SearchView? = null
    var filterButton: AppCompatImageView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.custom_accountdetails_transactionsview, container, false)
         mTransactionsList = view.findViewById(R.id.transactions_list)
         mLoader = view.findViewById(R.id.accounts_transactions_loader)
         mEmptyView = view.findViewById(R.id.empty_view)
        searchView = view.findViewById(R.id.simpleSearchView)
        filterButton = view.findViewById(R.id.filter_button)

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapters.filter?.filter(newText)
                return false
            }

        })

        filterButton?.setOnClickListener {
            val alertDialog: AlertDialog.Builder? = context?.let { it1 -> AlertDialog.Builder(it1) }
            val factory = LayoutInflater.from(context)
            val view: View = factory.inflate(com.pwc.banking.R.layout.custom_accountdetails_filter_view, null)
            alertDialog?.setView(view)
            alertDialog?.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog?.setPositiveButton("Apply",
                DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog?.show()
        }


        return  view
    }


    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenCreated{
            StringObjectConstants.arrangementId?.let { getTransactions(it) }
        }
    }



    /**
     * This method helps to get the list of Transactions which is coroutine scope
     * For this to work, need to inject private val transactionsUseCase: TransactionsUseCase by inject() in this fragment
     * And register with Koin :
     * single<TransactionsUseCase> { TransactionsUseCaseImpl(get()) }
    factory {
    val serverUri = URI("${apiRoot()}/transaction-manager")
    TransactionClientApi(
    context = get<Application>(),
    moshi = get(), parser = get(),
    serverUri = serverUri, provider = get(),
    backbase = get())
    }
     */
    private suspend fun getTransactions(
        accountId: String
    ) {
        mLoader?.visibility = View.VISIBLE
        withContext(Dispatchers.IO) {
            when (val result = transactionsUseCase.getTransactions(
                0,
                100,
                accountId,
                "",
                null
            )) {
                is CallState.Success -> {
                    print(result.data)
                    result.data?.let { list ->
                        if (list.isEmpty()) {
                            null
                        } else {
                            list.forEach {
                                transactionsList.add(TransactionItem {
                                    id = it.arrangementId
                                    type = it.type
                                    billingStatus = it.billingStatus
                                    transactionAmountCurrency = Amount {
                                        value = it.transactionAmountCurrency?.value
                                        currency = it.transactionAmountCurrency?.currency
                                    }
                                    description = it.description
                                    bookingDate = it.bookingDate
                                    creditDebitIndicator = CreditDebitIndicatorItem.CREDIT
                                })
                            }
                        }
                    }
                }
                CallState.Empty -> {
                    transactionsList.clear()
                }
                is CallState.Error -> {
                    try {
                        transactionsList.clear()
                    }catch (exception: Exception){
                        print("Transactions not available" + exception.message)
                    }

                }
            }
        }

        mLoader?.visibility = View.GONE
        if (transactionsList.isEmpty()) {
            mTransactionsList?.visibility = View.GONE
            mEmptyView?.visibility = View.VISIBLE
        } else {
            with(mTransactionsList) {
                this?.addItemDecoration(
                    DividerItemDecoration(
                        this?.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
                mTransactionsList?.visibility = View.VISIBLE
                mEmptyView?.visibility = View.GONE
                adapters = context?.let { RecyclerAdapterForTransactions(it, transactionsList) }!!
                this?.adapter = adapters
            }
        }

    }
}