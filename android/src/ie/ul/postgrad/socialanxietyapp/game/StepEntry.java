package ie.ul.postgrad.socialanxietyapp.game;

/**
 * Created by Robert on 04-Dec-17.
 */

public class StepEntry {

    private int steps;
    private String date;

    public StepEntry(int steps, String date) {
        this.steps = steps;
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public String getDate() {
        return date;
    }
}
