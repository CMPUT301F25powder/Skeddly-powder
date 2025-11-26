package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.skeddly.R;
import com.example.skeddly.business.user.User;

import java.util.ArrayList;
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

        User user = getItem(position);
        if (user != null) {
            TextView textUserName = convertView.findViewById(R.id.text_user_full_name);
            TextView textUserPhone = convertView.findViewById(R.id.text_user_phone);
            TextView textUserEmail = convertView.findViewById(R.id.text_user_email);
            TextView textUserPrivilege = convertView.findViewById(R.id.text_user_privilege);

            textUserName.setText(user.getPersonalInformation().getName());
            textUserPhone.setText(user.getPersonalInformation().getPhoneNumber());
            textUserEmail.setText(user.getPersonalInformation().getEmail());
            textUserPrivilege.setText(user.getPrivilegeLevel().toString());

            return convertView;

        }
        return convertView;
    }


}
