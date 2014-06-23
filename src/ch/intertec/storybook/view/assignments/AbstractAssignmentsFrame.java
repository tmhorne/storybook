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
package ch.intertec.storybook.view.assignments;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.ItemPeer;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.Tag.TagType;
import ch.intertec.storybook.model.TagAssignmentData;
import ch.intertec.storybook.model.TagLink;
import ch.intertec.storybook.model.TagLinkPeer;
import ch.intertec.storybook.model.TagPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;

@SuppressWarnings("serial")
public abstract class AbstractAssignmentsFrame extends JFrame implements IRefreshable,
		WindowListener, PropertyChangeListener {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(AbstractAssignmentsFrame.class);

	protected JTable table;

	public AbstractAssignmentsFrame() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initGUI();
		PCSDispatcher.getInstance().addPropertyChangeListener(
				Property.PROJECT, this);
	}

	abstract protected TagType getTagType();
	
	abstract protected String[] getColumnNames();

	abstract protected int showConfirmDialog(Tag tag);

	abstract protected JButton getDeleteButton();

	abstract protected JButton getManageTagsButton();
	
	abstract protected JButton getAddTagButton();

	abstract protected void editTagLink(int row);
	
	abstract protected AbstractAction getAddAction();
	
	public void refresh() {
		getContentPane().removeAll();
		initGUI(true);
		validate();
		repaint();
		pack();
	}

	private void initGUI() {
		initGUI(false);
	}
	
	private void initGUI(boolean keepSize) {
		MigLayout layout = new MigLayout(
				"wrap,fill",
				"[]",
				"[grow][]");
		setLayout(layout);
		int w = 1000;
		int h = 600;
		if (keepSize) {
			w = getWidth();
			h = getHeight();
		}
		setPreferredSize(new Dimension(w, h));
		ImageIcon icon = (ImageIcon)I18N.getIcon("icon.sb");
		setIconImage(icon.getImage());

		String[] columnNames = getColumnNames();
		DefaultTableColumnModel columns = new DefaultTableColumnModel();
		int i = 0;
		for (String cn : columnNames) {
			TableColumn c = new TableColumn(i);
			c.setHeaderValue(cn);
			columns.addColumn(c);
			++i;
		}

		TagTableModel model = new TagTableModel(getTagType());
		table = new JTable(model, columns);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(Object.class, new MultiLineCellRenderer());
		// table.setRowHeight(table.getRowHeight() * 3);
		table.setAutoCreateRowSorter(true);
		// 0: tag
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		// 1: category
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		// 2: assigned to
		table.getColumnModel().getColumn(2).setPreferredWidth(400);
		// 3: period
		table.getColumnModel().getColumn(3).setPreferredWidth(200);
		// TableRowResizer trr = new TableRowResizer(table);

		// handle double click
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getComponent().isEnabled()
						&& e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2) {
					Point p = e.getPoint();
					int row = table.rowAtPoint(p);
					// int column = table.columnAtPoint(p);
					getThis().editTagLink(row);
				}
			}
		});
		
		// edit assignments
		JButton btEdit = new JButton();
		btEdit.setAction(getEditAction());
		btEdit.setText(I18N.getMsg("msg.common.edit"));
		btEdit.setIcon(I18N.getIcon("icon.small.edit"));

		// delete assignments
		JButton btDeleteAssignments = getDeleteButton();
		
		// add new assignments
		JButton btAdd = new JButton();
		btAdd.setAction(getAddAction());
		btAdd.setText(I18N.getMsg("msg.common.new"));
		btAdd.setIcon(I18N.getIcon("icon.small.add"));

		// add tag
		JButton btAddTag = getAddTagButton();
		
		// manage tags
		JButton btManageTags = getManageTagsButton();

		// refresh
		JButton btRefresh = new JButton();
		btRefresh.setAction(getRefreshAction());
		btRefresh.setIcon(I18N.getIcon("icon.small.refresh"));
		btRefresh.setToolTipText(I18N.getMsg("msg.common.refresh"));
		
		// close button
		JButton btClose = new JButton();
		btClose.setAction(getCloseAction());
		SwingTools.addEscAction(btClose, getCloseAction());
		btClose.setText(I18N.getMsg("msg.common.close"));
		btClose.setIcon(I18N.getIcon("icon.small.close"));

		// layout
		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane, "grow");
		this.add(btAdd, "split 7");
		this.add(btEdit);
		this.add(btDeleteAssignments);
		this.add(btRefresh);
		this.add(btAddTag, "gap 20");
		this.add(btManageTags);
		this.add(btClose, "gap push");
	}

	private AbstractAction getRefreshAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				getThis().refresh();
			}
		};
	}
	
	private AbstractAction getCloseAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				getThis().dispose();
			}
		};
	}

	protected AbstractAction getDeleteAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (getThis().table == null) {
					return;
				}
				int row = table.getSelectedRow();
				if (row == -1) {
					return;
				}
				int rowModel = table.convertRowIndexToModel(row);
				TagTableModel model = (TagTableModel) table.getModel();
				Tag tag = model.getTag(rowModel);
				// confirmation dialog
				int n = showConfirmDialog(tag);
				if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
					return;
				}
				ArrayList<TagLink> links = tag.getLinks();
				TagLinkPeer.deleteAssignments(links);
			}
		};
	}
	
	private AbstractAction getEditAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (getThis().table == null) {
					return;
				}
				int row = getThis().table.getSelectedRow();
				if (row == -1) {
					return;
				}
				getThis().editTagLink(row);
			}
		};
	}

	protected AbstractAssignmentsFrame getThis() {
		return this;
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		PCSDispatcher.getInstance().removeAllPropertyChangeListener(this);
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (PCSDispatcher.isPropertyFired(Property.PROJECT, evt)) {
			refresh();
			return;
		}
	}
}

@SuppressWarnings("serial")
class TagTableModel extends AbstractTableModel {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(TagTableModel.class);

	private List<Tag> tags;

	private int rowHeights[];

	public TagTableModel(TagType tagType) {
		if (tagType == TagType.ITEM) {
			tags = new ArrayList<Tag>();
			List<Item> items = ItemPeer.doSelectAll();
			for (Item item : items) {
				tags.add(item);
			}
		} else {
			tags = TagPeer.doSelectAll();
		}
		rowHeights = new int[tags.size()];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Tag tag = tags.get(rowIndex);
		switch (columnIndex) {
		case 0:
			// tag
			return tag.getName();
		case 1:
			// category
			return tag.getCategory();
		case 2:
			// assigned to
			return this.getAssignedTo(tag, rowIndex);
		case 3:
			// period
			return this.getPeriods(tag);
		default:
			break;
		}
		// we should never come this point
		return new String("Not implemented yet.");
	}

	@Override
	public int getRowCount() {
		return tags.size();
	}

	@Override
	public int getColumnCount() {
		return 6;
	}

	public int getRowHeight(int row) {
		return this.rowHeights[row];
	}

	public Tag getTag(int rowIndex) {
		return tags.get(rowIndex);
	}

	public Item getItem(int rowIndex) {
		return (Item) tags.get(rowIndex);
	}

	private String getPeriods(Tag tag) {
		try {
			String ret = "";
			ArrayList<TagLink> links = tag.getLinks();
			for (TagLink link : links) {
				if (link.hasPeriod()) {
					ret += link.getPeriod();
				} else if (link.hasStartScene()) {
					ret += link.getStartScene().getDateStr();
				}
				ret += "\n";
			}
			return ret;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	private String getAssignedTo(Tag tag, int rowIndex) {
		TagAssignmentData data = tag.getAssignedTo();
		this.rowHeights[rowIndex] = data.getLines();
		return data.getText();
	}
}

@SuppressWarnings("serial")
class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {

	public MultiLineCellRenderer() {
		setLineWrap(true);
		setWrapStyleWord(true);
		// setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// int height_wanted = (int) getPreferredSize().getHeight();
		// if (height_wanted != table.getRowHeight(row))
		// table.setRowHeight(row, height_wanted);

		// int countLines = MultiLineCellRenderer.countLines(value.toString());
		// if(countLines>1){
		// System.out.println(countLines);
		// table.setRowHeight(table.getRowHeight() * countLines);
		// }

		try {
			int h = ((TagTableModel) table.getModel()).getRowHeight(row);
			table.setRowHeight(row, (h + 1) * 18);
		} catch (Exception e) {
			// ignore
		}

		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}
		setFont(table.getFont());
		if (hasFocus) {
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			if (table.isCellEditable(row, column)) {
				setForeground(UIManager.getColor("Table.focusCellForeground"));
				setBackground(UIManager.getColor("Table.focusCellBackground"));
			}
		} else {
			setBorder(new EmptyBorder(1, 2, 1, 2));
		}
		setText((value == null) ? "" : value.toString());
		return this;
	}	
}
