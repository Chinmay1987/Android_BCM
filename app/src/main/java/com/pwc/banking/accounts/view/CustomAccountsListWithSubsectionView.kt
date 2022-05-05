package com.pwc.banking.accounts.view
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.backbase.android.client.products.ProductsClient
import com.backbase.android.client.products.dto.productsummary.ProductSummary
import com.backbase.android.client.products.dto.productsummary.product.BaseProduct
import com.backbase.android.client.products.dto.productsummary.product.CurrentAccount
import com.backbase.android.client.products.dto.productsummary.product.GeneralAccount
import com.backbase.android.client.products.dto.productsummary.product.Loan
import com.backbase.android.client.products.dto.productsummary.product.SavingsAccount
import com.backbase.android.client.products.listener.ProductSummaryListener
import com.backbase.android.retail.journey.accounts_and_transactions.AccountsUseCase
import com.backbase.android.retail.journey.accounts_and_transactions.Amount
import com.backbase.android.retail.journey.accounts_and_transactions.accounts.Account
import com.backbase.android.retail.journey.accounts_and_transactions.accounts.AccountSummaryResponse
import com.backbase.android.retail.journey.accounts_and_transactions.accounts.AccountType
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.list.TransactionsScreen
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.list.TransactionsScreenArgs
import com.backbase.android.utils.net.response.Response
import com.pwc.banking.accounts.Listeners.CellClickListener
import com.pwc.banking.accounts.model.DataModel
import com.pwc.banking.accounts.adapters.RecyclerAdapterForAccounts
import com.pwc.banking.accounts.adapters.GridAdapterForMultiPositionAccounts
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

import androidx.recyclerview.widget.GridLayoutManager
import com.pwc.banking.R
import com.pwc.banking.accounts.model.AccountsData
import java.util.*
import kotlin.collections.ArrayList

class CustomAccountsListWithSubsectionView : Fragment(), CellClickListener {

    private val client: ProductsClient by inject()
    private var accountLists = mutableListOf<DataModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.custom_accounts_screen, container, false)
        val mLoader = view.findViewById<ProgressBar>(R.id.accounts_loader)
        val mAccountsList = view.findViewById<RecyclerView>(R.id.accounts_list)
        val mMultiPositionList = view.findViewById<RecyclerView>(R.id.multiposition_list)

        accountLists.clear()
        lifecycleScope.launchWhenCreated {
            mLoader.visibility = View.VISIBLE
            val accounts = withContext(Dispatchers.IO) {
                getAccounts()
            }

            mLoader.visibility = View.GONE
            Log.d("TRAINING", accounts.toString())

            with(mAccountsList) {
                this.addItemDecoration(
                    DividerItemDecoration(
                        this.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
                setHasFixedSize(true)
                adapter = RecyclerAdapterForAccounts(accountLists, this@CustomAccountsListWithSubsectionView)
            }

            with(mMultiPositionList) {
                this.addItemDecoration(
                    DividerItemDecoration(
                        this.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
                val gridLayoutManager = GridLayoutManager(activity, 2)
                setLayoutManager(gridLayoutManager);
                setHasFixedSize(true)

                adapter = GridAdapterForMultiPositionAccounts(accounts!!) { account ->
                    findNavController().navigate(
                        R.id.action_customAccountsScreen_to_transactionsScreen,
                        account.toTransactionsScreenArgs()
                    )

                }
            }
        }
        return view
    }


//    private suspend fun getParties():List<PaymentParty>{
//        val response = suspendCoroutine<DefaultPaymentPartyFilterImpl> {
//            val listener = object : PaymentL
//        }
//    }
    private suspend fun getAccounts(): List<Account>? {
        val response = suspendCoroutine<AccountSummaryResponse> { continuation ->
            val listener = object : ProductSummaryListener {
                override fun onSuccess(response: ProductSummary) {

                    response.currentAccounts?.let { value ->
                        accountLists.add(DataModel.Header(value.name))
                    }
                    val accounts = response.currentAccounts?.products?.let { list ->
                        if (list.isEmpty()) {
                            null
                        } else {
                            list.map { it.toAccount() }
                        }
                    }

                    if (accounts != null) {
                        for (account in accounts) {
                            accountLists.add(
                                DataModel.Accounts(
                                    account.accountId,
                                    account.accountName,
                                    account.accountNumber,
                                    account.accountType,
                                    amount = account.amount
                                )
                            )
                        }
                    }
                    response.savingsAccounts?.let { value ->
                        accountLists.add(DataModel.Header(value.name))
                    }
                    val savingsAccounts = response.savingsAccounts?.products?.let { list ->
                        if (list.isEmpty()) {
                            null
                        } else {
                            list.map { it.toAccount() }
                        }
                    }
                    if (savingsAccounts != null) {
                        for (account in savingsAccounts) {
                            accountLists.add(
                                DataModel.Accounts(
                                    account.accountId,
                                    account.accountName,
                                    account.accountNumber,
                                    account.accountType,
                                    amount = account.amount
                                )
                            )
                        }
                    }

                    response.loans?.let { value ->
                        accountLists.add(DataModel.Header(value.name))
                    }
                    val loanAccounts = response.loans?.products?.let { list ->
                        if (list.isEmpty()) {
                            null
                        } else {
                            list.map { it.toAccount() }
                        }
                    }
                    if (loanAccounts != null) {
                        for (account in loanAccounts) {
                            accountLists.add(
                                DataModel.Accounts(
                                    account.accountId,
                                    account.accountName,
                                    account.accountNumber,
                                    account.accountType,
                                    amount = account.amount
                                )
                            )
                        }
                    }

                    val productAccounts = response.customProductKinds?.get(0)?.products?.let { list ->
                        if (list.isEmpty()) {
                            null
                        } else {
                            list.map { it.toAccount() }
                        }
                    }

                    if (accounts == null)
                        resumeFailure()
                    else continuation.resume(
                        AccountSummaryResponse {
                            //accountList = concatenate(accounts, savingsAccounts?: listOf(), loanAccounts?: listOf(), productAccounts?: listOf())
                            accountList = productAccounts ?: listOf()
                            result = AccountsUseCase.AccountsTransactionsResult.SUCCESS
                        }
                    )
                }

                override fun onError(errorResponse: Response) = resumeFailure()

                private fun resumeFailure() {
                    continuation.resume(
                        AccountSummaryResponse {
                            accountList = emptyList()
                            result = AccountsUseCase.AccountsTransactionsResult.FAILURE
                        }
                    )
                }
            }

            client.getProductSummary(listener)
        }

        return when (response.result) {
            AccountsUseCase.AccountsTransactionsResult.SUCCESS -> response.accountList
            AccountsUseCase.AccountsTransactionsResult.FAILURE -> null
        }
    }

    fun <T> concatenate(vararg lists: List<T>): List<T> {
        val result: MutableList<T> = ArrayList()
        for (list in lists) {
            result += list
        }
        return result
    }

    private fun CurrentAccount.toAccount() = Account {
        accountId = id
        accountName = accountHolderNames
        accountNumber = BBAN
        accountType = this@toAccount.accountType
        amount = availableBalance?.let { availableBalance ->
            Amount {
                value = bookedBalance
                currency = Currency.getInstance(this@toAccount.currency)
            }
        }
        bban = BBAN
        iban = IBAN
    }

    private fun SavingsAccount.toAccount() = Account {
        accountId = productKind
        accountName = accountHolderNames
        accountNumber = BBAN
        accountType = this@toAccount.accountType
        amount = bookedBalance?.let { availableBalance ->
            Amount {
                value = bookedBalance
                currency = Currency.getInstance(this@toAccount.currency)
            }
        }
        bban = BBAN
        iban = IBAN
    }

    private fun Loan.toAccount() = Account {
        accountId = productKind
        accountName = accountHolderNames
        accountNumber = BBAN
        accountType = this@toAccount.accountType
        amount = bookedBalance?.let { availableBalance ->
            Amount {
                value = bookedBalance
                currency = Currency.getInstance(this@toAccount.currency)
            }
        }
        bban = BBAN
        iban = IBAN
    }

    private fun GeneralAccount.toAccount() = Account {
        accountId = name
        accountName = bankAlias
        accountNumber = BBAN
        accountType = this@toAccount.accountType
        amount = availableBalance?.let { availableBalance ->
            Amount {
                value = availableBalance
                currency = Currency.getInstance(this@toAccount.currency)
            }
        }
        bban = BBAN
        iban = IBAN
    }

    private val BaseProduct.accountType: AccountType
        get() = when (productKind) {
            "CreditCards" -> AccountType.CreditCard
            "CurrentAccounts" -> AccountType.CurrentAccount
            "DebitCards" -> AccountType.DebitCard
            "GeneralAccounts" -> AccountType.GeneralAccount
            "InvestmentAccounts" -> AccountType.InvestmentAccount
            "Loans" -> AccountType.Loan
            "SavingsAccounts" -> AccountType.SavingsAccount
            "TermDeposits" -> AccountType.TermDeposit
            else -> throw IllegalArgumentException("$productKind is not supported.")
        }

    private fun Account.toTransactionsScreenArgs(): Bundle = TransactionsScreen.getArgs(
        TransactionsScreenArgs {
            accountId = this@toTransactionsScreenArgs.accountId
            accountName = this@toTransactionsScreenArgs.accountName
            accountType = this@toTransactionsScreenArgs.accountType
            accountNumber = this@toTransactionsScreenArgs.accountNumber
            amount = this@toTransactionsScreenArgs.amount
        }
    )



    private fun DataModel.Accounts.toTransactionsScreenArgs(): Bundle = TransactionsScreen.getArgs(
        TransactionsScreenArgs {
            accountId = this@toTransactionsScreenArgs.accountId
            accountName = this@toTransactionsScreenArgs.accountName
            accountType = this@toTransactionsScreenArgs.accountType
            accountNumber = this@toTransactionsScreenArgs.accountNumber
            amount = this@toTransactionsScreenArgs.amount
        }
    )

    override fun onCellClickListener(dataModel: DataModel.Accounts) {
        findNavController().navigate(
            R.id.action_customAccountsScreen_to_transactionsScreen,
            dataModel.toTransactionsScreenArgs()
        )
    }

}
