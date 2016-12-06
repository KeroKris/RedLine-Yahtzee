

/**
 * Simple ScoreBoard class, for now just iterates through the players and prints out individual score boards as well
 * as a list of the players in ranking.
 */
public class ScoreBoard {

    /**
     * 
     */
    public static void print(Player[] players) {

        System.out.println("********************************");
        System.out.println("*********GAME HAS ENDED*********");
        System.out.println("********************************");


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

        for (Player p :
                players) {
            System.out.println(p.getName() + "'s Score is:");
            p.printScore();
        }
        System.out.println("********************************");
        System.out.println(players[1] + " is the winner!");
        System.out.println("********************************");
        for (Player p :
                players) {
            System.out.println(p.getName() + ", score: " + p.getTotalScore());
        }
    }
}