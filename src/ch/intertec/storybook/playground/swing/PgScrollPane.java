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

package ch.intertec.storybook.playground.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Test class for playing around.
 * 
 * @author martin
 * 
 */

@SuppressWarnings("serial")
public class PgScrollPane extends AbstractAction {
	private JFrame frame;
	private JPanel panel;
	private JScrollPane scrollPane;
	private int count = 0;
	private Point save = null;

	public static void main(String[] args) {
		new PgScrollPane();
	}

	public PgScrollPane() {
		super("scroll");
		frame = new JFrame();
		frame.setSize(300, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initGUI();
		frame.setVisible(true);
	}

	private void initGUI() {
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(200, 400));
		JButton button = new JButton("scroll");
		button.setAction(this);
		panel.add(button);
		for (int i = 0; i < 10; ++i) {
			JButton b = new JButton("test fdsjfjdskl");
			b.setAction(this);
			panel.add(b);
		}
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(panel);
		// setPreferredSize(new Dimension(450, 110));
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("action");
		Point p = scrollPane.getViewport().getViewPosition();
		System.out.println("pos: " + p);
		// p.y += 4;
		// scrollPane.getViewport().setViewPosition(p);
		if (count % 2 == 0) {
			// save
			System.out.println("save: " + save);
			save = p;
		} else {
			// restore
			System.out.println("restore: " + save);
			scrollPane.getViewport().setViewPosition(save);
		}
		++count;
	}
}
