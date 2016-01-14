package com.astuetz.viewpager.extensions.fragment;

import java.util.ArrayList;

/**
 * Created by Viktor on 1/14/2016.
 */
public class AchievementStorage {
    private static ArrayList<Achievement> trips= new ArrayList<Achievement>();


    //prevent any other class from instantiating
    private AchievementStorage(){}



    protected static  void addAchievement( Achievement item){
        trips.add(item);
    }

    protected static ArrayList<Achievement>  getAchievements(){
        return trips;
    }

    public static void clear() {trips.clear();}
}
