/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008-2009 Martin Mustun

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

package ch.intertec.storybook.recoverytool;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import ch.intertec.storybook.view.IRefreshable;

public class LoggerPanel extends JPanel implements IRefreshable {

	private static final long serialVersionUID = -8537384122043142349L;

	private JTextArea taLog;
	
	public LoggerPanel(){
		initGUI();
	}

	private void initGUI() {
		MigLayout layout = new MigLayout("fill");
		setLayout(layout);
		
		taLog = new JTextArea();
		taLog.setEditable(false);
		JScrollPane scroller = new JScrollPane(taLog);
		
		add(scroller, "grow");
	}

	public void appendText(String text){
		taLog.append(text);
	}
	
	@Override
	public void refresh() {
		removeAll();
		initGUI();
	}
}
