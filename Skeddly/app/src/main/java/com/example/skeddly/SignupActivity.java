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
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.user.Authenticator;
import com.example.skeddly.business.user.PersonalInformation;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLoaded;
import com.example.skeddly.databinding.ActivitySignupBinding;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Signup activity for the application.
 */
public class SignupActivity extends AppCompatActivity {
    @SuppressWarnings({"ConstantValue", "MismatchedStringCase"})
    private final boolean useFirebaseEmulator = BuildConfig.FLAVOR.equals("emulateFirestore");
    private String firebaseEmulatorAddress = BuildConfig.EMULATOR_ADDRESS;

    private ActivitySignupBinding binding;
    private EditText fullNameEditText;
    private EditText emailEditText;
    private Button submitButton;
    private boolean loaded = false;

    private Uri qrOpenUri;

    private Authenticator authenticator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // Inflate the layout
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
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

        fullNameEditText = binding.textSignUpFullName;
        emailEditText = binding.textSignUpEmail;
        EditText phoneNumberEditText = binding.textSignUpPhoneNumber;

        submitButton = binding.btnAccountCreate;
        toggleSubmitButton();

        // See if we were opened by a QR code or special link
        qrOpenUri = getLaunchLink();

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

                authenticator.getUser().setPersonalInformation(newUserInformation);
                authenticator.commitUserChanges();

                switchToMain();
            }
        });

        // See if emulator is in use
        if (useFirebaseEmulator && firebaseEmulatorAddress == null) {
            // Use firebase emulator is set but address wasn't provided
            StandardPopupDialogFragment spdf = StandardPopupDialogFragment.newInstance(
                    getString(R.string.dialog_firebase_emu_title),
                    getString(R.string.dialog_firebase_emu_contents),
                    "firebaseEmulator",true);
            spdf.show(getSupportFragmentManager(), null);

            getSupportFragmentManager().setFragmentResultListener("firebaseEmulator", this, (requestKey, result) -> {
                if (!result.getBoolean("buttonChoice")) {
                    finish();
                }

                firebaseEmulatorAddress = result.getString("typedText");

                if (firebaseEmulatorAddress == null || firebaseEmulatorAddress.length() < 7) {
                    firebaseEmulatorAddress = "10.0.2.2";
                }

                setupFirebaseEmulator();
                loadUser();
            });
        } else {
            // Setup emulator with provided address if needed
            if (useFirebaseEmulator) {
                setupFirebaseEmulator();
            }

            loadUser();
        }
    }

    /**
     * Toggles the submit button based on the text fields.
     */
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

    /**
     * Switches to the main activity, providing the necessary information in the intent.
     */
    private void switchToMain() {
        Intent mainActivity = new Intent(getBaseContext(), MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainActivity.putExtra("QR", qrOpenUri);
        startActivity(mainActivity);
        finish();

        loaded = true;

    }

    /**
     * Gets the launch link if there is one. The launch link is usually provided from scanning
     * a QR code to open the app straight to an event.
     * @return The launch link, or null if there isn't one.
     */
    @Nullable
    private Uri getLaunchLink() {
        Intent intent = getIntent();
        return intent.getData();
    }

    public boolean getLoaded() {
        return this.loaded;
    }

    private void setupFirebaseEmulator() {
        // Authentication Emulator
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.useEmulator(firebaseEmulatorAddress, 9099);

        // Firestore Emulator
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.useEmulator(firebaseEmulatorAddress, 8080);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                .build();
        firestore.setFirestoreSettings(settings);

        // Functions Emulator
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        functions.useEmulator(firebaseEmulatorAddress, 5001);

        // Storage Emulator
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.useEmulator(firebaseEmulatorAddress, 9199);
    }

    private void loadUser() {
        DatabaseHandler database = new DatabaseHandler();
        authenticator = new Authenticator(this, database);
        authenticator.addListenerForUserLoaded(new UserLoaded() {
            @Override
            public void onUserLoaded(User loadedUser, boolean shouldShowSignup) {
                if (!shouldShowSignup) {
                    switchToMain();
                } else {
                    binding.signUpPage.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
