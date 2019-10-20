package ru.sbrf.sberwifi

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.util.*

class FragmentStateHelper(private val fragmentManager: FragmentManager) {

    private val fragmentState = EnumMap<FragmentEnum, Fragment.SavedState?>(FragmentEnum::class.java)

    fun restoreState(fragmentEnum: FragmentEnum, fragment: Fragment) {
        fragmentState[fragmentEnum]?.let { savedState ->
            if (!fragment.isAdded) {
                fragment.setInitialSavedState(savedState)
            }
        }
    }

    fun saveState(fragmentEnum: FragmentEnum, fragment: Fragment) {
        if (fragment.isAdded) {
            fragmentState[fragmentEnum] = fragmentManager.saveFragmentInstanceState(fragment)
        }
    }

    companion object {

        @Volatile
        public var INSTANCE: FragmentStateHelper? = null

        fun getInstance(fragmentManager: FragmentManager) {
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: FragmentStateHelper(fragmentManager).also { INSTANCE = it }
            }
        }
    }
}