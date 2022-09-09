package WhiteBoard;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class Draw extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Graphics g;
	private Color C;
	private Color back;

	// private

	public Draw(Graphics g, Color c,Color B) {
		this.g = g;
		this.C = c;
		this.back=B;
		// TODO Auto-generated constructor stub
	}
	
	
	public void paintLine(int[] xPoints, int[] yPoints) {
		g.setColor(C);
		this.g.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
		// TODO Auto-generated method stub

	}

	public void paintFree(int[] xPoints, int[] yPoints) {
		g.setColor(C);
		this.g.drawPolyline(xPoints, yPoints, 2);
	}


	public void clearLine(int[] xPoints, int[] yPoints) {
		g.setColor(back);		
		this.g.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
		
	}

	public void paintRect(int[] xPoints, int[] yPoints) {
		g.setColor(C);
		this.g.drawLine(xPoints[1], yPoints[0], xPoints[1], yPoints[1]);
		this.g.drawLine(xPoints[0], yPoints[1], xPoints[1], yPoints[1]);
		this.g.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[0]);
		this.g.drawLine(xPoints[0], yPoints[0], xPoints[0], yPoints[1]);
		
	}

	public void clearRect(int[] xPoints, int[] yPoints) {
		g.setColor(back);
		this.g.drawLine(xPoints[1], yPoints[0], xPoints[1], yPoints[1]);
		this.g.drawLine(xPoints[0], yPoints[1], xPoints[1], yPoints[1]);
		this.g.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[0]);
		this.g.drawLine(xPoints[0], yPoints[0], xPoints[0], yPoints[1]);
		
	}
	
	public void clear(int xPoint, int yPoint) {		
		g.setColor(back);
		this.g.fillRect(xPoint, yPoint, 25, 25);
		System.out.println(xPoint);
		System.out.println(yPoint);
	}
	
	public void updateSc(BufferedImage i) {
		this.g.drawImage(i, 10, 23, 627, 685, null);
		
	}
	public void text(String s, int x , int y) {
		this.g.setColor(C);
		g.setFont(new Font("Dialog", Font.PLAIN, 12)); 
		g.drawString(s, x, y);
		
	}
	
	
	public void paintCircle(int[] xPoints, int[] yPoints) {
		g.setColor(C);
		int dia = xPoints[1]-xPoints[0];
		this.g.drawOval(xPoints[0], xPoints[0],dia , dia);
		
	}
	public void clearCircle(int[] xPoints, int[] yPoints) {
		g.setColor(back);
		int dia = xPoints[1]-xPoints[0];
		this.g.drawOval(xPoints[0], xPoints[0],dia , dia);
		
	}
	

}
