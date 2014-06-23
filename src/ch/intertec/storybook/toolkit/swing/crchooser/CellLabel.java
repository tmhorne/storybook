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

package ch.intertec.storybook.toolkit.swing.crchooser;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;

import ch.intertec.storybook.toolkit.swing.SwingTools;


@SuppressWarnings("serial")
public class CellLabel extends JLabel {

	private int col;
	private int row;

	private final Color hiColor = SwingTools.getNiceRed();
	private final Color bgColor = Color.white;

	public CellLabel(int col, int row) {
		setFocusable(true);
		setPreferredSize(new Dimension(20, 20));
		setBorder(SwingTools.getBorderDefault());
		setOpaque(true);
		setHighlighted(false);
		this.col = col;
		this.row = row;
		// setText("pos:" + col + "/" + row);
	}

	public int getColumn() {
		return col + 1;
	}

	public int getRow() {
		return row + 1;
	}

	public void setHighlighted(boolean h) {
		if (h) {
			setBackground(hiColor);
			return;
		}
		setBackground(bgColor);
	}
}
