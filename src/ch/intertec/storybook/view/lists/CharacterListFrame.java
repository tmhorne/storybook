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
package ch.intertec.storybook.view.lists;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.toolkit.swing.table.GenderTableCellRenderer;
import ch.intertec.storybook.view.model.dbtable.CharacterDbTable;

@SuppressWarnings("serial")
public class CharacterListFrame extends AbstractListFrame {

	@Override
	void init() {
		table = new CharacterDbTable();
		setTitle("msg.dlg.mng.persons.title");
		TableColumnModel colModel = table.getJTable().getColumnModel();
		TableColumn col = colModel.getColumn(6);
		col.setCellRenderer(new GenderTableCellRenderer());
		table.getJTable().setRowHeight(20);
	}

	@Override
	int getPreferredWidth() {
		return 800;
	}

	@Override
	protected void addListeners() {
		PCSDispatcher.getInstance().addPropertyChangeListener(
				Property.CHARACTER, this);
		PCSDispatcher.getInstance().addPropertyChangeListener(Property.GENDER,
				this);
	}

	@Override
	protected void removeListeners() {
		PCSDispatcher.getInstance().removeAllPropertyChangeListener(this);
	}
}
