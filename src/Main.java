
import java.util.*;

/**
 * 
 */
public class Main {

    private static Scanner input = new Scanner(System.in);
    private static int playerAmount;
    private static Player[] players;
    private static int gameID;

    /**
     * Main method, contains a setUpGame method call, the main game loop
     * and a call to the ScoreBoard's print method for final results.
     */
    public static void main(String[] args) {

        DatabaseHandler.databaseSession();

        System.out.println("Welcome to Yahtzee");
        DatabaseHandler.printHighScore();

/*        //test Parameters
        gameID = 1000;
        Die[] testDice = {new Die(1), new Die(2), new Die(3), new Die(4), new Die(5)};
        Scores testScore = new Scores(YahtzeeSet.LARGE_STRAIGHT);
        testScore.setScore(40);
        DatabaseHandler.logRound(1, 2, testDice, testScore);*/
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

        sortPlayersByPoints();
        DatabaseHandler.logGame(players, gameID);
        ScoreBoard.print(players);
        DatabaseHandler.endSession();
    }

    /**
     * Sorts the players in order of points, made with a simple bubble sort
     */
    private static void sortPlayersByPoints() {
        boolean swapped=true;
        int i, j = players.length;
        Player tmp;
        while(swapped){
            swapped=false;
            i=0;
            while(i<j-1){
                if(players[i].getTotalScore() > players[i+1].getTotalScore()){
                    tmp = players[i+1];
                    players[i+1] = players[i];
                    players[i] = tmp;
                    swapped=true;
                }
                i++;
            }
            j--;
        }
    }

    /**
     * Sets up the game.
     * Amount of players, player names.
     */
    private static void setUpGame() {
        System.out.println("How many Players do you want? (1/2)");

        playerAmount = Integer.parseInt(input.nextLine());
        if (playerAmount > 2) playerAmount = 2;
        players = new Player[playerAmount];

        for (int i = 0; i < playerAmount; i++) {
            System.out.println("Player " + (i+1) + "");
            login(i);
            System.out.println("\n");
        }
        System.out.println("\nplayers are:\n");
        for (Player p :
                players) {
            System.out.println(p.getName());
        }

        gameID = DatabaseHandler.insertNewGameSession();
    }

    /**
     * Getter for the Scanner Object, for required input in the Player class.
     * @return the Scanner Object
     */
    public static Scanner getInput() {
        return input;
    }

    public static int getGameID() {
        return gameID;
    }

    public static void login(int i){

        boolean playerPicked = false;
        while (!playerPicked){

            System.out.println("What is your name?");

            //check if player exist in the database
            String name = input.nextLine();
            boolean exists = DatabaseHandler.getPlayerName(name);
            if (!exists){
                System.out.println("Player name not found. Do you want to create a new player? (Y/N)");
                if (input.nextLine().toLowerCase().equals("y")){
                    //Create new player in database
                    System.out.println("Enter password");
                    DatabaseHandler.insertNewPlayer(name, input.nextLine());
                    // INSERT playerName, password
                    int playerId = DatabaseHandler.getPlayerId(name);
                    players[i] = new Player(name, playerId);
                    playerPicked = true;
                }
            }else {
                System.out.println("Player found, enter password:");

                //check password with database
                if (DatabaseHandler.checkPassword(name, input.nextLine())){
                    int playerId = DatabaseHandler.getPlayerId(name);
                    players[i] = new Player(name, playerId);
                    playerPicked = true;
                }
            }
        }
    }


}