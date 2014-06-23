package ch.intertec.storybook.view.ideas;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import ch.intertec.storybook.model.Idea;
import ch.intertec.storybook.model.IdeasPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.table.IconButtonColumn;
import ch.intertec.storybook.toolkit.swing.table.IdeasStatusTableCellRenderer;
import ch.intertec.storybook.toolkit.swing.table.StandardTableCellRenderer;
import ch.intertec.storybook.view.IRefreshable;
import ch.intertec.storybook.view.model.IdeasTableModel;


/**
 * Provides a read-only table.
 * 
 * @author martin
 * 
 */
@SuppressWarnings("serial")
public class IdeasJTable extends JTable implements MouseListener, IRefreshable {

	private IdeasFrame parent;

	@Override
	public boolean isCellEditable(int row, int column) {
		boolean retour = false;
		switch (column) {
		case 3:
		case 4:
			retour = true;
		}
		return retour;
	}

	public IdeasJTable(int numRows, int numColumns) {
		super(numRows, numColumns);
	}

	public IdeasJTable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
	}

	public IdeasJTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
	}

	public IdeasJTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
	}

	public IdeasJTable(final IdeasFrame parent, Idea.Status status) {
		super(new IdeasTableModel(status, parent));
		this.parent = parent;
		this.setDefaultRenderer(Object.class, new StandardTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {

				Component c = super.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);
				((JComponent) c).setToolTipText(value.toString());
				return c;
			}
		});
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
				width = 200;
				col.setCellRenderer(new IdeasStatusTableCellRenderer());
				break;
			case 1:
			case 2:
				width = 300;
				break;
			default:
				width = 25;

			}
			col.setPreferredWidth(width);
		}

		new IconButtonColumn(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				int modelRow = Integer.valueOf(e.getActionCommand());
				editIdea(modelRow);
			}
		}, 3, "icon.small.edit", "msg.idea.btn.edit.tooltip");
		
		new IconButtonColumn(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				int ret = JOptionPane.showConfirmDialog(getThis().getParent(), I18N
						.getMsg("msg.idea.btn.delete.confirm"), "",
						JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION) {
					int modelRow = Integer.valueOf(e.getActionCommand());
					IdeasTableModel model = (IdeasTableModel) getThis()
							.getModel();
					Idea gn = model.getByRowNumber(modelRow);
					if (gn != null) {
						try {
							IdeasPeer.doDelete(gn);
						} catch (Exception exc) {
							// ignore
						}
					}
					refresh();
				}
			}
		}, 4, "icon.small.remove", "msg.idea.btn.delete.tooltip");
		this.addMouseListener(this);
	}

	private void editIdea(int modelRow) {
		IdeasTableModel model = (IdeasTableModel) getThis().getModel();
		Idea gn = model.getByRowNumber(modelRow);
		EditIdeaDialog dialog = new EditIdeaDialog(true, parent, gn.getId(), gn
				.getNote(), gn.getCategory(), gn.getStatus(), false);
		dialog.setVisible(true);
	}

	public IdeasJTable(Vector<?> rowData, Vector<?> columnNames) {
		super(rowData, columnNames);
	}

	private IdeasJTable getThis() {
		return this;
	}

	public void mouseClicked(MouseEvent e) {
		Object o = e.getSource();
		if (o instanceof IdeasJTable) {
			if (e.getClickCount() == 2) {
				Point p = e.getPoint();
				int row = this.rowAtPoint(p);
				this.editIdea(row);
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
		((IdeasTableModel) this.getModel()).refresh();
	}

}
