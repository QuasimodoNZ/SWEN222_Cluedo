import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JTextArea;

public class Board {
	private Location[][] locations;
	private List<Player> players;
	private Enums.Weapon weaponSolution;
	private Enums.RoomName roomSolution;
	private Enums.Character characterSolution;
	private String GUIMove;
	private Map<Enums.Weapon, Point> weaponLocations;
	private boolean gameWon = false;

	private Player currentPlayer = null;
	private int movesLeft = 0;
	private JTextArea textOutput;

	/**
	 *********** Board Constructor ***********
	 */
	public Board() {
		players = new LinkedList<Player>();
	}

	public void startBoard(Scanner inputReader) {
		// Asks user for the number of players
		System.out.println("Welcome to Cludo");
		System.out.println("Please enter the number of players (3-6)");

		while (inputReader.hasNext()) {
			int num = inputReader.nextInt();
			inputReader.nextLine();
			// Checks for a valid number
			if (!(3 <= num && num <= 6)) {
				System.out.println("Invaild player range");
				continue;
			}
			// Adds human players
			System.out.println("Choose from these players:");
			for (Enums.Character c : Enums.Character.toList())
				System.out.println("\t" + c.toString());
			System.out.println();
			while (num > 0) {
				System.out.println("Enter Player " + num + "'s character:");
				if (addPlayer(inputReader.nextLine().toLowerCase(), true)) {
					num--;
				} else {
					System.out.println("Invalid Player Entry");
				}
			}
			break;
		}

		if (players.size() < 6) {
			// Adds the unused characters
			for (Enums.Character c : Enums.Character.toList()) {
				addPlayer(c.toString(), false);
			}
		}

		dealCards();

		locations = newBoard();
		weaponLocations = new HashMap<Enums.Weapon, Point>();
		for (int i = 0; i < Enums.Weapon.toList().size(); i++)
			weaponLocations.put(Enums.Weapon.toList().get(i), new Point(11 + i,
					12));
	}

	public boolean addPlayer(String name, boolean iC) {
		try {
			Enums.Character character = Enums.Character.toEnum(name);
			if (unselected(character)) {
				Player p = new Player(character, iC);
				players.add(p);
				System.out.println(character.name() + " Added");
				return true;
			} else {
				return false;
			}
		} catch (IllegalArgumentException e) {
			return false;
		}

	}

	// Checks all of the players to see if they already have that Character
	public boolean unselected(Enums.Character character) {
		for (Player p : players) {
			if (p.getCharacter().equals(character)) {
				return false;
			}
		}
		return true;
	}

	public void dealCards() {
		List<Enums.Weapon> weaponCards = Enums.Weapon.toList();
		List<Enums.RoomName> roomCards = Enums.RoomName.toList();
		for (int i = 0; i < roomCards.size(); i++)
			if (roomCards.get(i) == Enums.RoomName.SWIMMING_POOL) {
				roomCards.remove(i);
				break;
			}

		List<Enums.Character> characterCards = Enums.Character.toList();

		Collections.shuffle(weaponCards);
		Collections.shuffle(roomCards);
		Collections.shuffle(characterCards);

		weaponSolution = weaponCards.remove(0);
		roomSolution = roomCards.remove(0);
		characterSolution = characterCards.remove(0);

		// Loops through all the players adding cards until all the lists are
		// empty
		int i = 0;
		while (true)
			for (Player p : players)
				if (p.isControlled()) {
					if (weaponCards.size() > 0)
						p.getWeaponCards().add(weaponCards.remove(0));
					else if (characterCards.size() > 0)
						p.getCharacterCards().add(characterCards.remove(0));
					else if (roomCards.size() > 0)
						p.getRoomCards().add(roomCards.remove(0));
					else
						return;
				}

	}

	private Location[][] newBoard() {
		Location[][] board = new Location[29][29];

		// Initialise room to be null
		for (int x = 0; x < board.length; x++)
			for (int y = 0; y < board[0].length; y++)
				board[x][y] = new Location(new Point(x, y), null);

		// Intitialises all the rooms by marking the locations that are owned by
		// this room, marking the locations that the players will be on when in
		// the room and the doors within this room (not the passageways)

		// The Spa
		List<Location> roomLocations = new ArrayList<Location>(6);
		Room room = new Room(Enums.RoomName.SPA, roomLocations);
		for (int x = 0; x < 5; x++)
			for (int y = 0; y < 8; y++)
				board[x][y] = new Location(new Point(x, y), room);
		for (int y = 0; y < 6; y++)
			board[5][y] = new Location(new Point(5, y), room);

		for (int x = 2; x < 4; x++)
			for (int y = 2; y < 5; y++)
				roomLocations.add(board[x][y]);

		List<Location> firstList = new LinkedList<Location>();
		firstList.add(board[5][6]);
		List<Location> secondList = new LinkedList<Location>();
		secondList.addAll(board[4][5].getRoom().getLocations());
		new Door("spa door", firstList, secondList);

		// The Theatre and its locations
		roomLocations = new ArrayList<Location>(6);
		room = new Room(Enums.RoomName.THEATRE, roomLocations);

		for (int x = 8; x < 13; x++)
			for (int y = 0; y < 8; y++)
				board[x][y] = new Location(new Point(x, y), room);
		
		for (int x = 9; x < 12; x++)
			for (int y = 3; y < 5; y++)
				roomLocations.add(board[x][y]);

		firstList = new LinkedList<Location>();
		firstList.add(board[10][8]);
		secondList = new LinkedList<Location>();
		secondList.addAll(board[10][7].getRoom().getLocations());
		new Door("theatre door", firstList, secondList);

		// The Living Room
		roomLocations = new ArrayList<Location>(6);
		room = new Room(Enums.RoomName.LIVING_ROOM, roomLocations);

		for (int x = 14; x < 20; x++)
			for (int y = 0; y < 8; y++)
				board[x][y] = new Location(new Point(x, y), room);
		for (int x = 15; x < 18; x++)
			board[x][8] = new Location(new Point(x, 8), room);

		for (int x = 15; x < 18; x++)
			for (int y = 3; y < 5; y++)
				roomLocations.add(board[x][y]);

		firstList = new LinkedList<Location>();
		firstList.add(board[16][9]);
		secondList = new LinkedList<Location>();
		secondList.addAll(board[16][8].getRoom().getLocations());
		new Door("living room door", firstList, secondList);

		// The Observatory
		roomLocations = new ArrayList<Location>(6);
		room = new Room(Enums.RoomName.OBSERVATORY, roomLocations);
		for (int x = 22; x < 29; x++)
			for (int y = 0; y < 9; y++)
				board[x][y] = new Location(new Point(x, y), room);

		for (int x = 24; x < 26; x++)
			for (int y = 3; y < 6; y++)
				roomLocations.add(board[x][y]);

		firstList = new LinkedList<Location>();
		firstList.add(board[21][8]);
		secondList = new LinkedList<Location>();
		secondList.addAll(board[22][8].getRoom().getLocations());
		new Door("observatory door", firstList, secondList);

		// The Patio
		roomLocations = new ArrayList<Location>(6);
		room = new Room(Enums.RoomName.PATIO, roomLocations);
		for (int x = 0; x < 4; x++)
			for (int y = 10; y < 19; y++)
				board[x][y] = new Location(new Point(x, y), room);
		for (int x = 4; x < 8; x++)
			for (int y = 11; y < 18; y++)
				board[x][y] = new Location(new Point(x, y), room);

		for (int x = 2; x < 5; x++)
			for (int y = 13; y < 15; y++)
				roomLocations.add(board[x][y]);

		firstList = new LinkedList<Location>();
		firstList.add(board[5][10]);
		secondList = new LinkedList<Location>();
		secondList.addAll(board[4][14].getRoom().getLocations());
		new Door("north patio door", firstList, secondList);
		firstList = new LinkedList<Location>();
		firstList.add(board[5][18]);
		new Door("south patio door", firstList, secondList);

		// The Swimming Pool
		roomLocations = new ArrayList<Location>(6);
		room = new Room(Enums.RoomName.SWIMMING_POOL, roomLocations);
		for (int x = 10; x < 19; x++)
			for (int y = 11; y < 17; y++)
				board[x][y] = new Location(new Point(x, y), room);

		for (int x = 13; x < 16; x++)
			for (int y = 13; y < 15; y++)
				roomLocations.add(board[x][y]);

		firstList = new LinkedList<Location>();
		firstList.add(board[14][10]);
		secondList = new LinkedList<Location>();
		secondList.addAll(board[14][14].getRoom().getLocations());
		new Door("north swimming pool door", firstList, secondList);
		firstList = new LinkedList<Location>();
		firstList.add(board[10][17]);
		new Door("southwest swimming pool door", firstList, secondList);
		firstList = new LinkedList<Location>();
		firstList.add(board[17][17]);
		new Door("southeast swimming pool door", firstList, secondList);

		// The Hall
		roomLocations = new ArrayList<Location>(6);
		room = new Room(Enums.RoomName.HALL, roomLocations);
		for (int x = 20; x < 29; x++)
			for (int y = 11; y < 18; y++)
				board[x][y] = new Location(new Point(x, y), room);

		for (int x = 22; x < 25; x++)
			for (int y = 14; y < 16; y++)
				roomLocations.add(board[x][y]);

		firstList = new LinkedList<Location>();
		firstList.add(board[22][10]);
		secondList = new LinkedList<Location>();
		secondList.addAll(board[25][14].getRoom().getLocations());
		new Door("north hall door", firstList, secondList);
		firstList = new LinkedList<Location>();
		firstList.add(board[19][15]);
		new Door("west hall door", firstList, secondList);

		// The Kitchen
		roomLocations = new ArrayList<Location>(6);
		room = new Room(Enums.RoomName.KITCHEN, roomLocations);
		for (int x = 0; x < 7; x++)
			for (int y = 21; y < 29; y++)
				board[x][y] = new Location(new Point(x, y), room);
		board[6][21] = new Location(new Point(6, 21), null);

		for (int x = 2; x < 5; x++)
			for (int y = 24; y < 26; y++)
				roomLocations.add(board[x][y]);

		firstList = new LinkedList<Location>();
		firstList.add(board[6][21]);
		secondList = new LinkedList<Location>();
		secondList.addAll(board[6][22].getRoom().getLocations());
		new Door("kitchen door", firstList, secondList);

		// The Dining Room
		roomLocations = new ArrayList<Location>(6);
		room = new Room(Enums.RoomName.DINING_ROOM, roomLocations);

		for (int x = 9; x < 17; x++)
			for (int y = 23; y < 29; y++)
				board[x][y] = new Location(new Point(x, y), room);
		for (int x = 10; x < 16; x++)
			for (int y = 19; y < 23; y++)
				board[x][y] = new Location(new Point(x, y), room);

		for (int x = 12; x < 14; x++)
			for (int y = 22; y < 25; y++)
				roomLocations.add(board[x][y]);

		firstList = new LinkedList<Location>();
		firstList.add(board[12][18]);
		secondList = new LinkedList<Location>();
		secondList.addAll(board[13][22].getRoom().getLocations());
		new Door("north dining room door", firstList, secondList);
		firstList = new LinkedList<Location>();
		firstList.add(board[16][22]);
		new Door("east dining room door", firstList, secondList);

		// The Guest House
		roomLocations = new ArrayList<Location>(6);
		room = new Room(Enums.RoomName.GUEST_HOUSE, roomLocations);

		for (int x = 20; x < 29; x++)
			for (int y = 20; y < 29; y++)
				board[x][y] = new Location(new Point(x, y), room);

		for (int x = 23; x < 25; x++)
			for (int y = 23; y < 26; y++)
				roomLocations.add(board[x][y]);

		board[20][20] = new Location(new Point(20, 20), null);

		firstList = new LinkedList<Location>();
		firstList.add(board[20][20]);
		secondList = new LinkedList<Location>();
		secondList.addAll(board[22][22].getRoom().getLocations());
		new Door("guest house door", firstList, secondList);

		// Secret passage way between the spa and guest house
		firstList = new LinkedList<Location>();
		firstList.addAll(board[4][5].getRoom().getLocations());
		secondList = new LinkedList<Location>();
		secondList.addAll(board[22][22].getRoom().getLocations());
		new Door("secret passageway", firstList, secondList);

		// Secret passage way between the observatory and kitchen
		firstList = new LinkedList<Location>();
		firstList.addAll(board[22][8].getRoom().getLocations());
		secondList = new LinkedList<Location>();
		secondList.addAll(board[6][22].getRoom().getLocations());
		new Door("secret passageway", firstList, secondList);

		for (Player p : players)
			switch (p.getCharacter()) {
			case JACK_MUSTARD:
				p.moveLocation(board[7][28]);
				break;
			case KASANDRA_SCARLETT:
				p.moveLocation(board[18][28]);
				break;
			case DIANE_WHITE:
				p.moveLocation(board[0][19]);
				break;
			case JACOB_GREEN:
				p.moveLocation(board[0][9]);
				break;
			case ELEANOR_PEACOCK:
				p.moveLocation(board[6][0]);
				break;
			case VICTOR_PLUM:
				p.moveLocation(board[20][0]);
				break;
			}

		return board;
	}

	public void setGUIMove(String s) {
		GUIMove = s;
	}

	public String getGUIMove() {
		return GUIMove;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player p) {
		currentPlayer = p;
	}

	public void setMovesLeft(int ml) {
		movesLeft = ml;
	}

	public int getMovesLeft() {
		return movesLeft;
	}

	public boolean getGameWon() {
		return gameWon;
	}

	public String getOptions(Player player, int movesLeft) {
		// Gets the location of the current players character
		// Location location = player.getLocation();
		String options = "";

		options = options + "\tmove x y\n";
		Location loc = player.getLocation();
		Room room = loc.getRoom();
		if (room != null) {
			// The player is inside another room
			if (room.toString().equals(Enums.RoomName.SWIMMING_POOL.toString()))
				options += "\tAccuse\n";
			else
				options += "\tSuggest\n";
		}
		options = options + "\tEnd Turn\n";
		return options;
	}

	/**
	 * Runs a game until a player wins
	 * 
	 * @param boardPanel
	 */
	public void playGame(BoardPanel boardPanel) {
		(new GameThread(this, boardPanel)).start();
	}

	/**
	 * Executes the move specified by the player
	 * <p>
	 * returns 1 if the move is moving a step or going through a door or passage
	 * <p>
	 * returns 0 if the move is invalid and will usually print out a reason why
	 * <p>
	 * returns 12 if the player executes a suggestion or an accusation
	 * 
	 * @param player
	 * @param move
	 * @return
	 */
	public int playTurn(Player player, String move, int movesAvailable) {
		if (move.equalsIgnoreCase("end turn"))
			return 12;
		if (move.startsWith("move")) {
			Location nextLocation = locations[Integer
					.parseInt(move.split(" ")[1])][Integer.parseInt(move
					.split(" ")[2])];
			List<Location> path = AStar(player.getLocation().getPoint(),
					nextLocation.getPoint());
			if (path.size() <= movesAvailable && nextLocation != null
					&& path.size() != 0) {
				player.moveLocation(nextLocation);
				return path.size();
			}
			System.out.println("That move is unavailable");
			if (textOutput != null)
				textOutput
						.setText("That is out of range or you cannot move there.\nTry again");
			return 0;
		}
		if ((move.startsWith("accuse") || move.startsWith("Accuse"))
				&& player.getLocation().getRoom().toString()
						.equals("Swimming Pool")) {
			BufferedReader input = new BufferedReader(new InputStreamReader(
					System.in));

			Enums.Character character = null;
			Enums.Weapon weapon = null;
			Enums.RoomName room = null;
			if (textOutput == null)
				try {
					System.out.println("Who do you think did it?");
					for (Enums.Character c : Enums.Character.toList())
						System.out.println("\t" + c.toString());
					character = Enums.Character.toEnum(input.readLine());

					System.out.println("What did they use?");
					for (Enums.Weapon c : Enums.Weapon.toList())
						System.out.println("\t" + c.toString());
					weapon = Enums.Weapon.toEnum(input.readLine());

					System.out.println("Where did they do it?");
					for (Enums.RoomName c : Enums.RoomName.toList())
						System.out.println("\t" + c.toString());
					room = Enums.RoomName.toEnum(input.readLine());
				} catch (IOException e) {
					System.out.println("Sorry, something has gone wrong.");
					return 0;
				} catch (IllegalArgumentException e) {
					System.out
							.println("Sorry, that is not an option. Maybe you spelt it wrong.");
					return 0;
				}
			else {
				for (Enums.Character c : Enums.Character.toList())
					if (move.contains(c.toString())) {
						character = c;
						break;
					}
				for (Enums.Weapon w : Enums.Weapon.toList())
					if (move.contains(w.toString())) {
						weapon = w;
						break;
					}
				for (Enums.RoomName rn : Enums.RoomName.toList())
					if (move.contains(rn.toString())) {
						room = rn;
						break;
					}
			}

			weaponLocations.put(weapon, player.getLocation().getPoint());
			for (Player p : players) {
				if (p.getCharacter() == character
						&& p.getLocation().getRoom() != player.getLocation()
								.getRoom()) {
					p.moveRoom(player.getLocation().getRoom());
					weaponLocations
							.put(weapon, player.getLocation().getPoint());
					break;
				}
			}

			String toBePrinted = "";
			if (room == roomSolution && weapon == weaponSolution
					&& character == characterSolution) {
				toBePrinted += player.toString() + " has won the game!\n";

			} else {
				toBePrinted += player.toString()
						+ " made a wrong accusation and has been kicked from the game\n";

				player.setControl(false);

				int numOfPlayersLeft = 0;
				Player winningPlayer = null;
				for (Player p : players)
					if (p.isControlled()) {
						numOfPlayersLeft++;
						winningPlayer = p;
					}
				if (numOfPlayersLeft == 1) {
					toBePrinted += winningPlayer.toString()
							+ " has won the game!\n";
				}

				toBePrinted += "These are their cards\nCharacter's\n";
				for (Enums.Character c : player.getCharacterCards())
					toBePrinted += c.toString() + "\n";
				toBePrinted += "Room's\n";
				for (Enums.RoomName rn : player.getRoomCards())
					toBePrinted += rn.toString() + "\n";
				toBePrinted += "Weapon's\n";
				for (Enums.Weapon w : player.getWeaponCards())
					toBePrinted += w.toString() + "\n";
			}
			System.out.println(toBePrinted);
			if (textOutput != null)
				textOutput.setText(toBePrinted);

			return 12;
			// checks if the player has won, return a special integer based on
			// outcome for playGame() to deal with
		}
		if ((move.startsWith("suggest") || move.startsWith("Suggest"))
				&& player.getLocation().getRoom() != null) {
			BufferedReader input = new BufferedReader(new InputStreamReader(
					System.in));

			Enums.Character suggestedCharacter = null;
			Enums.Weapon suggestedWeapon = null;

			if (textOutput == null)
				try {
					// Initates the suggestion
					// Reads in the
					System.out.println("Who do you think did it?");
					for (Enums.Character c : Enums.Character.toList())
						System.out.println("\t" + c.toString());
					suggestedCharacter = Enums.Character.toEnum(input
							.readLine());
					System.out.println("What did they use?");
					for (Enums.Weapon c : Enums.Weapon.toList())
						System.out.println("\t" + c.toString());
					suggestedWeapon = Enums.Weapon.toEnum(input.readLine());
					weaponLocations.put(suggestedWeapon, player.getLocation()
							.getPoint());

				} catch (IllegalArgumentException e) {
					System.out
							.println("Sorry, that is not an option. Maybe you spelt it wrong.");
					e.printStackTrace();
				} catch (IOException e) {
					System.out
							.println("Sorry, something seems to have gone wrong.");
					e.printStackTrace();
				}
			else {
				for (Enums.Character c : Enums.Character.toList())
					if (move.contains(c.toString())) {
						suggestedCharacter = c;
						break;
					}
				for (Enums.Weapon w : Enums.Weapon.toList())
					if (move.contains(w.toString())) {
						suggestedWeapon = w;
						break;
					}
			}

			Room suggestedRoom = player.getLocation().getRoom();

			// Moves the suggested player to the current room
			if (suggestedRoom == null)
				throw new IllegalArgumentException();
			for (Player p : players) {
				if (p.getCharacter() == suggestedCharacter
						&& p.getLocation().getRoom() != suggestedRoom) {
					p.moveRoom(suggestedRoom);
					weaponLocations.put(suggestedWeapon, player.getLocation()
							.getPoint());
					break;
				}
			}

			// Searchs through every player's hand. If a card is found that
			// refutes the suggestion then only one card will be displayed
			// (if the player has another card that can refute the
			// suggestion it will not be announced)
			boolean characterRefuted = false;
			boolean roomRefuted = false;
			boolean weaponRefuted = false;
			for (Player p : players) {
				if (p != player && p.isControlled()) {
					boolean foundSuggestion = false;
					if (!characterRefuted
							&& p.getCharacterCards().contains(
									suggestedCharacter)) {
						foundSuggestion = true;
						characterRefuted = true;
					}
					if (!foundSuggestion && !roomRefuted
							&& p.getRoomCards().contains(suggestedRoom)) {
						foundSuggestion = true;
						roomRefuted = true;
					}
					if (!foundSuggestion && !weaponRefuted
							&& p.getWeaponCards().contains(suggestedWeapon)) {
						weaponRefuted = true;
					}
				}
			}

			String toBePrinted = "";
			// Displays whether the suggestions were refuted or not.
			if (characterRefuted)
				toBePrinted += String.format("Character %s has been refuted\n",
						suggestedCharacter);
			else
				toBePrinted += String.format("Character %s was not refuted\n",
						suggestedCharacter);

			if (roomRefuted)
				toBePrinted += String.format("Room %s has been refuted\n",
						suggestedRoom);
			else
				toBePrinted += String.format("Room %s was not refuted\n",
						suggestedRoom);

			if (weaponRefuted)
				toBePrinted += String.format("Weapon %s has been refuted\n",
						suggestedWeapon);
			else
				toBePrinted += String.format("Weapon %s was not refuted\n",
						suggestedWeapon);

			System.out.println(toBePrinted);
			if (textOutput != null)
				textOutput.setText(toBePrinted);

			// Returns 12 so that the player has no more moves left
			return 12;
		}

		System.out.println("Your available moves are:\n");
		return 0;
	}

	public void drawBoard() {
		String s = "  ";
		for (int i = 0; i < locations.length; i++) {
			s += String.format("%2d", i);
		}
		s += "\n  ";
		for (int i = 0; i < locations.length; i++) {
			s += " _";
		}
		s += "\n";
		for (int y = 0; y < locations[0].length; y++) {
			for (int x = 0; x < locations.length; x++) {
				if (x == 0) {
					s += String
							.format("%2d|%s ", y, locations[x][y].toString());
				} else if (x == locations.length - 1) {
					s += String.format("%s|", locations[x][y].toString());
				} else {
					s += String.format("%s ", locations[x][y].toString());
				}
			}
			s += "\n";
		}
		System.out.println(s);
	}

	public void drawGUI(Graphics g) {
		if (locations != null) {
			/*g.setColor(new Color(0, 0, 255, 100));
			for (Location[] colomn : locations)
				for (Location l : colomn)
					if (l.getRoom() != null)
						g.fillRect(l.getPoint().x * 27 + 3,
								l.getPoint().y * 27 + 3, 24, 24);*/

			g.setColor(new Color(0, 255, 0, 100));
			for (Location[] colomn : locations)
				for (Location l : colomn)
					if (l.getDoors().size() > 0)
						g.fillRect(l.getPoint().x * 27 + 3,
								l.getPoint().y * 27 + 3, 24, 24);

			g.setColor(new Color(255, 0, 0, 100));
			if (currentPlayer != null) {
				for (Location l : getFringe(movesLeft,
						currentPlayer.getLocation()))
					g.fillRect(l.getPoint().x * 27 + 3,
							l.getPoint().y * 27 + 3, 24, 24);
			}
			for (Player p : players) {
				p.drawGUI(g);
			}
			for (Map.Entry<Enums.Weapon, Point> pair : weaponLocations
					.entrySet()) {
				try {
					g.drawImage(
							ImageIO.read(new File("Images/"
									+ pair.getKey().toString() + ".png")),
							pair.getValue().x * 27 + 3,
							pair.getValue().y * 27 + 3, null);
				} catch (IOException e) {
					System.out.println("Cannot draw weapon: "
							+ pair.getKey().toString());
					e.printStackTrace();
				}
			}
		}
	}

	public List<Player> getPlayers() {
		return this.players;
	}

	/**
	 * Returns a list of locations that can be reached starting from the
	 * argument location
	 * 
	 * @param numOfMoves
	 * @param startLocation
	 * @return
	 */
	public Set<Location> getFringe(int numOfMoves, Location startLocation) {

		// Initialises the list that will be returned
		Set<Location> availableOptions = new HashSet<Location>();

		Queue<FringeElement> fringe = new PriorityQueue<FringeElement>();
		fringe.add(new FringeElement(startLocation, numOfMoves));
		while (!fringe.isEmpty()) {
			FringeElement current = fringe.poll();
			if (!availableOptions.contains(current.location)) {
				availableOptions.add(current.location);
				if (current.steps > 0) {
					int x = current.location.getPoint().x;
					int y = current.location.getPoint().y;
					if (x > 0 && locations[x - 1][y] != null
							&& locations[x - 1][y].getRoom() == null)
						fringe.add(new FringeElement(locations[x - 1][y],
								current.steps - 1));
					if (x < locations.length - 1 && locations[x + 1][y] != null
							&& locations[x + 1][y].getRoom() == null)
						fringe.add(new FringeElement(locations[x + 1][y],
								current.steps - 1));
					if (y > 0 && locations[x][y - 1] != null
							&& locations[x][y - 1].getRoom() == null)
						fringe.add(new FringeElement(locations[x][y - 1],
								current.steps - 1));
					if (y < locations[0].length - 1
							&& locations[x][y + 1] != null
							&& locations[x][y + 1].getRoom() == null)
						fringe.add(new FringeElement(locations[x][y + 1],
								current.steps - 1));

					for (Door d : locations[x][y].getDoors()) {
						if (d.getFirstList().contains(locations[x][y]))
							for (Location l : d.getSecondList())
								fringe.add(new FringeElement(l,
										current.steps - 1));
						else
							for (Location l : d.getFirstList())
								fringe.add(new FringeElement(l,
										current.steps - 1));
					}

				}
			}
		}

		return availableOptions;
	}

	/**
	 * 
	 * @param start
	 *            location
	 * @param end
	 *            end location
	 * @return number of moves taken (-1 if not possible)
	 */
	public List<Location> AStar(Point start, Point end) {
		// Keeps track of what points have been visited
		Set<Point> visited = new HashSet<Point>();

		// put the start on
		// while the queue is not empty, dequeue node
		// if node == goal, return path
		// if node has been visited skip
		// mark node as visited
		// put all neighbours on

		// Creates the fringe and adds the starting point on, the queue is
		// prioritised by the heuristic
		Queue<QueueElement> fringe = new PriorityQueue<QueueElement>();
		fringe.add(new QueueElement(start, null, 0, start.distance(end)));

		// If the fringe is empty then there is no path to the end point
		while (!fringe.isEmpty()) {
			// Polls the closest point to the end so that it can be processed
			QueueElement qe = fringe.poll();
			// If the point is the end then it needs to work its way back to the
			// start noting the shortest path taken
			if (qe.getNode().equals(end)) {
				// This is the path that is to be returned
				List<Location> path = new LinkedList<Location>();
				Point currentNode = qe.getNode();
				QueueElement from = qe.getFrom();
				while (from != null) {
					path.add(locations[currentNode.x][currentNode.y]);
					currentNode = from.getNode();
					from = from.getFrom();
				}
				// path.add(locations[currentNode.x][currentNode.y]); // Adding
				// the start node to path
				Collections.reverse(path);
				return path;
			}
			if (!visited.contains(qe.getNode())) {
				visited.add(qe.getNode());

				int x = qe.getNode().x;
				int y = qe.getNode().y;
				if (x > 0 && locations[x - 1][y] != null
						&& locations[x - 1][y].getRoom() == null
						&& locations[x - 1][y].getCharacter() == null)
					fringe.add(new QueueElement(new Point(x - 1, y), qe, qe
							.getCost() + 1, qe.getCost() + 1
							+ new Point(x, y).distance(end)));
				if (x < locations.length - 1 && locations[x + 1][y] != null
						&& locations[x + 1][y].getRoom() == null
						&& locations[x + 1][y].getCharacter() == null)
					fringe.add(new QueueElement(new Point(x + 1, y), qe, qe
							.getCost() + 1, qe.getCost() + 1
							+ new Point(x + 1, y).distance(end)));
				if (y > 0 && locations[x][y - 1] != null
						&& locations[x][y - 1].getRoom() == null
						&& locations[x][y - 1].getCharacter() == null)
					fringe.add(new QueueElement(new Point(x, y - 1), qe, qe
							.getCost() + 1, qe.getCost() + 1
							+ new Point(x, y - 1).distance(end)));
				if (y < locations[0].length - 1 && locations[x][y + 1] != null
						&& locations[x][y + 1].getRoom() == null
						&& locations[x][y + 1].getCharacter() == null)
					fringe.add(new QueueElement(new Point(x, y + 1), qe, qe
							.getCost() + 1, qe.getCost() + 1
							+ new Point(x, y + 1).distance(end)));
				for (Door d : locations[x][y].getDoors()) {
					if (d.getFirstList().contains(locations[x][y])) {
						for (Location l : d.getSecondList()) {
							if (l.getCharacter() == null)
								fringe.add(new QueueElement(l.getPoint(), qe,
										qe.getCost() + 1, qe.getCost() + 1
												+ l.getPoint().distance(end)));
						}
					} else {
						for (Location l : d.getFirstList()) {
							if (l.getCharacter() == null)
								fringe.add(new QueueElement(l.getPoint(), qe,
										qe.getCost() + 1, qe.getCost() + 1
												+ l.getPoint().distance(end)));
						}
					}
				}

			}
		}
		return new LinkedList<Location>();
	}

	public static void main(String[] args) {
		Board board = new Board();
		board.startBoard(new Scanner(System.in));
		board.playGame(null);
	}

	/**
	 * A fringe element for that holds a location and the number of steps to get
	 * there.
	 * 
	 * @author benjamin
	 * 
	 */
	private class FringeElement implements Comparable {
		Location location;
		int steps;

		/**
		 * 
		 * @param location
		 *            the location to be stored
		 * @param steps
		 *            the number of steps to get to the location
		 */
		public FringeElement(Location location, int steps) {
			this.location = location;
			this.steps = steps;
		}

		@Override
		public int compareTo(Object o) {
			if (!(o instanceof FringeElement)) {
				throw new IllegalArgumentException(
						"Object is not a FringeElement");
			}
			FringeElement other = (FringeElement) o;
			// The higher priority element is the element with the most steps
			return other.steps - steps;
		}
	}

	private class QueueElement implements Comparable {
		private Point point; // The point that it is storing
		private QueueElement from; // The point that it came from
		private int cost; // Cost to get to this node
		private double heurastic; // Estimated cost to get to the goal + the
									// cost to get here

		public QueueElement(Point p, QueueElement f, int c, double h) {
			point = p;
			from = f;
			cost = c;
			heurastic = h;
		}

		public QueueElement getFrom() {
			return from;
		}

		public int getCost() {
			return cost;
		}

		public double getHeurastic() {
			return heurastic;
		}

		public Point getNode() {
			return point;
		}

		public String toString() {
			return point.toString() + cost + heurastic;
		}

		@Override
		public int compareTo(Object other) {
			if (other instanceof QueueElement)
				if (this.getHeurastic() - ((QueueElement) other).getHeurastic() < 0)
					return -1;
				else if (this.getHeurastic()
						- ((QueueElement) other).getHeurastic() > 0)
					return 1;
				else
					return 0;
			throw new IllegalArgumentException();
		}

	}

	public void setTextOutput(JTextArea to) {
		textOutput = to;
	}
}
