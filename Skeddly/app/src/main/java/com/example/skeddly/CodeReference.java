
// Purpose: The fragment code for the logs screen
// Rationale: Instructions say to navigate to a list of emoticon events so I figured a log screen would be the simplest to see that outside of the daily summary
// Outstanding Issues?: Limits on customization, I can't change the font like other things so I would need to research that further
//                      additionally using the recycling thing like the one lab or project 0 (i forget) would be good for a real app with lots of entries

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skeddly.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LogsFragment extends Fragment {

        private EmotionLogEntries logEntriesHere;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
            List<String> displayList = new ArrayList<>();
            View logsLayoutRoot = inflater.inflate(R.layout.fragment_log, container, false);
            ListView listViewNow = logsLayoutRoot.findViewById(R.id.listview_logs);

            if (getActivity() instanceof MainActivity){
                logEntriesHere = ((MainActivity) getActivity()).getLogEntries();
            }

            if (logEntriesHere != null) {
                SimpleDateFormat dateCurrentVar = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());

                for (EmotionLog log : logEntriesHere.getAllLogEntries()) {
                    String entry = log.getEmotionName() + " - " + dateCurrentVar.format(log.getTimeStamp());
                    displayList.add(entry);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(), android.R.layout.simple_list_item_1, displayList);

            listViewNow.setAdapter(adapter);

            return logsLayoutRoot;
        }
    }

package com.example.jlcarlet_EmotiLog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.jlcarlet_EmotiLog.R;

// Purpose: The fragment code for the home screen
// Rationale: I kept this labelled as home instead of something like logger so I could have an easier time comparing changes to make for one of the files
//            cause i was using what android gave me for the 3 menu options but renamed them all except home and i like it being named home now
// Outstanding Issues?: the chunk of code for each emotion looks like id be made fun of for

    public class HomeFragment extends Fragment {

        private EmotionLogEntries logEntriesHere;
        private Toast currentToast;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
            View homeLayoutRoot = inflater.inflate(R.layout.fragment_home, container, false);

            if (getActivity() instanceof MainActivity){
                logEntriesHere = ((MainActivity) getActivity()).getLogEntries();
            }

            homeLayoutRoot.findViewById(R.id.button_happy).setOnClickListener(v -> logEmotion("Happy"));
            homeLayoutRoot.findViewById(R.id.button_sad).setOnClickListener(v -> logEmotion("Sad"));
            homeLayoutRoot.findViewById(R.id.button_excited).setOnClickListener(v -> logEmotion("Excited"));
            homeLayoutRoot.findViewById(R.id.button_mad).setOnClickListener(v -> logEmotion("Mad"));
            homeLayoutRoot.findViewById(R.id.button_nervous).setOnClickListener(v -> logEmotion("Nervous"));
            homeLayoutRoot.findViewById(R.id.button_scared).setOnClickListener(v -> logEmotion("Scared"));
            homeLayoutRoot.findViewById(R.id.button_neutral).setOnClickListener(v -> logEmotion("Neutral"));

            return homeLayoutRoot;
        }
        private void logEmotion(String emotionName) {
            logEntriesHere.addLogEntry(emotionName);
            if (currentToast != null) {
                currentToast.cancel();
            }
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, null);
            TextView toastText = layout.findViewById(R.id.toast_text);
            toastText.setText(emotionName + " logged");
            currentToast = new Toast(getContext());
            currentToast.setDuration(Toast.LENGTH_SHORT);
            currentToast.setView(layout);
            currentToast.show();
            // customized toast Code assisted by ChatGPT (OpenAI), 2025-09-29
        }
    }

    public class DailySummaryFragment extends Fragment {

        private EmotionLogEntries logEntriesHere;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View dsViewRoot = inflater.inflate(R.layout.fragment_daily_summary, container, false);

            if (getActivity() instanceof MainActivity) {
                logEntriesHere = ((MainActivity) getActivity()).getLogEntries();
            }

            String[] emotions = {"Happy", "Sad", "Excited", "Mad", "Nervous", "Scared", "Neutral"};
            String summaryText = "";

            if (logEntriesHere != null) {
                int totalLogs = logEntriesHere.getAllLogEntries().size();
                for (String emotion : emotions) {
                    int count = logEntriesHere.countEmotionBLANK(emotion);
                    double percent = ((double) count / totalLogs) * 100;
                    summaryText = summaryText + emotion + ": " + count + " (" + String.format("%.1f", percent) + "%)\n";
                }
            }
            TextView textSummary = dsViewRoot.findViewById(R.id.text_daily_summary);
            textSummary.setText(summaryText);

            return dsViewRoot;
        }
    }
}
