package ch.intertec.storybook.playground.jung;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class JungTest extends JFrame {

	public JungTest() {
		super("JungTest");
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JungTest();
				@SuppressWarnings("unused")
				Test02 test = new Test02(frame.getContentPane());
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
}
