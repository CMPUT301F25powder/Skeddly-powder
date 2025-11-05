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

import java.util.ArrayList;

public class InboxAdapter extends ArrayAdapter<Notification> {
    private ArrayList<Notification> inbox;
    Context context;
    public InboxAdapter(Context context, ArrayList<Notification> inbox) {
        super(context, 0, inbox);
        this.inbox = inbox;
        this.context = context;
        Notification testNotif = new Notification();
        testNotif.setTitle("Test!");
        testNotif.setMessage("This is a fake notification.");
        this.inbox.add(testNotif);
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.notification, parent, false);
        }

        Notification notif = inbox.get(position);
        TextView title = view.findViewById(R.id.notification_title);
        TextView description = view.findViewById(R.id.notification_subtitle);

        title.setText(notif.getTitle());
        description.setText(notif.getMessage());

        return view;
    }

}
