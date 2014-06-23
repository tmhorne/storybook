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

import java.awt.Color;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.IDbColumn;
import ch.intertec.storybook.toolkit.swing.ReadOnlyTable;
import ch.intertec.storybook.toolkit.swing.table.ColorRenderer;

public abstract class AbstractDbTable {
	protected DefaultTableModel data;
	protected List<? extends DbTable> list;
	protected TableColumnModel columns;
	protected ReadOnlyTable jtable;
	protected int dbTableColumnIndex;

	abstract protected void init();
	abstract protected void fillData();
	abstract protected Vector<? extends IDbColumn> getColumns();
	
	public AbstractDbTable(){
		init();
		fillData();
		Vector<? extends IDbColumn> v = getColumns();
		columns = new DefaultTableColumnModel();
		for (int count = data.getColumnCount(), i = 0; i < count; i++) {
			TableColumn c = new TableColumn(i);
			// c.setHeaderValue(data.getColumnName(i));
			c.setHeaderValue(v.get(i).getDbColumn().getI18Name());
			columns.addColumn(c);
		}
		jtable = new ReadOnlyTable(data, columns);
		// doesn't work...
		jtable.setDefaultRenderer(Color.class, new ColorRenderer());
	}
		
	public JTable getJTable() {
		return jtable;
	}

	public DbTable getValueAt(int rowIndex) {
		return list.get(rowIndex);
	}
	
	public void refresh(){
		fillData();
		jtable.setModel(data);
	}
}
