package com.example.skeddly.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.skeddly.business.database.repository.EventRepository;
import com.example.skeddly.business.location.CustomLocation;
import com.example.skeddly.business.location.MapPopupType;
import com.example.skeddly.databinding.FragmentTestBinding;
import com.example.skeddly.ui.popup.MapPopupDialogFragment;
import com.example.skeddly.ui.popup.QRPopupDialogFragment;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * Fragment for the test screen
 */
public class TestFragment extends Fragment {
    private FragmentTestBinding binding;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTestBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView returnText = binding.textReturn;
        ImageView photoPickerImage = binding.imgPickerPhoto;

        // === Generic popup stuff ===
        Button testButton = binding.btnPopup;
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StandardPopupDialogFragment cdf = StandardPopupDialogFragment.newInstance("Title", "Contents", "testDialog");
                cdf.show(getChildFragmentManager(), null);
            }
        });

        getChildFragmentManager().setFragmentResultListener("testDialog", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Boolean b = result.getBoolean("buttonChoice");
                returnText.setText(String.format("Popup returned %s", b));
            }
        });

        // === Time picker button stuff ===
//        Button timePickerButton = binding.timePickerButton;
//        timePickerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                TimePickerDialogFragment tpf = new TimePickerDialogFragment();
//                tpf.show(getChildFragmentManager(), "timePicker");
//            }
//        });
//
//        getChildFragmentManager().setFragmentResultListener("timePicker", this, new FragmentResultListener() {
//            @Override
//            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
//                int hourOfDay = result.getInt("hourOfDay");
//                int minute = result.getInt("minute");
//
//                returnText.setText(String.format("%2d:%2d", hourOfDay, minute));
//            }
//        });

        // === Date Picker Button Stuff ===
//        Button datePickerButton = binding.datePickerButton;
//        datePickerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatePickerDialogFragment dpf = new DatePickerDialogFragment();
//                dpf.show(getChildFragmentManager(), "datePicker");
//            }
//        });
//
//        getChildFragmentManager().setFragmentResultListener("datePicker", this, new FragmentResultListener() {
//            @Override
//            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
//                int year = result.getInt("year");
//                int month = result.getInt("month");
//                int day = result.getInt("day");
//
//                returnText.setText(String.format("%d:%d:%d", year, month, day));
//            }
//        });

        // === Location Picker Stuff ===
        Button locationPickerButton = binding.btnPickerLocation;
        locationPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapPopupDialogFragment lpf = MapPopupDialogFragment.newInstance("locationPicker", MapPopupType.SET, null);
                lpf.show(getChildFragmentManager(), "LocationPicker");
            }
        });

        getChildFragmentManager().setFragmentResultListener("locationPicker", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                LatLng location = result.getParcelable("LatLng");

                if (location != null) {
                    returnText.setText(String.format("Latitude is %f, Longitude is %f", location.latitude, location.longitude));
                }
            }
        });

        // === Location Showing Stuff ===
        ArrayList<CustomLocation> entrantLocations = new ArrayList<>();
        entrantLocations.add(new CustomLocation(-113.41984842775818, 53.62688011398386));
        entrantLocations.add(new CustomLocation(-113.44747769828126, 53.62287224831365));
        entrantLocations.add(new CustomLocation(144.95975087827142, -37.767827475845436));
        FloatingActionButton showLocationsButton = binding.fabShowLocations;
        showLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapPopupDialogFragment lpf = MapPopupDialogFragment.newInstance("locationPicker", MapPopupType.VIEW, entrantLocations);
                lpf.show(getChildFragmentManager(), "LocationPicker");
            }
        });

        // === Photo Picker Stuff ===
        // Registers a photo picker activity launcher in single-select mode.
        this.pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        Button photoPickerButton = binding.btnPickerPhoto;
        photoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        photoPickerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        // === QR Code stuff ===
        Button qrButton = binding.btnQrDialog;
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRPopupDialogFragment qpf = QRPopupDialogFragment.newInstance("https://github.com/CMPUT301F25powder/Skeddly-powder");
                qpf.show(getChildFragmentManager(), null);
            }
        });

        // === DB Random stuff ===
        Button dbButton = binding.btnDb;
        EventRepository eventRepository = new EventRepository(FirebaseFirestore.getInstance());
        dbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                eventRepository.getAll().addOnCompleteListener(new OnCompleteListener<List<Event>>() {
//                    @Override
//                    public void onComplete(@NonNull Task<List<Event>> task) {
//                        Log.v("TEST_DB_BTN_GET_ALL", "Callback received!");
//
//                        if (!task.isSuccessful()) {
//                            Log.v("TEST_DB_BTN_GET_ALL", "Task failed!");
//                        } else {
//                            for (Event event : task.getResult()) {
//                                Log.v("TEST_DB_BTN_GET_ALL", event.getId());
//                            }
//                            Log.v("TEST_DB_BTN_GET_ALL", "Printed all IDs!");
//                        }
//
//
//                    }
//                });

//                eventRepository.listenAll(new SingleListenUpdate<List<Event>>() {
//                    @Override
//                    public void onUpdate(List<Event> newValue) {
//                        Log.v("TEST_DB_BTN_LISTEN_ALL", "Callback received!");
//
//                        for (Event event : newValue) {
//                            Log.v("TEST_DB_BTN_LISTEN_ALL", event.getId());
//                        }
//                    }
//                });


//                eventRepository.count().addOnCompleteListener(new OnCompleteListener<Long>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Long> task) {
//                        Log.v("TEST_DB_BTN_COUNT", "Callback received!");
//
//                        if (task.isSuccessful()) {
//                            Log.v("TEST_DB_BTN_COUNT", Long.toString(task.getResult()));
//                        } else {
//                            Log.v("TEST_DB_BTN_COUNT", "Task failed!");
//                        }
//                    }
//                });
//
//                eventRepository.listenById("69", new SingleListenUpdate<Event>() {
//                    @Override
//                    public void onUpdate(Event newValue) {
//                        Log.v("TEST_DB_BTN_LISTEN", "Callback received!");
//
//                        if (newValue == null) {
//                            Log.v("TEST_DB_BTN_LISTEN", "Got null!");
//                        } else {
//                            Log.v("TEST_DB_BTN_LISTEN", newValue.getId());
//                        }
//                    }
//                });

//                eventRepository.getById("67").addOnCompleteListener(new OnCompleteListener<Event>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Event> task) {
//                        Log.v("TEST_DB_BTN_GET", "Callback received!");
//
//                        if (!task.isSuccessful()) {
//                            Log.v("TEST_DB_BTN_GET", "Task failed!");
//                        } else {
//                            Log.v("TEST_DB_BTN_GET", task.getResult().getId());
//                        }
//                    }
//                });

//                Task<Void> asd = eventRepository.delete("67");
//
//                asd.addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.v("TEST_DB_BTN_DELETE", "Deleting task completed!");
//
//                        if (task.isSuccessful()) {
//                            Log.v("TEST_DB_BTN_DELETE", "Deleting task was successful!");
//                        } else if (task.isCanceled()) {
//                            Log.v("TEST_DB_BTN_DELETE", "Deleting task was cancelled!");
//                        } else {
//                            Log.v("TEST_DB_BTN_DELETE", "Deleting task was not successful!");
//                        }
//                    }
//                });
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
