package ch.intertec.storybook.toolkit.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import ch.intertec.storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class ProGlassPane extends JComponent implements MouseListener {

	private String textAvailable;
	private String textClick;
	private Window dlg;
	private boolean noRects = false;

	public ProGlassPane(Window dlg, boolean noRects) {
		this(dlg);
		this.noRects = noRects;
	}
	
	public ProGlassPane(Window dlg) {
		this.dlg = dlg;
		textAvailable = I18N.getMsg("msg.pro.available");
		textClick = I18N.getMsg("msg.pro.click");
		addMouseListener(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		if (noRects == false) {
			for (int i = 0; i < 1500; ++i) {
				int x = (int) (Math.random() * getWidth());
				int y = (int) (Math.random() * getHeight());
				Color clr = new RandomColor().randomPastelColor();
				g2.setColor(clr);
				g2.fillRect(x, y, 10, 10);
			}
		}

		g2.setColor(new Color(255, 255, 255, 120));
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(Color.gray);

		Font font = new Font("Sans Serif", Font.BOLD, 30);
		g2.setColor(Color.darkGray);
		g.setFont(font);
		FontMetrics metrics = g2.getFontMetrics(font);
		int hgt = metrics.getHeight();
		int adv = metrics.stringWidth(textAvailable);
		Dimension size = new Dimension(adv + 2, hgt + 2);
		g2.drawString(textAvailable,
				(int) (getWidth() / 2 - size.getWidth() / 2),
				(int) (getHeight() / 2 - size.getHeight() / 2));
		font = new Font("Sans Serif", Font.BOLD, 16);
		g.setFont(font);
		hgt = metrics.getHeight();
		adv = metrics.stringWidth(textClick);
		size = new Dimension(adv + 2, hgt + 2);
		g2.drawString(textClick, (int) (getWidth() / 2 - size.getWidth() / 2),
				(int) (getHeight() / 2 - size.getHeight() / 2) + 40);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		dlg.dispose();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
