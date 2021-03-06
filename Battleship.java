/**
 * @ AUTHOR NAME HERE
 * @ Starter Code By Guocheng
 *
 * 2016-01-30
 * For: Purdue Hackers - Battleship
 * Battleship Client
 */

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.InetAddress;

public class Battleship {
    public static String API_KEY = "487674642"; ///////// PUT YOUR API KEY HERE /////////
    public static String GAME_SERVER = "battleshipgs.purduehackers.com";
    public String opponent;
    public File opponentFile;

    //////////////////////////////////////  PUT YOUR CODE HERE //////////////////////////////////////

    char[] letters;
    int[][] grid;
    int turnNumber;
    boolean destroyerAlive;
    boolean submarineAlive;
    boolean cruiserAlive;
    boolean battleshipAlive;
    boolean carrierAlive;

    int[][] ourGrid;

    void placeShips(String opponentID) {
        // initialize stuff
        turnNumber = 0;
        destroyerAlive = true;
        submarineAlive = true;
        cruiserAlive = true;
        battleshipAlive = true;
        carrierAlive = true;

        System.out.println("***************** " + opponentID + " *****************");

        // Fill Grid With -1s
        for(int i = 0; i < grid.length; i++) { for(int j = 0; j < grid[i].length; j++) grid[i][j] = -1; }
        for(int i = 0; i < ourGrid.length; i++) { for(int j = 0; j < ourGrid[i].length; j++) ourGrid[i][j] = 0; }

        // Save opponent ID
        this.opponent = opponentID;
        try {
            this.opponentFile = new File(opponentID + ".txt");
            if (!this.opponentFile.exists()) this.opponentFile.createNewFile();
        }
        catch(Exception e) {

        }

        // Place Ships
        /*String[] pos;

        pos = getRandomPos(1);
        for (int i = 0; i < pos.length; i++) {
            System.out.println(pos[i]);
        }

        placeDestroyer(pos[0], pos[1]);

        pos = getRandomPos(2);
        for (int i = 0; i < pos.length; i++) {
            System.out.println(pos[i]);
        }
        placeSubmarine(pos[0], pos[1]);

        pos = getRandomPos(2);
        for (int i = 0; i < pos.length; i++) {
            System.out.println(pos[i]);
        }
        placeCruiser(pos[0], pos[1]);

        pos = getRandomPos(3);
        for (int i = 0; i < pos.length; i++) {
            System.out.println(pos[i]);
        }
        placeBattleship(pos[0], pos[1]);

        pos = getRandomPos(4);
        for (int i = 0; i < pos.length; i++) {
            System.out.println(pos[i]);
        }
        placeCarrier(pos[0], pos[1]);*/

        if (Math.random() < 0.5) {
            placeDestroyer("F6", "F7");    // size 2
            placeSubmarine("F0", "H0");    // size 3
            placeCruiser("A7", "C7");    // size 3
            placeBattleship("H2", "H5");    // size 4
            placeCarrier("B1", "B5");        // size 5
        } else {
            placeDestroyer("H2", "H3");    // size 2
            placeSubmarine("F7", "H7");    // size 3
            placeCruiser("B7", "D7");    // size 3
            placeBattleship("A2", "A5");    // size 4
            placeCarrier("C0", "C4");        // size 5
        }
    }

    void makeMove() {
        int shoti = 0;
        int shotj = 0;

        if (turnNumber < 4) {
            // first four shots; just survey the area to throw off
            // the probability distribution so it doesn't go for the
            // center four squares every time
            switch (turnNumber) {
                case 0:
                    shoti = 2;
                    shotj = 2;
                    break;
                case 1:
                    shoti = 5;
                    shotj = 2;
                    break;
                case 2:
                    shoti = 2;
                    shotj = 5;
                    break;
                case 3:
                    shoti = 5;
                    shotj = 5;
                    break;
                default:
                    break;
            }
        } else {
            // rest of the shots based on probability
            Point p = getBestShot();
            shoti = (int) p.getX();
            shotj = (int) p.getY();
        }

        if (this.grid[shoti][shotj] > -1) {
            System.out.println("Something went wrong... " + this.grid[shoti][shotj]);
        }

        String wasHitSunkOrMiss = placeMove(this.letters[shoti] + String.valueOf(shotj));

        if (wasHitSunkOrMiss.equals("Sunk")) {
            determineSink(shoti, shotj);
            this.grid[shoti][shotj] = 2;
        } else if (wasHitSunkOrMiss.equals("Hit")) {
            this.grid[shoti][shotj] = 1;
        } else {
            // miss
            this.grid[shoti][shotj] = 0;
        }

        turnNumber++;
        return;
    }

    boolean isValidLocation(int x, int y) { return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length && ourGrid[x][y] == 0; }

    String[] getRandomPos(int size) {
        Boolean valid = false;
        String[] coordinates = new String[2];

        while (!valid) {
            // Starting coordinates are random
            int x1 = (int) (Math.random() * grid.length);
            int y1 = (int) (Math.random() * grid[0].length);

            // Check if starting coordinates are valid
            if (!isValidLocation(x1, y1)) {
                continue;
            }

            // Set the initial coordinates
            coordinates[0] = Character.toString((char) (x1 + 'A'));
            coordinates[0] += y1;

            // 0=north, 1=east, 2=south, 3=west
            int orientation = (int) (Math.random() * 4);

            // North
            if (orientation == 0 && isValidLocation(x1, y1 + size)) {
                for (int i = 0; i < size; i++) {
                    if (isValidLocation(x1, y1 + i)) {
                        continue;
                    }
                }
                for (int i = 0; i < size; i++) {
                    ourGrid[x1][y1 + i] = 1;
                }

                coordinates[1] = Character.toString((char) (x1 + 'A'));
                coordinates[1] += y1 + size;
            } else if (orientation == 1 && isValidLocation(x1 + size, y1)) {
                for (int i = 0; i < size; i++) {
                    if (ourGrid[x1 + i][y1] != -1) {
                        coordinates = getRandomPos(size);
                        return coordinates;
                    }
                }
                for (int i = 0; i < size; i++) {
                    ourGrid[x1 + i][y1] = 1;
                }

                coordinates[1] = Character.toString((char) (x1 + size + 'A'));
                coordinates[1] += y1;
            } else if (orientation == 2 && isValidLocation(x1, y1 - size)) {
                for (int i = 0; i < size; i++) {
                    if (ourGrid[x1][y1 - 1] != -1) {
                        coordinates = getRandomPos(size);
                        return coordinates;
                    }
                }
                for (int i = 0; i < size; i++) {
                    ourGrid[x1][y1 - 1] = 1;
                }


                coordinates[1] = Character.toString((char) (x1 + 'A'));
                coordinates[1] += y1 - size;
            } else if (orientation == 3 && isValidLocation(x1 - size, y1)) {
                for (int i = 0; i < size; i++) {
                    if (ourGrid[x1 - i][y1] != -1) {
                        coordinates = getRandomPos(size);
                        return coordinates;
                    }
                }
                for (int i = 0; i < size; i++) {
                    ourGrid[x1 - i][y1] = 1;
                }

                coordinates[1] = Character.toString((char) (x1 - size + 'A'));
                coordinates[1] += y1;
            } else {
                coordinates = getRandomPos(size);
            }
        }

        return coordinates;
    }

    /**
     * @return best shot, highest probability
     */
    Point getBestShot() {
        // probability distribution of the grid
        int[][] pGrid = new int[8][8];

        // iterate over each position
        for (int i = 0; i < this.grid.length; i++) {
            for (int j = 0; j < this.grid[i].length; j++) {
                // put a little more weight on the top left squares because of the default positions
                if (i < 5 && j < 5) {
                    pGrid[i][j] += 5;
                }

                // test if we can fit the ships in this cell some possible ways
                int[] shipSizes 	= new int[]{2,3,3,4,5};
                int[] shipWeights 	= new int[]{5,3,4,3,2};
                boolean[] shipLives = new boolean[]{destroyerAlive, submarineAlive, cruiserAlive, battleshipAlive, carrierAlive};

                for (int k = 0; k < shipSizes.length; k++) {
                    // if ship is not alive continue, no need to update
                    if (!shipLives[k])
                        continue;

                    updateScoreLeftToRight(pGrid, i, j, shipSizes[k], shipWeights[k]);
                    updateScoreTopToBottom(pGrid, i, j, shipSizes[k], shipWeights[k]);
                }
            }
        }

        printPGrid(pGrid);
        return max(pGrid);
    }

    /**
     * @param pGrid
     * @return point coordinates with max score
     */
    Point max(int[][] pGrid) {
        // choose highest probability point
        int besti = 0;
        int bestj = 0;
        double best = 0d;

        for (int i = 0; i < pGrid.length; i++) {
            for (int j = 0; j < pGrid[i].length; j++) {
                if (pGrid[i][j] > best) {
                    besti = i;
                    bestj = j;
                    best = pGrid[i][j];
                }
            }
        }

        return new Point(besti, bestj);
    }

    void updateScoreLeftToRight(int[][] pGrid, int i, int j, int len, int weight) {
        updateScore(pGrid, i, j, len, 0, weight);
    }

    void updateScoreTopToBottom(int[][] pGrid, int i, int j, int len, int weight) {
        updateScore(pGrid, i, j, len, 1, weight);
    }

    /**
     * Determine score of placing a ship at this certain position
     * @param pGrid grid of scores
     * @param i		starting i index of ship
     * @param j		starting j index of ship
     * @param len	length of ship
     * @param dir	direction (0 = left to right, 1 = top to bottom)
     * @param weight arbitrary weight for the ship
     */
    void updateScore(int[][] pGrid, int i, int j, int len, int dir, int weight) {
        int score = 0;

        for (int k = 0; k < len; k++) {
            int val;
            try {
                switch (dir) {
                    case 0:
                        // left to right
                        val = this.grid[i][j + k];
                        break;
                    case 1:
                        // top to bottom
                        val = this.grid[i + k][j];
                        break;
                    default:
                        return;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                // not possible to fit ship here
                return;
            }

            // left and right
            switch (val) {
                case -1:
                    // no strike yet
                    score += 1;
                    break;
                case 1:
                    // square has a hit
                    score += 10;
                    break;
                default:
                    // on top of a miss square, or a sink
                    // not possible to place ship here
                    return;
            }
        }

        // return if we can't update them all
        if (score < 1) {
            return;
        }

        // now fill in pGrid with score
        for (int k = 0; k < len; k++) {
            switch (dir) {
                case 0:
                    // left to right
                    // increase the score of this cell if no shot has been taken towards it
                    pGrid[i][j + k] += this.grid[i][j + k] > -1 ? 0 : score + weight;
                    break;
                case 1:
                    // top to bottom
                    // increase the score of this cell if no shot has been taken towards it
                    pGrid[i + k][j] += this.grid[i + k][j] > -1 ? 0 : score + weight;
                    break;
                default:
                    return;
            }
        }
    }

    void determineSink(int i, int j) {
        // determine which ship was sunk
        // update values around to sink

    }

    void printPGrid(int[][] pGrid) {
        System.out.println("pGrid:");
        for (int i = 0; i < pGrid.length; i++) {
            for (int j = 0; j < pGrid[i].length; j++) {
                System.out.printf("|%5d  ", pGrid[i][j]);
            }
            System.out.print("|");

            System.out.print("\t\t\t");
            for (int j = 0; j < pGrid[i].length; j++) {
                System.out.printf("|%3d ", this.grid[i][j]);
            }
            System.out.print("|");

            System.out.println();
        }
        System.out.println("\n\n");
    }

    void recordMove(String wasHitSunkOrMiss, int i, int j) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.opponent + ".txt", true));
            PrintWriter s = new PrintWriter(bw);
            s.print(wasHitSunkOrMiss);
            s.print(this.letters[i]);
            s.print(String.valueOf(j));
            s.print('\n');
            s.close();
        }
        catch (Exception e) {

        }

    }

    ////////////////////////////////////// ^^^^^ PUT YOUR CODE ABOVE HERE ^^^^^ //////////////////////////////////////

    Socket socket;
    String[] destroyer, submarine, cruiser, battleship, carrier;

    String dataPassthrough;
    String data;
    BufferedReader br;
    PrintWriter out;
    Boolean moveMade = false;

    public Battleship() {
        this.grid = new int[8][8];
        this.ourGrid = new int[8][8];
        for(int i = 0; i < grid.length; i++) { for(int j = 0; j < grid[i].length; j++) grid[i][j] = -1; }
        this.letters = new char[] {'A','B','C','D','E','F','G','H'};

        destroyer = new String[] {"A0", "A0"};
        submarine = new String[] {"A0", "A0"};
        cruiser = new String[] {"A0", "A0"};
        battleship = new String[] {"A0", "A0"};
        carrier = new String[] {"A0", "A0"};
    }

    void connectToServer() {
        try {
            InetAddress addr = InetAddress.getByName(GAME_SERVER);
            socket = new Socket(addr, 23345);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.print(API_KEY);
            out.flush();
            data = br.readLine();
        } catch (Exception e) {
            System.out.println("Error: when connecting to the server...");
            socket = null;
        }

        if (data == null || data.contains("False")) {
            socket = null;
            System.out.println("Invalid API_KEY");
            System.exit(1); // Close Client
        }
    }



    public void gameMain() {
        while(true) {
            try {
                if (this.dataPassthrough == null) {
                    this.data = this.br.readLine();
                }
                else {
                    this.data = this.dataPassthrough;
                    this.dataPassthrough = null;
                }
            } catch (IOException ioe) {
                System.out.println("IOException: in gameMain");
                ioe.printStackTrace();
            }
            if (this.data == null) {
                try { this.socket.close(); }
                catch (IOException e) { System.out.println("Socket Close Error"); }
                return;
            }

            if (data.contains("Welcome")) {
                String[] welcomeMsg = this.data.split(":");
                placeShips(welcomeMsg[1]);
                if (data.contains("Destroyer")) { // Only Place Can Receive Double Message, Pass Through
                    this.dataPassthrough = "Destroyer(2):";
                }
            } else if (data.contains("Destroyer")) {
                this.out.print(destroyer[0]);
                this.out.print(destroyer[1]);
                out.flush();
            } else if (data.contains("Submarine")) {
                this.out.print(submarine[0]);
                this.out.print(submarine[1]);
                out.flush();
            } else if (data.contains("Cruiser")) {
                this.out.print(cruiser[0]);
                this.out.print(cruiser[1]);
                out.flush();
            } else if (data.contains("Battleship")) {
                this.out.print(battleship[0]);
                this.out.print(battleship[1]);
                out.flush();
            } else if (data.contains("Carrier")) {
                this.out.print(carrier[0]);
                this.out.print(carrier[1]);
                out.flush();
            } else if (data.contains( "Enter")) {
                this.moveMade = false;
                this.makeMove();
            } else if (data.contains("Error" )) {
                System.out.println("Error: " + data);
                System.exit(1); // Exit sys when there is an error
            } else if (data.contains("Die" )) {
                System.out.println("Error: Your client was disconnected using the Game Viewer.");
                System.exit(1); // Close Client
            } else {
                System.out.println("Received Unknown Response:" + data);
                System.exit(1); // Exit sys when there is an unknown response
            }
        }
    }

    void placeDestroyer(String startPos, String endPos) {
        destroyer = new String[] {startPos.toUpperCase(), endPos.toUpperCase()};
    }

    void placeSubmarine(String startPos, String endPos) {
        submarine = new String[] {startPos.toUpperCase(), endPos.toUpperCase()};
    }

    void placeCruiser(String startPos, String endPos) {
        cruiser = new String[] {startPos.toUpperCase(), endPos.toUpperCase()};
    }

    void placeBattleship(String startPos, String endPos) {
        battleship = new String[] {startPos.toUpperCase(), endPos.toUpperCase()};
    }

    void placeCarrier(String startPos, String endPos) {
        carrier = new String[] {startPos.toUpperCase(), endPos.toUpperCase()};
    }

    String placeMove(String pos) {
        if(this.moveMade) { // Check if already made move this turn
            System.out.println("Error: Please Make Only 1 Move Per Turn.");
            System.exit(1); // Close Client
        }
        this.moveMade = true;

        this.out.print(pos);
        out.flush();
        try { data = this.br.readLine(); }
        catch(Exception e) { System.out.println("No response after from the server after place the move"); }

        if (data.contains("Hit")) return "Hit";
        else if (data.contains("Sunk")) return "Sunk";
        else if (data.contains("Miss")) return "Miss";
        else {
            this.dataPassthrough = data;
            return "Miss";
        }
    }

    public static void main(String[] args) {
        Battleship bs = new Battleship();
        while(true) {
            bs.connectToServer();
            if (bs.socket != null) bs.gameMain();
        }
    }
}

