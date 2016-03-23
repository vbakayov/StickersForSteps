package stickers;

import android.content.Context;

import Database.Database;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/**
 * This class is used to randomly pick a sticker
 * based on probability count
 */
public class DistributedRandomNumberGenerator {

    private HashMap<Integer, Double> distribution;
    private double distSum;

    public DistributedRandomNumberGenerator(Context cn) {
        distribution = new HashMap<>();
        if(distribution.size()== 0){
            Database db = Database.getInstance(cn );
            List stickers =  db.getAllStickers();
            ListIterator<Sticker> listIterator = stickers.listIterator();
            while (listIterator.hasNext()) {
                Sticker sticker = listIterator.next();
                String popularity=  sticker.getPopularity();
                switch (popularity){
                    case "common":      addNumber(sticker.getId(),0.5); break;
                    case "uncommon":    addNumber(sticker.getId(),0.3); break;
                    case "rare":        addNumber(sticker.getId(),0.15); break;
                    case "super rare":  addNumber(sticker.getId(),0.05); break;
                }

            }

        }
    }

    private void addNumber(int value, double distribution) {
        if (this.distribution.get(value) != null) {
            distSum -= this.distribution.get(value);
        }
        this.distribution.put(value, distribution);
        distSum += distribution;
    }

    public int getDistributedRandomNumber() {
        //returns a number from zero to one
        double rand = Math.random();
        //calculate the probability of occurring over the distribution sum
        double ratio = 1.0f / distSum;
        double tempDist = 0;
        //loop the sticker's distributions  and sum them
        for (Integer i : distribution.keySet()) {
            tempDist += distribution.get(i);
            if (rand / ratio <= tempDist) {
                return i; //return the sticker ID
            }
        }
        return 0;
    }

}