package com.example.skeddly.business.user;

/**
 * Interface for the user loaded callback
 */
public interface UserLoaded {
    void onUserLoaded(User user, boolean shouldShowSignupPage);
}
