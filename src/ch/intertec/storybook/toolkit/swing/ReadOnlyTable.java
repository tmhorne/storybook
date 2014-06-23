package ch.intertec.storybook.toolkit.swing;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Provides a read-only table.
 * 
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class ReadOnlyTable extends JTable {

	@Override
	public boolean isCellEditable(int row, int column) {
		// return super.isCellEditable(row, column);
		return false;
	}

	public ReadOnlyTable() {
		super();
	}

	public ReadOnlyTable(int numRows, int numColumns) {
		super(numRows, numColumns);
	}

	public ReadOnlyTable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
	}

	public ReadOnlyTable(TableModel dm, TableColumnModel cm,
			ListSelectionModel sm) {
		super(dm, cm, sm);
	}

	public ReadOnlyTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
	}

	public ReadOnlyTable(TableModel dm) {
		super(dm);
	}

	public ReadOnlyTable(Vector<?> rowData, Vector<?> columnNames) {
		super(rowData, columnNames);
	}
}
