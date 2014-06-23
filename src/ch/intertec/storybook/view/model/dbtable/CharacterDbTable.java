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
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.SbCharacter.Column;
import ch.intertec.storybook.model.SbCharacterPeer;

//public class CharacterDbTable extends JTable implements MouseListener,
//        IRefreshable, PropertyChangeListener {
public class CharacterDbTable extends AbstractDbTable{

	private Vector<Column> columns;
	
	@Override
	protected void init() {
		columns = new Vector<Column>();
		columns.add(Column.CATEGORY);
		columns.add(Column.ABBREVIATION);
		columns.add(Column.FIRSTNAME);
		columns.add(Column.LASTNAME);
		columns.add(Column.BIRTHDAY);
		columns.add(Column.OCCUPATION);
		columns.add(Column.GENDER_ID);
		dbTableColumnIndex = 0;
	}
	
	@Override
	protected void fillData() {
		list = SbCharacterPeer.doSelectAll();
		data = new DefaultTableModel(columns, list.size());
		for (int rowIndex = 0; rowIndex < list.size(); ++rowIndex) {
			SbCharacter character = (SbCharacter)list.get(rowIndex);
			for (int columnIndex = 0; columnIndex < columns.size(); ++columnIndex) {
				Object value = null;
				switch (columns.get(columnIndex)) {
				case ABBREVIATION:
					value = character.getAbbreviation();
					break;
				case FIRSTNAME:
					value = character.getFirstname();
					break;					
				case LASTNAME:
					value = character.getLastname();
					break;
				case BIRTHDAY:
					value = character.getBirthdayStr();
					break;					
				case GENDER_ID:
					value = character.getGender();
					break;
				case CATEGORY:
					value = character.getCategoryStr();
					break;
				case OCCUPATION:
					value = character.getOccupation();
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

/*
    private ManageCharactersFrame parent;

    @Override
    public boolean isCellEditable(int row, int column) {
        // return super.isCellEditable(row, column);
        boolean retour = false;
        switch (column) {
            case 5:
            case 6:
                retour = true;
        }
        return retour;
    }

    public CharacterDbTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public CharacterDbTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    public CharacterDbTable(TableModel dm, TableColumnModel cm,
            ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    public CharacterDbTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    public CharacterDbTable(final ManageCharactersFrame parent) {
        super(new CharacterTableModel());
        this.parent = parent;
        this.setDefaultRenderer(
                Object.class, new StandardTableCellRenderer());
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setAutoCreateRowSorter(true);
        this.setRowHeight(20);
        // set column widths
        TableColumnModel colModel = this.getColumnModel();
        for (int i = 0; i < colModel.getColumnCount(); ++i) {
            TableColumn col = colModel.getColumn(i);
            int width = 0;
            switch (i) {
                case 0:
                    width = 100;
                    break;
                case 1:
                case 2:
                    width = 150;
                    break;
                case 3:
                    width = 100;
                    break;
                case 4:
                    width = 75;
                    break;
                default:
                    width = 25;

            }
            col.setPreferredWidth(width);
        }

        new IconButtonColumn(this, new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                int modelRow = Integer.valueOf(e.getActionCommand());
                editCharacter(modelRow);
            }
        }, 5, "icon.small.edit", "msg.person.btn.edit.tooltip");
        new IconButtonColumn(this, new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                int ret = JOptionPane.showConfirmDialog(getThis(), I18N.getMsg("msg.person.btn.delete.confirm"), "", JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.YES_OPTION) {
                    int modelRow = Integer.valueOf(e.getActionCommand());
                    CharacterTableModel model = (CharacterTableModel) getThis().getModel();
                    SbCharacter gn = model.getByRowNumber(modelRow);
                    if (gn != null) {
                        try {
                            SbCharacterPeer.doDelete(gn);
                        } catch (Exception exc) {
                        }
                    }
                }
            }
        }, 6, "icon.small.remove", "msg.person.btn.delete.tooltip");
        this.addMouseListener(this);
    }

    public void editCharacter(int modelRow) {
        CharacterTableModel model = (CharacterTableModel) getThis().getModel();
        SbCharacter gn = model.getByRowNumber(modelRow);
        CharacterDialog dial = new CharacterDialog(this.parent, gn);
        SwingTools.showModalDialog(dial, this.parent);
    }

    public CharacterDbTable(Vector<?> rowData, Vector<?> columnNames) {
        super(rowData, columnNames);
    }

    private CharacterDbTable getThis() {
        return this;
    }

    public void mouseClicked(MouseEvent e) {
        Object o = e.getSource();
        if (o instanceof JTable) {
            if (e.getClickCount() == 2) {
                Point p = e.getPoint();
                int row = this.rowAtPoint(p);
                editCharacter(row);
            }
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void refresh() {
        ((CharacterTableModel) this.getModel()).refresh();
    }

    public void propertyChange(PropertyChangeEvent evt) {
    }
    */

}
