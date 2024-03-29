package edu.dartmouth.cs.arcadion.arcadion;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.dartmouth.cs.arcadion.arcadion.Login.SignInActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private Map<Marker, ArrayList<String>> markers = new HashMap<>();

    private String mEmailValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        checkPermissions();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        SharedPreferences mPrefs = getSharedPreferences(Constants.sharePrefName, MODE_PRIVATE);
        String mKey = "email_key";
        mEmailValue = mPrefs.getString(mKey, "");
        Log.d(TAG, mEmailValue);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        // Get user location and animate camera there
        LatLng collis = new LatLng(43.702778, -72.289849);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(collis));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(collis,17));
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Read from the database
        mDatabase.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "LISTENING");
                Object value = dataSnapshot.getValue(Object.class);
                if (value == null) {
                    return;
                }
                Log.d(TAG, "id follows: lsdflisjfsf");
                HashMap<String, ArrayList<String>> list = (HashMap<String, ArrayList<String>>) value;
                for (String key: list.keySet()) {
                    Log.d(TAG, key);
                    Log.d(TAG, list.get(key).toString());
                    if (list.get(key) == null) {
                        continue;
                    }
                    displayMarker(list.get(key), key);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void displayMarker(ArrayList<String> data, String key) {
        data.add(key);
        Geocoder geocoder = new Geocoder(this);
        Log.d(TAG, "YOUR ADDRESS:");
        try {
            List<Address> address = geocoder.getFromLocationName(data.get(3), 1);
            if (address.size() > 0) {
                Log.d(TAG, String.valueOf(address.get(0).getLatitude()));
                Log.d(TAG, "that was it bruh");
                LatLng pos = new LatLng(address.get(0).getLatitude(), address.get(0).getLongitude());
                Marker newMarker = mMap.addMarker(new MarkerOptions().position(pos).title(data.get(0)));
                markers.put(newMarker, data);
            }
        } catch(IOException e) {
            Log.d(TAG, "IO Exception");
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (mEmailValue.equals("")) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.no_email_view_text),
                    Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<String> data = markers.get(marker);
            Intent intent = new Intent(MapsActivity.this,
                    ViewEventActivity.class);
            intent.putExtra("MarkerData", data);
            startActivity(intent);
        }
        return true;
    }


        // if 'Register' clicked, go to ProfileActivity
    public void onSigninClicked(View v) {
        Intent intent = new Intent(MapsActivity.this,
                SignInActivity.class);
        startActivity(intent);
    }

    // if create event clicked, go to CreateEventActivity
    public void onCreateClicked(View v) {
        Log.d(TAG, mEmailValue);
        if (mEmailValue.equals("")) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.no_email_create_text),
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MapsActivity.this,
                    CreateEventActivity.class);
            startActivity(intent);
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT < 23)
            return;

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "check permission por favor");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important for the app.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                            }
                        }
                    });
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                } else {
                    //Never ask again and handle your app without permission.
                }
            }
        }
    }
}
