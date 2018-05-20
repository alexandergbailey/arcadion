package edu.dartmouth.cs.arcadion.arcadion;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateEventActivity extends AppCompatActivity {

    private static final String TAG = "CreateEventActivity";

    private String mTitle;
    private String mAddress;
    private String mFoodType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
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

    public void onSubmitClicked(View v) {
        mTitle = (String) ((EditText) findViewById(R.id.event_title)).getText().toString();
        mAddress = (String) ((EditText) findViewById(R.id.event_address)).getText().toString();
        mFoodType = (String) ((EditText) findViewById(R.id.event_food)).getText().toString();
        EventEntry event = new EventEntry(mTitle, mAddress, mFoodType);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.push().setValue(event.getEvent())
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Insert is done!
                            Log.d(TAG, "Success!");
                        } else {
                            // Failed
                            if (task.getException() != null)
                                Log.w(TAG, task.getException().getMessage());
                        }
                    }
                });
        Intent intent = new Intent(CreateEventActivity.this,
                MapsActivity.class);
        startActivity(intent);
    }
}
