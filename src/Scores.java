/**
 * Scores Class, made to be put in a list and keep track of the different scores for the player.
 * Contains which set it represents, if the set has been scored and the score in points.
 *
 * Created by Kristoffer on 2016-12-05.
 */
public class Scores {

    private YahtzeeSet set;
    private boolean picked;
    private int score;

    public Scores(YahtzeeSet set) {
        this.set = set;
        this.picked = false;
        this.score = 0;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    public void setScore(int score) {
        this.setPicked(true);
        this.score = score;
    }

    public YahtzeeSet getSet() {
        return set;
    }

    public boolean isPicked() {
        return picked;
    }

    public int getScore() {
        return score;
    }
}
