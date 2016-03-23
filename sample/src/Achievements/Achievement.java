package Achievements;

import java.io.Serializable;

/**
 * Created by Viktor on 1/14/2016.
 * A helper class to represent an achievement
 */
public class Achievement implements Serializable {
    private boolean achieved;
    private String pictureSrc;
    private String description;
    private String title;

    public Achievement(String title, boolean achieved, String pictureSrc, String description) {
        this.achieved = achieved;
        this.pictureSrc = pictureSrc;
        this.description = description;
        this.title = title;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }

    public String getPictureSrc() {
        return pictureSrc;
    }

    public void setPictureSrc(String pictureSrc) {
        this.pictureSrc = pictureSrc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
