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

package ch.intertec.storybook.main;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
public class InfoPanel extends JPanel {

	private JTextPane textPane;

	public InfoPanel() {
		initGUI();
	}

	private void initGUI() {
		setPreferredSize(new Dimension(100, 300));

		MigLayout layout = new MigLayout("insets 0", "[fill,grow]", "");
		setLayout(layout);
		setBorder(SwingTools.getCompoundBorder(I18N.getMsg("msg.info.title")));

		textPane = new JTextPane();
		textPane.setOpaque(true);
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		JScrollPane scroller = new JScrollPane(textPane);
		scroller.setPreferredSize(new Dimension(Integer.MAX_VALUE,
				Integer.MAX_VALUE));
		add(scroller);
	}

	public void setText(String text) {
		textPane.setText(text);
		textPane.setCaretPosition(0);
	}
}
