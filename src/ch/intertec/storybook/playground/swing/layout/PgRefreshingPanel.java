/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.intertec.storybook.playground.swing.layout;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class PgRefreshingPanel extends JFrame {

	private JPanel contentPanel;
	private JScrollPane scroller;
	private int counter = 0;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new PgRefreshingPanel();
			}
		});
	}

	public PgRefreshingPanel() {
		super();
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initGUI();
		setVisible(true);
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,wrap"));

		refresh();

		scroller = new JScrollPane(contentPanel);
		add(scroller, "grow");

		MyAction a = new MyAction("do it");
		JButton bt = new JButton(a);
		add(bt);
	}

	private void refresh() {
		contentPanel = new JPanel(new MigLayout("wrap 50"));
		for (int c = 0; c < 50; ++c) {
			for (int r = 0; r < 50; ++r) {
				JPanel panel = new JPanel();
				panel.add(new JLabel("" + counter + ": " + c + "/" + r));
				contentPanel.add(panel);
			}
		}
		++counter;
	}

	class MyAction extends AbstractAction {
		public MyAction(String text) {
			super(text);
		}

		public void actionPerformed(ActionEvent e) {
			int vsb = scroller.getVerticalScrollBar().getValue();
			int hsb = scroller.getHorizontalScrollBar().getValue();
			refresh();
//			scroller.getViewport().removeAll();
			scroller.setViewportView(contentPanel);
			scroller.getVerticalScrollBar().setValue(vsb);
			scroller.getHorizontalScrollBar().setValue(hsb);
			// scroller.invalidate();
			// scroller.repaint();
		}
	}
}
