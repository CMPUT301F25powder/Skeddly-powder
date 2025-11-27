package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.MainActivity;
import com.example.skeddly.R;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.database.repository.EventRepository;
import com.example.skeddly.business.database.repository.TicketRepository;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.FragmentEventHistoryBinding;
import com.example.skeddly.ui.adapter.EventHistoryAdapter; // Import the new adapter
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator; // Import Comparator
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

    private void fetchUserEventHistory(String userId) {
        TicketRepository ticketRepository = new TicketRepository(FirebaseFirestore.getInstance(), userId, null);
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

                // Sort the list of events in descending order based on their ticket's timestamp.
                eventHistoryList.sort(new Comparator<Event>() {
                    @Override
                    public int compare(Event e1, Event e2) {
                        Ticket ticket1 = ticketMap.get(e1.getId());
                        Ticket ticket2 = ticketMap.get(e2.getId());

                        return Long.compare(ticket2.getTicketTime(), ticket1.getTicketTime());
                    }
                });

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
