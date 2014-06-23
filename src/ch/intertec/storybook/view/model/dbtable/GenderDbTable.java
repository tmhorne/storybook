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

import ch.intertec.storybook.model.Gender;
import ch.intertec.storybook.model.Gender.Column;
import ch.intertec.storybook.model.GenderPeer;
import ch.intertec.storybook.model.IDbColumn;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.view.model.dbtable.DbValue.Permission;

public class GenderDbTable extends AbstractDbTable {

	private Vector<Column> columns;

	@Override
	protected void init() {
		columns = new Vector<Column>();
		columns.add(Column.PERMISSION);
		columns.add(Column.NAME);
		columns.add(Column.CHILDHOOD);
		columns.add(Column.ADOLESCENCE);
		columns.add(Column.ADULTHOOD);
		columns.add(Column.RETIREMENT);
		dbTableColumnIndex = 0;
	}

	@Override
	protected void fillData() {
		list = GenderPeer.doSelectAll();
		data = new DefaultTableModel(columns, list.size());
		for (int rowIndex = 0; rowIndex < list.size(); ++rowIndex) {
			Gender gender = (Gender) list.get(rowIndex);
			for (int columnIndex = 0; columnIndex < columns.size(); ++columnIndex) {
				DbValue value = null;
				switch (columns.get(columnIndex)) {
				case NAME:
					value = new DbValue(gender.getName());
					break;
				case PERMISSION:
					if (gender.isMaleOrFemale()) {
						value = new DbValue(
								I18N.getMsg("msg.permission.not.editable"),
								Permission.NON_EDITABLE);
					} else {
						value = new DbValue(
								I18N.getMsg("msg.permission.editable"),
								Permission.EDITABLE);
					}
					break;
				case CHILDHOOD:
					value = new DbValue(Integer.toString(gender.getChildhood()));
					break;
				case ADOLESCENCE:
					value = new DbValue(Integer.toString(gender.getAdolescence()));
					break;
				case ADULTHOOD:
					value = new DbValue(Integer.toString(gender.getAdulthood()));
					break;
				case RETIREMENT:
					value = new DbValue(Integer.toString(gender.getRetirement()));
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
