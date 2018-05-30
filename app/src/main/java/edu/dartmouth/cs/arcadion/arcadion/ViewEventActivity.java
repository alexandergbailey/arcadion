package edu.dartmouth.cs.arcadion.arcadion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ViewEventActivity extends AppCompatActivity {

    private static final String TAG = "ViewEventActivity";
    ArrayList<String> data;
    TextView title;
    TextView food;
    TextView location;
    TextView address;
    TextView amount;
    TextView upCount;
    TextView downCount;
    TextView overCount;
    int like;
    int dislike;
    boolean upvote = false;
    boolean downvote = false;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        View view = findViewById(R.id.eventview);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //enable back-arrow
        }
        data = (ArrayList<String>) getIntent().getStringArrayListExtra("MarkerData");
        title = view.findViewById(R.id.title);
        title.setText(data.get(0));
        food = view.findViewById(R.id.food);
        food.setText(data.get(1));
        location = view.findViewById(R.id.location);
        location.setText(data.get(2));
        address = view.findViewById(R.id.address);
        address.setText(data.get(3));
        amount = view.findViewById(R.id.amount);
        amount.setText(data.get(4));
        upCount = view.findViewById(R.id.up_count);
        upCount.setText(data.get(7));
        downCount = view.findViewById(R.id.down_count);
        downCount.setText(data.get(8));
        overCount = view.findViewById(R.id.over_count);
        overCount.setText(data.get(9));
        like = Integer.valueOf(data.get(7));
        dislike = Integer.valueOf(data.get(8));

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

    public void onUpClicked(View v) {
        upvote = true;
        downvote = false;
        upCount.setText(String.valueOf(Integer.valueOf(data.get(7))+1));
        downCount.setText(String.valueOf(Integer.valueOf(data.get(8))));
    }

    public void onDownClicked(View v) {
        upvote = false;
        downvote = true;
        upCount.setText(String.valueOf(Integer.valueOf(data.get(7))));
        downCount.setText(String.valueOf(Integer.valueOf(data.get(8))+1));
    }

    public void onOverClicked(View v) {
        String id = data.get(10);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (Integer.valueOf(data.get(9)) >= 2) {
            mDatabase.child(id).removeValue();
        } else {
            ArrayList<String> update = new ArrayList<>();
            for (int i=0; i<10; i++) {
                update.add(data.get(i));
            }
            update.set(9,String.valueOf(Integer.valueOf(data.get(9))+1));
            mDatabase.child(id).setValue(update);
        }
        Toast.makeText(getApplicationContext(),
                getString(R.string.over),
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ViewEventActivity.this,
                MapsActivity.class);
        startActivity(intent);
    }

    public void onRatedClicked(View v) {
        String id = data.get(10);
        ArrayList<String> update = new ArrayList<>();
        for (int i=0; i<10; i++) {
            update.add(data.get(i));
        }
        if (upvote) {
            update.set(7,String.valueOf(Integer.valueOf(data.get(7))+1));
            Toast.makeText(getApplicationContext(),
                    getString(R.string.upvote),
                    Toast.LENGTH_SHORT).show();
        }
        if (downvote) {
            update.set(8,String.valueOf(Integer.valueOf(data.get(8))+1));
            Toast.makeText(getApplicationContext(),
                    getString(R.string.downvote),
                    Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, id);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(id).setValue(update);
        Intent intent = new Intent(ViewEventActivity.this,
                MapsActivity.class);
        startActivity(intent);
    }
}
