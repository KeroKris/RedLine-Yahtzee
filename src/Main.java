
import java.util.*;

/**
 * 
 */
public class Main {

    private static Scanner input = new Scanner(System.in);
    private static int playerAmount;
    private static Player[] players;

    /**
     * Main method, contains a setUpGame method call, the main game loop
     * and a call to the ScoreBoard's print method for final results.
     */
    public static void main(String[] args) {

        setUpGame();

        System.out.println("\n\nPress any key to start game\n\n");
        input.nextLine();

        boolean setsRemaining = true;
        while (setsRemaining){
            for (Player p :
                    players) {
                p.playTurn();
                if (!p.isSetsRemaining()) setsRemaining = false;
            }
        }

        ScoreBoard.print(players);
    }

    /**
     * Sets up the game.
     * Amount of players, player names.
     */
    private static void setUpGame() {
        System.out.println("How many Players do you want?");

        playerAmount = Integer.parseInt(input.nextLine());
        if (playerAmount > 4) playerAmount = 4;
        players = new Player[playerAmount];

        for (int i = 0; i < playerAmount; i++) {
            System.out.println("Player " + (i+1) + ", what is your name?");
            String playerName = input.nextLine();
            players[i] = new Player(playerName);
        }
        System.out.println("\nplayers are:\n");
        for (Player p :
                players) {
            System.out.println(p.getName());
        }
    }

    /**
     * Getter for the Scanner Object, for required input in the Player class.
     * @return the Scanner Object
     */
    public static Scanner getInput() {
        return input;
    }
}