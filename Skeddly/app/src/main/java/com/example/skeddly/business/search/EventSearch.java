package com.example.skeddly.business.search;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.example.skeddly.business.event.Event;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;

public class EventSearch {
    private SimpleCursorAdapter simpleCursorAdapter;
    private final String EVENT_NAME_SUGGESTION_ID = "eventName";
    private final String[] from = new String[] {EVENT_NAME_SUGGESTION_ID};
    private final int[] to = new int[] {android.R.id.text1};
    private ArrayList<Event> eventList;
    private SearchView searchBar;
    public EventSearch(Context context, SearchView newSearchBar, ArrayList<Event> eventList) {
        this.eventList = eventList;
        this.searchBar = newSearchBar;

        simpleCursorAdapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        searchBar.setSuggestionsAdapter(simpleCursorAdapter);

        searchBar.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) simpleCursorAdapter.getItem(position);
                String txt = cursor.getString(cursor.getColumnIndexOrThrow("eventName"));
                searchBar.setQuery(txt, true);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }
        });
    }

    public void setOnSearchFinishedListener(SearchFinishedListener callback) {
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isBlank()) {
                    callback.onSearchFinished();
                } else {
                    populateAdapter(newText);
                }

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                callback.onSearchFinished(query);
                populateAdapter(query);
                searchBar.clearFocus();

                return true;
            }
        });
    }

    private void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, EVENT_NAME_SUGGESTION_ID });
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);
            String eventName = event.getEventDetails().getName();

            int clampedEndNameIndex = Math.min(query.length(), eventName.length());
            String queryLengthName = eventName.substring(0, clampedEndNameIndex);

            if (checkNameSuggestionMatch(queryLengthName, query))
                c.addRow(new Object[] {i, eventName});
        }

        simpleCursorAdapter.changeCursor(c);
    }

    public boolean checkNameSuggestionMatch(String name, String query) {
        if (query.isBlank()) {
            return true;
        }

        LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();

        int distance = levenshteinDistance.apply(name.toLowerCase(), query.toLowerCase());

        return distance <= 2;
    }
}
