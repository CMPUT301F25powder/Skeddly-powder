package com.example.skeddly.business.database.repository.adapter;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.database.repository.GenericRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * This classes uses the given repositories to supply data to an ArrayAdapter.
 * If multiple repos are provided, you can switch between which is shown in the ArrayAdapter.
 * @param <T> The type of object that this adapter shall manage.
 */
public class RepositoryToArrayAdapter<T extends DatabaseObject> {
    private final ArrayList<GenericRepository<T>> repositories;
    private final ArrayList<List<T>> data;
    private final ArrayAdapter<T> adapter;
    private final ArrayList<ListenerRegistration> listeners;
    private int curDataset;
    private final boolean realtimeUpdates;

    /**
     * Create a new RepositoryToArrayAdapter with multiple repositories.
     * Real time updates are an option via the boolean.
     * <p>
     * Warning: If using the real time updates, the caller must ensure to remove the real time
     * listener via the removeListener() method once updates are no longer required. This can be
     * done inside the onDestroyView() function for example.
     * @param repositories The repositories that shall be queried for data
     * @param adapter The adapter to add data to
     * @param realtimeUpdates Whether we should use real time updates for data.
     */
    public RepositoryToArrayAdapter(List<GenericRepository<T>> repositories, ArrayAdapter<T> adapter, boolean realtimeUpdates) {
        this.repositories = new ArrayList<>();
        this.data = new ArrayList<>();
        this.adapter = adapter;
        this.listeners = new ArrayList<>();
        this.curDataset = 0;
        this.realtimeUpdates = realtimeUpdates;

        setupListener(repositories);
    }

    /**
     * Create a new RepositoryToArrayAdapter with a single repository.
     * Real time updates are an option via the boolean.
     * <p>
     * Warning: If using the real time updates, the caller must ensure to remove the real time
     * listener via the removeListener() method once updates are no longer required. This can be
     * done inside the onDestroyView() function for example.
     * @param repository The repository that shall be queried for data
     * @param adapter The adapter to add data to
     * @param realtimeUpdates Whether we should use real time updates for data.
     */
    public RepositoryToArrayAdapter(GenericRepository<T> repository, ArrayAdapter<T> adapter, boolean realtimeUpdates) {
        this(List.of(repository), adapter, realtimeUpdates);
    }

    /**
     * Sets up the array adapter with the data by querying the repository.
     */
    private void setupListener(List<GenericRepository<T>> repositories) {
        for (GenericRepository<T> repository : repositories) {
            setupSingleListener(repository);
        }
    }

    /**
     * Sets up a single repository with this adapter.
     * @param repository The repository to set up with the adapter.
     */
    private void setupSingleListener(GenericRepository<T> repository) {
        int dataArrIdx = repositories.size();
        data.add(null);
        repositories.add(repository);

        if (realtimeUpdates) {
            listeners.add(repository.listenAll(new SingleListenUpdate<List<T>>() {
                @Override
                public void onUpdate(List<T> newValue) {
                    data.set(dataArrIdx, newValue);
                    loadDataToAdapter(dataArrIdx);
                }
            }));
        } else {
            repository.getAll().addOnCompleteListener(new OnCompleteListener<List<T>>() {
                @Override
                public void onComplete(@NonNull Task<List<T>> task) {
                    if (task.isSuccessful()) {
                        data.set(dataArrIdx, task.getResult());
                        loadDataToAdapter(dataArrIdx);
                    }
                }
            });
        }
    }

    /**
     * Given an index of a repository, load the data that it has to the ArrayAdapter if it is the
     * current one. Otherwise, do nothing.
     * @param idxChanged The index of the repository.
     */
    private void loadDataToAdapter(int idxChanged) {
        if (idxChanged != curDataset || data.get(curDataset) == null) {
            return;
        }

        adapter.clear();
        adapter.addAll(data.get(curDataset));
        adapter.notifyDataSetChanged();
    }

    /**
     * Switch to a new dataset given by the repository. If the repository does not exist in the
     * ones managed by this adapter, an exception is thrown.
     * @throws IllegalArgumentException If the repository passed is not managed by this adapter.
     * @param repository The repository who we should switch to showing in the array adapter
     */
    public void switchDataset(GenericRepository<T> repository) throws IllegalArgumentException {
        int index = repositories.indexOf(repository);

        if (index == -1) {
            throw new IllegalArgumentException();
        }

        curDataset = index;
        loadDataToAdapter(curDataset);
    }

    /**
     * Removes the real time listener if one is present. If one is not present, or the listener
     * has already been removed, nothing happens.
     */
    public void removeListener() {
        for (ListenerRegistration listenerRegistration : listeners) {
            listenerRegistration.remove();
        }
        listeners.clear();
    }
}
