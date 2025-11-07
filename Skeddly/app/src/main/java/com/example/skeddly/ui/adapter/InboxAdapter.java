package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skeddly.R;
import com.example.skeddly.business.Notification;
import com.example.skeddly.business.user.User;

import java.util.ArrayList;
import java.util.List;

public class InboxAdapter extends ArrayAdapter<Notification> implements Filterable {
    private List<Notification> originalNotifications;
    private List<Notification> filteredNotifications;
    private NotificationFilter filter;
    private Context context;

    public InboxAdapter(Context context, ArrayList<Notification> notifs) {
        super(context, 0, notifs);
        this.context = context;
        this.originalNotifications = new ArrayList<>(notifs);
        this.filteredNotifications = new ArrayList<>(notifs);
    }
    public void removeNotification(Notification notification) {
        if (notification == null) {
            return;
        }

        // Remove the notification from the filtered list (the one being displayed)
        filteredNotifications.remove(notification);

        // IMPORTANT: Also remove it from the original, unfiltered list
        originalNotifications.remove(notification);

        // Tell the ListView to refresh itself
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredNotifications.size();
    }

    @Override
    public Notification getItem(int position) {
        return filteredNotifications.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.notification, parent, false);
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

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new NotificationFilter();
        }
        return filter;
    }

    private class NotificationFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Notification> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0 || "3".contentEquals(constraint)) {
                // No filter or "show all" filter applied
                filteredList.addAll(originalNotifications);
            } else {
                try {
                    int displayMode = Integer.parseInt(constraint.toString());
                    for (Notification n : originalNotifications) {
                        if (n.getType() != null && n.getType().ordinal() == displayMode) {
                            filteredList.add(n);
                        }
                    }
                } catch (NumberFormatException e) {
                    // Handle case where constraint is not a number, if necessary
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredNotifications.clear();
            if (results.values != null) {
                filteredNotifications.addAll((List<Notification>) results.values);
            }
            notifyDataSetChanged();
        }
    }
}