package com.example.skeddly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.user.Authenticator;
import com.example.skeddly.business.user.PersonalInformation;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLoaded;
import com.example.skeddly.databinding.SignUpPageBinding;


public class SignupActivity extends CustomActivity {
    private SignUpPageBinding binding;
    private User user;
    private EditText fullNameEditText;
    private EditText emailEditText;
    private Button submitButton;

    private Uri qrOpenUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // Inflate the layout
        binding = SignUpPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ConstraintLayout mainLayout = binding.signUpPage;
        mainLayout.setVisibility(View.GONE);

        // Don't go off the screen
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Dont need bottom padding since nav bar takes care of it
            v.setPadding(33, systemBars.top, 33, systemBars.bottom);
            return insets;
        });

        fullNameEditText = binding.fullNameSignUpText;
        emailEditText = binding.emailSignUpText;
        EditText phoneNumberEditText = binding.phoneNumberSignUpText;

        submitButton = binding.createAccountButton;
        toggleSubmitButton();

        // See if we were opened by a QR code or special link
        qrOpenUri = getLaunchLink();

        DatabaseHandler database = new DatabaseHandler(this);
        Authenticator authenticator = new Authenticator(this, database);
        authenticator.addListenerForUserLoaded(new UserLoaded() {
            @Override
            public void onUserLoaded(User loadedUser, boolean shouldShowSignup) {
                user = loadedUser;

                if (!shouldShowSignup) {
                    switchToMain();
                } else {
                    mainLayout.setVisibility(View.VISIBLE);
                }

                // Listen for any changes to the user itself
                database.singleListen(database.getUsersPath().child(user.getId()), User.class, new SingleListenUpdate<User>() {
                    @Override
                    public void onUpdate(User newValue) {
                        setUser(newValue);
                    }
                });
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                toggleSubmitButton();
            }
        };

        fullNameEditText.addTextChangedListener(textWatcher);
        emailEditText.addTextChangedListener(textWatcher);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalInformation newUserInformation = new PersonalInformation();

                newUserInformation.setName(String.valueOf(fullNameEditText.getText()));
                newUserInformation.setEmail(String.valueOf(emailEditText.getText()));
                newUserInformation.setPhoneNumber(String.valueOf(phoneNumberEditText.getText()));

                user.setPersonalInformation(newUserInformation);

                authenticator.commitUserChanges();

                switchToMain();
            }
        });
    }

    private void toggleSubmitButton() {
        boolean fullNameFilled = fullNameEditText.getText().length() > 0;
        boolean emailFilled = emailEditText.getText().length() > 0;

        submitButton.setEnabled(fullNameFilled && emailFilled);

        if (fullNameFilled && emailFilled) {
            submitButton.setAlpha(1f);
        } else {
            submitButton.setAlpha(.5f);
        }
    }

    private void switchToMain() {
        Intent mainActivity = new Intent(getBaseContext(), MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainActivity.putExtra("USER", user);
        mainActivity.putExtra("QR", qrOpenUri);
        startActivity(mainActivity);
        finish();
    }

    @Nullable
    private Uri getLaunchLink() {
        Intent intent = getIntent();
        return intent.getData();
    }
}
