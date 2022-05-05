package com.pwc.banking.accounts.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.backbase.android.client.gen2.arrangementclient2.api.BalancesApi
import com.backbase.android.client.gen2.arrangementclient2.api.BalancesApiParams
import com.backbase.android.client.gen2.arrangementclient2.model.AggregatedBalances
import com.backbase.android.client.products.ProductsClient
import com.backbase.android.client.products.dto.productsummary.ProductSummary
import com.backbase.android.client.products.dto.productsummary.product.*
import com.backbase.android.client.products.listener.ProductSummaryListener
import com.backbase.android.clients.common.ResponseListener
import com.backbase.android.retail.journey.accounts_and_transactions.AccountsUseCase
import com.backbase.android.retail.journey.accounts_and_transactions.Amount
import com.backbase.android.retail.journey.accounts_and_transactions.accounts.Account
import com.backbase.android.retail.journey.accounts_and_transactions.accounts.AccountSummaryResponse
import com.backbase.android.retail.journey.accounts_and_transactions.accounts.AccountType
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.list.TransactionsScreen
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.list.TransactionsScreenArgs
import com.backbase.android.utils.net.response.Response
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pwc.banking.R
import com.pwc.banking.SharedPreferencesDataUtil
import com.pwc.banking.accounts.constants.MultiPositionAccountConstants
import com.pwc.banking.accounts.Listeners.ExpandableListClickListenerNew
import com.pwc.banking.accounts.adapters.AccountSummaryAdapter
import com.pwc.banking.accounts.adapters.AccountsSummaryRecyclerAdapter
import com.pwc.banking.accounts.adapters.ImageViewPagerAdapter
import com.pwc.banking.accounts.model.AccountsData
import com.pwc.banking.accounts.model.DataModel
import com.pwc.banking.accounts.model.DataModelForAccounts
import com.pwc.banking.accounts.model.DataModelForAccountsNew
import com.pwc.banking.changeDateFormatFromAnother
import com.pwc.banking.maskString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class AccountSummaryView : Fragment(), ExpandableListClickListenerNew {

    private val client: ProductsClient by inject()
    private val balanceClient: BalancesApi by inject()
    var accountLists : MutableList<DataModelForAccountsNew> = ArrayList()
    var getBlanaceList: List<AggregatedBalances> = ArrayList()
    val sharedPrefKeyForAccounts:String = "Accounts_Access_Key"
    val KeyForAccounts:String = "ACCOUNTS"
    private var screenTitle: TextView? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.accounts_screen_expandablelist_main_view, container, false)

        screenTitle = view.findViewById(R.id.screen_title)
        val mLoader = view.findViewById<ProgressBar>(R.id.accounts_loader)
        val mAccountsList = view.findViewById<RecyclerView>(R.id.accounts_list)
        val mImageView = view.findViewById<ViewPager2>(R.id.imageview_viewpager)
        mImageView.adapter = ImageViewPagerAdapter()

        accountLists.clear()
        lifecycleScope.launchWhenCreated {


            mLoader.visibility = View.VISIBLE
         // mImageView.setImageDrawable(getResources().getDrawable(R.drawable.accounts_transaction_banners));
           // accountLists = SharedPreferencesDataUtil(context).getList() as MutableList<DataModelForAccountsNew>
            if(accountLists.size <= 0){
                withContext(Dispatchers.IO) {
                    getAggregatedBalance()
                    getAccounts()
                    Log.d("List Of Accounts", this.toString())
                }
            }

            mLoader.visibility = View.GONE
            //Log.d("List Of Accounts", accounts.toString())
            with(mAccountsList) {
                this.addItemDecoration(
                    DividerItemDecoration(
                        this.context,
                        DividerItemDecoration.VERTICAL
                    )
                )

                //setHasFixedSize(true)
                setList(accountLists)
                adapter = AccountSummaryAdapter(context, accountLists,this@AccountSummaryView)
            }
        }
        return view
    }


    private fun <T> setList(list: List<T>?) {
        val gson = Gson()
        val json = gson.toJson(list)
        SharedPreferencesDataUtil(context).set(json)
    }

    operator fun set(key: String?, value: String?) {
        val sharedPreferences =
            context?.getSharedPreferences(sharedPrefKeyForAccounts, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString(key, value)
        editor?.commit()
    }
    private fun getList(): List<DataModelForAccounts> {
        var arrayItems: List<DataModelForAccounts> = ArrayList()
        val sharedPreferences =
            context?.getSharedPreferences(sharedPrefKeyForAccounts, Context.MODE_PRIVATE)
        val serializedObject =
            sharedPreferences?.getString(KeyForAccounts, null)
        if (serializedObject != null) {
            val gson = Gson()
            val type = object : TypeToken<List<DataModelForAccounts?>?>() {}.type
            arrayItems = gson.fromJson(serializedObject, type)
        }
        return arrayItems
    }
    private suspend fun getAccounts(): List<Account>? {

        val response = suspendCoroutine<AccountSummaryResponse> { continuation ->
            val listener = object : ProductSummaryListener {
                override fun onSuccess(response: ProductSummary) {

                    if(response.customProductKinds?.size!! >= 0){
                        for(productsList in response.customProductKinds!!){
                            var productAccounts = productsList.products?.let { list ->
                                if (list.isEmpty()) {
                                    null
                                } else {
                                    val localList : MutableList<AccountsData> = ArrayList()
                                    list.forEach {

                                        localList.add(AccountsData(
                                            accountId = it.id,
                                            accountName = it.bankAlias,
                                            accountNumber = it.BBAN,
                                            accountType = it.accountType,
                                            amount = it.bookedBalance?.let { availableblance ->
                                                Amount {
                                                    value = availableblance
                                                    currency = Currency.getInstance(it.currency)
                                                }
                                            },
                                            bban = it.BBAN,
                                            iban = it.productTypeName,

                                            accruedInterest = if(it.additions?.containsKey("accountOpenDate") == true){
                                                it.additions?.getValue("accountOpenDate")?.changeDateFormatFromAnother()
                                            } else{
                                                ""
                                            },

                                            rateOfInterest  = if(it.additions?.containsKey("rateOfInterest") == true){
                                                it.additions?.getValue("rateOfInterest")
                                            } else{
                                                ""
                                            },
                                            aggregatedBalance = if(it.additions?.containsKey("aggregateDepositBalance") == true){
                                                it.additions?.getValue("aggregateDepositBalance")
                                            } else{
                                                ""
                                            },
                                            nextPaymentDate = if(it.additions?.containsKey("nextPaymentDate") == true){
                                                //changeDateFormatFromAnother( it.additions?.getValue("nextPaymentDate"))
                                                it.additions?.getValue("nextPaymentDate")?.changeDateFormatFromAnother()
                                            } else{
                                                ""
                                            },
                                            nextPaymentAmount  = if(it.additions?.containsKey("nextPaymentAmount") == true){
                                                it.additions?.getValue("nextPaymentAmount")
                                            } else{
                                                ""
                                            },
                                            maturityDate = if(it.additions?.containsKey("maturityDate") == true){
                                                //changeDateFormatFromAnother( it.additions?.getValue("maturityDate"))
                                                it.additions?.getValue("maturityDate")?.changeDateFormatFromAnother()
                                            } else{
                                                ""
                                            }
                                        ))

                                    }
                                    val (orderedList: MutableList<AccountsData>, multiPositionParent) = getOrderedMultiPositionList(
                                        localList
                                    )

                                    accountLists.add(
                                        DataModelForAccountsNew(multiPositionParent?.accountName,multiPositionParent?.accountNumber?.maskString(), (localList.size-1).toString(),
                                            multiPositionParent?.aggregatedBalance, orderedList
                                        )
                                    )
                                }
                            }

                        }
                    }

                    val currentAccountProductName = response.currentAccounts?.name
                    val accounts = response.currentAccounts?.products?.let { list ->
                        if (list.isEmpty()) {
                            null
                        } else {
                            val localList : MutableList<AccountsData> = ArrayList()
                            list.forEach {
                                screenTitle?.text = "Hello, ${it.accountHolderNames}"
                                localList.add(AccountsData(
                                    accountId = it.id,
                                    accountName = it.bankAlias,
                                    accountNumber = it.BBAN,
                                    accountType = it.accountType,
                                    amount = it.bookedBalance?.let { availableblance ->
                                        Amount {
                                            value = availableblance
                                            currency = Currency.getInstance(it.currency)
                                        }
                                    },
                                    bban = it.BBAN,
                                    iban = it.productTypeName,
                                    accruedInterest = if(it.additions?.containsKey("accountOpenDate") == true){
                                       // changeDateFormatFromAnother( it.additions?.getValue("accountOpenDate"))
                                        it.additions?.getValue("accountOpenDate")?.changeDateFormatFromAnother()
                                    } else{
                                        ""
                                    },
                                    rateOfInterest  = if(it.additions?.containsKey("rateOfInterest") == true){
                                        it.additions?.getValue("rateOfInterest")
                                    } else{
                                        ""
                                    }
                                ))

                            }

                            accountLists.add(DataModelForAccountsNew(currentAccountProductName,"",localList.size.toString(), getAggregatedBalance(MultiPositionAccountConstants.balanceCurrentAccount), localList))
                        }
                    }
                    val savingsAccountProductName = response.savingsAccounts?.name
                    response.savingsAccounts?.products?.let { list ->
                        if (list.isEmpty()) {
                            null
                        } else {
                            val localList : MutableList<AccountsData> = ArrayList()

                            list.forEach {
                                localList.add(AccountsData(
                                    accountId = it.id,
                                    accountName = it.bankAlias,
                                    accountNumber = it.BBAN,
                                    accountType = it.accountType,
                                    amount = it.bookedBalance?.let { availableblance ->
                                        Amount {
                                            value = availableblance
                                            currency = Currency.getInstance(it.currency)
                                        }
                                    },
                                    bban = it.BBAN,
                                    iban = it.productTypeName,
                                    accruedInterest = if(it.additions?.containsKey("accountOpenDate") == true){
                                       // changeDateFormatFromAnother( it.additions?.getValue("accountOpenDate"))
                                        it.additions?.getValue("accountOpenDate")?.changeDateFormatFromAnother()
                                    } else{
                                        ""
                                    },
                                    rateOfInterest  = if(it.additions?.containsKey("rateOfInterest") == true){
                                        it.additions?.getValue("rateOfInterest")
                                    } else{
                                        ""
                                    }
                                ))

                            }
                            accountLists.add(DataModelForAccountsNew(savingsAccountProductName, "",localList.size.toString(),getAggregatedBalance(MultiPositionAccountConstants.balanceSavingAccount), localList))
                        }
                    }

                    val loanAccountProductName = response.loans?.name
                    response.loans?.products?.let { list ->
                        if (list.isEmpty()) {
                            null
                        } else {
                            val localList : MutableList<AccountsData> = ArrayList()
                            list.forEach {
                                localList.add(AccountsData(
                                    accountId = it.id,
                                    accountName = it.bankAlias,
                                    accountNumber = it.BBAN,
                                    accountType = it.accountType,
                                    amount = it.bookedBalance?.let { availableblance ->
                                        Amount {
                                            value = availableblance
                                            currency = Currency.getInstance(it.currency)
                                        }
                                    },
                                    bban = it.BBAN,
                                    iban = it.productTypeName,
                                    accruedInterest = if(it.additions?.containsKey("accountOpenDate") == true){
                                        //changeDateFormatFromAnother( it.additions?.getValue("accountOpenDate"))
                                        it.additions?.getValue("accountOpenDate")?.changeDateFormatFromAnother()
                                    } else{
                                        ""
                                    },
                                    rateOfInterest  = if(it.additions?.containsKey("rateOfInterest") == true){
                                        it.additions?.getValue("rateOfInterest")
                                    } else{
                                        ""
                                    },
                                    nextPaymentDate = if(it.additions?.containsKey("nextPaymentDate") == true){
                                        //changeDateFormatFromAnother( it.additions?.getValue("nextPaymentDate"))
                                        it.additions?.getValue("nextPaymentDate")?.changeDateFormatFromAnother()
                                    } else{
                                        ""
                                    },
                                    nextPaymentAmount  = if(it.additions?.containsKey("nextPaymentAmount") == true){
                                        it.additions?.getValue("nextPaymentAmount")
                                    } else{
                                        ""
                                    },
                                    maturityDate = if(it.additions?.containsKey("maturityDate") == true){
                                        //changeDateFormatFromAnother( it.additions?.getValue("maturityDate"))
                                        it.additions?.getValue("maturityDate")?.changeDateFormatFromAnother()
                                    } else{
                                        ""
                                    }
                                ))

                            }
                            accountLists.add(DataModelForAccountsNew(loanAccountProductName, "", localList.size.toString(), getAggregatedBalance(MultiPositionAccountConstants.balanceLoanAccount),  localList))
                        }
                    }

                    if (accounts == null)
                        resumeFailure()
                    else continuation.resume(
                        AccountSummaryResponse {
                            //accountList = concatenate(accounts, savingsAccounts?: listOf(), loanAccounts?: listOf(), productAccounts?: listOf())
                            //accountList = productAccounts ?: listOf()
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

    private fun getOrderedMultiPositionList(productAccounts: List<AccountsData>?): Pair<MutableList<AccountsData>, AccountsData?> {
        val orderedList: MutableList<AccountsData> = ArrayList()
        val multiPositionParent = productAccounts?.firstOrNull() {
            it.iban == MultiPositionAccountConstants.parentAccount
        }

        val multiPositionspend = productAccounts?.firstOrNull() {
            it.iban == MultiPositionAccountConstants.mspendAccount
        }
        if (multiPositionspend != null) {
            orderedList.add(multiPositionspend)
        }

        val multiPositionsave = productAccounts?.firstOrNull() {
            it.iban == MultiPositionAccountConstants.msaveAccount
        }
        if (multiPositionsave != null) {
            orderedList.add(multiPositionsave)
        }

        val multiPositionMortgage = productAccounts?.firstOrNull() {
            it.iban == MultiPositionAccountConstants.mmortgageAccount
        }
        if (multiPositionMortgage != null) {
            orderedList.add(multiPositionMortgage)
        }

        val multiPositionLoanAccount = productAccounts?.firstOrNull() {
            it.iban == MultiPositionAccountConstants.mloanAccount
        }
        if (multiPositionLoanAccount != null) {
            orderedList.add(multiPositionLoanAccount)
        }

        val multiPositionAutoSaveAccount = productAccounts?.firstOrNull() {
            it.iban == MultiPositionAccountConstants.mautosaveAccount
        }
        if (multiPositionAutoSaveAccount != null) {
            orderedList.add(multiPositionAutoSaveAccount)
        }

        val multiPositionCashback = productAccounts?.firstOrNull() {
            it.iban == MultiPositionAccountConstants.mcashbackAccount
        }
        if (multiPositionCashback != null) {
            orderedList.add(multiPositionCashback)
        }

        val multiPositionLoyaltyPoints = productAccounts?.firstOrNull() {
            it.iban == MultiPositionAccountConstants.mloayltypointsAccount
        }
        if (multiPositionLoyaltyPoints != null) {
            orderedList.add(multiPositionLoyaltyPoints)
        }

        val multiPositionRentPocketAccount = productAccounts?.firstOrNull() {
            it.iban == MultiPositionAccountConstants.mrentPocketAccount
        }
        if (multiPositionRentPocketAccount != null) {
            orderedList.add(multiPositionRentPocketAccount)
        }


        val multiPositionCarePocketAccount = productAccounts?.firstOrNull() {
            it.iban == MultiPositionAccountConstants.mcarepocketAccount
        }
        if (multiPositionCarePocketAccount != null) {
            orderedList.add(multiPositionCarePocketAccount)
        }

        val multiPositionLoanPocketAccount = productAccounts?.firstOrNull() {
            it.iban == MultiPositionAccountConstants.mloanpocketAccount
        }
        if (multiPositionLoanPocketAccount != null) {
            orderedList.add(multiPositionLoanPocketAccount)
        }

        val multiPositionMusicPocketAccount = productAccounts?.firstOrNull() {
            it.iban == MultiPositionAccountConstants.mmusicpocketAccount
        }
        if (multiPositionMusicPocketAccount != null) {
            orderedList.add(multiPositionMusicPocketAccount)
        }

        return Pair(orderedList, multiPositionParent)
    }

    fun <T> concatenate(vararg lists: List<T>): List<T> {
        val result: MutableList<T> = ArrayList()
        for (list in lists) {
            result += list
        }
        return result
    }
//    private fun maskString(input: String?): String? {
//        return input?.replace(".(?=.{4})".toRegex(), "\u00B7")
//    }

    private fun CurrentAccount.toAccount() = Account {
        accountId = id ?: ""
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
        iban = productTypeName
    }



    private fun SavingsAccount.toAccount() = Account {
        accountId = id ?: ""
        accountName = bankAlias
        accountNumber = BBAN
        accountType = this@toAccount.accountType
        amount = bookedBalance?.let { availableBalance ->
            Amount {
                value = availableBalance
                currency = Currency.getInstance(this@toAccount.currency)
            }
        }
        bban = BBAN
        iban = productTypeName
    }

    private fun Loan.toAccount() = Account {
        accountId = id ?: ""
        accountName = bankAlias
        accountNumber = BBAN
        accountType = this@toAccount.accountType
        amount = bookedBalance?.let { availableBalance ->
            Amount {
                value = bookedBalance
                currency = Currency.getInstance(this@toAccount.currency)
            }
        }
        bban = BBAN
        iban = productTypeName
    }

    private fun GeneralAccount.toAccount() = Account {
        accountId = id ?: ""
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
        iban = productTypeName
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

    private fun AccountsData.toTransactionsScreenArgs(): Bundle = TransactionsScreen.getArgs(
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

    override fun onCellClickListener(dataModel: AccountsData) {
        findNavController().navigate(
            R.id.action_customAccountsScreen_to_transactionsScreen,
            dataModel.toTransactionsScreenArgs()
        )
    }


    private suspend fun getAggregatedBalance():List<AggregatedBalances>  {
        withContext(Dispatchers.IO){
            val result = balanceClient.getAggregations (
                BalancesApiParams.GetAggregations{
                    productKindIds = listOf(1,2,5)
                    groupedBy = "PRODUCT_KIND"
                }
            )
            result.enqueue(object :
                ResponseListener<List<com.backbase.android.client.gen2.arrangementclient2.model.AggregatedBalances>> {
                override fun onError(errorResponse: Response) {
                     getBlanaceList = emptyList()
                }

                override fun onSuccess(payload: List<com.backbase.android.client.gen2.arrangementclient2.model.AggregatedBalances>) {
                     getBlanaceList = payload
                }

            })

        }
        return getBlanaceList
    }

    private fun getAggregatedBalance( productKindName:String?):String?{
        var balance:String? = null
        if(getBlanaceList.isNotEmpty()){
            val aggredbalance = getBlanaceList.firstOrNull{
                it.productKindName == productKindName
            }
            balance = aggredbalance?.aggregatedBalances?.get(0)?.amount
        }
        return balance
    }
}