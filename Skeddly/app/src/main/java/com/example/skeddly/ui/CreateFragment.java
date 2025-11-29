package com.example.skeddly.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.skeddly.MainActivity;
import com.example.skeddly.R;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.repository.EventRepository;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.event.EventDetail;
import com.example.skeddly.business.event.EventSchedule;
import com.example.skeddly.business.location.MapPopupType;
import com.example.skeddly.databinding.FragmentCreateEditBinding;
import com.example.skeddly.ui.popup.CategorySelectorDialogFragment;
import com.example.skeddly.ui.popup.MapPopupDialogFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * Callback function for the MaterialTimePicker used to pick the time. Includes a reference to the
 * original picker in the callback.
 */
interface MaterialTimePickerCallback {
    void onPositiveButtonClick(MaterialTimePicker picker);
}

/**
 * Fragment for creating an event
 */
public class CreateFragment extends Fragment {
    private FragmentCreateEditBinding binding;
    private CalendarConstraints calendarConstraints;
    private UnderlineSpan underlineSpan;
    private EventRepository eventRepository;
    private boolean isEdit;

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
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
    private LocalTime eventStartTime;
    private LocalTime eventEndTime;

    // Registration
    private LocalDate regStartDate;
    private LocalDate regEndDate;
    private LocalTime regStartTime;
    private LocalTime regEndTime;

    private LatLng eventLocation;
    private String eventId;

    // Popup Selector Constants
    private final String categoryTitle = "Select Category";
    private final String[] catArray = {"Indoor", "Outdoor", "In-person", "Virtual", "Hybrid",
            "Arts & Crafts", "Physical activity"};

    private final String[] dayArray = Arrays.copyOfRange(new DateFormatSymbols().getWeekdays(), 1, 8);
    private final String dayTitle = "Select Day";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateEditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Variables
        underlineSpan = new UnderlineSpan();
        calendarConstraints = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build();
        eventRepository = new EventRepository(FirebaseFirestore.getInstance());

        // Hide them because we don't want them here
        binding.btnBack.setVisibility(View.INVISIBLE);
        binding.btnQrCode.setVisibility(View.INVISIBLE);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use the NavController to navigate back to the home screen.
                NavHostFragment.findNavController(CreateFragment.this).navigateUp();
            }
        });

        // Get arguments
        isEdit = false;
        eventId = null;
        if (getArguments() != null) {
            this.eventId = getArguments().getString("eventId");
            binding.btnBack.setVisibility(View.VISIBLE);
            isEdit = true;
            if (this.eventId != null && !this.eventId.isEmpty()) {
                loadEventData(this.eventId);
            }
        }

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

        binding.imgEvent.setOnClickListener(new View.OnClickListener() {
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
                MapPopupDialogFragment lpf = MapPopupDialogFragment.newInstance("locationPicker", MapPopupType.SET, null);
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

        setupSelector(binding.textDaySelect, dayTitle, dayArray, daysOfWeek);
        setupSelector(binding.textCategorySelect, categoryTitle, catArray, categories);

        setupDatePicker(binding.textDateStart, new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                eventStartDate = LocalDate.ofInstant(Instant.ofEpochMilli(selection), ZoneOffset.UTC);
                binding.textDateStart.setText(underlineString(eventStartDate.format(dateFormatter)));
                updateConfirmButton();
            }
        });
        setupDatePicker(binding.textDateFinish, new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                eventEndDate = LocalDate.ofInstant(Instant.ofEpochMilli(selection), ZoneOffset.UTC);
                binding.textDateFinish.setText(underlineString(eventEndDate.format(dateFormatter)));
                updateConfirmButton();
            }
        });
        setupDatePicker(binding.textRegDateStart, new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                regStartDate = LocalDate.ofInstant(Instant.ofEpochMilli(selection), ZoneOffset.UTC);
                binding.textRegDateStart.setText(underlineString(regStartDate.format(dateFormatter)));
                updateConfirmButton();
            }
        });
        setupDatePicker(binding.textRegDateFinish, new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                regEndDate = LocalDate.ofInstant(Instant.ofEpochMilli(selection), ZoneOffset.UTC);
                binding.textRegDateFinish.setText(underlineString(regEndDate.format(dateFormatter)));
                updateConfirmButton();
            }
        });

        setupTimePicker(binding.textTimeStart, new MaterialTimePickerCallback() {
            @Override
            public void onPositiveButtonClick(MaterialTimePicker picker) {
                eventStartTime = LocalTime.of(picker.getHour(), picker.getMinute());
                binding.textTimeStart.setText(underlineString(eventStartTime.format(timeFormatter)));
                updateConfirmButton();
            }
        });
        setupTimePicker(binding.textTimeFinish, new MaterialTimePickerCallback() {
            @Override
            public void onPositiveButtonClick(MaterialTimePicker picker) {
                eventEndTime = LocalTime.of(picker.getHour(), picker.getMinute());
                binding.textTimeFinish.setText(underlineString(eventEndTime.format(timeFormatter)));
                updateConfirmButton();
            }
        });
        setupTimePicker(binding.textRegTimeStart, new MaterialTimePickerCallback() {
            @Override
            public void onPositiveButtonClick(MaterialTimePicker picker) {
                regStartTime = LocalTime.of(picker.getHour(), picker.getMinute());
                binding.textRegTimeStart.setText(underlineString(regStartTime.format(timeFormatter)));
                updateConfirmButton();
            }
        });
        setupTimePicker(binding.textRegTimeFinish, new MaterialTimePickerCallback() {
            @Override
            public void onPositiveButtonClick(MaterialTimePicker picker) {
                regEndTime = LocalTime.of(picker.getHour(), picker.getMinute());
                binding.textRegTimeFinish.setText(underlineString(regEndTime.format(timeFormatter)));
                updateConfirmButton();
            }
        });

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event event = createEvent();

                // Put event in db
                if (isEdit) {
                    event.setId(eventId);
                    eventRepository.updateEvent(event);
                } else {
                    eventRepository.set(event);
                }

                // Reset the create event screen
                if (!isEdit) resetCreateScreen();

                if (isEdit) Toast.makeText(requireContext(), "Edited Event!", Toast.LENGTH_SHORT).show();
                else Toast.makeText(requireContext(), "Created Event!", Toast.LENGTH_SHORT).show();
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

        binding.editEventTitle.addTextChangedListener(textWatcher);
        binding.editEventDescription.addTextChangedListener(textWatcher);
        binding.editLotteryCriteria.addTextChangedListener(textWatcher);
        binding.editWaitlistLimit.addTextChangedListener(textWatcher);
        binding.editAttendeeLimit.addTextChangedListener(textWatcher);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetCreateScreen();
        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
        if (navView != null) {
            navView.setOnItemSelectedListener(null);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            androidx.navigation.ui.NavigationUI.setupWithNavController(navView, navController);
        }
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

                if (selected != null) {
                    selectedItems.clear();
                    selectedItems.addAll(selected);
                    String selectedString = String.join(", ", selectedItems);

                    textView.setText(selectedString);
                    updateConfirmButton();
                }
            }
        });
    }

    /**
     * Setup a time picker and call you back when the user has finished picking the time
     * @param view The view that should have the click associated with
     * @param callback The callback function that should be ran
     */
    private void setupTimePicker(View view, MaterialTimePickerCallback callback) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker mtp = new MaterialTimePicker.Builder().build();
                mtp.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onPositiveButtonClick(mtp);
                    }
                });
                mtp.show(getChildFragmentManager(), null);
            }
        });
    }

    /**
     * Setup a date picker and call you back when the user has finished picking the date
     * @param view The view that should have the click associated with
     * @param callback The callback function that should be ran
     */
    private void setupDatePicker(View view, MaterialPickerOnPositiveButtonClickListener<Long> callback) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> mdp = MaterialDatePicker.Builder.datePicker()
                        .setCalendarConstraints(calendarConstraints)
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

                mdp.addOnPositiveButtonClickListener(callback);
                mdp.show(getChildFragmentManager(), null);
            }
        });
    }

    /**
     * Updates which date fields are shown based on if the event is selected to be recurring or not
     */
    private void updateRecurring() {
        if (isRecurring) {
            binding.textDateDash.setVisibility(View.VISIBLE);
            binding.textDateFinish.setVisibility(View.VISIBLE);
            binding.textDaySelect.setVisibility(View.VISIBLE);
        } else {
            binding.textDateDash.setVisibility(View.INVISIBLE);
            binding.textDateFinish.setVisibility(View.INVISIBLE);
            binding.textDaySelect.setVisibility(View.GONE);
        }
    }

    /**
     * Updates whether the confirm button is enabled or not based on if the form is fully filled.
     */
    private void updateConfirmButton() {
        Button confirmButton = binding.btnConfirm;

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
        if (binding.editEventTitle.length() <= 0 || binding.editEventDescription.length() <= 0 ||
                binding.editLotteryCriteria.length() <= 0) {
            System.out.println("Title or description missing.");
            return false;
        }

        // Schedule must be set
        if (eventStartTime == null || eventEndTime == null || eventStartDate == null || (isRecurring && eventEndDate == null)) {
            System.out.println("Schedule is not set.");
            return false;
        }

        // Start date can't happen after the end date
        if (isRecurring && eventStartDate.isAfter(eventEndDate)) {
            System.out.println("The event of is negative length (End date < Start date).");
            return false;
        }

        // Event can't be scheduled in the past
        LocalDateTime eventStart = LocalDateTime.of(eventStartDate, eventStartTime);
        if (eventStart.isBefore(LocalDateTime.now())) {
            System.out.println("The registration date is in the past.");
            return false;
        }

        // If it's recurring, we need at least one day of the week
        if (isRecurring && daysOfWeek.isEmpty()) {
            System.out.println("The event is recurring, but no day of the week is set.");
            return false;
        }

        // Registration period must be set
        if (regStartTime == null || regEndTime == null || regStartDate == null || regEndDate == null) {
            System.out.println("The registration period is unset.");
            return false;
        }

        LocalDateTime regStart = LocalDateTime.of(regStartDate, regStartTime);
        LocalDateTime regEnd = LocalDateTime.of(regEndDate, regEndTime);

        // Registration start can't happen after registration end
        if (regStart.isAfter(regEnd)) {
            System.out.println("The registration period is of negative length (Start date > End date).");
            return false;
        }

        // Registration start and end date must happen before event start date
        if (regEnd.isAfter(eventStart)) {
            System.out.println("The registration period overlaps with the event.");
            return false;
        }

        // Attendee Limit
        if (binding.editAttendeeLimit.length() <= 0) {
            System.out.println("The attendee limit is less than 0.");
            return false;
        }

        // Waitlist Limit can't be less than Attendee Limit (if there is a Waitlist Limit)
        int waitlistLimitContent = 0;
        int attendeeLimitContent = 0;

        if (binding.editWaitlistLimit.length() > 0 && binding.editAttendeeLimit.length() > 0) {
            waitlistLimitContent = Integer.parseInt(binding.editWaitlistLimit.getText().toString());
            attendeeLimitContent = Integer.parseInt(binding.editAttendeeLimit.getText().toString());
        }

        if (binding.editWaitlistLimit.length() >= 0 && waitlistLimitContent < attendeeLimitContent) {
            System.out.println("The waitlist limit is less than the attendee limit.");
            return false;
        }

        // Location
        if (eventLocation == null) {
            System.out.println("There is no location.");
            return false;
        }

        // Needs an image
        if (imageBytes == null) {
            System.out.println("There is no image.");
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
        EventDetail eventDetails = new EventDetail(
                binding.editEventTitle.getText().toString(),
                binding.editEventDescription.getText().toString(),
                binding.editLotteryCriteria.getText().toString(),
                categories);

        LocalDate endDate = isRecurring ? this.eventEndDate : eventStartDate;

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

        LocalDateTime eventStart = LocalDateTime.of(eventStartDate, eventStartTime);
        LocalDateTime eventEnd = LocalDateTime.of(endDate, eventEndTime);
        LocalDateTime regStart = LocalDateTime.of(regStartDate, regStartTime);
        LocalDateTime regEnd = LocalDateTime.of(regEndDate, regEndTime);
        EventSchedule eventSchedule = new EventSchedule(eventStart, eventEnd, regStart, regEnd, eventDays);

        // Get the list limits
        int attendeeLimit = Integer.parseInt(binding.editAttendeeLimit.getText().toString());
        int waitListLimit = 0;

        if (binding.editWaitlistLimit.length() > 0) {
            waitListLimit = Integer.parseInt(binding.editWaitlistLimit.getText().toString());
        }

        return new Event(eventDetails, eventSchedule, eventLocation,
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),
                waitListLimit, attendeeLimit, binding.checkboxGeoLocationReq.isChecked(), imageBytes);
    }

    /**
     * Renders the image stored in the byte array as the event image.
     */
    private void updateEventImage() {
        Glide.with(this).load(imageBytes).into(binding.imgEvent);
    }

    /**
     * Resets the create event screen after creating an event.
     */
    private void resetCreateScreen() {
        isEdit = false;
        eventId = null;
        binding.imgEvent.setImageDrawable(null);
        binding.textEventTitleOverlay.setText(R.string.event_title_location);
        binding.switchRecurrence.setChecked(false);
        binding.textDateStart.setText(R.string.fragment_create_edit_date);
        binding.textDateFinish.setText(R.string.fragment_create_edit_date);
        binding.textDaySelect.setText("");
        binding.textTimeStart.setText(R.string.fragment_create_edit_time);
        binding.textTimeFinish.setText(R.string.fragment_create_edit_time);
        binding.editEventTitle.setText("");
        binding.editEventDescription.setText("");
        binding.textCategorySelect.setText("");
        binding.textRegDateStart.setText(R.string.fragment_create_edit_date);
        binding.textRegDateFinish.setText(R.string.fragment_create_edit_date);
        binding.textRegTimeStart.setText(R.string.fragment_create_edit_time);
        binding.textRegTimeFinish.setText(R.string.fragment_create_edit_time);
        binding.editLotteryCriteria.setText("");
        binding.editWaitlistLimit.setText("");
        binding.editAttendeeLimit.setText("");
        updateConfirmButton();
    }

    /**
     * Underline the given string and return it as a SpannableString
     * @param string The string to underline
     * @return The given string, but underlined as a SpannableString
     */
    private SpannableString underlineString(String string) {
        SpannableString spannedString = new SpannableString(string);
        spannedString.setSpan(underlineSpan, 0, spannedString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        return spannedString;
    }

    /**
     * Loads event data from Firestore and populates the UI.
     * @param eventId The ID of the event to load.
     */
    private void loadEventData(String eventId) {
        eventRepository.get(eventId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                populateUI(task.getResult());
            } else {
                Toast.makeText(getContext(), "Error: Event not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Populates the UI with the given event.
     * @param event The event to populate the UI with.
     */
    private void populateUI(Event event) {
        EventDetail details = event.getEventDetails();
        EventSchedule schedule = event.getEventSchedule();

        // Details
        binding.editEventTitle.setText(details.getName());
        binding.editEventDescription.setText(details.getDescription());
        binding.editLotteryCriteria.setText(details.getEntryCriteria());
        this.categories.clear();
        this.categories.addAll(details.getCategories());
        binding.textCategorySelect.setText(String.join(", ", categories));

        // Schedule
        isRecurring = schedule.isRecurring();
        binding.switchRecurrence.setChecked(isRecurring);
        updateRecurring();

        if (isRecurring && schedule.getDaysOfWeek() != null) {
            daysOfWeek.clear();
            List<String> allDays = Arrays.asList(dayArray);
            List<Boolean> eventDays = schedule.getDaysOfWeek();
            for (int i = 0; i < eventDays.size(); i++) {
                if (eventDays.get(i)) {
                    daysOfWeek.add(allDays.get(i));
                }
            }
            if (!daysOfWeek.isEmpty()) {
                binding.textDaySelect.setText(underlineString(String.join(", ", daysOfWeek)));
            }
        }

        // Datetime population
        long eventStartEpoch = schedule.getStartTime();
        long eventEndEpoch = schedule.getEndTime();
        long regStartEpoch = schedule.getRegStart();
        long regEndEpoch = schedule.getRegEnd();

        this.eventStartDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(eventStartEpoch), ZoneId.systemDefault()).toLocalDate();
        this.eventEndDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(eventEndEpoch), ZoneId.systemDefault()).toLocalDate();
        this.eventStartTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(eventStartEpoch), ZoneId.systemDefault()).toLocalTime();
        this.eventEndTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(eventEndEpoch), ZoneId.systemDefault()).toLocalTime();

        this.regStartDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(regStartEpoch), ZoneId.systemDefault()).toLocalDate();
        this.regEndDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(regEndEpoch), ZoneId.systemDefault()).toLocalDate();
        this.regStartTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(regStartEpoch), ZoneId.systemDefault()).toLocalTime();
        this.regEndTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(regEndEpoch), ZoneId.systemDefault()).toLocalTime();

        binding.textDateStart.setText(underlineString(this.eventStartDate.format(dateFormatter)));
        binding.textDateFinish.setText(underlineString(this.eventEndDate.format(dateFormatter)));
        binding.textTimeStart.setText(underlineString(this.eventStartTime.format(timeFormatter)));
        binding.textTimeFinish.setText(underlineString(this.eventEndTime.format(timeFormatter)));

        binding.textRegDateStart.setText(underlineString(this.regStartDate.format(dateFormatter)));
        binding.textRegDateFinish.setText(underlineString(this.regEndDate.format(dateFormatter)));
        binding.textRegTimeStart.setText(underlineString(this.regStartTime.format(timeFormatter)));
        binding.textRegTimeFinish.setText(underlineString(this.regEndTime.format(timeFormatter)));

        // Limits
        binding.editAttendeeLimit.setText(String.valueOf(event.getParticipantList().getMax()));
        if (event.getWaitingList().getMax() != Integer.MAX_VALUE) {
            binding.editWaitlistLimit.setText(String.valueOf(event.getWaitingList().getMax()));
        }

        // Location
        if (event.getLocation() != null) {
            this.eventLocation = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());
            binding.textEventTitleOverlay.setText(String.format(Locale.getDefault(), "%.2f, %.2f", this.eventLocation.latitude, this.eventLocation.longitude));
        }

        // Image
        if (event.getImageb64() != null) {
            this.imageBytes = Base64.decode(event.getImageb64(), Base64.DEFAULT);
            updateEventImage();
        }


        // Log Location
        binding.checkboxGeoLocationReq.setChecked(event.getLogLocation());

        // Update Button
        updateConfirmButton();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);

        if (navView != null) {
            navView.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.navigation_home) {
                    // Mimic the back button's behavior
                    System.out.println("Back button pressed");
                    Navigation.findNavController(view).navigateUp();
                    return true;
                }
                // For any other button, let the default behavior happen
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                return androidx.navigation.ui.NavigationUI.onNavDestinationSelected(item, navController);
            });
        }
    }
}
