package com.pwc.banking.transactions.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.pwc.banking.R
import com.pwc.banking.transactions.listeners.ManagedFeatureClickListener
import com.pwc.banking.transactions.model.AdditionsModal

class RecyclerAdapterForManagedFeatures(
private val context: Context,
private val values: List<AdditionsModal>,
private val clickListener: ManagedFeatureClickListener
) : RecyclerView.Adapter<RecyclerAdapterForManagedFeatures.ViewHolder>() {

    var managedFeaturesList : MutableList<AdditionsModal> = ArrayList()

    init {
        managedFeaturesList = values as MutableList<AdditionsModal>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_accountdetails_listofswitch, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = managedFeaturesList[position]
        holder.switchView.text = item.displayName
        holder.switchView.isChecked = item.status
        holder.switchView.setOnCheckedChangeListener { buttonView, isChecked ->
            // do something, the isChecked will be
            // true if the switch is in the On position
            clickListener.onChangeCheckedStatus(item, isChecked)
        }
    }

    override fun getItemCount(): Int = managedFeaturesList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var switchView: SwitchCompat = view.findViewById(R.id.switch_main)
        val root: ConstraintLayout = view.findViewById(R.id.tranactiondetails_managedfeatures_id)
    }



}