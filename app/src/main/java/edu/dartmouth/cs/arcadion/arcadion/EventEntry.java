package edu.dartmouth.cs.arcadion.arcadion;

import java.util.ArrayList;

public class EventEntry {

    public String title = "";
    public String address = "";
    public String food_type = "";

    public EventEntry(){};

    public EventEntry(String title, String address, String food_type){
        this.title = title;
        this.address = address;
        this.food_type = food_type;
    }

    public ArrayList<String> getEvent() {
        return new ArrayList<String>() {{
            add(title);
            add(address);
            add(food_type);
        }};
    }
}
