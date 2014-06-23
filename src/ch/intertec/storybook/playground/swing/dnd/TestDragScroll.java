package ch.intertec.storybook.playground.swing.dnd;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.TransferHandler;
import javax.swing.event.MouseInputAdapter;

@SuppressWarnings("serial")
public class TestDragScroll extends JPanel implements Scrollable {

	private final String text = "Drag me";
	private Point location = new Point(100, 100);
	protected boolean dragging;

	public TestDragScroll() {
		setPreferredSize(new Dimension(1000, 1000));
//		setAutoscrolls(true);
		setTransferHandler(new TransferHandler("text") {

			@Override
			public boolean canImport(JComponent comp,
					DataFlavor[] transferFlavors) {
				return true;
			}

			@Override
			public boolean importData(TransferSupport support) {
				System.out.println("TestDragScroll.TestDragScroll().new TransferHandler() {...}.importData(): ");
				if (support.isDrop()) {
					location = support.getDropLocation().getDropPoint();
					repaint();
					return true;
				} else {
					return false;
				}
			}
		});

		MouseInputAdapter listener = new MouseInputAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				System.out.println("TestDragScroll.TestDragScroll().new MouseInputAdapter() {...}.mouseDragged(): ");
				if (!dragging) {
					dragging = true;
					getTransferHandler().exportAsDrag(TestDragScroll.this, e,
							TransferHandler.COPY);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				System.out.println("TestDragScroll.TestDragScroll().new MouseInputAdapter() {...}.mouseReleased(): ");
				dragging = false;
			}
		};
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}

	public String getText() {
		return text;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawString(text, location.x, location.y);
	}

	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(250, 250);
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 10;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 100;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {

			public void run() {
				JFrame frame = new JFrame("Test");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.getContentPane().add(
						new JScrollPane(new TestDragScroll()));
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
