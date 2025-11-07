package com.example.skeddly.ui.popup;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.skeddly.R;
import com.example.skeddly.databinding.PopupGenericBinding;
import com.example.skeddly.databinding.PopupQrBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Dialog fragment for the QR popup
 */
public class QRPopupDialogFragment extends DialogFragment {
    private String link = "link";
    private ActivityResultLauncher<Intent> filePickerActivityResultLauncher;
    private Bitmap qrBitmap;

    /**
     * Create a new instance of the QRPopupDialogFragment
     * @param link The link that should be shown in the QR code.
     * @return A new QRPopupDialogFragment with the link set.
     */
    public static QRPopupDialogFragment newInstance(String link) {
        Bundle arg = new Bundle();
        arg.putString("link", link);

        QRPopupDialogFragment popup = new QRPopupDialogFragment();
        popup.setArguments(arg);

        return popup;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as a dialog or embedded fragment.
        View view = inflater.inflate(R.layout.popup_qr, container, false);
        PopupQrBinding binding = PopupQrBinding.bind(view);

        Bundle arg = getArguments();

        if (arg != null) {
            link = arg.getString("link");
        }

        Button buttonExport = binding.buttonExport;
        Button buttonClose = binding.buttonClose;
        ImageView imageQrCode = binding.imageQrCode;

        // Create QR Code image and set image of imageView
        Log.d("Link", "Given link: " + link);
        qrBitmap = createQR(link);
        imageQrCode.setImageBitmap(qrBitmap);

        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFile();
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        filePickerActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            if (result.getData() != null) {
                                Uri uri = result.getData().getData();
                                alterDocument(uri, qrBitmap);
                                Toast.makeText(requireContext(), "QR code has been exported!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

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
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    /**
     * Create a QR code from the given link.
     * @param link The link to create a QR code from
     * @return A bitmap of the QR code
     */
    private Bitmap createQR(String link) {
        try {
            // Initialize BarcodeEncoder
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

            // Encode link into a QR code
            // You can change the size (e.g., 400x400)
            int qrCodeSize = 300; // in pixels

            return barcodeEncoder.encodeBitmap(link, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize);

        } catch (WriterException e) {
            Log.e("QRCode", "Error generating QR code", e);
            // Handle the error (e.g., show a placeholder or error message)
        }

        return null;
    }

    /**
     * Let the user pick a location to create the new qr code image file at
     */
    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_TITLE, "qr.png");

        filePickerActivityResultLauncher.launch(intent);
    }

    /**
     * Export a bitmap to the given URI as a png file.
     * @param uri The uri of the document to alter
     * @param bitmap The bitmap to write to it as a png
     */
    private void alterDocument(Uri uri, Bitmap bitmap) {
        try {
            ParcelFileDescriptor pfd = requireActivity().getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
