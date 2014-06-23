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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import com.toedter.calendar.JDateChooser;

@SuppressWarnings("serial")
public class PgJCalendar extends JFrame implements ActionListener {

	private JDateChooser dateChooser;
	
	public static void main(String[] args) {
		new PgJCalendar();
	}

	public PgJCalendar() {
		super();
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initGUI();
		setVisible(true);
	}

	private void initGUI() {
		setLayout(new MigLayout());

		JLabel label = new JLabel("date");
		add(label);
		
//		JCalendar jcal = new JCalendar();
//		add(jcal);
		dateChooser = new JDateChooser(new Date());
		add(dateChooser, "w 90");
		
		JButton bt = new JButton("get date");
		bt.addActionListener(this);
		add(bt, "newline");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(dateChooser.getDate());
	}
}
