package com.example.charubhargava.tutorial1;

import java.util.Locale;

public class StationDisplayItem {
    private String StationName;
    private String StationId;
    private String Duration;
    private String genre;

    public StationDisplayItem(){
        this.StationName = "";
        this.StationId = "";
        this.Duration = "";
        this.genre = "";
    }

    public StationDisplayItem(String name, String id, String genre){
        this.StationName = name;
        this.StationId = id;
        this.Duration = "";
        this.genre = genre;
    }

    public StationDisplayItem(String name, String id, long time, String genre){
        this.StationName = name;
        this.StationId = id;
        this.Duration = inHours(time);
        this.genre = genre;
    }

    private String inHours (long sec){
//        int h = (int) sec/3600;
        int h = 0;
        int m =(int) (sec)/60;
        int s = (int) (sec - m*60);
//        return String.format(Locale.getDefault(),"(%02d : %02d h)",h,m);
        if(m == 0) return String.format(Locale.getDefault(),"%02d sec",s);
        else return String.format(Locale.getDefault(),"%02d min %02d sec",m, s);

    }
    public String getTitle(){
        return StationName + " " + Duration;
    }

    public String getId() {
        return StationId;
    }

    public String getDuration(){
        if(this.Duration == null || this.Duration.equals("")) return " ";
        else return this.Duration;
    }

    @Override
    public String toString() {
//        if(this.Duration == null || this.Duration.equals(""))
//            return StationName;
//        else
//            return StationName + " " + Duration ;
    return StationName + " (" + genre + ") ";
    }

}
