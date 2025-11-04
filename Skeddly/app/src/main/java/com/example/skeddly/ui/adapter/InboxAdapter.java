package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.skeddly.business.Notification;

import java.util.ArrayList;

public class InboxAdapter extends ArrayAdapter<Notification> {
    public InboxAdapter(Context context, ArrayList<Notification> notifList) {
        super(context, 0, notifList);
    }

}
