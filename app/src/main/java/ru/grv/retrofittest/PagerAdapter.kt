package ru.grv.retrofittest

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class PagerAdapter(fm: FragmentManager, extras: Bundle) : FragmentPagerAdapter(fm) {
    private val bundle = extras
    var speakerSelect: String? = null
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                ReportFragment().apply {
                    if (bundle != null) {
                        arguments = bundle
                    }
                }
            }
            else -> {
                return SpeakerFragment().apply {
                    if (bundle != null) {
                        arguments = bundle
                    }
                }
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Доклад"
            else -> {
                return "Докладчик"
            }
        }
    }
}