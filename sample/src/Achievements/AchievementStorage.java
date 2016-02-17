package Achievements;

import java.util.ArrayList;

import Achievements.Achievement;

/**
 * Created by Viktor on 1/14/2016.
 */
public class AchievementStorage {
    private static ArrayList<Achievement> trips= new ArrayList<Achievement>();


    //prevent any other class from instantiating
    private AchievementStorage(){}



    public static  void addAchievement(Achievement item){
        trips.add(item);
    }

    public static ArrayList<Achievement>  getAchievements(){
        return trips;
    }

    public static void clear() {trips.clear();}
}
