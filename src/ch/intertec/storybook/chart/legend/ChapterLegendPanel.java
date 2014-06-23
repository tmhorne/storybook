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

package ch.intertec.storybook.chart.legend;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import ch.intertec.storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class ChapterLegendPanel extends AbstractLegendPanel {

	@Override
	public void initGUI() {
		// color label
		JLabel label = new JLabel("", SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(20, 20));
		label.setOpaque(true);
		label.setBackground(Color.lightGray);
		add(label, "sg");
		
		// text label
		add(new JLabel(I18N.getMsg("msg.common.chapter")),
				"gapright 5");
	}
}
