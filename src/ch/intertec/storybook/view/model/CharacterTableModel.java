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

import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.SbCharacterPeer;
import java.util.List;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class CharacterTableModel extends AbstractTableModel {

    private String[] columnNames;
    private List<SbCharacter> charactersList;
    private Object[][] data;

    public CharacterTableModel() {
        columnNames = new String[7];
        columnNames[0] = SbCharacter.Column.ABBREVIATION.getDbColumn().getI18Name();
        columnNames[1] = SbCharacter.Column.FIRSTNAME.getDbColumn().getI18Name();
        columnNames[2] = SbCharacter.Column.LASTNAME.getDbColumn().getI18Name();
        columnNames[3] = SbCharacter.Column.BIRTHDAY.getDbColumn().getI18Name();
        columnNames[4] = SbCharacter.Column.GENDER_ID.getDbColumn().getI18Name();
        columnNames[5] = "";
        columnNames[6] = "";
        this.refresh();
    }

    public void refresh() {
        this.charactersList = SbCharacterPeer.doSelectAll();
        this.data = new Object[charactersList.size()][7];
        int c = 0;
        for (SbCharacter character : charactersList) {
            data[c][0] = character.getAbbreviation();
            data[c][1] = character.getFirstname();
            data[c][2] = character.getLastname();
            data[c][3] = character.getBirthdayStr();
            if (character.getGender() != null) {
                data[c][4] = character.getGender().toString();
            } else {
                data[c][4] = "-";
            }
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
        return (data != null) ? data.length : 0;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return (data != null && data.length > rowIndex) ? data[rowIndex][columnIndex] : null;
    }

    public SbCharacter getById(int id) {
        SbCharacter character = null;
        for (SbCharacter gn : this.charactersList) {
            if (gn.getId() == id) {
                character = gn;
                break;
            }
        }
        return character;
    }

    public SbCharacter getByRowNumber(int nb) {
        return (nb < this.charactersList.size()) ? this.charactersList.get(nb) : null;
    }
}
