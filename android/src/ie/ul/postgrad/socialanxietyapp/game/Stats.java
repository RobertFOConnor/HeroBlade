package ie.ul.postgrad.socialanxietyapp.game;

/**
 * Created by Robert on 16-Aug-17.
 */

public class Stats {

    private int wins;
    private int chestsOpened;

    public Stats(int wins, int chestsOpened) {
        this.wins = wins;
        this.chestsOpened = chestsOpened;
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
}
