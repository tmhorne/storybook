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

package ch.intertec.storybook.toolkit.swing.panel;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import ch.intertec.storybook.toolkit.swing.ColorUtil;
import ch.intertec.storybook.toolkit.swing.IPastelComponent;


public class PastelPanel extends JPanel implements IPastelComponent {
	private static final long serialVersionUID = -7308148217729488897L;
	private Color color;

	public PastelPanel(LayoutManager layout, Color color) {
		super(layout);
		init(color);
	}

	public PastelPanel(Color color) {
		super();
		init(color);
	}

	private void init(Color color) {
		this.color = color;
		if (color != null) {
			setOpaque(true);
			setBackground(ColorUtil.getPastel(color));
		}
	}

	@Override
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		init(color);
	}
}
