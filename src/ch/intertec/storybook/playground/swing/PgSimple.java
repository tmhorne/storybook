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

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class PgSimple extends JFrame {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new PgSimple();
			}
		});
	}

	public PgSimple() {
		super();
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initGUI();
		setVisible(true);
	}

	private void initGUI() {
		setLayout(new MigLayout());
	}
}
