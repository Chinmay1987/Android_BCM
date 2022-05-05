package com.pwc.banking.transactions.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pwc.banking.transactions.fragments.DetailsFragment
import com.pwc.banking.transactions.fragments.ScheduleTransfersFragment
import com.pwc.banking.transactions.fragments.TransactionsFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> {
                TransactionsFragment()
            }
            1 -> {
                DetailsFragment()
            }
            2 -> {
                ScheduleTransfersFragment()
            }
            else -> {
                TransactionsFragment()
            }
        }

    }

    override fun getItemCount(): Int {
        return 3
    }


}