package com.example.skeddly.business.database.repository;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

/**
 * This classes uses a given repository to supply data to an ArrayAdapter.
 * @param <T> The type of object that this adapter shall manage.
 */
public class RepositoryToArrayAdapter<T extends DatabaseObject> {
    private final GenericRepository<T> repository;
    private final ArrayAdapter<T> adapter;
    private ListenerRegistration listener;

    /**
     * Create a new RepositoryToArrayAdapter. Real time updates are an option via the boolean.
     * <p>
     * Warning: If using the real time updates, the caller must ensure to remove the real time
     * listener via the removeListener() method once updates are no longer required. This can be
     * done inside the onDestroyView() function for example.
     * @param repository The repository that shall be queried for data
     * @param adapter The adapter to add data to
     * @param realtimeUpdates Whether we should use real time updates for data.
     */
    public RepositoryToArrayAdapter(GenericRepository<T> repository, ArrayAdapter<T> adapter, boolean realtimeUpdates) {
        this.repository = repository;
        this.adapter = adapter;
        this.listener = null;

        setupListener(realtimeUpdates);
    }

    /**
     * Sets up the array adapter with the data by querying the repository.
     * @param realtimeUpdates Whether real time updates should be used.
     */
    private void setupListener(boolean realtimeUpdates) {
        adapter.clear();

        if (realtimeUpdates) {
            listener = repository.listenAll(new SingleListenUpdate<List<T>>() {
                @Override
                public void onUpdate(List<T> newValue) {
                    adapter.clear();
                    adapter.addAll(newValue);
                    adapter.notifyDataSetChanged();
                }
            });
        } else {
            repository.getAll().addOnCompleteListener(new OnCompleteListener<List<T>>() {
                @Override
                public void onComplete(@NonNull Task<List<T>> task) {
                    if (task.isSuccessful()) {
                        adapter.addAll(task.getResult());
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /**
     * Removes the real time listener if one is present. If one is not present, or the listener
     * has already been removed, nothing happens.
     */
    public void removeListener() {
        if (listener != null) {
            listener.remove();
            listener = null;
        }

    }
}
