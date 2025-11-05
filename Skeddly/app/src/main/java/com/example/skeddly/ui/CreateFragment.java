package com.example.skeddly.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.bumptech.glide.Glide;
import com.example.skeddly.MainActivity;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.event.EventDetail;
import com.example.skeddly.business.event.EventSchedule;
import com.example.skeddly.databinding.CreateEditEventViewBinding;
import com.example.skeddly.ui.popup.DatePickerDialogFragment;
import com.example.skeddly.ui.popup.MapPopupDialogFragment;
import com.example.skeddly.ui.popup.TimePickerDialogFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StyleSpan;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class CreateFragment extends Fragment {
    private CreateEditEventViewBinding binding;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM. d, yyyy");

    private final ArrayList<String> categories = new ArrayList<>();

    private final ArrayList<String> daysOfWeek = new ArrayList<>();
    private boolean isRecurring;

    private byte[] imageBytes;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private LatLng eventLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreateEditEventViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Variables
        ImageButton buttonBack = binding.buttonBack;
        ImageButton buttonQrCode = binding.buttonQrCode;

        ImageView eventImage = binding.eventImage;

        TextView textEventTitleOverlay = binding.textEventTitleOverlay;

        MaterialSwitch switchRecurrence = binding.switchRecurrence;

        UnderlineSpan underlineSpan = new UnderlineSpan();

        TextView textDateStart = binding.textDateStart;
        TextView textDateFinish = binding.textDateFinish;

        TextView textDayOfWeek = binding.textDayOfWeek;
        String[] dayArray = Arrays.copyOfRange(new DateFormatSymbols().getWeekdays(), 1, 8);
        String dayTitle = "Select Day";

        TextView textTimeStart = binding.textTimeStart;
        TextView textTimeFinish = binding.textTimeFinish;

        TextView textCategorySelector = binding.textCategorySelector;
        String[] catArray = {"Indoor", "Outdoor", "In-person", "Virtual", "Hybrid", "Arts & Crafts",
                "Physical activity", "???????????", "??????????", "???????????", "??????????"};
        String categoryTitle = "Select Category";

        Button buttonConfirm = binding.confirmButton;

        // Hide them because we don't want them here
        buttonBack.setVisibility(View.INVISIBLE);
        buttonQrCode.setVisibility(View.INVISIBLE);

        // Registers a photo picker activity launcher in single-select mode.
        this.pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    imageBytes = stream.toByteArray();
                    bitmap.recycle();

                    updateEventImage();
                    updateConfirmButton();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        // Setup location picker
        textEventTitleOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapPopupDialogFragment lpf = MapPopupDialogFragment.newInstance("locationPicker");
                lpf.show(getChildFragmentManager(), "LocationPicker");
            }
        });

        // Handle result
        getChildFragmentManager().setFragmentResultListener("locationPicker", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                eventLocation = result.getParcelable("LatLng");

                if (eventLocation != null) {
                    textEventTitleOverlay.setText(String.format(Locale.getDefault(), "%.2f, %.2f", eventLocation.latitude, eventLocation.longitude));
                    updateConfirmButton();
                }
            }
        });

        isRecurring = false;
        updateRecurring();
        updateConfirmButton();

        switchRecurrence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                isRecurring = isChecked;
                updateRecurring();
                updateConfirmButton();
            }
        });

        setupSelector(textDayOfWeek, dayArray, dayTitle, daysOfWeek);
        setupSelector(textCategorySelector, catArray, categoryTitle, categories);

        setupDatePicker(textDateStart, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int year = result.getInt("year");
                int monthNum = result.getInt("month");
                int day = result.getInt("day");

                startDate = LocalDate.of(year, monthNum + 1, day);

                SpannableString startDateStr = new SpannableString(startDate.format(dateFormatter));
                startDateStr.setSpan(underlineSpan, 0, startDateStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                textDateStart.setText(startDateStr);
                updateConfirmButton();
            }
        });
        setupDatePicker(textDateFinish, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int year = result.getInt("year");
                int monthNum = result.getInt("month");
                int day = result.getInt("day");

                endDate = LocalDate.of(year, monthNum + 1, day);

                SpannableString endDateStr = new SpannableString(endDate.format(dateFormatter));
                endDateStr.setSpan(underlineSpan, 0, endDateStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                textDateFinish.setText(endDateStr);
                updateConfirmButton();
            }
        });

        setupTimePicker(textTimeStart, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                startTime = LocalTime.of(result.getInt("hourOfDay"), result.getInt("minute"));

                SpannableString startTimeStr = new SpannableString(startTime.format(timeFormatter));
                startTimeStr.setSpan(underlineSpan, 0, startTimeStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                textTimeStart.setText(startTimeStr);
                updateConfirmButton();
            }
        });

        setupTimePicker(textTimeFinish, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                endTime = LocalTime.of(result.getInt("hourOfDay"), result.getInt("minute"));

                SpannableString endTimeStr = new SpannableString(endTime.format(timeFormatter));
                endTimeStr.setSpan(underlineSpan, 0, endTimeStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                textTimeFinish.setText(endTimeStr);
                updateConfirmButton();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event event = createEvent();
                Toast.makeText(requireContext(), "Event created!", Toast.LENGTH_SHORT).show();

                // Put event in db
                DatabaseHandler dbHandler = new DatabaseHandler(requireContext());
                dbHandler.getEventsPath().child(event.getId()).setValue(event);

                // User owns the event
                MainActivity mainActivity = (MainActivity) requireActivity();
                mainActivity.getUser().addOwnedEvent(event);
                mainActivity.notifyUserChanged();
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateConfirmButton();
            }
        };

        binding.valueEventTitle.addTextChangedListener(textWatcher);
        binding.valueDescription.addTextChangedListener(textWatcher);
        binding.editAttendeeLimit.addTextChangedListener(textWatcher);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     *
     * @param textSelector
     * @param array
     */
    private void setupSelector(TextView textSelector, String[] array, String title, ArrayList<String> selectedItems) {
        boolean[] selected = new boolean[array.length];

        textSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                // Set title
                builder.setTitle(title);

                // Set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(array, selected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {}
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedItems.clear();

                        for (int i = 0; i < selected.length; i++) {
                            if (selected[i]) {
                                selectedItems.add(array[i]);
                            }
                        }

                        String result = String.join(", ", selectedItems);

                        textSelector.setText(result);
                        updateConfirmButton();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < selected.length; i++) {
                            // Remove all selection
                            selected[i] = false;

                            textSelector.setText("");
                        }
                    }
                });

                builder.show();
            }
        });
    }

    /**
     *
     * @param textView
     */
    private void setupTimePicker(TextView textView, FragmentResultListener callback) {
        String requestKey = Integer.toString(textView.getId());

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialogFragment tpf = TimePickerDialogFragment.newInstance(requestKey);
                tpf.show(getChildFragmentManager(), requestKey);
            }
        });

        getChildFragmentManager().setFragmentResultListener(requestKey, this, callback);
    }

    /**
     *
     * @param textView
     * @param callback
     */
    private void setupDatePicker(TextView textView, FragmentResultListener callback) {
        String requestKey = Integer.toString(textView.getId());

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment dpf = DatePickerDialogFragment.newInstance(requestKey);
                dpf.show(getChildFragmentManager(), requestKey);
            }
        });

        getChildFragmentManager().setFragmentResultListener(requestKey, this, callback);
    }

    /**
     *
     */
    private void updateRecurring() {
        if (isRecurring) {
            binding.textDateDash.setVisibility(View.VISIBLE);
            binding.textDateFinish.setVisibility(View.VISIBLE);
            binding.textDayOfWeek.setVisibility(View.VISIBLE);
        } else {
            binding.textDateDash.setVisibility(View.INVISIBLE);
            binding.textDateFinish.setVisibility(View.INVISIBLE);
            binding.textDayOfWeek.setVisibility(View.GONE);
        }
    }

    /**
     *
     */
    private void updateConfirmButton() {
        Button confirmButton = binding.confirmButton;


        if (!isFilledIn()) {
            confirmButton.setAlpha(.5f);
            confirmButton.setEnabled(false);
        } else {
            confirmButton.setAlpha(1);
            confirmButton.setEnabled(true);
        }
    }

    /**
     *
     * @return
     */
    private boolean isFilledIn() {
        // Event Title and Description check
        EditText eventTitle = binding.valueEventTitle;
        EditText eventDescription = binding.valueDescription;

        if (eventTitle.length() <= 0 || eventDescription.length() <= 0) {
            return false;
        }

        // Schedule must be set
        if (startTime == null || endTime == null || startDate == null || (isRecurring && endDate == null)) {
            return false;
        }

        // Start can't happen after the end
        if (startTime.isAfter(endTime) || (isRecurring && startDate.isAfter(endDate))) {
            return false;
        }

        // Event can't be scheduled in the past
        LocalDateTime start = LocalDateTime.of(startDate, startTime);
        if (start.isBefore(LocalDateTime.now())) {
            return false;
        }

        // If it's recurring, we need at least one day of the week
        if (isRecurring && daysOfWeek.isEmpty()) {
            return false;
        }

        // Attendee Limit
        EditText attendeeLimit = binding.editAttendeeLimit;

        if (attendeeLimit.length() <= 0) {
            return false;
        }

        // Location
        if (eventLocation == null) {
            return false;
        }

        // Needs an image
        if (imageBytes == null) {
            return false;
        }

        return true;
    }

    private Event createEvent() {
        EditText eventTitle = binding.valueEventTitle;
        EditText eventDescription = binding.valueDescription;
        EventDetail eventDetails = new EventDetail(eventTitle.getText().toString(), eventDescription.getText().toString(), categories);


        LocalDateTime start = LocalDateTime.of(startDate, startTime);
        LocalDate endDate = this.endDate;

        if (!isRecurring) {
            endDate = startDate;
        }

        LocalDateTime end = LocalDateTime.of(endDate, endTime);

        Boolean[] eventDays = null;
        if (isRecurring) {
            eventDays = new Boolean[7];

            for (String day : daysOfWeek) {
                List<String> dayList = Arrays.asList(Arrays.copyOfRange(new DateFormatSymbols().getWeekdays(), 1, 8));
                eventDays[dayList.indexOf(day)] = true;
            }
        }
        EventSchedule eventSchedule = new EventSchedule(start, end, eventDays);

        // Get the list limits
        EditText editAttendeeLimit = binding.editAttendeeLimit;
        EditText editWaitlistLimit = binding.editWaitlistLimit;
        int attendeeLimit = Integer.parseInt(editAttendeeLimit.getText().toString());
        int waitListLimit = 0;

        if (editWaitlistLimit.length() > 0) {
            waitListLimit = Integer.parseInt(editWaitlistLimit.getText().toString());
        }

        MainActivity mainActivity = (MainActivity) requireActivity();

        return new Event(eventDetails, eventSchedule, eventLocation,
                mainActivity.getUser().getId(), waitListLimit, attendeeLimit, imageBytes);
    }

    private void updateEventImage() {
        Glide.with(this).load(imageBytes).into(binding.eventImage);
    }
}
