package edu.dartmouth.cs.arcadion.arcadion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class ArcadionDialogFragment extends DialogFragment {

    private static final String TAG = "ArcadionDialogFragment";

    public static final int DIALOG_ID_ERROR = -1;
    public static final int DIALOG_ID_PHOTO_PICKER = 1;

    // For photo picker selection:
    public static final int ID_PHOTO_PICKER_FROM_CAMERA = 0;

    private static final String DIALOG_ID_KEY = "dialog_id";

    public static ArcadionDialogFragment newInstance(int dialog_id) {
        ArcadionDialogFragment frag = new ArcadionDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ID_KEY, dialog_id);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int dialog_id = getArguments().getInt(DIALOG_ID_KEY);

        final Activity parent = getActivity();

        // Setup dialog appearance and onClick Listeners
        switch (dialog_id) {
            case DIALOG_ID_PHOTO_PICKER:
                // Build picture picker dialog for choosing from camera or gallery
                AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                builder.setTitle(R.string.ui_profile_photo_picker_title);
                // Set up click listener, firing intents open camera
                DialogInterface.OnClickListener dlistener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Log.d(TAG, "Dialog OnClick");
                        // Item is ID_PHOTO_PICKER_FROM_CAMERA
                        // Call the onPhotoPickerItemSelected in the parent
                        // activity, i.e., ameraControlActivity in this case
                        ((RegisterActivity) parent)
                                .onPhotoPickerItemSelected(item);
                    }
                };
                // Set the item/s to display and create the dialog
                builder.setItems(R.array.ui_profile_photo_picker_items, dlistener);
                return builder.create();
            case ID_PHOTO_PICKER_FROM_CAMERA:
                AlertDialog.Builder build = new AlertDialog.Builder(parent);
                build.setTitle(R.string.ui_profile_photo_picker_from_camera_title);
                DialogInterface.OnClickListener dlisten = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Log.d(TAG, "Dialog OnClick");
                        // Item is ID_PHOTO_PICKER_FROM_CAMERA
                        // Call the onPhotoPickerItemSelected in the parent
                        // activity, i.e., ameraControlActivity in this case
                        ((RegisterActivity) parent)
                                .onPhotoPickerItemSelected(item);
                    }
                };
                // Set the item/s to display and create the dialog
                build.setItems(R.array.ui_profile_photo_picker_items, dlisten);
                return build.create();
            default:
                return null;
        }
    }
}
