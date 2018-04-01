package com.example.charubhargava.tutorial1;

import java.util.Locale;

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
        this.Duration = "";
    }

    public StationDisplayItem(String name, String id, long time){
        this.StationName = name;
        this.StationId = id;
        this.Duration = inHours(time);
    }

    private String inHours (long sec){
        int h = (int) sec/3600;
        int m =(int) (sec-h*3600)/60;
        return String.format(Locale.getDefault(),"(%02d : %02d h)",h,m);

    }
    public String getTitle(){
        return StationName + " " + Duration;
    }

    public String getId() {
        return StationId;
    }

    @Override
    public String toString() {
        if(this.Duration == null || this.Duration.equals(""))
            return StationName;
        else
            return StationName + " " + Duration;
    }
}
