package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skeddly.R;
import com.example.skeddly.business.database.repository.UserRepository;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLevel;
import com.example.skeddly.databinding.ItemAdminUserBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(Context context, List<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_user, parent, false);
        }

        ItemAdminUserBinding itemAdminUserBinding = ItemAdminUserBinding.bind(convertView);
        Spinner privilegeSpinner = convertView.findViewById(R.id.privilege_spinner);

        User user = getItem(position);
        if (user != null) {
            TextView textUserName = itemAdminUserBinding.textUserFullName;
            TextView textUserPhone = itemAdminUserBinding.textUserPhone;
            TextView textUserEmail = itemAdminUserBinding.textUserEmail;

            textUserName.setText(user.getPersonalInformation().getName());
            textUserPhone.setText(user.getPersonalInformation().getPhoneNumber());
            textUserEmail.setText(user.getPersonalInformation().getEmail());


            // Set up the spinner adapter
            ArrayAdapter<UserLevel> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, UserLevel.values());
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            privilegeSpinner.setAdapter(spinnerAdapter);

            // Find the correct position for the user's current privilege level.
            int currentPosition = Arrays.asList(UserLevel.values()).indexOf(user.getPrivilegeLevel());

            privilegeSpinner.setSelection(currentPosition, false);

            // Set a listener to handle selection changes from user interaction.
            privilegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                    UserLevel newLevel = (UserLevel) parent.getItemAtPosition(spinnerPosition);

                    // Only update if the level has actually changed from the user's original level
                    if (user.getPrivilegeLevel() != newLevel) {
                        user.setPrivilegeLevel(newLevel);
                        UserRepository userRepository = new UserRepository(FirebaseFirestore.getInstance());
                        userRepository.set(user)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Updated " + user.getPersonalInformation().getName(), Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        return convertView;
    }


}
