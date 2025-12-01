package com.example.skeddly.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.MainActivity;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.database.repository.EventRepository;
import com.example.skeddly.business.database.repository.TicketRepository;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.FragmentEventHistoryBinding;
import com.example.skeddly.ui.adapter.EventHistoryAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Fragment for the event history screen
 */
public class EventHistoryFragment extends Fragment {
    private FragmentEventHistoryBinding binding;
    private EventHistoryAdapter eventHistoryAdapter;
    private ArrayList<Event> eventHistoryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEventHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        User currentUser = ((MainActivity) requireActivity()).getUser();

        eventHistoryList = new ArrayList<>();
        // Adapter initialization will be done after data is fetched
        fetchUserEventHistory(currentUser.getId());

        return root;
    }

    /**
     * Fetches the user's event history from the database and populates the event history list
     * @param userId The ID of the user to fetch the history for
     */
    private void fetchUserEventHistory(String userId) {
        TicketRepository ticketRepository = new TicketRepository(FirebaseFirestore.getInstance(), null, userId);
        EventRepository eventRepository = new EventRepository(FirebaseFirestore.getInstance());

        ticketRepository.getAll().addOnSuccessListener(tickets -> {
            if (tickets == null || tickets.isEmpty()) {
                return;
            }

            // Create a map of EventID -> Ticket for easy lookup
            Map<String, Ticket> ticketMap = tickets.stream()
                    .collect(Collectors.toMap(Ticket::getEventId, Function.identity(), (t1, t2) -> t1));

            List<Task<Event>> eventFetchTasks = new ArrayList<>();
            for (Ticket ticket : tickets) {
                if (ticket.getEventId() != null && !ticket.getEventId().isEmpty()) {
                    eventFetchTasks.add(eventRepository.get(ticket.getEventId()));
                }
            }

            Tasks.whenAllSuccess(eventFetchTasks).addOnSuccessListener(results -> {
                eventHistoryList.clear();
                for (Object result : results) {
                    if (result instanceof Event) {
                        eventHistoryList.add((Event) result);
                    }
                }

                eventHistoryAdapter = new EventHistoryAdapter(getContext(), eventHistoryList, ticketMap);
                binding.listEvents.setAdapter(eventHistoryAdapter);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
