package uz.juo.hobee.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.juo.hobee.ui.info_viewpager.InfoViewPagerFragment

class MedInfoViewPagerAdapter( fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return InfoViewPagerFragment.newInstance(position.toString())
    }
}