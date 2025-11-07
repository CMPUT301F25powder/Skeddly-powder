package com.example.skeddly.utilities;

import androidx.test.espresso.IdlingResource;

import com.example.skeddly.SignupActivity;

/**
 * A {@link IdlingResource} that waits for {@link SignupActivity} to complete.
 * Used to wait for {@link com.google.firebase.Firebase} components to load.
 */
public class LoginIdlingResource implements IdlingResource {
    private IdlingResource.ResourceCallback resourceCallback = null;
    private SignupActivity signupActivity;

    public LoginIdlingResource(SignupActivity signupActivity) {
        this.signupActivity = signupActivity;
    }

    /**
     * The name of the class.
     * @return String
     */
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    /**
     * Used when the {@link IdlingResource} is polled to check whether or not {@link SignupActivity} is done loading.
     * @return boolean
     */
    @Override
    public boolean isIdleNow() {
        if (this.signupActivity.getLoaded()) {
            if (this.resourceCallback != null) {
                this.resourceCallback.onTransitionToIdle();
            }

            return true;
        }

        return false;
    }

    /**
     * For handling callbacks when the {@link LoginIdlingResource} transitions.
     * @param callback The {@link androidx.test.espresso.IdlingResource.ResourceCallback} used to handle {@link ResourceCallback#onTransitionToIdle()}.
     */
    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }
}
