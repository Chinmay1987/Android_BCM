package com.pwc.banking.accounts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pwc.banking.R

class ImageViewPagerAdapter : RecyclerView.Adapter<ImageViewPagerAdapter.ViewHolder>() {

    private val imagesList = arrayListOf(
        R.drawable.savetoborrow_one, R.drawable.save_toborrow_banner,R.drawable.crypto_banner
    )

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
            .from(parent.context)
            .inflate(R.layout.account_summary_viewpage_image_item,parent,false))
    }

    override fun getItemCount(): Int = imagesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.findViewById<AppCompatImageView>(R.id.imageView).let {
            it.setImageDrawable(ContextCompat.getDrawable(it.context,imagesList[position]))
        }
    }
}