package ch.intertec.storybook.playground.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

/** An application that requires no other files. */
public class PgGlassPane {

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("GlassPaneDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(400, 400));

		JComponent glass = new MyGlassPane();
		frame.setGlassPane(glass);
		glass.setVisible(true);

		frame.add(new JLabel("jfdskl dsjklfds"));

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}

@SuppressWarnings("serial")
class MyGlassPane extends JComponent {
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.pink);
		g.drawLine(0, 0, 100, 100);
	}
}
