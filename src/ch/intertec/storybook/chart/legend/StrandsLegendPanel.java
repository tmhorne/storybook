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

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.ColorUtil;
import ch.intertec.storybook.toolkit.swing.label.CleverLabel;

@SuppressWarnings("serial")
public class StrandsLegendPanel extends AbstractLegendPanel {

	public void initGUI() {
		add(new JLabel(I18N.getMsg("msg.report.caption.strands")),
				"gapright 5");
		for (Strand strand : StrandPeer.doSelectAll()) {
			CleverLabel label = new CleverLabel(strand.getName(),
					SwingConstants.CENTER);
			label.setPreferredSize(new Dimension(100, 20));
			label.setBackground(ColorUtil.darker(strand.getColor(), 0.05));
			add(label, "sg");
		}
	}
}
