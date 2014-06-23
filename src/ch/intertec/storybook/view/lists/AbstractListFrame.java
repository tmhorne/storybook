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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.action.TableCopyAction;
import ch.intertec.storybook.action.TableDeleteAction;
import ch.intertec.storybook.action.TableEditAction;
import ch.intertec.storybook.action.TableNewAction;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Gender;
import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.table.StandardTableCellRenderer;
import ch.intertec.storybook.view.IRefreshable;
import ch.intertec.storybook.view.model.dbtable.AbstractDbTable;
import ch.intertec.storybook.view.model.dbtable.ChapterDbTable;
import ch.intertec.storybook.view.model.dbtable.CharacterDbTable;
import ch.intertec.storybook.view.model.dbtable.DbValue;
import ch.intertec.storybook.view.model.dbtable.GenderDbTable;
import ch.intertec.storybook.view.model.dbtable.ItemDbTable;
import ch.intertec.storybook.view.model.dbtable.LocationDbTable;
import ch.intertec.storybook.view.model.dbtable.PartDbTable;
import ch.intertec.storybook.view.model.dbtable.StrandDbTable;
import ch.intertec.storybook.view.model.dbtable.TagDbTable;

@SuppressWarnings("serial")
public abstract class AbstractListFrame extends JFrame implements
		ListSelectionListener, ActionListener, MouseListener,
		PropertyChangeListener, WindowListener, IRefreshable {

	protected AbstractDbTable table;
	
	// actions
	private AbstractAction editAction;
	private AbstractAction copyAction;
	protected AbstractAction orderUpAction;
	protected AbstractAction orderDownAction;

	private JTable jtable;

	private JButton btEdit;
	private JButton btCopy;
	private JButton btDelete;
	private JButton btOrderUp;
	private JButton btOrderDown;
	
	protected boolean showOrderButtons = false;
	
	private static final String CMD_COPY = "copy";
	private static final String CMD_DELETE = "delete";
	
	abstract void init();
	abstract int getPreferredWidth();
	
	public AbstractListFrame() {
		init();
		initGUI();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(this);
		PCSDispatcher.getInstance().addPropertyChangeListener(
				Property.PROJECT, this);
	}

	private void initGUI() {
		MigLayout layout = new MigLayout(
				"wrap,fill",
				"[]",
				"[grow][]");
		setLayout(layout);
		setPreferredSize(new Dimension(getPreferredWidth(),
				SwingTools.getPreferredHeight(500)));
		ImageIcon icon = (ImageIcon)I18N.getIcon("icon.sb");
		setIconImage(icon.getImage());		
		
		jtable = table.getJTable();
		jtable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jtable.setAutoCreateRowSorter(true);
		jtable.getTableHeader().setReorderingAllowed(false);
		jtable.setDefaultRenderer(Object.class, new StandardTableCellRenderer());
		jtable.removeMouseListener(this);
		jtable.addMouseListener(this);
		jtable.getSelectionModel().addListSelectionListener(this);
		jtable.getColumnModel().getSelectionModel()
				.addListSelectionListener(this);
		jtable.getInputMap()
				.put(KeyStroke.getKeyStroke("ENTER"), "editByEnter");
		jtable.getActionMap().put("editByEnter", getEditAction());
		KeyStroke copy = SwingTools.getKeyStrokeCopy();
		jtable.registerKeyboardAction(this, CMD_COPY, copy,
				JComponent.WHEN_FOCUSED);
		KeyStroke delete = SwingTools.getKeyStrokeDelete();
		jtable.registerKeyboardAction(this, CMD_DELETE, delete,
				JComponent.WHEN_FOCUSED);

		JScrollPane scroller = new JScrollPane(jtable);
		scroller.setBorder(SwingTools.getEtchedBorder());

		// new button
		JButton btNew = new JButton();
		btNew.setAction(getNewAction());
		SwingTools.addEnterAction(btNew, getNewAction());
		btNew.setText(I18N.getMsg("msg.common.new"));
		btNew.setIcon(I18N.getIcon("icon.small.new"));

		// edit button
		btEdit = new JButton();
		btEdit.setAction(getEditAction());
		btEdit.setText(I18N.getMsg("msg.common.edit"));
		btEdit.setIcon(I18N.getIcon("icon.small.edit"));

		// delete
		btDelete = new JButton();
		btDelete.setAction(getDeleteAction());
		btDelete.setText(I18N.getMsg("msg.common.delete"));
		btDelete.setIcon(I18N.getIcon("icon.small.delete"));

		// copy
		btCopy = new JButton();
		btCopy.setAction(getCopyAction());
		btCopy.setText(I18N.getMsg("msg.common.copy"));
		btCopy.setIcon(I18N.getIcon("icon.small.copy"));

		// order up
		btOrderUp = new JButton();
		btOrderUp.setAction(getOrderUpAction());
		btOrderUp.setIcon(I18N.getIcon("icon.small.arrow.up"));

		// order down
		btOrderDown = new JButton();
		btOrderDown.setAction(getOrderDownAction());
		btOrderDown.setIcon(I18N.getIcon("icon.small.arrow.down"));

		// refresh
		JButton btRefresh = new JButton();
		btRefresh.setAction(getRefreshAction());
		btRefresh.setIcon(I18N.getIcon("icon.small.refresh"));
		btRefresh.setToolTipText(I18N.getMsg("msg.common.refresh"));

		// close
		JButton btClose = new JButton();
		btClose.setAction(getCloseAction());
		SwingTools.addEscAction(btClose, getCloseAction());
		btClose.setText(I18N.getMsg("msg.common.close"));
		btClose.setIcon(I18N.getIcon("icon.small.close"));

		// layout
		add(scroller, "grow");
		if (showOrderButtons) {
			add(btNew, "split 8");
		} else {
			add(btNew, "split 6");
		}
		add(btEdit);
		add(btCopy);
		add(btDelete);
		if (showOrderButtons) {
			add(btOrderUp, "sg");
			add(btOrderDown, "sg");
		}
		add(btRefresh, "");
		add(btClose, "gap push");
		
		checkButtonState(0);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (PCSDispatcher.isPropertyFired(Property.PROJECT, evt)) {
			refresh();
			return;
		}
		
		try {
			int selectedRow = jtable.getSelectedRow();
			table.refresh();
			jtable.setColumnSelectionAllowed(false);
			jtable.setRowSelectionAllowed(true);
			jtable.setRowSelectionInterval(selectedRow, selectedRow);
		} catch (java.lang.IllegalArgumentException e) {
			// ignore
		}
	}
	
	abstract protected void addListeners();
	
	abstract protected void removeListeners();

	protected AbstractListFrame getThis() {
		return this;
	}
	
	private AbstractAction getRefreshAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				refresh();
			}
		};
	}
	
	private AbstractAction getDeleteAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				JTable jtable = table.getJTable();
				int[] rows = jtable.getSelectedRows();
				if (rows.length == 0) {
					return;
				}
				ArrayList<TableDeleteAction> actions = new ArrayList<TableDeleteAction>();
				for (int row : rows) {
					int modelRow = jtable.convertRowIndexToModel(row);
					Object value = jtable.getValueAt(modelRow, 0);
					if (value instanceof DbValue) {
						DbValue dbValue = (DbValue) value;
						if (!dbValue.isEditable()) {
							continue;
						}
					}
					DbTable dbTable = table.getValueAt(modelRow);
					TableDeleteAction action = new TableDeleteAction(dbTable);
					action.setParentFrame(getThis());
					actions.add(action);
				}
				for (TableDeleteAction action : actions) {
					action.actionPerformed(null);
				}
			}
		};
	}

	private AbstractAction getNewAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				TableNewAction action = null;
				if (table instanceof LocationDbTable) {
					action = new TableNewAction(new Location());
				} else if (table instanceof CharacterDbTable) {
					action = new TableNewAction(new SbCharacter());
				} else if (table instanceof PartDbTable) {
					action = new TableNewAction(new Part());
				} else if (table instanceof ChapterDbTable) {
					action = new TableNewAction(new Chapter());
				} else if (table instanceof StrandDbTable) {
					action = new TableNewAction(new Strand());
				} else if (table instanceof ItemDbTable) {
					action = new TableNewAction(new Item());
				} else if (table instanceof TagDbTable) {
					action = new TableNewAction(new Tag());
				} else if (table instanceof GenderDbTable) {
					action = new TableNewAction(new Gender());
				}
				if (action != null) {
					action.setParentFrame(getThis());
					action.actionPerformed(null);
				}
			}
		};
	}

	private AbstractAction getEditAction() {
		if (editAction == null) {
			editAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					JTable jtable = table.getJTable();
					int row = jtable.getSelectedRow();
					if (row == -1) {
						return;
					}
					int modelRow = jtable.convertRowIndexToModel(row);
					Object value = jtable.getValueAt(modelRow, 0);
					if (value instanceof DbValue) {
						DbValue dbValue = (DbValue) value;
						if (!dbValue.isEditable()) {
							return;
						}
					}
					DbTable dbTable = table.getValueAt(modelRow);
					TableEditAction action = new TableEditAction(dbTable);
					action.setParentFrame(getThis());
					action.actionPerformed(null);
				}
			};
		}
		return editAction;
	}

	private AbstractAction getCopyAction() {
		if (copyAction == null) {
			copyAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					JTable jtable = table.getJTable();
					int row = jtable.getSelectedRow();
					if (row == -1) {
						return;
					}
					int modelRow = jtable.convertRowIndexToModel(row);
					DbTable dbObj = table.getValueAt(modelRow);
					TableCopyAction action = new TableCopyAction(dbObj);
					action.actionPerformed(evt);
				}
			};
		}
		return copyAction;
	}

	protected AbstractAction getOrderUpAction() {
		if (orderUpAction == null) {
			orderUpAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					// no default implementation
				}
			};
		}
		return orderUpAction;
	}

	protected AbstractAction getOrderDownAction() {
		if (orderDownAction == null) {
			orderDownAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					// no default implementation
				}
			};
		}
		return orderDownAction;
	}

	private AbstractAction getCloseAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				removeListeners();
				getThis().dispose();
			}
		};
	}
	
	@Override
	public void setTitle(String resourceKey) {
		super.setTitle(I18N.getMsg(resourceKey));
	}
	
	@Override
	public void refresh() {
		removeListeners();
		getContentPane().removeAll();
		if (!ProjectTools.isProjectOpen()) {
			validate();
			repaint();
			return;
		}
		
		try {
			int selectedRow = jtable.getSelectedRow();
			table.refresh();
			initGUI();
			jtable.setColumnSelectionAllowed(false);
			jtable.setRowSelectionAllowed(true);
			jtable.setRowSelectionInterval(selectedRow, selectedRow);
		} catch (java.lang.IllegalArgumentException e) {
			// ignore
		}

		validate();
		addListeners();
	}
	
	@Override
	public void mouseClicked(MouseEvent evt) {
		Object o = evt.getSource();
		if (o instanceof JTable) {
			if (evt.getClickCount() == 2) {
				getEditAction().actionPerformed(null);
			}
		}
		evt.consume();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void windowActivated(WindowEvent e) {
	}
	
	@Override
	public void windowClosed(WindowEvent e) {
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		PCSDispatcher.getInstance().removeAllPropertyChangeListener(this);
	}
	
	@Override
	public void windowDeactivated(WindowEvent e) {
	}
	
	@Override
	public void windowDeiconified(WindowEvent e) {
	}
	
	@Override
	public void windowIconified(WindowEvent e) {
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		// addListeners();
		refresh();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareTo(CMD_COPY) == 0) {
			getCopyAction().actionPerformed(e);
			refresh();
			return;
		}
		if (e.getActionCommand().compareTo(CMD_DELETE) == 0) {
			getDeleteAction().actionPerformed(e);
			return;
		}
	}
	
	private void checkButtonState(int count) {
		if (count == 0) {
			btEdit.setEnabled(false);
			btCopy.setEnabled(false);
			btDelete.setEnabled(false);
			btOrderUp.setEnabled(false);
			btOrderDown.setEnabled(false);
			return;
		}
		if (count == 1) {
			btEdit.setEnabled(true);
			btCopy.setEnabled(true);
			btDelete.setEnabled(true);
			btOrderUp.setEnabled(true);
			btOrderDown.setEnabled(true);
			return;
		}
		btEdit.setEnabled(false);
		btCopy.setEnabled(false);
		btDelete.setEnabled(true);
		btOrderUp.setEnabled(false);
		btOrderDown.setEnabled(false);
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		int count = jtable.getSelectedRowCount();
		checkButtonState(count);
	}
}
