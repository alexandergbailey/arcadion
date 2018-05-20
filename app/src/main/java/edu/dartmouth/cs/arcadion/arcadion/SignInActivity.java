package edu.dartmouth.cs.arcadion.arcadion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

public class SignInActivity extends AppCompatActivity{

    private static final String TAG = "SignInActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
    }

    // if 'sign in' clicked, go to MainActivity
    public void onSignInClicked(View v) {
        Intent intent = new Intent(SignInActivity.this,
                MapsActivity.class);
        intent.putExtra("Extra","from_signin");
        startActivity(intent);
    }

    // if 'Register' clicked, go to ProfileActivity
    public void onRegisterClicked(View v) {
        Intent intent = new Intent(SignInActivity.this,
                RegisterActivity.class);
        intent.putExtra("Extra","from_signin");
        startActivity(intent);
    }
}