package ch.intertec.storybook.playground.swing;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class PgCoordinates extends JFrame {

	public PgCoordinates() {
		initGUI();
	}

	public static void main(String args[]) {
		new PgCoordinates();
	}

	private void initGUI() {
		MigLayout layout = new MigLayout("debug");
		setLayout(layout);

		JPanel panel1 = new JPanel();
		panel1.setBackground(Color.blue);
		panel1.add(new JLabel("Panel 1"));

		JPanel panel2 = new JPanel();
		panel2.setBackground(Color.cyan);
		panel2.add(new JLabel("Panel 2"));

		JPanel panel3 = new JPanel();
		panel3.setBackground(Color.yellow);
		panel3.add(new JLabel("Panel 3"));
		
		panel2.add(panel3);
		panel1.add(panel2);

		add(panel1);
		setSize(500, 300);
		// pack();
		setVisible(true);

		System.out.println("panel1 bounds: " + panel1.getBounds());
		System.out.println("panel2 bounds: " + panel2.getBounds());
		System.out.println("panel3 bounds: " + panel3.getBounds());
		
		Rectangle rect = panel3.getBounds();
		Rectangle rect2 = SwingUtilities.convertRectangle(
				panel3, rect, panel1.getParent());
		System.out.println("rect conv: " + rect2);
	}
}
