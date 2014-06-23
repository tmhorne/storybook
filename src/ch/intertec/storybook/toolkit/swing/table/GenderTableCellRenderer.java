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

package ch.intertec.storybook.toolkit.swing.table;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.Gender;

public class GenderTableCellRenderer extends StandardTableCellRenderer {

	public GenderTableCellRenderer() {
		super();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table,
				value, isSelected, hasFocus, row, column);
		if (value == null) {
			return label;
		}
		JPanel panel = new JPanel(new MigLayout("insets 0 2 0 2,fill"));
		Gender gender = (Gender) value;
		panel.setBackground(label.getBackground());
		panel.setOpaque(true);
		JLabel lbGender = new JLabel(gender.toString(), gender.getIcon(),
				JLabel.LEFT);
		panel.add(lbGender, "grow");
		return panel;
	}
}
