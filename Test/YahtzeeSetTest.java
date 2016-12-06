import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Kristoffer on 2016-12-04.
 */
public class YahtzeeSetTest {

    Die[] dice = new Die[5];


    @Before
    public void setDice(){
        dice[0] = new Die(5);
        dice[1] = new Die(5);
        dice[2] = new Die(5);
        dice[3] = new Die(5);
        dice[4] = new Die(3);
    }

    @Test
    public void threeOfAKindTestWithLastDice(){

        dice[0].setResult(1);
        dice[4].setResult(5);
        assertEquals(20, YahtzeeSet.FOUR_OF_A_KIND.evaluateSet(dice));
    }

    @Test
    public void threeOfAKindTestWithFirstDice(){


        assertEquals(20, YahtzeeSet.FOUR_OF_A_KIND.evaluateSet(dice));
    }

    @Test
    public void threeOfAKindFailTest(){
        dice[0].setResult(1);

        assertEquals(0, YahtzeeSet.FOUR_OF_A_KIND.evaluateSet(dice));
    }

    @Test
    public void largeStraightTest(){
        dice[0].setResult(2);
        dice[1].setResult(3);
        dice[2].setResult(4);
        dice[3].setResult(5);
        dice[4].setResult(6);

        assertEquals(40, YahtzeeSet.LARGE_STRAIGHT.evaluateSet(dice));
    }

    @Test
    public void smallStraightTest(){

        dice[0].setResult(4);
        dice[1].setResult(3);
        dice[2].setResult(6);
        dice[3].setResult(5);
        dice[4].setResult(6);

        assertEquals(30, YahtzeeSet.SMALL_STRAIGHT.evaluateSet(dice));

    }

}