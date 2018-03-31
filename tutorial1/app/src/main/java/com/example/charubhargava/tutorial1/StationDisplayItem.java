package com.example.charubhargava.tutorial1;

public class StationDisplayItem {
    private String StationName;
    private String StationId;
    private String Duration;

    public StationDisplayItem(){
        this.StationName = "";
        this.StationId = "";
        this.Duration = "";
    }

    public StationDisplayItem(String name, String id){
        this.StationName = name;
        this.StationId = id;
        this.Duration = " ";
    }

    public StationDisplayItem(String name, String id, long time){
        this.StationName = name;
        this.StationId = id;
        this.Duration = Long.toString(time);
    }

    public String getTitle(){
        return StationName + " " + Duration;
    }

    public String getId() {
        return StationId;
    }

    @Override
    public String toString() {
        return StationName;
    }
}
