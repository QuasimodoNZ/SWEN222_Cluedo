import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Scanner;

import javax.swing.*;

@SuppressWarnings("serial")
public class CluedoFrame extends JFrame {
	private static Board board;

	private JFrame frame;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem;
	private JMenuItem menuItem2;
	private String userInput;

	private static BoardPanel boardPanel;
	private JPanel sidePanel;

	private static JTextArea textOutput;
	private JScrollPane textSP;
	private JButton showCardsButton;
	private JButton accuseButton;
	private JButton suggestButton;
	private JButton endTurnButton;

	private GridBagConstraints grid;

	private static int windowHeight = 850;
	private int sidePanelWidth = 250;
	private int windowWidth = windowHeight + sidePanelWidth;

	/**
	 * Creates all of the J areas and adds it to the frame
	 */
	public CluedoFrame() {
		super("Cluedo");

		board = new Board();
		setupComponents();
		addComponents();

		// Tells the board that there is another place for its output
		board.setTextOutput(textOutput);

		// Once it is all set up, make the interface visible
		frame.setVisible(true);
	}

	/**
	 * Creates and sets values for all of the components
	 */
	private void setupComponents() {
		// Set up the window
		frame = new JFrame("cluedo");
		frame.setSize(windowWidth, windowHeight);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Setup MenuBar
		menuBar = new JMenuBar();

		// Setup the Menu
		menu = new JMenu("Options");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");

		// Setup menu items
		menuItem = createStartGameItem();

		menuItem2 = new JMenuItem("Quit", KeyEvent.VK_T);
		menuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				ActionEvent.ALT_MASK));
		menuItem2.getAccessibleContext().setAccessibleDescription(
				"Ends current game and quits");

		// Setup the main board panel
		boardPanel = new BoardPanel(board);
		boardPanel.addMouseListener(boardPanel);

		// Setup the side panel
		sidePanel = new JPanel(new GridBagLayout());
		sidePanel.setSize(sidePanelWidth, windowHeight);
		sidePanel.setBackground(Color.gray);

		// Setup components to go into the sidePanel
		// Text Area
		textOutput = new JTextArea(5, 25);
		textOutput.setEditable(false);
		textOutput.setText("  Welcome to Cluedo");
		textSP = new JScrollPane(textOutput);
		// Buttons
		showCardsButton = newShowCardsButton();
		accuseButton = newAccuseButton("Accuse");
		suggestButton = newAccuseButton("Suggest");
		endTurnButton = new JButton("End Turn");
		endTurnButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.setGUIMove("end turn");
			}
		});

		// Setup the grid
		grid = new GridBagConstraints();
		grid.insets = new Insets(5, 5, 5, 5);
	}

	/**
	 * Adds all of the components to there parent object
	 */
	private void addComponents() {
		menuBar.add(menu);
		menu.add(menuItem);
		menu.add(menuItem2);

		grid.gridwidth = 3;
		grid.gridx = 0;
		grid.gridy = 0;
		sidePanel.add(textSP, grid);
		grid.gridwidth = 1;
		grid.gridx = 0;
		grid.gridy = 2;
		sidePanel.add(accuseButton, grid);
		grid.gridy = 3;
		sidePanel.add(suggestButton, grid);
		grid.gridx = 2;
		grid.gridy = 2;
		sidePanel.add(showCardsButton, grid);
		grid.gridy = 3;
		sidePanel.add(endTurnButton, grid);

		frame.setJMenuBar(menuBar);
		frame.add(boardPanel, BorderLayout.WEST);
		frame.add(sidePanel, BorderLayout.EAST);
	}

	private JMenuItem createStartGameItem() {
		JMenuItem newMenuItem = new JMenuItem("Start New Game", KeyEvent.VK_T);
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		newMenuItem.getAccessibleContext().setAccessibleDescription(
				"Ends current game and starts a new one");
		newMenuItem.addActionListener(new ActionListener() {
			// Setup a popup dialog for choosing number of players
			@Override
			public void actionPerformed(ActionEvent e) {
				Window parentWindow = SwingUtilities
						.windowForComponent(menuItem);
				final JDialog dialog = new JDialog(parentWindow, "New Game");
				dialog.setLocation(200, 350);
				dialog.setModal(true);

				// Setup Panel to hold components
				JPanel numOfPlayersPanel = new JPanel();

				// In initialization code:
				// Create the radio buttons.
				final JRadioButton player3 = new JRadioButton("3 Players");
				player3.setMnemonic(KeyEvent.VK_3);
				player3.setActionCommand("3 Players");
				player3.setSelected(true);

				final JRadioButton player4 = new JRadioButton("4 Players");
				player3.setMnemonic(KeyEvent.VK_4);
				player3.setActionCommand("4 Players");
				player3.setSelected(true);

				final JRadioButton player5 = new JRadioButton("5 Players");
				player3.setMnemonic(KeyEvent.VK_5);
				player3.setActionCommand("5 Players");
				player3.setSelected(true);

				final JRadioButton player6 = new JRadioButton("6 Players");
				player3.setMnemonic(KeyEvent.VK_6);
				player3.setActionCommand("6 Players");
				player3.setSelected(true);

				// Group the radio buttons.
				final ButtonGroup group = new ButtonGroup();
				group.add(player3);
				group.add(player4);
				group.add(player5);
				group.add(player6);

				// Set actions for the submit button on the select No. players
				// window
				final JButton submit = new JButton("Submit");
				submit.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int numPlayers;
						if (player3.isSelected()) {
							numPlayers = 3;
						} else if (player4.isSelected()) {
							numPlayers = 4;
						} else if (player5.isSelected()) {
							numPlayers = 5;
						} else {
							numPlayers = 6;
						}
						userInput = numPlayers + "\n";
						final List<Enums.Character> availableCharacters = Enums.Character
								.toList();

						for (int i = 0; i < numPlayers; i++) {
							Window parentWindow = SwingUtilities
									.windowForComponent(submit);
							final JDialog dialog2 = new JDialog(parentWindow,
									"Character Selection");
							dialog2.setLocation(200, 350);
							dialog2.setModal(true);

							// Create a panel to hold all of the character
							// selection combo boxes
							JPanel characterPanel = new JPanel();
							final JComboBox<String> character1 = new JComboBox<String>();
							for (Enums.Character c : availableCharacters)
								character1.addItem(c.toString());
							final JButton submit2 = new JButton("Submit");
							submit2.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									String player = (String) character1
											.getSelectedItem();
									availableCharacters.remove(Enums.Character
											.toEnum(player));
									userInput += player + "\n";
									dialog2.dispose();
								}
							});
							characterPanel.add(character1);
							characterPanel.add(submit2);
							dialog2.add(characterPanel);
							dialog2.pack();
							dialog.setVisible(false);
							dialog2.setVisible(true);
						}
						board.startBoard(new Scanner(userInput));
						board.playGame(boardPanel);
						boardPanel.repaint();
					}
				});

				numOfPlayersPanel.add(player3);
				numOfPlayersPanel.add(player4);
				numOfPlayersPanel.add(player5);
				numOfPlayersPanel.add(player6);
				numOfPlayersPanel.add(submit);
				dialog.add(numOfPlayersPanel);
				dialog.pack();
				dialog.setVisible(true);

			}
		});
		return newMenuItem;
	}

	/**
	 * Creates an accuse button that displays a dialog that contains 2 combo
	 * boxes, a label and a submit button
	 * 
	 * @return The accuse button
	 */
	private static JButton newAccuseButton(final String type) {
		final JButton button = new JButton(type);
		button.addActionListener(new ActionListener() {
			// Setup a popup dialog for accusing
			@Override
			public void actionPerformed(ActionEvent e) {
				Window parentWindow = SwingUtilities.windowForComponent(button);
				final JDialog dialog = new JDialog(parentWindow, type);
				dialog.setLocation(200, 350);
				dialog.setModal(true);

				// Setup Panel to hold components
				JPanel accusePanel = new JPanel();
				String[] items1 = { "Kasandra Scarlett", "Jack Mustard", "Diane White",
						"Jacob Green", "Eleanor Peacock", "Victor Plum" };
				final JComboBox<String> characters = new JComboBox<String>(
						items1);
				String[] items2 = { "Rope", "Candlestick", "Knife", "Pistol",
						"Baseball Bat", "Dumbbell", "Trophy", "Poison", "Axe" };
				final JComboBox<String> weapons = new JComboBox<String>(items2);
				String[] items3 = { "Spa", "Theatre", "Living Room",
						"Observatory", "Patio", "Swimming Pool", "Hall",
						"Kitchen", "Dinning Room", "Guest House" };
				
				final JComboBox<String> rooms = new JComboBox<String>(items3);
				
				if (board.getCurrentPlayer() == null){
					textOutput.setText("You must start a game to Suggest or Accuse");
					return;
				}
				if (board.getCurrentPlayer().getLocation().getRoom() == null){
					textOutput.setText("You must be in a room to Suggest or Accuse");
					return;
				}
				String currentRoom = board.getCurrentPlayer().getLocation().getRoom().toString();
				final JLabel room = new JLabel(currentRoom);

				final JButton submit = new JButton("Submit");
				submit.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						String character = (String) characters
								.getSelectedItem();
						String weapon = (String) weapons.getSelectedItem();
						if (type.equals("Accuse")){
							String room = (String) rooms.getSelectedItem();
							board.setGUIMove(type +" " + character + " " + weapon
									+ " " + room+"\n");
						} else {
							board.setGUIMove(type+" "+character+" "+weapon+"\n");
						}
						dialog.dispose();
					}
				});

				// Add all the components
				accusePanel.add(characters);
				accusePanel.add(weapons);
				if (type.equals("Suggest")){
					accusePanel.add(room);
				} else {
					accusePanel.add(rooms);
				}
				accusePanel.add(submit);
				dialog.add(accusePanel);
				dialog.pack();
				dialog.setVisible(true);
			}
		});
		return button;
	}

	private JButton newShowCardsButton() {
		final JButton button = new JButton("Show Cards");
		button.addActionListener(new ActionListener() {
			// Setup a popup dialog for showing cards
			@Override
			public void actionPerformed(ActionEvent e) {
				Window parentWindow = SwingUtilities.windowForComponent(button);
				final JDialog dialog = new JDialog(parentWindow, "Your Cards");
				dialog.setLocation(50, 50);
				dialog.setSize(700,700);
				dialog.setModal(true);

				// Setup Panel to hold components
				JPanel cardsPanel = new JPanel();
				if (board.getCurrentPlayer() != null) {
					for (Enums.Character card : board.getCurrentPlayer()
							.getCharacterCards()) {
						ImagePanel image = new ImagePanel("Images/Cards/"
								+ card.toString()+".png");
						image.setVisible(true);
						cardsPanel.add(image);
					}
					for (Enums.Weapon card : board.getCurrentPlayer()
							.getWeaponCards()) {
						ImagePanel image = new ImagePanel("Images/Cards/"
								+ card.toString()+".png");
						image.setVisible(true);
						cardsPanel.add(image);
					}
					for (Enums.RoomName card : board.getCurrentPlayer()
							.getRoomCards()) {
						ImagePanel image = new ImagePanel("Images/Cards/"
								+ card.toString()+".png");
						image.setVisible(true);
						cardsPanel.add(image);
					}
					dialog.add(cardsPanel);
					dialog.setVisible(true);
				}

			}
		});
		return button;

	}
}
