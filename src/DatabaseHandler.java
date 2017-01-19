import java.sql.*;

/**
 * Created by Kristoffer on 2017-01-05.
 */

public class DatabaseHandler {

    private static Connection connection = null;
    private static ResultSet resultSet;
    private static Statement statement;



    public static void databaseSession(){

        String jdbcUrl = "jdbc:sqlserver://localhost\\(local):1433;user=Test;password=tester1;databaseName=ECYatzy";
        try {
            connection = DriverManager.getConnection(jdbcUrl);
            statement = connection.createStatement();

//            readTableFromDatabase("PlayerRegister");
//
//            while (resultSet.next()){
//                System.out.println(resultSet.getString(1) + "\t" + resultSet.getString(2));
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

//        ResultSet rs = null;
        try {
            resultSet = statement.executeQuery("select * from " + table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public static void logRound(int playerId, int currentRound, Die[] dice, Scores currentSetScored) {


        String diceString = "";

        for (int i = 0; i < dice.length; i++) {
            diceString += dice[i].getDieResult().getValue();
        }
        System.out.println("logging " + diceString + " to Database, Current gameID: " + Main.getGameID());
        System.out.println(currentSetScored.getSet().toString());

        try {
            //notes
            statement.executeUpdate("INSERT INTO [dbo].[GameRounds]\n" +
                    "           ([GameId]\n" +
                    "           ,[RoundNumber]\n" +
                    "           ,[PlayerID]\n" +
                    "           ,[Result]\n" +
                    "           ,[Combination]\n" +
                    "           ,[Points])\n" +
                    "     VALUES\n" +
                    "           (" + Main.getGameID() + "\n" +
                    "           ," + currentRound + "\n" +
                    "           ," + playerId + "\n" +
                    "           ,'" + diceString + "'\n" +
                    "           ,'"+ currentSetScored.getSet().toString() + "'\n" +
                    "           ,"+ currentSetScored.getScore()+")");
            //end notes
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //TODO write method that sends the round to the correct database.
    }

    public static void logGame(Player[] players, int gameID) {

        try {
            statement.executeUpdate("UPDATE GameSession\n" +  //Will need to log losing player at some point
                    "SET WinnerID = "+ players[0].getId() +", [Winner Points] = " + players[0].getTotalScore() + ", LoserID = " + players[1].getId() + " \n" +
                    "WHERE GameID = " + Main.getGameID() + "\n\n" +
                    "INSERT INTO HighScore (PlayerID, Points) VALUES (" + players[0].getId() + ", " + players[0].getTotalScore() + ")\n" +
                    "INSERT INTO HighScore (PlayerID, Points) VALUES (" + players[1].getId() + ", " + players[1].getTotalScore() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void printHighScore(){

        try {
            resultSet = statement.executeQuery("SELECT p.Name, hs.Points\n" +
                    "FROM PlayerRegister AS p\n" +
                    "JOIN dbo.Highscore AS hs\n" +
                    "ON p.id = hs.PlayerID\n" +
                    "ORDER BY Points DESC");

            System.out.println("====HIGHSCORE====");
            System.out.println("Name\tPoints");
            while (resultSet.next()){
                System.out.println(resultSet.getString(1) +"\t" + resultSet.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public static boolean checkPassword(String name, String password) {

        try {
            resultSet = statement.executeQuery("SELECT PlayerRegister.name FROM PlayerRegister " +
                    "WHERE name = '" + name + "' AND identificatorQuery = '"+password+"'");
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void insertNewPlayer(String name, String s) {

        try {
            statement.executeUpdate("INSERT INTO PlayerRegister (name, identificatorQuery)\n" +
                    "VALUES ('" + name + "', '" + s + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int insertNewGameSession() {

        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("INSERT INTO GameSession ([Winner Points]) VALUES (0)", ps.RETURN_GENERATED_KEYS);
            ps.executeUpdate();
            resultSet = ps.getGeneratedKeys();

            if (resultSet.next()){
                return resultSet.getInt(1);
            }
//            statement.executeQuery("");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

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
}
