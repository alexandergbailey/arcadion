package edu.dartmouth.cs.arcadion.arcadion;

import android.util.Log;

import java.util.ArrayList;

public class EventEntry {

    private static final String TAG = "EventEntry";

    public String title = "";
    public String food_type = "";
    public String location = "";
    public String address = "";
    public String amt = "";
    public String alot = "0";
    public String notAlot = "0";
    public String like = "0";
    public String dislike = "0";
    public String over = "0";

    public EventEntry(){};

    public EventEntry(String title, String food_type, String location, String address, int amount){
        this.title = title;
        this.food_type = food_type;
        this.location = location;
        this.address = address;
        this.amt = "Amount unspecified";
        Log.d(TAG, Integer.toString(amount));
        if (amount == 2131230815) {
            this.amt = "A lot of food";
        }
        if (amount == 2131230875) {
            this.amt = "Not a lot of food";
        }
    }

    public ArrayList<String> getEvent() {
        return new ArrayList<String>() {{
            add(title);
            add(food_type);
            add(location);
            add(address);
            add(amt);
            add(alot);
            add(notAlot);
            add(like);
            add(dislike);
            add(over);
        }};
    }
}
