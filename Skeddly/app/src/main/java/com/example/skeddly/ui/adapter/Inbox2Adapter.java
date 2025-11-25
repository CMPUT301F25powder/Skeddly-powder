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
import com.example.skeddly.business.notification.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * InboxAdapter is an ArrayAdapter of notifications that shows the inbox.
 */
public class Inbox2Adapter extends ArrayAdapter<Notification> {

    /**
     * Constructor for the InboxAdapter.
     * @param context The context of the app.
     * @param notifs The notifications that the adapter should show.
     */
    public Inbox2Adapter(Context context, List<Notification> notifs) {
        super(context, 0, notifs);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_notification, parent, false);
        }

        Notification notif = getItem(position);

        TextView title = view.findViewById(R.id.notification_title);
        TextView description = view.findViewById(R.id.notification_subtitle);

        if (notif != null) {
            title.setText(notif.getTitle());
            description.setText(notif.getMessage());
        }

        return view;
    }
}
