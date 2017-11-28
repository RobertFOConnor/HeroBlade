package ie.ul.postgrad.socialanxietyapp;

/**
 * Created by Robert on 20-Nov-17.
 */

public class MoodEntry {

    private int rating;
    private String description;
    private String date;

    public MoodEntry(int rating, String description, String date) {
        this.rating = rating;
        this.description = description;
        this.date = date;
    }

    public int getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }
}
