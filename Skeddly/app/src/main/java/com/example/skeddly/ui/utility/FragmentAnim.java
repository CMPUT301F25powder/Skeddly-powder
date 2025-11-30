package com.example.skeddly.ui.utility;

import androidx.fragment.app.FragmentTransaction;

/**
 * Houses functions related to animating transitions between fragments.
 */
public class FragmentAnim {
    /**
     * Given a fragment transaction, set the default animations on it.
     * @param transaction The fragment transaction to apply the animations on.
     * @return The transaction with the animations applied.
     */
    public static FragmentTransaction setDefaultAnimations(FragmentTransaction transaction) {
        return transaction.setCustomAnimations(
                androidx.navigation.ui.R.anim.nav_default_enter_anim,
                androidx.navigation.ui.R.anim.nav_default_exit_anim,
                androidx.navigation.ui.R.anim.nav_default_pop_enter_anim,
                androidx.navigation.ui.R.anim.nav_default_pop_exit_anim
        );
    }
}
