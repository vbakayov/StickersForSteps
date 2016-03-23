package Achievements;

import java.util.ArrayList;

import Achievements.Achievement;

/**
 *
 * Have all the achievement  stored in this object
 */
public class AchievementStorage {
    private static ArrayList<Achievement> achievements= new ArrayList<Achievement>();


    //prevent any other class from instantiating
    private AchievementStorage(){}


    public static  void addAchievement(Achievement item){
        achievements.add(item);
    }

    public static ArrayList<Achievement>  getAchievements(){
        return achievements;
    }

    public static void clear() {achievements.clear();}
}
