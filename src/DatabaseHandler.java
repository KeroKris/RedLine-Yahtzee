import java.sql.*;

/**
 * Database Handler for the Yahtzee Project. Handles all the DB functionality
 * Created by Kristoffer on 2017-01-05.
 */

public class DatabaseHandler {

    private static Connection connection = null;
    private static ResultSet resultSet;
    private static Statement statement;


    /**
     * Initiates the databaseSession by connecting to a local database.
     */
    public static void databaseSession(){


        String jdbcUrl = "jdbc:sqlserver://localhost\\(local):1433;user=Test;password=tester1;databaseName=ECYatzy";
        jdbcUrl = "jdbc:sqlserver://mssql4.gear.host;user=ecyatzy;password=Ev6HaoL~Mp!I;databaseName=ECYatzy";
        try {
            connection = DriverManager.getConnection(jdbcUrl);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ends the Session by closing the statement and the connection.
     */
    public static void endSession(){
        try {
            statement.close();
            connection.close();
            System.out.println("Connection to Database closed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Selects all the elements in a table for the static field ResultSet.
     * @param table String representation of the wanted table
     * @return returns the wanted ResultSet (Might remove this and make a Setter at a later point) ========***
     */
    private static ResultSet readTableFromDatabase(String table){

        try {
            resultSet = statement.executeQuery("select * from " + table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    /**
     * Logs a round to the database.
     * @param playerId ID of the player
     * @param currentRound The in game round for the player
     * @param dice The dice result, will be transformed to a string representation before entered to the Query
     * @param currentSetScored The Yahtzee set the player wishes to score, will be split up into a Name and a Score before entered to Query
     */
    public static void logRound(int playerId, int currentRound, Die[] dice, Scores currentSetScored) {

        String diceString = "";

        for (int i = 0; i < dice.length; i++) {
            diceString += dice[i].getDieResult().getValue();
        }
        System.out.println("logging " + diceString + " to Database, Current gameID: " + Main.getGameID());
        String set = currentSetScored.getSet().toString();

        String query = "InsertNewRound " + Main.getGameID() + ", " + currentRound + ", " + playerId + ", '" + diceString + "', '" + set + "', " + currentSetScored.getScore();
        System.out.println("SQL Query: " + query);

        try {
            statement.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs the Game Session to the database using a Stored Procedure
     * @param players The players, expected to be sorted in order of points
     * @param gameID The ID for the game to be updated in the database.
     */
    public static void logGame(Player[] players, int gameID) {

        System.out.println("score for winner is:" + players[0].getTotalScore());
        String query = "EXECUTE UpdateGameSession " + gameID + ", " + players[0].getId() + ", " + players[0].getTotalScore();

        // Adds in the other players if there are any
        for (int i = 1; i < players.length; i++) {
            query += (", " + players[i].getId());
        }
        System.out.println("SQL Query: " + query);

        try {
            statement.executeUpdate(query);
            for (int i = 0; i < players.length; i++) {
                statement.executeUpdate("EXECUTE InsertScoreToHighscore " + players[i].getId() + ", " + players[i].getTotalScore());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a Select statement and prints the highscore list returned from the database
     */
    public static void printHighScore(){

        try {
            resultSet = statement.executeQuery("SELECT TOP 10 p.Name, hs.Points\n" +
                    "FROM PlayerRegister AS p\n" +
                    "JOIN dbo.Highscore AS hs\n" +
                    "ON p.id = hs.PlayerID\n" +
                    "ORDER BY Points DESC");

            int x = 0;
            System.out.println("====HIGHSCORE====");
            System.out.printf("%-7s%-15s%-15s\n", "Rank", "Name", "Points");
            while (resultSet.next()){
                System.out.printf("%-7s%-15s%-15s\n",++x + "", resultSet.getString(1), resultSet.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns true if the Player name exists in the database
     * @param s the entered name
     * @return true if name exists in database
     */
    public static boolean getPlayerName(String s) {

        s = s.toLowerCase().trim();

        try {
            resultSet = statement.executeQuery("SELECT PlayerRegister.name FROM PlayerRegister WHERE name ='" + s + "'");
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Simple password check to the database, not a perfect solution.
     * @param name The name to check the password on
     * @param password The password to check
     * @return returns true if password is validated
     */
    public static boolean checkPassword(String name, String password) {

        try {
            resultSet = statement.executeQuery("SELECT PlayerRegister.name FROM PlayerRegister " +
                    "WHERE name = '" + name + "' AND identificatorQuery = '" + password + "'");
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Creates a new player in the database
     * @param name name to enter
     * @param s password
     */
    public static void insertNewPlayer(String name, String s) {

        try {
            statement.executeUpdate("INSERT INTO PlayerRegister (name, identificatorQuery)\n" +
                    "VALUES ('" + name + "', '" + s + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new Game Session in the database and returns the id generated as the Primary Key for the table
     * @return the game ID
     */
    public static int insertNewGameSession() {
//
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("INSERT INTO GameSession (WinnerPoints) VALUES (0)", ps.RETURN_GENERATED_KEYS);
            ps.executeUpdate();
            resultSet = ps.getGeneratedKeys();

            if (resultSet.next()){
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Returns the id for the player.
     * @param name Name to get ID for.
     * @return Returns the ID, -1 if Name is not found.
     */
    public static int getPlayerId(String name) {
        try {
            resultSet = statement.executeQuery("SELECT id FROM PlayerRegister WHERE name = '" + name + "'");

            while (resultSet.next()){
                int id = resultSet.getInt(1);
                System.out.println("player ID = " + id);
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Prints the game history for the specific player
     * @param playerName the Name to get game history for
     */
    public static void printGameHistory(String playerName) {
        try {
            resultSet = statement.executeQuery("EXECUTE getGameSessionsForPlayer N'" + playerName.toLowerCase().trim() + "';");

            System.out.printf("%-10s%-12s%-20s%-15s%-15s%-15s%-15s\n", "Game ID", "Winner", "Winner's Points", "Second", "Third", "Fourth", "Date");
            while (resultSet.next()){
                System.out.printf("%-10s%-12s%-20s%-15s%-15s%-15s%-15s\n", resultSet.getString(1),
                        resultSet.getString(2),resultSet.getString(3),
                            resultSet.getString(4), resultSet.getString(5),
                                resultSet.getString(6),resultSet.getString(7));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the game log for the specified game ID
     * @param gameIdString The Game ID
     */
    public static void printGameLog(String gameIdString) {
        try {
            resultSet = statement.executeQuery("SELECT gameRound.RoundNumber, player.name, gameRound.Result, gameRound.Combination, gameRound.Points\n" +
                    "FROM GameRounds AS gameRound\n" +
                    "JOIN PlayerRegister AS player\n" +
                    "ON gameRound.PlayerID = player.id\n" +
                    "WHERE gameRound.GameId = " + gameIdString);

            System.out.printf("%-10s%-12s%-10s%-20s%-10s\n", "Round", "Player", "Result", "Combination", "Points");
            while (resultSet.next()){
                System.out.printf("%-10s%-12s%-10s%-20s%-10s\n", resultSet.getString(1), resultSet.getString(2),
                        resultSet.getString(3),resultSet.getString(4),resultSet.getString(5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
