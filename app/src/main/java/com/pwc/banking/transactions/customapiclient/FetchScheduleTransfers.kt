package com.pwc.banking.transactions.customapiclient

import android.content.Context
import com.backbase.android.Backbase
import com.backbase.android.client.paymentorderclient2.model.IdentifiedPaymentOrder
import com.backbase.android.clients.common.Call
import com.backbase.android.clients.common.ResponseBodyParser
import com.backbase.android.common.utils.dbs.CustomNetworkDBSProvider
import com.backbase.android.common.utils.dbs.DBSDataImpl
import com.backbase.android.dbs.DBSClient
import com.backbase.android.dbs.DBSDataProvider
import com.backbase.android.retail.journey.payments.upcoming.datasource.PaymentOrderViewData
import com.backbase.android.utils.net.request.RequestMethods
import com.pwc.banking.transactions.model.RoundUpRequestModal
import com.pwc.banking.transactions.model.RoundUpResponseModal
import com.pwc.banking.transactions.model.ScheduleTransfersResponseModel
import com.squareup.moshi.Moshi
import java.net.URI
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class FetchScheduleTransfers(context: Context,
                             val moshi: Moshi,
                             val parser: ResponseBodyParser,
                             var serverUri: URI,
                             var provider: DBSDataProvider = CustomNetworkDBSProvider(context),
                             var backbase: Backbase = requireNotNull(Backbase.getInstance()) { "The Backbase instance must not be null!" }
) : DBSClient, DBSDataImpl() {
    override fun setBaseURI(uri: URI) {
        this.serverUri = uri
    }

    override fun setDataProvider(dataProvider: DBSDataProvider?) {
        this.provider = requireNotNull(dataProvider) { "The provider must not be null!" }
    }

    override fun getBaseURI(): URI? {
        return serverUri
    }

    override fun getDataProvider(): DBSDataProvider? {
        return provider
    }

    fun makeGetCall(arrangmentId:String): Call<List<ScheduleTransfersResponseModel>> {
        val headers: Map<String, String> = mapOf(
            "Content-Type" to "application/json"
        )
        val queryParams = mutableMapOf<String, List<String>>()
        val path = "payment-order-service/client-api/v2/payment-orders?status=ACCEPTED&status=READY&executionDateFrom=2022-04-25&from=0&size=10&orderBy=requestedExecutionDate&direction=ASC&originatorArrangementId=a6b19d70-5141-4271-bb8a-eb37b6ec6e17"
        val request = com.backbase.android.clients.common.buildRequest(
            RequestMethods.GET,
            serverUri,
            path,
            queryParams,
            backbase
        )

        return Call(request, provider, parser, ScheduleTransfersResponseModel::class.java)
    }
}