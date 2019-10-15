package ru.sbrf.sberwifi

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentStateHelper(private val fragmentManager: FragmentManager) {

    private val fragmentState = mutableMapOf<String, Fragment.SavedState?>()

    fun restoreState(fragment: Fragment, idFragment: String) {
        fragmentState[idFragment]?.let { savedState ->
            if (!fragment.isAdded) {
                fragment.setInitialSavedState(savedState)
            }
        }
    }

    fun saveState(fragment: Fragment, idFragment: String) {
        if (fragment.isAdded) {
            fragmentState[idFragment] = fragmentManager.saveFragmentInstanceState(fragment)
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