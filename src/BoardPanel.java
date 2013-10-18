import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class BoardPanel extends JPanel implements MouseListener{
	private Board board;
	private BufferedImage background;
	
	public BoardPanel(Board b){
		this.board = b;
		try {
			background= ImageIO.read(new File("Images/cluedo.png"));
		} catch (IOException e) {
			System.out.printf("Invaild File: %s\n", e);
		}
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(786, 786);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(background, 0, 0, null);
		board.drawGUI(g);
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		board.setGUIMove("move " + e.getX()/27+ " " + e.getY()/27);
	}

}
