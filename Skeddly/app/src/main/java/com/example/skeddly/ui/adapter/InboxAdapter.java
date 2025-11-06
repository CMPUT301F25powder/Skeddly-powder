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
import com.example.skeddly.business.database.SingleListenUpdate;

import java.util.ArrayList;

public class InboxAdapter extends ArrayAdapter<String> {
    private ArrayList<String> inbox;
    Context context;
    DatabaseHandler databaseHandler;
    public InboxAdapter(Context context, ArrayList<String> inbox) {
        super(context, 0, inbox);
        this.inbox = inbox;
        this.context = context;
        this.databaseHandler = new DatabaseHandler(context);
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.notification, parent, false);
        }

        String notifId = inbox.get(position);

        TextView title = view.findViewById(R.id.notification_title);
        TextView description = view.findViewById(R.id.notification_subtitle);

        SingleListenUpdate<Notification> update = (notif) -> {
            title.setText(notif.getTitle());
            description.setText(notif.getMessage());
        };

        this.databaseHandler.singleListen(this.databaseHandler.getNotificationsPath().child(notifId), Notification.class, update);

        return view;
    }

}
