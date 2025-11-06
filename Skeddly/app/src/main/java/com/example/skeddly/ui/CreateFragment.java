package com.example.skeddly.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import com.example.skeddly.ui.popup.CategorySelectorDialogFragment;
import com.example.skeddly.ui.popup.DatePickerDialogFragment;
import com.example.skeddly.ui.popup.MapPopupDialogFragment;
import com.example.skeddly.ui.popup.TimePickerDialogFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

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
import java.util.Objects;


public class CreateFragment extends Fragment {
    private CreateEditEventViewBinding binding;

    // For launching the image picker built in activity
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM. d, yyyy");

    // Form info for creating the event
    private boolean isRecurring;
    private final ArrayList<String> categories = new ArrayList<>();
    private final ArrayList<String> daysOfWeek = new ArrayList<>();
    private byte[] imageBytes;

    // Scheduling
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private LatLng eventLocation;

    // Popup Selector Constants
    private final String categoryTitle = "Select Category";
    private final String[] catArray = {"Indoor", "Outdoor", "In-person", "Virtual", "Hybrid",
            "Arts & Crafts", "Physical activity"};

    private final String[] dayArray = Arrays.copyOfRange(new DateFormatSymbols().getWeekdays(), 1, 8);
    private final String dayTitle = "Select Day";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreateEditEventViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Variables
        UnderlineSpan underlineSpan = new UnderlineSpan();

        // Hide them because we don't want them here
        binding.buttonBack.setVisibility(View.INVISIBLE);
        binding.buttonQrCode.setVisibility(View.INVISIBLE);

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

        binding.eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        // Setup location picker
        binding.textEventTitleOverlay.setOnClickListener(new View.OnClickListener() {
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
                    binding.textEventTitleOverlay.setText(String.format(Locale.getDefault(), "%.2f, %.2f", eventLocation.latitude, eventLocation.longitude));
                    updateConfirmButton();
                }
            }
        });

        isRecurring = false;
        updateRecurring();
        updateConfirmButton();

        binding.switchRecurrence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                isRecurring = isChecked;
                updateRecurring();
                updateConfirmButton();
            }
        });

        setupSelector(binding.textDayOfWeek, dayTitle, dayArray, daysOfWeek);
        setupSelector(binding.textCategorySelector, categoryTitle, catArray, categories);

        setupDatePicker(binding.textDateStart, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int year = result.getInt("year");
                int monthNum = result.getInt("month");
                int day = result.getInt("day");

                startDate = LocalDate.of(year, monthNum + 1, day);

                SpannableString startDateStr = new SpannableString(startDate.format(dateFormatter));
                startDateStr.setSpan(underlineSpan, 0, startDateStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                binding.textDateStart.setText(startDateStr);
                updateConfirmButton();
            }
        });
        setupDatePicker(binding.textDateFinish, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int year = result.getInt("year");
                int monthNum = result.getInt("month");
                int day = result.getInt("day");

                endDate = LocalDate.of(year, monthNum + 1, day);

                SpannableString endDateStr = new SpannableString(endDate.format(dateFormatter));
                endDateStr.setSpan(underlineSpan, 0, endDateStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                binding.textDateFinish.setText(endDateStr);
                updateConfirmButton();
            }
        });

        setupTimePicker(binding.textTimeStart, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                startTime = LocalTime.of(result.getInt("hourOfDay"), result.getInt("minute"));

                SpannableString startTimeStr = new SpannableString(startTime.format(timeFormatter));
                startTimeStr.setSpan(underlineSpan, 0, startTimeStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                binding.textTimeStart.setText(startTimeStr);
                updateConfirmButton();
            }
        });

        setupTimePicker(binding.textTimeFinish, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                endTime = LocalTime.of(result.getInt("hourOfDay"), result.getInt("minute"));

                SpannableString endTimeStr = new SpannableString(endTime.format(timeFormatter));
                endTimeStr.setSpan(underlineSpan, 0, endTimeStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                binding.textTimeFinish.setText(endTimeStr);
                updateConfirmButton();
            }
        });

        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
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
     * Setup a category selector with the given strings and store the selected strings into the
     * provided ArrayList.
     * @param textView The textview that should have the click listener and have its text changed
     * @param title The title of the popup
     * @param categories The categories that we want in the popup
     */
    private void setupSelector(TextView textView, String title, String[] categories, ArrayList<String> selectedItems) {
        String requestKey = Integer.toString(textView.getId());
        CategorySelectorDialogFragment catSelector = CategorySelectorDialogFragment.newInstance(title, categories, requestKey);

        // Show the selector when pressing the textview
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catSelector.show(getChildFragmentManager(), "categorySelector");
            }
        });

        // Update the text in the textview
        getChildFragmentManager().setFragmentResultListener(requestKey, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                ArrayList<String> selected = result.getStringArrayList("selectedItems");

                selectedItems.clear();
                selectedItems.addAll(selected);
                String selectedString = String.join(", ", selectedItems);

                textView.setText(selectedString);
                updateConfirmButton();
            }
        });
    }

    /**
     * Setup a date picker and call you back when the user has finished picking the date
     * @param view The view that should have the click associated with
     * @param callback The callback function that should be ran
     */
    private void setupTimePicker(View view, FragmentResultListener callback) {
        String requestKey = Integer.toString(view.getId());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialogFragment tpf = TimePickerDialogFragment.newInstance(requestKey);
                tpf.show(getChildFragmentManager(), requestKey);
            }
        });

        getChildFragmentManager().setFragmentResultListener(requestKey, this, callback);
    }

    /**
     * Setup a date picker and call you back when the user has finished picking the date
     * @param view The view that should have the click associated with
     * @param callback The callback function that should be ran
     */
    private void setupDatePicker(View view, FragmentResultListener callback) {
        String requestKey = Integer.toString(view.getId());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment dpf = DatePickerDialogFragment.newInstance(requestKey);
                dpf.show(getChildFragmentManager(), requestKey);
            }
        });

        getChildFragmentManager().setFragmentResultListener(requestKey, this, callback);
    }

    /**
     * Updates which date fields are shown based on if the event is selected to be recurring or not
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
     * Updates whether the confirm button is enabled or not based on if the form is fully filled.
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
     * Validation function that checks whether the entire creation form has been filled in
     * @return True if the form has been fully filled. False otherwise.
     */
    private boolean isFilledIn() {
        if (binding.valueEventTitle.length() <= 0 || binding.valueDescription.length() <= 0) {
            return false;
        }

        // Schedule must be set
        if (startTime == null || endTime == null || startDate == null || (isRecurring && endDate == null)) {
            return false;
        }

        // Start date can't happen after the end date
        if (isRecurring && startDate.isAfter(endDate)) {
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
        if (binding.editAttendeeLimit.length() <= 0) {
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

    /**
     * Create an event object based on the filled in form.
     * Form should be filled in before calling this.
     * @return The event constructed from the form data.
     */
    private Event createEvent() {
        EventDetail eventDetails = new EventDetail(binding.valueEventTitle.getText().toString(),
                binding.valueDescription.getText().toString(), categories);

        LocalDateTime start = LocalDateTime.of(startDate, startTime);
        LocalDate endDate = this.endDate;

        if (!isRecurring) {
            // Starts and ends on the same day
            endDate = startDate;
        }

        LocalDateTime end = LocalDateTime.of(endDate, endTime);

        // Build the boolean array from the selected days
        Boolean[] eventDays = null;
        if (isRecurring) {
            eventDays = new Boolean[7];
            Arrays.fill(eventDays, false);

            for (String day : daysOfWeek) {
                List<String> dayList = Arrays.asList(Arrays.copyOfRange(new DateFormatSymbols().getWeekdays(), 1, 8));
                eventDays[dayList.indexOf(day)] = true;
            }
        }
        EventSchedule eventSchedule = new EventSchedule(start, end, eventDays);

        // Get the list limits
        int attendeeLimit = Integer.parseInt(binding.editAttendeeLimit.getText().toString());
        int waitListLimit = 0;

        if (binding.editWaitlistLimit.length() > 0) {
            waitListLimit = Integer.parseInt(binding.editWaitlistLimit.getText().toString());
        }

        MainActivity mainActivity = (MainActivity) requireActivity();

        return new Event(eventDetails, eventSchedule, eventLocation,
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),
                waitListLimit, attendeeLimit, imageBytes);
    }

    /**
     * Renders the image stored in the byte array as the event image.
     */
    private void updateEventImage() {
        Glide.with(this).load(imageBytes).into(binding.eventImage);
    }
}
