/**
 * @ AUTHOR NAME HERE
 * @ Starter Code By Guocheng
 *
 * 2016-01-30
 * For: Purdue Hackers - Battleship
 * Battleship Client
 */

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

		/*placeDestroyer("D6", "D7"); // size 2
		placeSubmarine("E5", "E7"); // size 3
		placeCruiser("F5", "F7");   // size 3
		placeBattleship("G4", "G7");// size 4
		placeCarrier("H3", "H7");	// size 5*/
	}

	void makeMove() {

		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if (this.grid[i][j] == -1) {
					String wasHitSunkOrMiss = placeMove(this.letters[i] + String.valueOf(j));
					recordMove(wasHitSunkOrMiss, i, j);
					if (wasHitSunkOrMiss.equals("Hit") || wasHitSunkOrMiss.equals("Sunk")) {
						this.grid[i][j] = 1;
					} else {
						this.grid[i][j] = 0;			
					}
					return;
				}
			}
		}
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

