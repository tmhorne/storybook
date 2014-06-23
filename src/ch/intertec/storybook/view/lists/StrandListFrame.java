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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.toolkit.swing.table.ColorTableCellRenderer;
import ch.intertec.storybook.view.model.dbtable.StrandDbTable;

@SuppressWarnings("serial")
public class StrandListFrame extends AbstractListFrame {

	@Override
	void init() {
		table = new StrandDbTable();
		setTitle("msg.dlg.mng.strands.title");
		TableColumnModel colModel = table.getJTable().getColumnModel();
		TableColumn col = colModel.getColumn(3);
		col.setCellRenderer(new ColorTableCellRenderer());
		showOrderButtons = true;
	}

	@Override
	int getPreferredWidth() {
		return 800;
	}

	@Override
	protected void addListeners() {
		PCSDispatcher.getInstance().addPropertyChangeListener(Property.STRAND,
				this);
	}

	@Override
	protected void removeListeners() {
		PCSDispatcher.getInstance().removeAllPropertyChangeListener(this);
	}

	@Override
	protected AbstractAction getOrderUpAction() {
		if (orderUpAction == null) {
			orderUpAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					JTable jtable = table.getJTable();
					int row = jtable.getSelectedRow();
					if (row == -1) {
						return;
					}
					int modelRow = jtable.convertRowIndexToModel(row);
					DbTable dbObj = table.getValueAt(modelRow);
					if (!(dbObj instanceof Strand)) {
						return;
					}
					Strand strand = (Strand) dbObj;
					SortAction act = new SortAction(strand, true);
					act.actionPerformed(null);
					refresh();
					ListSelectionModel selModel = jtable.getSelectionModel();
					int idx = selModel.getMinSelectionIndex();
					--idx;
					selModel.setSelectionInterval(idx, idx);
				}
			};
		}
		return orderUpAction;
	}

	@Override
	protected AbstractAction getOrderDownAction() {
		if (orderDownAction == null) {
			orderDownAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					JTable jtable = table.getJTable();
					int row = jtable.getSelectedRow();
					if (row == -1) {
						return;
					}
					int modelRow = jtable.convertRowIndexToModel(row);
					DbTable dbObj = table.getValueAt(modelRow);
					if (!(dbObj instanceof Strand)) {
						return;
					}
					Strand strand = (Strand) dbObj;
					SortAction act = new SortAction(strand, false);
					act.actionPerformed(null);
					refresh();
					ListSelectionModel selModel = jtable.getSelectionModel();
					int idx = selModel.getMinSelectionIndex();
					++idx;
					if (idx > jtable.getRowCount() - 1) {
						idx = jtable.getRowCount() - 1;
					}
					selModel.setSelectionInterval(idx, idx);
				}
			};
		}
		return orderDownAction;
	}

	private class SortAction extends AbstractAction {
		private Strand strand;
		private boolean inc;

		public SortAction(Strand strand, boolean inc) {
			this.strand = strand;
			this.inc = inc;
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			try {
				Strand strand2 = null;
				if (inc) {
					strand2 = StrandPeer.findLeftNeighbor(strand);
				} else {
					strand2 = StrandPeer.findRightNeighbor(strand);
				}
				if (strand2 == null) {
					return;
				}

				// swap strands
				int sort1 = strand.getSort();
				int sort2 = strand2.getSort();
				strand.setSort(sort2);
				strand.save();
				strand2.setSort(sort1);
				strand2.save();
			} catch (Exception e) {
				e.printStackTrace();
			}
			PCSDispatcher.getInstance().firePropertyChange(
					Property.STRAND_ORDER, null, null);
		}
	}
}
