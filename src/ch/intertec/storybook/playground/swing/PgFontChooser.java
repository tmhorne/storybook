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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.toolkit.swing.FontChooser;

@SuppressWarnings("serial")
public class PgFontChooser extends JFrame implements ActionListener {

	public static void main(String[] args) {
		new PgFontChooser();
	}

	public PgFontChooser() {
		super();
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initGUI();
		setVisible(true);
	}

	private void initGUI() {
		setLayout(new MigLayout());
		
		JButton bt = new JButton("font chooser");
		bt.addActionListener(this);
		add(bt, "newline");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Font font = null;
		font = FontChooser.showDialog(this, null, font);
		if (font != null) {
			System.out.println("font        : " + font);
			System.out.println("font family : " + font.getFamily());
			System.out.println("font name   : " + font.getName());
			System.out.println("font style  : " + font.getStyle());
			System.out.println("font size   : " + font.getSize());
		}
	}
}
