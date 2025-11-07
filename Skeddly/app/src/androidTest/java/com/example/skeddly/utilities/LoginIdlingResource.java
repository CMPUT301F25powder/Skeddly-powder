package com.example.skeddly.utilities;

import androidx.test.espresso.IdlingResource;

import com.example.skeddly.SignupActivity;

public class LoginIdlingResource implements IdlingResource {
    private IdlingResource.ResourceCallback resourceCallback = null;
    private SignupActivity signupActivity;

    public LoginIdlingResource(SignupActivity signupActivity) {
        this.signupActivity = signupActivity;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

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

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }
}
