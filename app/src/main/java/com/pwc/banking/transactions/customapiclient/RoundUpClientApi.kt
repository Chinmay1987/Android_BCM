package com.pwc.banking.transactions.customapiclient

import android.content.Context
import com.backbase.android.Backbase
import com.backbase.android.clients.common.Call
import com.backbase.android.clients.common.ResponseBodyParser
import com.backbase.android.common.utils.dbs.CustomNetworkDBSProvider
import com.backbase.android.common.utils.dbs.DBSDataImpl
import com.backbase.android.dbs.DBSClient
import com.backbase.android.dbs.DBSDataProvider
import com.backbase.android.utils.net.request.RequestMethods
import com.pwc.banking.transactions.model.RoundUpRequestModal
import com.pwc.banking.transactions.model.RoundUpResponseModal
import com.squareup.moshi.Moshi
import java.net.URI

class RoundUpClientApi(context: Context,
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

    fun makePOSTCall(requestBody: RoundUpRequestModal): Call<RoundUpResponseModal> {
        val bodyType = RoundUpRequestModal::class.java
        val serializedBody: String? =
            moshi.adapter(bodyType).toJson(
                requestBody
            )
        val headers: Map<String, String> = mapOf(
            "Content-Type" to "application/json"
        )
        val queryParams = mutableMapOf<String, List<String>>()
        ///val path = "client-api/v1/preference-status"
        val path = "csp-dis-account-features-service/client-api/v1/feature-status"

        val request = com.backbase.android.clients.common.buildRequest(
            RequestMethods.POST,
            serverUri,
            path,
            queryParams,
            backbase
        )
        request.body = serializedBody
        request.headers = headers
        return Call(request, provider, parser, RoundUpResponseModal::class.java)
    }
}

