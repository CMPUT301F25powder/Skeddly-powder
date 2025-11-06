package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skeddly.R;
import com.example.skeddly.business.Notification;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.user.User;

public class InboxAdapter extends ArrayAdapter<Notification> {
    private User user;
    Context context;
    DatabaseHandler databaseHandler;
    public InboxAdapter(Context context, User user) {
        super(context, 0);
        this.user = user;
        this.context = context;
        this.databaseHandler = new DatabaseHandler();

        this.addAll(user.getNotifications());
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.notification, parent, false);
        }

        Notification notif = this.getItem(position);

        TextView title = view.findViewById(R.id.notification_title);
        TextView description = view.findViewById(R.id.notification_subtitle);

        title.setText(notif.getTitle());
        description.setText(notif.getMessage());

        return view;
    }

}
