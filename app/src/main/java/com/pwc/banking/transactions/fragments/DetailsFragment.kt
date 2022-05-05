package com.pwc.banking.transactions.fragments

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.TooltipCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.backbase.android.client.products.ProductsClient
import com.backbase.android.client.products.dto.accounts.ArrangementItem
import com.backbase.android.client.products.listener.ArrangementListener
import com.backbase.android.clients.common.*
import com.backbase.android.utils.net.response.Response
import com.google.gson.GsonBuilder
import com.pwc.banking.*
import com.pwc.banking.R
import com.pwc.banking.transactions.adapters.RecyclerAdapterForManagedFeatures
import com.pwc.banking.transactions.customapiclient.RoundUpClientApi
import com.pwc.banking.transactions.listeners.ManagedFeatureClickListener
import com.pwc.banking.transactions.model.AdditionsModal
import com.pwc.banking.transactions.model.ManagedFeaturesEnum
import com.pwc.banking.transactions.model.RoundUpRequestModal
import com.pwc.banking.transactions.model.RoundUpResponseModal
import org.json.JSONException
import org.koin.android.ext.android.inject
import kotlin.coroutines.suspendCoroutine


class DetailsFragment : Fragment(), ManagedFeatureClickListener {
    private val productsClient: ProductsClient by inject()
    private val roundupClient: RoundUpClientApi by inject()
    var accountType: TextView? = null
    var accountBalance: TextView? = null
    var accountStatus: TextView? = null
    var monthlyRoundupAmount: TextView? = null
    var monthlyRoundupGroup: Group? = null
    var accountOpeningDate: TextView? = null
    var mLoader:ProgressBar? = null

    var monthlyInstallmentAmount: TextView? = null
    var principalAmount: TextView? = null
    var monthlyInstallmentLabel: TextView? = null
    var principallabel: TextView? = null
    var interestRate: TextView? = null
    var interestRateLabel: TextView? = null

    var term: TextView? = null

    var loanOrInterstDetailsTitle:TextView?=null
    var termLable:TextView? = null
    var statusBadge:AppCompatImageView? = null

    var rootLayout:ConstraintLayout?= null
    var mEmptyView:TextView?= null
    var accountBalanceLabel:TextView?= null
    var manageAccountsCardId:CardView? = null

    val pattern = "/\\s/g".toRegex()
    var modal:List<AdditionsModal> = ArrayList()
    private lateinit var adapters: RecyclerAdapterForManagedFeatures
    private var managedFeaturesRecyclerView:RecyclerView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.custom_accountdetails_details_view, container, false)

        mLoader = view.findViewById(R.id.accountsdetails_loader)
        rootLayout = view.findViewById(R.id.account_details_item_root)
        mEmptyView = view.findViewById(R.id.empty_view)

        manageAccountsCardId = view.findViewById(R.id.manage_accounts_cardid)
        managedFeaturesRecyclerView = view.findViewById(R.id.managed_features_list)


        accountType = view.findViewById(R.id.account_type_value)
        accountBalance = view.findViewById(R.id.account_balance_value)
        accountBalanceLabel = view.findViewById(R.id.account_balance_label)
        accountStatus = view.findViewById(R.id.account_status_value)
        monthlyRoundupAmount = view.findViewById(R.id.account_monthlyroundup_value)
        monthlyRoundupGroup = view.findViewById(R.id.monthlyroundup_additions)


        monthlyInstallmentLabel = view.findViewById(R.id.monthlyinstallment_label)
        principallabel = view.findViewById(R.id.principalamount_label)
        monthlyInstallmentAmount = view.findViewById(R.id.monthlyinstallment_value)
        principalAmount = view.findViewById(R.id.principalamount_value)
        interestRate = view.findViewById(R.id.interestrate__value)
        interestRateLabel = view.findViewById(R.id.interestrate_label)
        term = view.findViewById(R.id.accuredinsterest__value)

        loanOrInterstDetailsTitle = view.findViewById(R.id.loandetails_title)
        termLable = view.findViewById(R.id.accruedinterest_label)
        statusBadge = view.findViewById(R.id.status_badge)

        return view
    }

    private fun updateUi(
        arrangementItem: ArrangementItem?
    ) {
        if(arrangementItem?.productKindName != null){
            accountType?.text = arrangementItem?.productTypeName
            accountBalance?.text = StringConstantUtils.dollor+" "+ arrangementItem?.bookedBalance?.let {
                roundup(
                    it.toDouble())
            }
            accountStatus?.text = arrangementItem?.state?.state
            accountOpeningDate?.text = arrangementItem?.accountOpeningDate?.changeDateFormatFromAnother()
            manageAccountsCardId?.visibility = View.GONE
            monthlyRoundupGroup?.visibility = View.GONE

            interestRate?.text = arrangementItem?.accountInterestRate.toString()+StringConstantUtils.percentage
            statusBadge?.let { TooltipCompat.setTooltipText(it, "This is a dynamic interest rate based on the aggregate balance of your deposit accounts") }

            if(arrangementItem?.productTypeName == StringConstantUtils.managedFeaturesProductTypeNameLoan){
                monthlyInstallmentAmount?.text = StringConstantUtils.dollor+arrangementItem?.monthlyInstalmentAmount.toString()
                principalAmount?.text = StringConstantUtils.dollor+arrangementItem?.principalAmount?.let {
                    roundup(
                        it.toDouble()
                    )
                }

                term?.text = arrangementItem?.termNumber.toString()+" month(s)"
                accountBalanceLabel?.text = StringConstantUtils.outstandingAmount
                manageAccountsCardId?.visibility = View.GONE
                monthlyRoundupGroup?.visibility = View.GONE


            }else{
                val setKey: Map<String, String>? = arrangementItem?.additions
                setKey?.forEach {
                    if(it.key == StringConstantUtils.additionsKeyFeatures){
                       // val additions = arrangementItem?.additions?.values
                        val additions = it.value
                        StringObjectConstants.arrangementNumber = arrangementItem?.number
                        if(additions != null){
                            modal = try {
                                val data = additions.replace("\"", "").replace(" ","")
                                //val modifieddata = data.substring(1, data.length - 1).replace(" ", "")
                                val gson = GsonBuilder().create()
                                gson.fromJson(data,Array<AdditionsModal>::class.java).toList()

                            } catch (err: JSONException) {
                                emptyList()
                            }
                            if(modal.isNotEmpty()){
                                manageAccountsCardId?.visibility = View.VISIBLE
                                with(managedFeaturesRecyclerView){
//                            this?.addItemDecoration(
//                                DividerItemDecoration(
//                                    this?.context,
//                                    DividerItemDecoration.VERTICAL
//                                )
//                            )
                                    this?.addItemDecorationWithoutLastDivider(DividerItemDecoration.VERTICAL)
                                    this?.addItemDecorationWithoutLastDivider(DividerItemDecoration.HORIZONTAL)
                                    adapters = context?.let { RecyclerAdapterForManagedFeatures(it, modal, this@DetailsFragment) }!!

                                    this?.adapter = adapters
                                }


                            }
                        }
                    }
                    else if(it.key == StringConstantUtils.additionsKeyMonthlyRoundup){
                        monthlyRoundupGroup?.visibility = View.VISIBLE
                        monthlyRoundupAmount?.text = "$ ${it.value}"
                    }
                }


                monthlyInstallmentAmount?.visibility = View.GONE
                principalAmount?.visibility = View.GONE
                monthlyInstallmentLabel?.visibility = View.GONE
                principallabel?.visibility = View.GONE


                term?.text = arrangementItem?.accruedInterest?.let {
                    roundup(
                        it.toDouble()
                    )
                }+StringConstantUtils.percentage
                termLable?.text = StringConstantUtils.accruedInterest
                loanOrInterstDetailsTitle?.text = StringConstantUtils.interestDetails


            }

        }
    }

    override fun onPause() {
        super.onPause()
    }

    private fun roundup(value: Double):String{
        return String.format("%.2f", value)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenCreated{
            mLoader?.visibility = View.VISIBLE
            rootLayout?.visibility = View.GONE
            StringObjectConstants.arrangementId?.let { getArrangements(it) }
        }
    }

    private suspend fun getArrangements(accountid : String): ArrangementItem {
        val response = suspendCoroutine<ArrangementItem> { continuation ->
            val listener = object : ArrangementListener {
                override fun onSuccess(responseSuccess: ArrangementItem) {
                    mLoader?.visibility = View.GONE
                    rootLayout?.visibility = View.VISIBLE
                    updateUi(responseSuccess)
                }

                override fun onError(p0: Response) {
                    mLoader?.visibility = View.GONE
                    mEmptyView?.visibility = View.VISIBLE
                }
            }
            productsClient.getArrangement(accountid, listener)
        }
        return response
    }

    override fun onChangeCheckedStatus(dataModel: AdditionsModal, checkedStatus:Boolean) {
        val requestBody = RoundUpRequestModal(
            arrangementNumber = StringObjectConstants.arrangementNumber,
            derivativeType = dataModel.getDisplayName,
            status = checkedStatus,
            positionNumber = dataModel.targetPositionNumber
        )
        roundupClient?.makePOSTCall(requestBody)?.enqueue(object : ResponseListener<RoundUpResponseModal>{
            override fun onSuccess(payload: RoundUpResponseModal) {
                print(payload)
                Toast.makeText(context, " Successfully updated the feature", Toast.LENGTH_LONG).show()
            }
            override fun onError(errorResponse: Response) {
                print(errorResponse)
            }
        })
    }

    private val AdditionsModal.getDisplayName: String
        get() = when (displayName) {
            "CashBack" -> ManagedFeaturesEnum.CASH_BACK.shortName
            "AutoSave" -> ManagedFeaturesEnum.AUTO_SAVE.shortName
            "Loyalty" -> ManagedFeaturesEnum.LOYALTY.shortName
            "CarPocket" -> ManagedFeaturesEnum.CAR_POCKET.shortName
            "MortgagePocket" -> ManagedFeaturesEnum.MORTGAGE_POCKET.shortName
            "RentPocket" -> ManagedFeaturesEnum.RENT_POCKET.shortName

            else -> throw IllegalArgumentException("$displayName is not supported.")
        }
}