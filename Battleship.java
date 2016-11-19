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
import java.lang.Thread;
import java.util.ArrayList;

public class Battleship {
	public static String API_KEY = "487674642"; ///////// PUT YOUR API KEY HERE /////////
	public static String GAME_SERVER = "battleshipgs.purduehackers.com";
	public String opponent;
	public File opponentFile;

	//////////////////////////////////////  PUT YOUR CODE HERE //////////////////////////////////////

	char[] letters;
	int[][] grid;
    boolean destroyerAlive = true;
    boolean submarineAlive = true;
    boolean cruiseAlive = true;
    boolean battleshipAlive = true;
    boolean carrierAlive = true;
	int[][] ourGrid;

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

	void placeShips(String opponentID) {
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
		String[] pos;

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
		placeCarrier(pos[0], pos[1]);

        /*
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
		}*/
	}

	void makeMove() {

		Point p = getBestShot();
		int shoti = (int)p.getX();
		int shotj = (int)p.getY();

		switch (move()) {
			case 0:	shoti = 2; shotj = 2; break;
			case 1: shoti = 5; shotj = 2; break;
			case 2: shoti = 2; shotj = 5; break;
			case 3: shoti = 5; shotj = 5; break;
			default: break;
		}

		String wasHitSunkOrMiss = placeMove(this.letters[shoti] + String.valueOf(shotj));

		if (wasHitSunkOrMiss.equals("Sunk")) {
			determineSink(shoti, shotj);
			this.grid[shoti][shotj] = 2;
		} else if (wasHitSunkOrMiss.equals("Hit")) {
			this.grid[shoti][shotj] = 1;
		} else {
			this.grid[shoti][shotj] = 0;
		}
		return;
	}

	int move() {
		int move = 0;
		for (int i = 0; i < this.grid.length; i++) {
			for (int j = 0; j < this.grid[i].length; j++) {
				if (this.grid[i][j] > -1)
					move++;
			}
		}
		return move;
	}

	Point getBestShot() {
		// probability distribution of the grid
		double[][] pGrid = new double[8][8];

		// iterate over each position
		for (int i = 0; i < this.grid.length; i++) {
			for (int j = 0; j < this.grid[i].length; j++) {
				// if cell is hit, zero probability
				if (this.grid[i][j] > -1) {
					pGrid[i][j] = 0;
					continue;
				}

				if (i < 5 && j < 5) {
					pGrid[i][j] += 5;
				}

				// test if we can fit the ships in this cell some possible ways
				// test Destroyer	size 2
				int size = 2;
				int weight = 5;
				if (destroyerAlive) {

					updatePGrid(pGrid, i, j, weight, determinePlacementRight(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementDown(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementLeft(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementUp(i, j, size));
				}

				// test Submarine	size 3
				size = 3;
				weight = 3;
				if (submarineAlive) {
					updatePGrid(pGrid, i, j, weight, determinePlacementRight(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementDown(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementLeft(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementUp(i, j, size));
				}

				// test Cruiser		size 3
				size = 3;
				weight = 4;
				if (cruiseAlive) {
					updatePGrid(pGrid, i, j, weight, determinePlacementRight(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementDown(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementLeft(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementUp(i, j, size));
				}

				// test Battleship 	size 4
				size = 4;
				weight = 3;
				if (battleshipAlive) {
					updatePGrid(pGrid, i, j, weight, determinePlacementRight(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementDown(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementLeft(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementUp(i, j, size));
				}

				// test Carrier 	size 5
				size = 5;
				weight = 2;
				if (carrierAlive) {
					updatePGrid(pGrid, i, j, weight, determinePlacementRight(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementDown(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementLeft(i, j, size));
					updatePGrid(pGrid, i, j, weight, determinePlacementUp(i, j, size));
				}

			}
		}

		printPGrid(pGrid);

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

	void updatePGrid(double[][] pGrid, int i, int j, int weight, int place) {
		if (place > 0) {
			pGrid[i][j] += place + weight;
		}
	}

	// 0 if we can place it no conflict
	// 1 if we place it on a hit spot, no sink confirmed
	// 2 if we place it on a hit spot, sink confirmed
	int determinePlacementDown(int i, int j, int len) {
		int placement = 0;

		for (int k = 0; k < len; k++) {
			if (i + k >= this.grid.length) {
				return -1;
			}

			// up and down
			switch (this.grid[i + k][j]) {
				case 1:
					placement += 10;
					break;
				case 2:
					return -1;
				default:
					placement += 1;
					break;
			}
		}

		return placement;
	}

	// 0 if we can place it no conflict
	// 1 if we place it on a hit spot, no sink confirmed
	// 2 if we place it on a hit spot, sink confirmed
	int determinePlacementUp(int i, int j, int len) {
		int placement = 0;

		for (int k = 0; k < len; k++) {
			if (i - k < 0) {
				return -1;
			}

			// up and down
			switch (this.grid[i - k][j]) {
				case 1:
					placement += 10;
					break;
				case 2:
					return -1;
				default:
					placement += 1;
					break;
			}
		}

		return placement;
	}

	// 0 if we can place it no conflict
	// 1 if we place it on a hit spot, no sink confirmed
	// 2 if we place it on a hit spot, sink confirmed
	int determinePlacementRight(int i, int j, int len) {
		int placement = 0;

		for (int k = 0; k < len; k++) {
			if (j + k >= this.grid[i].length) {
				return -1;
			}

			// left and right
			switch (this.grid[i][j + k]) {
				case 1:
					placement += 10;
					break;
				case 2:
					return -1;
				default:
					placement += 1;
					break;
			}
		}

		return placement;
	}
	// 0 if we can place it no conflict
	// 1 if we place it on a hit spot, no sink confirmed
	// 2 if we place it on a hit spot, sink confirmed
	int determinePlacementLeft(int i, int j, int len) {
		int placement = 0;

		for (int k = 0; k < len; k++) {
			if (j - k < 0) {
				return -1;
			}

			// left and right
			switch (this.grid[i][j - k]) {
				case 1:
					placement += 10;
					break;
				case 2:
					return -1;
				default:
					placement += 1;
					break;
			}
		}

		return placement;
	}

	void determineSink(int i, int j) {
		// determine which ship was sunk
		// update values around to sink

	}

	void printPGrid(double[][] pGrid) {
		System.out.println("pGrid:");
		for (int i = 0; i < pGrid.length; i++) {
			for (int j = 0; j < pGrid[i].length; j++) {
				System.out.printf("|%4.0f  ", pGrid[i][j]);
			}
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

