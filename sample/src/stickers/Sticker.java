package stickers;

/**
 * Created by Viktor on 12/9/2015.
 * Helper class representing a sticker
 * and passing it around between the different views and elements
 */
public class Sticker {
    private Integer id;
    private String name;
    private String movie;
    private String popularity;
    private String description;
    private Integer count;
    //0 for not active, 1 for selected, 2 for glued
    private Integer status;

    public String getImagesrc() {
        return imagesrc;
    }

    public void setImagesrc(String imagesrc) {
        this.imagesrc = imagesrc;
    }

    private String imagesrc;

    public Sticker(Integer id, String name, String movie, String popularity,String imgsrc, String description, Integer count, Integer status) {
        this.id = id;
        this.name = name;
        this.movie = movie;
        this.popularity = popularity;
        this.imagesrc=imgsrc;
        this.description = description;
        this.count = count;
        this.status = status;
    }

    public Sticker() {

    }

    public Integer getStatus() {
        return status;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMovie() {
        return movie;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getDescription() {
        return description;
    }


    public Integer getCount() {
        return count;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription(String description) {
        return description;
    }


    public void setCount(Integer count) {
        this.count = count;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Sticker [id=" + id + ", title=" + name + ", movie=" + movie +", description="+ description
                +", status="+ status+", count="+ count+", popularity="+ popularity+ ", img="+imagesrc
                + "]";
    }
}
