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

import java.util.ArrayList;

public class ViewEventActivity extends AppCompatActivity {

    private static final String TAG = "ViewEventActivity";
    private RadioGroup mRadio_Amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        View view = findViewById(R.id.eventview);
        mRadio_Amount = findViewById(R.id.food_rating_amt);
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

    int like = 0;
    int dislike = 0;

    public void onUpClicked(View v) {
        like++;
    }

    public void onDownClicked(View v) {
        dislike++;
    }
}
