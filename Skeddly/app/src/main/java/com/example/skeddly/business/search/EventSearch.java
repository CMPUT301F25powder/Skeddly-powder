package com.example.skeddly.business.search;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.example.skeddly.R;
import com.example.skeddly.business.event.Event;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;

public class EventSearch {
    // Constants
    private final String EVENT_NAME_SUGGESTION_ID = "eventName"; // The key for the event name metadata is used in suggestions (you probably won't need to change this)
    private final double STRING_COMPARE_MINIMUM = 0.1f; // How "picky" you want the search results to be, higher is more picky. DEFAULT: 0.1f
    private final int MAX_SUGGESTIONS = 7; // The maximum number of suggestions that can appear.
    private final String[] from = new String[] {EVENT_NAME_SUGGESTION_ID};
    private final int[] to = new int[] {android.R.id.text1};
    // Internal
    private SimpleCursorAdapter simpleCursorAdapter;
    private SimpleCursorAdapter filterCursorAdapter;
    private ArrayList<Event> eventList;
    private SearchView searchBar;
    public EventSearch(Context context, SearchView newSearchBar, ArrayList<Event> eventList) {
        this.eventList = eventList;
        this.searchBar = newSearchBar;

        final MatrixCursor matrixCursor = new MatrixCursor(new String[]{ BaseColumns._ID, "filter" });
        matrixCursor.addRow(new Object[] {0, "filterr"});

        filterCursorAdapter = new SimpleCursorAdapter(context, R.layout.fragment_event_filter_menu, null, new String [] {"filter"}, to, CursorAdapter.NO_SELECTION);
        searchBar.setSuggestionsAdapter(filterCursorAdapter);
        filterCursorAdapter.changeCursor(matrixCursor);

        // Set up adapter for suggestions
        simpleCursorAdapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        searchBar.setSuggestionsAdapter(simpleCursorAdapter);
        searchBar.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                // Query a suggestion when clicked in the dropdown
                Cursor cursor = (Cursor) simpleCursorAdapter.getItem(position);
                String txt = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_NAME_SUGGESTION_ID));
                searchBar.setQuery(txt, true);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }
        });
    }

    /**
     * Allows for external objects to listen for when a search query is submitted.
     * @param callback
     */
    public void setOnSearchFinishedListener(SearchFinishedListener callback) {
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle different query scenarios
                if (newText.isBlank()) {
                    callback.onSearchFinished();
                } else {
                    populateAdapter(newText);
                }

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle when query is submitted (user presses enter, clicks suggestion, etc.)
                callback.onSearchFinished(query);
                populateAdapter(query);
                searchBar.clearFocus();

                return true;
            }
        });
    }

    /**
     * Implements custom logic for determining what suggestions to show.
     * @param query Current search string that is used to populate the suggestions dropdown
     */
    private void populateAdapter(String query) {
        final MatrixCursor matrixCursor = new MatrixCursor(new String[]{ BaseColumns._ID, EVENT_NAME_SUGGESTION_ID });

        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);
            String eventName = event.getEventDetails().getName();

            // If name matches, add it to the suggestions row UI
            if (matrixCursor.getCount() <= MAX_SUGGESTIONS && checkNameSuggestionMatch(eventName, query))
                matrixCursor.addRow(new Object[] {i, eventName});
        }

        simpleCursorAdapter.changeCursor(matrixCursor);
    }

    /**
     * Determin if query and an event name are a close enough match using {@link LevenshteinDistance}.
     * @param name The name of the event.
     * @param query The query to compare.
     * @return True if they are a close enough match, false otherwise.
     */
    public boolean checkNameSuggestionMatch(String name, String query) {
        // Blank queries should show everything
        if (query.isBlank()) {
            return true;
        }

        LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();

        double distance = levenshteinDistance.apply(name.toLowerCase(), query.toLowerCase());

        // Normalize the comparison so string length doesn't matter.
        // https://www.cse.lehigh.edu/%7Elopresti/Publications/1996/sdair96.pdf - Equation 6
        double score = 1.0f / Math.exp(distance / (Math.max(name.length(), query.length()) - distance));

        return score > STRING_COMPARE_MINIMUM;
    }
}
