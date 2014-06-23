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
package ch.intertec.storybook.view.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import ch.intertec.storybook.model.Gender;
import ch.intertec.storybook.model.GenderPeer;


@SuppressWarnings("serial")
public class GenderTableModel extends AbstractTableModel {

    private String[] columnNames;
    private List<Gender> gendersList;
    private Object[][] data;

    public GenderTableModel() {
        columnNames = new String[3];
        columnNames[0] = Gender.Column.NAME.getDbColumn().getI18Name();
        columnNames[1] = "";
        columnNames[2] = "";
        this.refresh();
    }

    public void refresh() {
        this.gendersList = GenderPeer.doSelectAll();
        this.data = new Object[gendersList.size()][3];
        int c = 0;
        for (Gender gender : gendersList) {
        	data[c][0] = gender.toString();
            ++c;
        }
        this.fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return (data!=null)?data.length:0;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return (data!=null && data.length>rowIndex) ? data[rowIndex][columnIndex] : null;
    }

    public Gender getById(int id) {
        Gender gender = null;
        for (Gender gn : this.gendersList) {
            if (gn.getId() == id) {
                gender = gn;
                break;
            }
        }
        return gender;
    }

    public Gender getByRowNumber(int nb) {
        return (nb < this.gendersList.size()) ? this.gendersList.get(nb) : null;
    }
}
