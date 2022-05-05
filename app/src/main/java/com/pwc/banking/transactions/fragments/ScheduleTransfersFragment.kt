package com.pwc.banking.transactions.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.backbase.android.client.paymentorderclient2.api.PaymentOrdersApi
import com.backbase.android.client.paymentorderclient2.model.IdentifiedPaymentOrder
import com.backbase.android.client.products.ProductsClient
import com.backbase.android.clients.common.ResponseListener
import com.backbase.android.retail.journey.payments.upcoming.datasource.PaymentOrderViewData
import com.backbase.android.utils.net.response.Response
import com.pwc.banking.R
import com.pwc.banking.StringObjectConstants
import com.pwc.banking.transactions.customapiclient.FetchScheduleTransfers
import com.pwc.banking.transactions.customapiclient.RoundUpClientApi
import com.pwc.banking.transactions.model.RoundUpResponseModal
import com.pwc.banking.transactions.model.ScheduleTransfersResponseModel
import org.koin.android.ext.android.inject
import java.time.LocalDate

class ScheduleTransfersFragment: Fragment() {
    private val scheduleTransferClient: FetchScheduleTransfers by inject()
    private val paymentOrderApiClient: PaymentOrdersApi by inject()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater?.inflate(R.layout.custom_transfers_main_view, container, false)

        return view
    }

    override fun onResume() {
        super.onResume()
       /* lifecycleScope.launchWhenCreated{
            StringObjectConstants.arrangementId?.let { getTransfers(it) }
        }*/
        lifecycleScope.launchWhenCreated{
           val paymentOrders =  paymentOrderApiClient.getPaymentOrders(
                status = listOf("ACCEPTED","READY"),
                executionDate = LocalDate.now(),
                from = 0,
                size = 0,
                orderBy = "requestedExecutionDate",
                direction = "ASC"
            )
            paymentOrders.enqueue(object: ResponseListener<List<IdentifiedPaymentOrder>> {
                override fun onError(errorResponse: Response) {
                    println(errorResponse)
                }

                override fun onSuccess(payload: List<IdentifiedPaymentOrder>) {
                   println(payload)
                }

            })
        }
    }


    private fun getTransfers(id:String){
        scheduleTransferClient?.makeGetCall(id)?.enqueue(object :
            ResponseListener<List<ScheduleTransfersResponseModel>> {
            override fun onSuccess(payload: List<ScheduleTransfersResponseModel>) {
                print(payload)

            }
            override fun onError(errorResponse: Response) {
                print(errorResponse)
            }
        })
    }
}