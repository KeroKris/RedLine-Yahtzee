
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

        mainMenu();
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
        DatabaseHandler.logGame(players, gameID);
        DatabaseHandler.endSession();
    }

    /**
     * Runs a loop with a menu, ends if you want to start a game.
     */
    private static void mainMenu() {
        int choice = 0;

        while (choice != 1){

            System.out.println("What do you want to do? \n" +
                    "1. Play a game\n" +
                    "2. Print Highscore\n" +
                    "3. Print game History\n" +
                    "4. Print game log");

            try{
                choice = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e){
                choice = -1;
            }

            switch (choice){
                case 1:
                    break;
                case 2:
                    DatabaseHandler.printHighScore();
                    System.out.println("\n");
                    break;
                case 3:
                    System.out.println("What player do you want the history for?");
                    String playerName = input.nextLine();
                    DatabaseHandler.printGameHistory(playerName);
                    System.out.println("\n");
                    break;
                case 4:
                    System.out.println("For what game do you want the log? (gameID)");
                    String gameIdString = input.nextLine();
                    DatabaseHandler.printGameLog(gameIdString);
                    break;
                default:
                    System.out.println("Choose 1-4");
                    break;
            }
        }
    }

    /**
     * Sorts the players in order of points, made with a simple bubble sort
     */
    public static void sortPlayersByPoints() {
        boolean swapped=true;
        int i, j = players.length;
        Player tmp;
        while(swapped){
            swapped=false;
            i=0;
            while(i<j-1){
                if(players[i].getTotalScore() < players[i+1].getTotalScore()){
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
        System.out.println("How many Players do you want? (1-4)");

        playerAmount = Integer.parseInt(input.nextLine());
        if (playerAmount > 4) playerAmount = 4;
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

    /**
     * Login method that asks for a name and checks with the database if it exists, if not the option will be provided
     * to create a new player. Also checks for a password if the name is found, just to keep unique names for players.
     * @param i
     */
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
                    System.out.println("Enter a password (note that the password is not encrypted)");
                    DatabaseHandler.insertNewPlayer(name, input.nextLine());
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