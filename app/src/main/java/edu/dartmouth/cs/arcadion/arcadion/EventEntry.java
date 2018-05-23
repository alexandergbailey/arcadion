package edu.dartmouth.cs.arcadion.arcadion;

import java.util.ArrayList;

public class EventEntry {

    public String title = "";
    public String food_type = "";
    public String location = "";
    public String address = "";

    public EventEntry(){};

    public EventEntry(String title, String address, String food_type){
        this.title = title;
        this.food_type = food_type;
        this.location = location;
        this.address = address;
    }

    public ArrayList<String> getEvent() {
        return new ArrayList<String>() {{
            add(title);
            add(address);
            add(food_type);
            add(location);
        }};
    }
}
