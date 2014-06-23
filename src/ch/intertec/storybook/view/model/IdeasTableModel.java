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

import ch.intertec.storybook.model.Idea;
import ch.intertec.storybook.model.IdeasPeer;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.view.ideas.IdeasFrame;

@SuppressWarnings("serial")
public class IdeasTableModel extends AbstractTableModel {

    private String[] columnNames;
    private List<Idea> ideasList;
    private Object[][] data;
    private Idea.Status status;
    private IdeasFrame parent;

    public IdeasTableModel(Idea.Status status, IdeasFrame parent) {
    	this.parent = parent;
        this.status = status;
        columnNames = new String[5];
        columnNames[0] = I18N.getMsg("msg.idea.table.status");
        columnNames[1] = I18N.getMsg("msg.idea.table.category");
        columnNames[2] = I18N.getMsg("msg.idea.table.idea");
        columnNames[3] = "";
        columnNames[4] = "";
    }

    public void refresh() {
        this.ideasList = IdeasPeer.doSelectIdeasByStatus(this.status);
        this.data = new Object[ideasList.size()][5];
        int c = 0;
        for (Idea idea : ideasList) {
            data[c][0] = idea.getStatus();
            data[c][1] = idea.getCategory();
            data[c][2] = idea.getNote();
            ++c;
        }
        this.fireTableDataChanged();
        this.parent.getTabbedPane().setTitleAt(this.status.getIndex(), this.status.toString()+ " ("+this.ideasList.size()+")");
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

    public Idea getById(int id) {
        Idea idea = null;
        for (Idea gn : this.ideasList) {
            if (gn.getId() == id) {
                idea = gn;
                break;
            }
        }
        return idea;
    }

    public Idea getByRowNumber(int nb) {
        return (nb < this.ideasList.size()) ? this.ideasList.get(nb) : null;
    }
}
