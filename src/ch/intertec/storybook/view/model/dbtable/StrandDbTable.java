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

package ch.intertec.storybook.view.model.dbtable;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import ch.intertec.storybook.model.IDbColumn;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.model.Strand.Column;


public class StrandDbTable extends AbstractDbTable {
	
	private Vector<Column> columns;
	
	@Override
	protected void init() {
		columns = new Vector<Column>();
		columns.add(Column.SORT);
		columns.add(Column.NAME);
		columns.add(Column.ABBREVIATION);
		columns.add(Column.COLOR);
		dbTableColumnIndex = 0;
	}

	@Override
	protected void fillData() {
		list = StrandPeer.doSelectAll();
		data = new DefaultTableModel(columns, list.size());
		for (int rowIndex = 0; rowIndex < list.size(); ++rowIndex) {
			Strand strand = (Strand)list.get(rowIndex);
			for (int columnIndex = 0; columnIndex < columns.size(); ++columnIndex) {
				Object value = null;
				switch (columns.get(columnIndex)) {
				case NAME:
					value = strand.getName();
					break;
				case ABBREVIATION:
					value = strand.getAbbreviation();
					break;
				case COLOR:
					value = strand.getColor();
					break;
				case SORT:
					value = strand.getSort();
					break;
				}
				data.setValueAt(value, rowIndex, columnIndex);
			}
		}		
	}
	
	@Override
	protected Vector<? extends IDbColumn> getColumns() {
		return columns;
	}
}
