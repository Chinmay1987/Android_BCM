package com.pwc.banking

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pwc.banking.accounts.model.DataModelForAccounts
import com.pwc.banking.accounts.model.DataModelForAccountsNew

class SharedPreferencesDataUtil(private val context: Context?) {
    private val sharedPrefKeyForAccounts:String = "Accounts_Access_Key"
    private val KeyForAccounts:String = "ACCOUNTS"

    fun set(value: String?) {
        val sharedPreferences =
            context?.getSharedPreferences(sharedPrefKeyForAccounts, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString(KeyForAccounts, value)
        editor?.commit()
    }
     fun getList(): List<DataModelForAccountsNew> {
        var arrayItems: List<DataModelForAccountsNew> = ArrayList()
        val sharedPreferences =
            context?.getSharedPreferences(sharedPrefKeyForAccounts, Context.MODE_PRIVATE)
        val serializedObject =
            sharedPreferences?.getString(KeyForAccounts, null)
        if (serializedObject != null) {
            val gson = Gson()
            val type = object : TypeToken<List<DataModelForAccountsNew?>?>() {}.type
            arrayItems = gson.fromJson(serializedObject, type)
        }
        return arrayItems
    }
}