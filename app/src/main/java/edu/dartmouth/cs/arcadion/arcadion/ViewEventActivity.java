package edu.dartmouth.cs.arcadion.arcadion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewEventActivity extends AppCompatActivity {

    private static final String TAG = "ViewEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        View view = findViewById(R.id.eventview);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //enable back-arrow
        }
        ArrayList<String> data = getIntent().getStringArrayListExtra("MarkerData");
        TextView title = view.findViewById(R.id.title);
        title.setText(data.get(0));
        TextView address = view.findViewById(R.id.address);
        address.setText(data.get(1));
        TextView food = view.findViewById(R.id.food);
        food.setText(data.get(2));

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
}
