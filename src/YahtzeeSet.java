import java.util.Iterator;

/**
 * Enum with the different Yahtzee sets with their separate evaluation methods according to:
 * https://en.wikipedia.org/wiki/Yahtzee
 * Note that the rules are different depending on version you play, chose the one from wikipedia
 * for a more international application potential.
 */

public enum YahtzeeSet {
    ACES(1) {
        @Override
        public int evaluateSet(Die[] dice) {
            int sum;

            sum = add(dice, DieResult.ONE);

            return sum;
        }
    },
    TWOS(2) {
        @Override
        public int evaluateSet(Die[] dice) {
            int sum;

            sum = add(dice, DieResult.TWO);

            return sum;
        }
    },
    THREES(3) {
        @Override
        public int evaluateSet(Die[] dice) {
            int sum;

            sum = add(dice, DieResult.THREE);

            return sum;
        }
    },
    FOURS(4) {
        @Override
        public int evaluateSet(Die[] dice) {
            int sum;

            sum = add(dice, DieResult.FOUR);

            return sum;
        }
    },
    FIVES(5) {
        @Override
        public int evaluateSet(Die[] dice) {
            int sum;

            sum = add(dice, DieResult.FIVE);

            return sum;
        }
    },
    SIXES(6) {
        @Override
        public int evaluateSet(Die[] dice) {
            int sum;

            sum = add(dice, DieResult.SIX);

            return sum;
        }
    },
    THREE_OF_A_KIND(7) {
        @Override
        public int evaluateSet(Die[] dice) {

            for (int i = dice.length-1; i > 1; i--) {
                if (dice[i].getDieResult().equals(dice[i-2].getDieResult())) return dice[i].getDieResult().getValue()*3;
            }
            return 0;
        }
    },
    FOUR_OF_A_KIND(8) {
        @Override
        public int evaluateSet(Die[] dice) {

            for (int i = dice.length-1; i > 2; i--) {
                if (dice[i].getDieResult().equals(dice[i-3].getDieResult())) return dice[i].getDieResult().getValue()*4;
            }
            return 0;
        }
    },
    FULL_HOUSE(9) {
        @Override
        public int evaluateSet(Die[] dice) {

                if (dice[dice.length-1].getDieResult().equals(dice[2].getDieResult())) {
                    if (dice[1].getDieResult().equals(dice[0].getDieResult()))
                    return 25;
            } else if (dice[dice.length-1].getDieResult().equals(dice[3].getDieResult())){
                    if (dice[2].getDieResult().equals(dice[0].getDieResult()))
                        return 25;
                }
            return 0;
        }
    },
    SMALL_STRAIGHT(10) {
        @Override
        public int evaluateSet(Die[] dice) {

            int[] values = {1,2,3,4};
            if (find(values,dice)) return 30;
            values = new int[]{2,3,4,5};
            if (find(values,dice)) return 30;
            values = new int[]{3,4,5,6};
            if (find(values, dice)) return 30;

            return 0;
        }
    },
    LARGE_STRAIGHT(11) {
        @Override
        public int evaluateSet(Die[] dice) {

                if (dice[0].getDieResult().getValue() == dice[1].getDieResult().getValue()-1 &&
                        dice[0].getDieResult().getValue() == dice[2].getDieResult().getValue()-2 &&
                            dice[0].getDieResult().getValue() == dice[3].getDieResult().getValue()-3 &&
                                dice[0].getDieResult().getValue() == dice[4].getDieResult().getValue()-4)
                    return 40;
            return 0;
        }
    },
    YAHTZEE(12) {
        @Override
        public int evaluateSet(Die[] dice) {
                if (dice[0].getDieResult().equals(dice[dice.length-1].getDieResult())) return 50;
            return 0;
        }
    },
    CHANCE(13) {
        @Override
        public int evaluateSet(Die[] dice) {

            int sum = 0;

            for (Die d :
                    dice) {
                sum += d.getDieResult().getValue();
            }
            return sum;
        }
    }, BONUS(99){
        @Override
        public int evaluateSet(Die[] dice) {
            return 0;
        }
    };


    private int ID;

    /**
     * Constructor
     * @param id the ID for the set
     */
    YahtzeeSet(int id) {
        this.ID = id;
    }

    /**
     * Abstract method implemented by the individual sets to evaluate the dice and return a score
     * @param dice the dice to be evaluated
     * @return the score
     */
    public abstract int evaluateSet(Die[] dice);

    /**
     * add method for the 6 first combo's, simply adds the values of the correct dice for the set.
     * @param dice the dice to be evaluated
     * @param result the set that the dice are evaluated against
     * @return the sum of all the correct dice
     */
    public int add(Die[] dice, DieResult result){
        int sum = 0;

        for (Die d :
                dice) {
            if (d.getDieResult() == result)
                sum += d.getDieResult().getValue();
        }
        return sum;
    }

    /**
     * method to find a specific set of values in a set of dice
     * @param values the expected values in an Array (ex: [1,2,3,4])
     * @param dice the dice to be evaluated
     * @return true if all values can be found.
     */
    public boolean find(int [] values, Die[] dice){

        boolean returnBool = false;

        for (int i = 0; i < values.length; i++) {
            returnBool = false;

            for (int j = 0; j < dice.length; j++) {
                if (dice[j].getDieResult().getValue() == values[i]){
                    returnBool = true;
                }
            }
            if (!returnBool) return false;
        }
        return returnBool;
    }

    /**
     * Getter for the sets by identifier
     * @param identifier the ID of the wanted set
     * @return the actual set, returns CHANCE if for something goes wrong for some reason.
     */
    public static YahtzeeSet getSetWithIdentifier(int identifier){

        for (YahtzeeSet set :
                values()) {
            if (set.ID == identifier) return set;
        }
        return CHANCE;
    }
}