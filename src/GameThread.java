import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class GameThread extends Thread {
	Board board;
	BoardPanel boardPanel;

	public GameThread(Board b, BoardPanel bp) {
		board = b;
		boardPanel = bp;
	}

	public void run() {
		Scanner inputReader = new Scanner(System.in);
		// BufferedReader inputReader = new BufferedReader(new
		// InputStreamReader(System.in));
		boolean showedCards = false;
		while (true) {

			for (Player player : board.getPlayers()) {
				board.setCurrentPlayer(player);
				// only asks a human controlled player for input
				if (player.isControlled()) {

					if (!showedCards) {
						System.out.println("Your cards are:\n\n- Characters:");
						for (Enums.Character c : player.getCharacterCards())
							System.out.println("\t" + c.toString());

						System.out.println("\n- Rooms:");
						for (Enums.RoomName r : player.getRoomCards())
							System.out.println("\t" + r.toString());
						System.out.println("\n- Weapons:");
						for (Enums.Weapon w : player.getWeaponCards())
							System.out.println("\t" + w.toString());

					}
					// Rolls the dice
					board.setMovesLeft(2 + (int) (Math.random() * 11));
					while (board.getMovesLeft() >= 0) {
						board.drawBoard();
						System.out
								.printf("%s turn with %d turns left\nYour available moves are:\n",
										player.toString(), board.getMovesLeft());
						System.out.println(board.getOptions(player,
								board.getMovesLeft()));
						if (boardPanel != null) {
							while (true) {
								if (board.getGUIMove() != null) {
									board.setMovesLeft(board.getMovesLeft()
											- board.playTurn(player,
													board.getGUIMove(),
													board.getMovesLeft()));
									board.setGUIMove(null);
									boardPanel.repaint();
									break;
								}
								try {
									Thread.sleep(250);

								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						} else {
							board.setMovesLeft(board.getMovesLeft()
									- board.playTurn(player,
											inputReader.nextLine(),
											board.getMovesLeft()));
						}

					}

				}
			}
			showedCards = true;
		}
	}
}
