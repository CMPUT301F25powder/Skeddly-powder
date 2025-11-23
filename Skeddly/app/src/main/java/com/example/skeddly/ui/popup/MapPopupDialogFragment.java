package com.example.skeddly.ui.popup;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.skeddly.R;
import com.example.skeddly.business.location.CustomLocation;
import com.example.skeddly.business.location.MapPopupType;
import com.example.skeddly.databinding.DialogMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Dialog fragment for the map popup
 */
public class MapPopupDialogFragment extends DialogFragment implements OnMapReadyCallback {
    // MAP STUFF
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    // PERMISSION STUFF
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private final String[] needed_permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private String requestKey = "requestKey";
    private MapPopupType mapPopupType;
    private ArrayList<CustomLocation> entrantLocations = null;
    private LatLng result;
    private DialogMapBinding binding;

    /**
     * Create a new instance of the MapPopupDialogFragment.
     * @param requestKey The requestKey that should be used when returning the result.
     * @param mapPopupType The MapPopupType defining the type of popup/behaviour wanted.
     * @param entrantLocations The arraylist of entrant locations to be marked on the map. Can be NULL if mapPopupType is SET
     * @return A new MapPopupDialogFragment with the given arguments set.
     */
    public static MapPopupDialogFragment newInstance(String requestKey, MapPopupType mapPopupType,
                                                     ArrayList<CustomLocation> entrantLocations) {
        Bundle args = new Bundle();
        args.putString("requestKey", requestKey);
        args.putSerializable("mapPopupType", mapPopupType);
        args.putParcelableArrayList("entrantLocations", entrantLocations);

        MapPopupDialogFragment popup = new MapPopupDialogFragment();
        popup.setArguments(args);

        return popup;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as a dialog or embedded fragment.
        View view = inflater.inflate(R.layout.dialog_map, container, false);
        binding = DialogMapBinding.bind(view);

        Bundle args = getArguments();

        if (args != null) {
            requestKey = args.getString("requestKey");
            mapPopupType = (MapPopupType) args.getSerializable("mapPopupType");
            entrantLocations = args.getParcelableArrayList("entrantLocations");
        }

        // Retrieve the map and call us back when you're done
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        // For getting our current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // GET PERMISSION THING
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                // its mad at me
                boolean fine_granted = result.getOrDefault(needed_permissions[0], false);
                boolean coarse_granted = result.getOrDefault(needed_permissions[1], false);

                if (fine_granted && coarse_granted) {
                    updateMapWithCurrentLocation();
                }
            }
        });

        if (mapPopupType == MapPopupType.SET) {
            binding.btnClose.setVisibility(View.GONE);

            // When the user presses enter, it pins the location on the map
            binding.addressEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        updateMapAddress(v.getText().toString());
                        return true;
                    }

                    return false;
                }
            });

            // When confirm, return the LatLng to the fragment
            Button confirmButton = binding.btnConfirm;
            confirmButton.setEnabled(false);
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("LatLng", result);
                    getParentFragmentManager().setFragmentResult(requestKey, bundle);
                    dismiss();
                }
            });
        } else if (mapPopupType == MapPopupType.VIEW) {
            binding.addressEntry.setVisibility(View.GONE);
            binding.btnConfirm.setVisibility(View.GONE);

            binding.btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using
        // onCreateView() is to modify the dialog characteristics. For example,
        // the dialog includes a title by default, but your custom layout might
        // not need it. Here, you can remove the dialog title, but you must
        // call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (mapPopupType == MapPopupType.SET || entrantLocations == null || entrantLocations.isEmpty()) {
            // We need to get the required permissions
            ArrayList<String> needed_permissions = new ArrayList<>();
            boolean granted = false;

            for (String permission : this.needed_permissions) {
                // If we don't have it, add it to the list
                if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    needed_permissions.add(permission);
                }
            }

            // Request perms if needed
            if (!needed_permissions.isEmpty()) {
                String[] perms = new String[2];
                requestPermissionLauncher.launch(needed_permissions.toArray(perms));
            } else {
                // We have all the permissions, update the map
                updateMapWithCurrentLocation();
            }
        } else if (mapPopupType == MapPopupType.VIEW) {
            // Add markers for each entrant location and position the map view to enclose all markers
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            int padding = 100;

            for (CustomLocation entrant : entrantLocations) {
                LatLng location = new LatLng(entrant.getLatitude(), entrant.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(location).title("Location"));
                builder.include(location);
            }

            LatLngBounds bounds = builder.build();

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        }
    }

    /**
     * Retrieves the current location from the device and updates the map to be centered there
     */
    public void updateMapWithCurrentLocation() {
        // If we don't have permission we cant do anything
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        CancellationTokenSource cts = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken()).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Move the camera to the position
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18));
                }
            }
        });
    }

    /**
     * Given an address, put a pin on the map there and update our current result location
     * @param address The address to put a pin in
     */
    public void updateMapAddress(String address) {
        LatLng location = getLocationFromAddress(address);


        if (location != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(location).title("Marker"));

            // Move the camera to the position
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 18));

            binding.btnConfirm.setEnabled(true);
        }

        result = location;
    }

    /**
     * Get a location from a provided string address.
     * @param strAddress The address to locate
     * @return A LatLng object that contains the latitude and longitude of our address
     */
    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(requireContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.isEmpty()) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return p1;
    }
}
