package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.skeddly.R;
import com.example.skeddly.business.database.repository.NotificationRepository;
import com.example.skeddly.business.notification.Notification;
import com.example.skeddly.business.notification.NotificationType;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.ItemAdminUserBinding;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class UserAdapter extends ArrayAdapter<User> {
    public interface OnMessageButtonClickListener {
        void onMessageButtonClick(String recipientId, String recipientName);
    }
    private OnMessageButtonClickListener messageButtonClickListener;

    public UserAdapter(Context context, List<User> users, OnMessageButtonClickListener listener) {
        super(context, 0, users);
        this.messageButtonClickListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_user, parent, false);
        }

        ItemAdminUserBinding itemAdminUserBinding = ItemAdminUserBinding.bind(convertView);

        User user = getItem(position);
        if (user != null) {
            TextView textUserName = itemAdminUserBinding.textUserFullName;
            TextView textUserPhone = itemAdminUserBinding.textUserPhone;
            TextView textUserEmail = itemAdminUserBinding.textUserEmail;
            TextView textUserPrivilege = itemAdminUserBinding.textUserPrivilege;
            Button messageButton = itemAdminUserBinding.buttonMessageUser;


            String userName = user.getPersonalInformation().getName();
            textUserName.setText(user.getPersonalInformation().getName());
            textUserPhone.setText(user.getPersonalInformation().getPhoneNumber());
            textUserEmail.setText(user.getPersonalInformation().getEmail());
            textUserPrivilege.setText(user.getPrivilegeLevel().toString());

            messageButton.setOnClickListener(v -> {
                if (messageButtonClickListener != null) {
                    messageButtonClickListener.onMessageButtonClick(user.getId(), userName);
                }
            });
        }

        return convertView;
    }

}
