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

import java.util.ArrayList;

public class InboxAdapter extends ArrayAdapter<Notification> {
    private User user;
    Context context;
    DatabaseHandler databaseHandler;
    private int displayMode;
    public InboxAdapter(Context context, User user, ArrayList<Notification> notifs) {
        super(context, 0, notifs);
        this.user = user;
        this.context = context;
        this.databaseHandler = new DatabaseHandler();

        this.setDisplayMode(3);
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

    public void setDisplayMode(int displayMode) {
        this.displayMode = displayMode;
        this.clear();
        if (this.displayMode == 3) {
            this.addAll(user.getNotifications());
        } else {
            for (Notification n : this.user.getNotifications()) {
                if (n.getType() != null && n.getType().ordinal() == this.displayMode) {
                    this.add(n);
                }
            }
        }
    }
    public void setDisplayMode(Notification.notification_type displayMode) {
        this.setDisplayMode(displayMode.ordinal());
    }

}
