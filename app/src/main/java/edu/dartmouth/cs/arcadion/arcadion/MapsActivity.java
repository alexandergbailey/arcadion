package edu.dartmouth.cs.arcadion.arcadion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.dartmouth.cs.arcadion.arcadion.Login.SignInActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MAPSACTIVITY";
    private GoogleMap mMap;

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
        String mEmailValue = mPrefs.getString(mKey, "");
        Log.d(TAG, mEmailValue);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng collis = new LatLng(43.702778, -72.289849);
        mMap.addMarker(new MarkerOptions().position(collis).title("Collis Free Food Table"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(collis));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(collis,17));
    }

    // if 'Register' clicked, go to ProfileActivity
    public void onSigninClicked(View v) {
        Intent intent = new Intent(MapsActivity.this,
                SignInActivity.class);
        startActivity(intent);
    }

    // if 'Register' clicked, go to ProfileActivity
    public void onCreateClicked(View v) {
        Intent intent = new Intent(MapsActivity.this,
                CreateEventActivity.class);
        startActivity(intent);
    }
}
