package edu.dartmouth.cs.arcadion.arcadion.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.dartmouth.cs.arcadion.arcadion.Constants;
import edu.dartmouth.cs.arcadion.arcadion.MapsActivity;
import edu.dartmouth.cs.arcadion.arcadion.R;

public class SignInActivity extends AppCompatActivity{

    private static final String TAG = "SignInActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAuth = FirebaseAuth.getInstance();
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //enable back-arrow
        }
    }

    // go back if back-arrow is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();    // go back if back-arrow pressed

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // if 'sign in' clicked, go to MainActivity
    public void onSignInClicked(View v) {
        Log.d(TAG, "got here");
        final String mInputEmail = (String) ((EditText) findViewById(R.id.sign_in_email)).getText().toString();
        final String mInputPassword = (String) ((EditText) findViewById(R.id.sign_in_password)).getText().toString();
        if (mInputEmail.equals("") || mInputPassword.equals("")) {
            Log.d(TAG, "Field empty");
            return;
        }
        mAuth.signInWithEmailAndPassword(mInputEmail, mInputPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    SharedPreferences mPrefs = getSharedPreferences(Constants.sharePrefName, MODE_PRIVATE);
                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    mEditor.clear();
                    String mKey = "email_key";
                    mEditor.putString(mKey, mInputEmail);
                    mEditor.apply();
                    String mEmailValue = mPrefs.getString(mKey, "");
                    Log.d(TAG, mEmailValue);
                    String mKey2 = "password_key";
                    mEditor.putString(mKey2, mInputPassword);

                    Intent intent = new Intent(SignInActivity.this,
                            MapsActivity.class);
                    Log.d(TAG, intent.toString());
                    startActivity(intent);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d(TAG, "createUserWithEmail:failure", task.getException());
//                               updateUI(null);
                }
            }
        });
    }

    // if 'Register' clicked, go to ProfileActivity
    public void onRegisterClicked(View v) {
        Intent intent = new Intent(SignInActivity.this,
                RegisterActivity.class);
        startActivity(intent);
    }
}