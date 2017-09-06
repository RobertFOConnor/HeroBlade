package ie.ul.postgrad.socialanxietyapp.game;

/**
 * Created by Robert on 16-Aug-17.
 */

public class Stats {

    private int wins;
    private int chestsOpened;
    private int totalSteps;

    public Stats(int wins, int chestsOpened, int totalSteps) {
        this.wins = wins;
        this.chestsOpened = chestsOpened;
        this.totalSteps = totalSteps;
    }

    public Stats() {

    }

    public void addWin() {
        this.wins++;
    }

    public void addChestsOpened() {
        this.chestsOpened++;
    }

    public int getWins() {
        return wins;
    }

    public int getChestsOpened() {
        return chestsOpened;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }
}
