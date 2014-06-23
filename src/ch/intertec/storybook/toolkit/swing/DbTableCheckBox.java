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

package ch.intertec.storybook.toolkit.swing;

import java.awt.Color;

import javax.swing.JCheckBox;

import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.DbTable;

@SuppressWarnings("serial")
public class DbTableCheckBox<T extends DbTable> extends JCheckBox {

	private T dbTable;

	public DbTableCheckBox(T dbTable) {
		super(dbTable.getLabelText());
		this.dbTable = dbTable;
		if (dbTable instanceof Strand) {
			Strand strand = (Strand) dbTable;
			Color color = strand.getColor();
			if (ColorUtil.isDark(color)) {
				setForeground(Color.white);
			}
			setBackground(color);
			setOpaque(true);
		}
	}

	public T getTable() {
		return dbTable;
	}
}
