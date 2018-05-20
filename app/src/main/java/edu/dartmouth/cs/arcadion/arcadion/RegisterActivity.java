package edu.dartmouth.cs.arcadion.arcadion;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.soundcloud.android.crop.Crop;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private static final int REQUEST_CODE_TAKE_FROM_GALLERY = 1;

    private Uri mImageCaptureUri;
    private ImageView mImageView;
    private boolean passwordSame = true;

    private EditText mNameTxt;
    private EditText mEmailTxt;
    private EditText mPasswordTxt;
    private EditText mPhoneTxt;
    private EditText mClassTxt;
    private EditText mMajorTxt;
    private RadioGroup mRadio;
    String extra;
    private FirebaseAuth mAuth;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the image capture uri before the activity goes into background
        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        checkPermissions();
        mAuth = FirebaseAuth.getInstance();
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //enable back-arrow
        }
        extra = getIntent().getStringExtra("Extra");
        mNameTxt = findViewById(R.id.profile_name);
        mEmailTxt = findViewById(R.id.profile_email);
        mPasswordTxt = findViewById(R.id.profile_password);
        mRadio = findViewById(R.id.profile_gender);
        mPhoneTxt = findViewById(R.id.profile_phone);
        mClassTxt = findViewById(R.id.profile_class);
        mMajorTxt = findViewById(R.id.profile_major);
        mImageView = (ImageView) findViewById(R.id.profile_image);
        String from_main = "from_main";
        // if editing profile, disable email editing and load profile picture
        if (extra.equals(from_main)) {
            mEmailTxt.setEnabled(false);
            loadSnap(); //load saved image
        } else {
            mImageView.setImageResource(R.drawable.anonymous_user);
        }

        // replace saved image with current image if there is one
        // for screen rotations
        if (savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY);
            mImageView.setImageURI(mImageCaptureUri);
        }
        // only load user data if profile is being edited, not registration from sign in
        if (extra.equals(from_main)) {
            loadUserData();
        }

        // check if password has been changed
        mPasswordTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordSame = mPasswordTxt.toString().equals(String.valueOf(s));
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // register button in top right of menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.register_option, menu);
        return true;
    }

    // save everything when register is clicked, under the condition that
    // all required fields are complete
    // go back if back-arrow is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.register:
                // if all required fields complete, save image, print toast, and finish
                if (saveUserData()) {
                    saveSnap();

                    // Making a "toast" informing the user the picture is saved.
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.ui_profile_toast_save_text),
                            Toast.LENGTH_SHORT).show();
                    if (passwordSame) { //if password hasn't been modified, finish profileActivity
                        finish();   //close the activity
                        return true;
                    }
                    else { // if password has been modified, exit to sign in
                        Log.d(TAG, mEmailTxt.getText().toString());
                        mAuth.createUserWithEmailAndPassword(mEmailTxt.getText().toString(), mPasswordTxt.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
//                                updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                                updateUI(null);
                                }
                            }
                        });
                        Intent intent = new Intent(this, SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //can't just go back
                        this.startActivity(intent);
                        return true;
                    }
                }
                return true;

            case android.R.id.home:
                onBackPressed();    // go back if back-arrow pressed

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission granted");
        }else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED){
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)||shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Show an explanation to the user *asynchronously*
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This permission is important for the app.")
                        .setTitle("Important permission required");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                    }
                });
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
            }
        }
    }

    public void onChangePhotoClicked(View v) {
        // changing the profile image, show the dialog asking the user
        // to choose between taking a picture and picking one
        displayDialog(ArcadionDialogFragment.DIALOG_ID_PHOTO_PICKER);
    }

    public void displayDialog(int id) {
        ArcadionDialogFragment fragment = ArcadionDialogFragment.newInstance(id);
        fragment.show(getFragmentManager(),
                getString(R.string.dialog_fragment_tag_photo_picker));

    }

    // Handle data after activity returns.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CODE_TAKE_FROM_CAMERA:
                // Send image taken from camera for cropping
                Log.d(TAG, "begin crop");
                beginCrop(mImageCaptureUri);
                break;

            case REQUEST_CODE_TAKE_FROM_GALLERY:
                mImageCaptureUri = data.getData();
                beginCrop(mImageCaptureUri);
                break;
        }
    }

    public void onPhotoPickerItemSelected(int item) {
        Intent intent;

        switch (item) {

            case ArcadionDialogFragment.ID_PHOTO_PICKER_FROM_CAMERA:
                // Take photo from camera
                // Construct an intent with action
                // MediaStore.ACTION_IMAGE_CAPTURE
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Construct temporary image path and name to save the taken
                // photo


                ContentValues values = new ContentValues(1);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                mImageCaptureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                intent.putExtra("return-data", true);
                try {
                    // Start a camera capturing activity
                    // REQUEST_CODE_TAKE_FROM_CAMERA is an integer tag you
                    // defined to identify the activity in onActivityResult()
                    // when it returns
                    startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            case ArcadionDialogFragment.DIALOG_ID_PHOTO_PICKER:
                Intent intent2 = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent2, REQUEST_CODE_TAKE_FROM_GALLERY);
        }
    }

    // load image
    private void loadSnap() {
        try {
            FileInputStream fis = openFileInput(getString(R.string.profile_photo_file_name));
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            mImageView.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            mImageView.setImageResource(R.drawable.anonymous_user); //default photo if none saved
        }
    }

    // save image
    private void saveSnap() {
        Log.d(TAG, "saveSnap");
        mImageView.buildDrawingCache();
        Bitmap bmap = mImageView.getDrawingCache();
        try {
            Log.d(TAG, "try");
            FileOutputStream fos = openFileOutput(getString(R.string.profile_photo_file_name), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            Log.d(TAG, "catch");
            ioe.printStackTrace();
        }
    }

    // Method to start Crop activity using the library
    private void beginCrop(Uri source) {
        Log.d(TAG, "Uri dest next");
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Log.d(TAG, "crop.of next");
        Crop.of(source, destination).asSquare().start(this);
        Log.d(TAG, "crop.of finished");
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mImageCaptureUri = Crop.getOutput(result);
            mImageView.setImageURI(mImageCaptureUri);
            mImageView.buildDrawingCache();
            Bitmap bmap = mImageView.getDrawingCache();
            mImageView.setImageBitmap(bmap);


        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "Crop Handled");
    }

    // load saved EditText fields
    private void loadUserData() {
        Log.d(TAG, "loadUserData");

        SharedPreferences mPrefs = getSharedPreferences(Constants.sharePrefName, MODE_PRIVATE);

        String mKey = "name_key";
        String mNameValue = mPrefs.getString(mKey, "");
        ((EditText) findViewById(R.id.profile_name)).setText(mNameValue);

        mKey = "email_key";
        String mEmailValue = mPrefs.getString(mKey, "");
        ((EditText) findViewById(R.id.profile_email)).setText(mEmailValue);

        mKey = "password_key";
        String mPasswordValue = mPrefs.getString(mKey, "");
        ((EditText) findViewById(R.id.profile_password)).setText(mPasswordValue);

        mKey = "phone_key";
        String mPhoneValue = mPrefs.getString(mKey, "");
        ((EditText) findViewById(R.id.profile_phone)).setText(mPhoneValue);

        mKey = "gender_key";

        int mIntValue = mPrefs.getInt(mKey, -1);
        if (mIntValue >=0) {
            RadioButton radioBtn = (RadioButton) ((RadioGroup) findViewById(R.id.profile_gender)).getChildAt(mIntValue);
            radioBtn.setChecked(true);
        }

        mKey = "class_key";
        String mClassValue = mPrefs.getString(mKey, "");
        ((EditText) findViewById(R.id.profile_class)).setText(mClassValue);

        mKey = "major_key";
        String mMajorValue = mPrefs.getString(mKey, "");
        ((EditText) findViewById(R.id.profile_major)).setText(mMajorValue);
    }

    // Save EditText fields
    private boolean saveUserData() {
        Log.d(TAG, "saveUserData");

        SharedPreferences mPrefs = getSharedPreferences(Constants.sharePrefName, MODE_PRIVATE);

        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.clear();

        String mNameValue = (String) mNameTxt.getText().toString();
        String mEmailValue = (String) mEmailTxt.getText().toString();
        String mPasswordValue = (String) mPasswordTxt.getText().toString();
        boolean error = false;

        // check if required fields are complete, if not, return false
        if (mNameValue.length() == 0) {
            EditText name = (EditText) findViewById(R.id.profile_name);
            name.setError("This field is required");
            error = true;
        }
        if (! mEmailValue.contains("@")) {
            EditText email = (EditText) findViewById(R.id.profile_email);
            email.setError("This field is required");
            error = true;
        }
        if (mPasswordValue.length() < 5) {
            EditText password = (EditText) findViewById(R.id.profile_password);
            password.setError("Password must be at least 5 characters");
            error = true;
        }
        if (mRadio.getCheckedRadioButtonId() == -1) {
            error = true;
            Toast.makeText(getApplicationContext(),
                    getString(R.string.ui_profile_gender_required_text),
                    Toast.LENGTH_SHORT).show();
        }

        if (error) {
            return false;
        }
        String mKey = "name_key";
        mEditor.putString(mKey, mNameValue);

        mKey = "email_key";
        mEditor.putString(mKey, mEmailValue);

        mKey = "password_key";
        mEditor.putString(mKey, mPasswordValue);

        mKey = "phone_key";
        String mPhoneValue = (String) mPhoneTxt.getText().toString();
        mEditor.putString(mKey, mPhoneValue);

        mKey = "gender_key";
        int mIntValue = mRadio.indexOfChild(findViewById(mRadio.getCheckedRadioButtonId()));
        mEditor.putInt(mKey, mIntValue);

        mKey = "class_key";
        String mClassValue = (String) mClassTxt.getText().toString();
        mEditor.putString(mKey, mClassValue);

        mKey = "major_key";
        String mMajorValue = (String) mMajorTxt.getText().toString();
        mEditor.putString(mKey, mMajorValue);

        mEditor.apply();

        Toast.makeText(getApplicationContext(), "Profile Saved: " + mNameValue, Toast.LENGTH_SHORT).show();

        return true;
    }
}

