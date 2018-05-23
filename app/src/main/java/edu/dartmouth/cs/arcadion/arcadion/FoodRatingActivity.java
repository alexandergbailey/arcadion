package edu.dartmouth.cs.arcadion.arcadion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

//import edu.dartmouth.cs.arcadionapp.R;

public class FoodRatingActivity extends AppCompatActivity{

    private RadioGroup mRadio_Amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_rating);

        mRadio_Amount = findViewById(R.id.food_rating_amt);
    }

//    ImageView mThumbsUp = findViewById(R.id.thumbs_up_image);
//    ImageView mThumbsDown = findViewById(R.id.thumbs_down_image);
//    int like = 0;
//    int dislike = 0;


//
//    Button mSubmitButton = findViewById(R.id.submit_button);
//
//    mSubmitButton.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Toast.makeText(FoodRatingActivity.this, R.string.registration_confirmation, Toast.LENGTH_SHORT).show();
//
//            Intent intent = new Intent(FoodRatingActivity.this, MainActivity.class);
//            //saveProfile(mEmail, mPassword, mProfileIsCreated);
//            startActivity(intent);
//
//        }
//    });

}
