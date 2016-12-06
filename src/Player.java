import java.util.ArrayList;
import java.util.List;

/**
 * Player Class,
 */
public class Player implements Evaluate {

    private List<Scores> scoresList;

//    private boolean[] pickedSets;
    private Die[] dice = new Die[5];
    private String name;
    private int totalScore = 0;
    private boolean scoredBonus = false, setsRemaining = true;

    /**
     * Constructor
     */
    public Player(String name) {
        this.name = name;
        for (int i = 0; i < dice.length; i++) {
            dice[i] = new Die();
//            pickedSets = new boolean[13];

            scoresList = new ArrayList<>();

            populateList(scoresList);
        }
    }

    private void populateList(List<Scores> scoresList) {
        scoresList.add(new Scores(YahtzeeSet.ACES));
        scoresList.add(new Scores(YahtzeeSet.TWOS));
        scoresList.add(new Scores(YahtzeeSet.THREES));
        scoresList.add(new Scores(YahtzeeSet.FOURS));
        scoresList.add(new Scores(YahtzeeSet.FIVES));
        scoresList.add(new Scores(YahtzeeSet.SIXES));
        scoresList.add(new Scores(YahtzeeSet.THREE_OF_A_KIND));
        scoresList.add(new Scores(YahtzeeSet.FOUR_OF_A_KIND));
        scoresList.add(new Scores(YahtzeeSet.FULL_HOUSE));
        scoresList.add(new Scores(YahtzeeSet.SMALL_STRAIGHT));
        scoresList.add(new Scores(YahtzeeSet.LARGE_STRAIGHT));
        scoresList.add(new Scores(YahtzeeSet.YAHTZEE));
        scoresList.add(new Scores(YahtzeeSet.CHANCE));
        scoresList.add(new Scores(YahtzeeSet.BONUS));

        scoresList.get(13).setPicked(true);
    }

    /**
     * Every player's turn
     */
    public void playTurn() {
        System.out.println("\n\n*******************************");
        System.out.println("It is " + name + "'s turn");
        System.out.println("*******************************\n");

        printScore();

        int rollCount = 3;
        rolling:
        while (rollCount > 0){

            System.out.println("\nRolls remaining: " + rollCount);

            System.out.println("Press any key to roll dice");
            Main.getInput().nextLine();

            roll(dice);
            System.out.println("You Rolled:");
            for (Die d :
                    dice) {
                d.printValue();
                System.out.println();
            }

            if (rollCount > 1){
                System.out.println("Do you want to score these dice? (y/n)");
                if (Main.getInput().nextLine().toLowerCase().equals("y")) break rolling;

                for (Die d :
                        dice) {
                    System.out.print("Do you want to save: ");
                    d.printValue();
                    System.out.print("? (y/n)");
                    if (Main.getInput().nextLine().toLowerCase().equals("y")) d.setSavedDie(true);
                    else d.setSavedDie(false);
                }
            }
            rollCount--;
        }

        System.out.println();
        chooseSet();

        resetDice();
        checkSetsRemaining();
        System.out.println("Press any key to end turn!");
        Main.getInput().nextLine();
    }

    /**
     * Rolls the dice
     * @param dice the dice to roll
     */
    public void roll(Die[] dice) {
        for (Die d :
                dice) {
            if (!d.isSavedDie())
                d.randomizeResult();
        }
    }

    /**
     * chooseSet method, prints the current score and asks what score to score next.
     * Then calls teh evaluateAndScore method for the corresponding set.
     */
    public void chooseSet() {
        boolean pickedValidSet = false;

        while (!pickedValidSet){
            printScore();

            System.out.println("Choose a set(1-13):");
            try{
                int setChosen = Integer.parseInt(Main.getInput().nextLine());

                if (setChosen > 0 && setChosen < 14 && !scoresList.get(setChosen-1).isPicked()){
                    scoresList.get(setChosen-1).setScore(evaluateAndScore(setChosen, dice));
                    pickedValidSet = true;
                } else {
                    System.out.println("Set not available, pick another.");
                }
            } catch (NumberFormatException e){
                System.out.println("Enter a number!");
            }
        }
    }

    /**
     * Sorts the dice, compares them to the entered Yahtzee enum to see if it fulfills the required set.
     * @param setChosen the entered set player wants to score
     * @param dice the dice sent to compare to the set
     * @return the scored points for specific set
     */
    @Override
    public int evaluateAndScore(int setChosen, Die[] dice) {

        dice = bubbleSortDice(dice);

        System.out.println("you chose set: " + YahtzeeSet.getSetWithIdentifier(setChosen) + ", with: ");
        for (Die d :
                dice) {
            d.printValue();
            System.out.print(" | ");
        }

        int score = YahtzeeSet.getSetWithIdentifier(setChosen).evaluateSet(dice);
        System.out.println("Score: " + YahtzeeSet.getSetWithIdentifier(setChosen).evaluateSet(dice));
        return score;
    }

    /**
     * Sorts a set of dice, made for easier evaluation to a set.
     * Chose a bubble sort for ease of implementation and because the data size is small.
     * @param dice the dice to be sorted
     * @return the sorted dice
     */
    private Die[] bubbleSortDice(Die[] dice) {
        boolean swapped = true;
        int i, j = dice.length;
        Die tmp;
        while(swapped){
            swapped = false;
            i = 0;
            while(i < j - 1){
                if(dice[i].getDieResult().getValue() > dice[i+1].getDieResult().getValue()){
                    tmp = dice[i+1];
                    dice[i+1] = dice[i];
                    dice[i] = tmp;
                    swapped = true;
                }
                i++;
            }
            j--;
        }
        return dice;
    }

    /**
     * Iterates through the list of scores and sets the setsRemaining to false if all sets are scored.
     */
    private void checkSetsRemaining() {

        for (Scores s :
                scoresList) {
            if (!s.isPicked()){
                return;
            }
        }
        setSetsRemaining(false);
    }

    /**
     * Iterates through all dice and sets them to not saved, ie for the next turn.
     */
    private void resetDice() {
        for (Die d :
                dice) {
           d.setSavedDie(false);
        }
    }


    public void setSetsRemaining(boolean setsRemaining) {
        this.setsRemaining = setsRemaining;
    }

    public boolean isSetsRemaining() {
        return setsRemaining;
    }

    /**
     * Prints the score for the current Player.
     * Also sets the bonus if bonus requirements are met.
     */
    public void printScore() {
        int bonusRequiredSum = 0;
        totalScore = 0;

        for (int i = 1; i <= 6; i++) {
            System.out.print(i + ".\t" + YahtzeeSet.getSetWithIdentifier(i));
            if (scoresList.get(i-1).isPicked()) {
                System.out.println("\t\t\tScored!\tpoints: " + scoresList.get(i-1).getScore());
                bonusRequiredSum+= scoresList.get(i-1).getScore();
                totalScore+= scoresList.get(i-1).getScore();
            } else System.out.println();
            if (bonusRequiredSum >= 63) scoredBonus = true;
        }
        if (scoredBonus) {
            scoresList.get(13).setScore(35);
            System.out.println("* \tBONUS:\t\t\tScored!\tpoints: " + scoresList.get(13).getScore());
            totalScore+= scoresList.get(14).getScore();
        }
        for (int i = 7; i <= 13; i++) {
            System.out.print(i + ".\t" + YahtzeeSet.getSetWithIdentifier(i));
            if (scoresList.get(i-1).isPicked()) {
                System.out.println("\t\t   Scored!\tpoints: " + scoresList.get(i-1).getScore());
                totalScore+= scoresList.get(i-1).getScore();
            } else System.out.println();
        }
        System.out.println("\nTotal Score: " + totalScore);
    }

    public String getName() {
        return name;
    }

    public int getTotalScore() {
        return totalScore;
    }
}