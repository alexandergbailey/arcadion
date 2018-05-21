package edu.dartmouth.cs.arcadion.arcadion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
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
                HashMap<String, ArrayList<String>> list = (HashMap<String, ArrayList<String>>) value;
                for (String key: list.keySet()) {
                    Log.d(TAG, key);
                    Log.d(TAG, list.get(key).toString());
                    if (list.get(key) == null) {
                        continue;
                    }
                    displayMarker(list.get(key));
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void displayMarker(ArrayList<String> data) {
        Geocoder geocoder = new Geocoder(this);
        Log.d(TAG, "YOUR ADDRESS:");
        try {
            List<Address> address = geocoder.getFromLocationName(data.get(1), 1);
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
        ArrayList<String> data = markers.get(marker);
        Intent intent = new Intent(MapsActivity.this,
                ViewEventActivity.class);
        intent.putExtra("MarkerData",data);
        startActivity(intent);
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
}
