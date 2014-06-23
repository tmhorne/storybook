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

package ch.intertec.storybook.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.ItemLink;
import ch.intertec.storybook.model.TagLink;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.Constants.ComponentName;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextArea;

/**
 * The root class for all dialogs implemented in Storybook.
 *
 * @author Martin
 */
@SuppressWarnings("serial")
public abstract class AbstractModifyDialog extends JDialog implements ChangeListener,
		WindowListener, IRefreshable {

	public static final String COMP_NAME_TAB_COMMON = "tab:common";
	public static final String COMP_NAME_TAB_NOTES = "tab:notes";
	
	protected static Logger logger = Logger.getLogger(AbstractModifyDialog.class);
	
	protected DbTable table = null;
	protected ArrayList<TagLink> tagLinks = null;
	protected ArrayList<ItemLink> itemLinks = null;
	
	protected JPanel internalPanel = null;
	protected JPanel panel = null;
	protected JTabbedPane tabbedPane = null;
	protected JLabel lbError = new JLabel();

	private int lastSelectedIndex = 0;
	private boolean useScrollPane = false;
	
	// actions
	protected AbstractAction okAction;
	protected AbstractAction cancelAction;

	protected boolean canceled = false;
	protected boolean edit = false;
	
	protected List<JComponent> inputComponentList = new ArrayList<JComponent>();;
		
	protected JComponent focusComponent;
	
	public AbstractModifyDialog(){
		super();
		internalInit();
	}
	
	public AbstractModifyDialog(JFrame frame) {
		super(frame);
		internalInit();
		this.init();
		this.initPanels();
		this.initInternalGUI();
	}

	public AbstractModifyDialog(JFrame frame, DbTable table) {
		super(frame);
		this.table = table;
		this.edit = true;
		internalInit();
		this.init();
		this.initPanels();
		this.initInternalGUI();
		this.setValuesFromTable();
	}

	public AbstractModifyDialog(JFrame frame, ArrayList<TagLink> tagLinks) {
		super(frame);
		this.tagLinks = tagLinks;
		if (!tagLinks.isEmpty()) {
			this.table = tagLinks.get(0);
		}
		this.edit = true;
		internalInit();
		this.init();
		this.initPanels();
		this.initInternalGUI();
		this.setValuesFromTable();
	}

	public AbstractModifyDialog(JFrame frame, ArrayList<ItemLink> itemLinks,
			boolean dummy) {
		super(frame);
		this.itemLinks = itemLinks;
		if (!itemLinks.isEmpty()) {
			this.table = itemLinks.get(0);
		}
		this.edit = true;
		internalInit();
		this.init();
		this.initPanels();
		this.initInternalGUI();
		this.setValuesFromTable();
	}

	public AbstractModifyDialog(Action action) {
		super();
		edit = false;
		internalInit();
		init();
		initPanels();
		initInternalGUI();
		setValuesFromAction(action);
	}

	/**
	 * Instantiates any objects required for the running of this dialog.
	 */
	protected abstract void init();
	
	/**
	 * Starts the GUI with the necessary panels and data.
	 */
	protected abstract void initGUI();
	
	/**
	 * Override this method for clean up stuff before the dialog is closed.
	 */
	protected void finish() {
	}
	
	/**
	 * Defines the layout to be used by the dialog.
	 * @return a populated {@link MigLayout} object to be used in the layout manager.
	 */
	protected abstract MigLayout getMigLayout();
	
	/**
	 * Preloads the data into the dialog from the associated {@link DbTable} instance.
	 */
	protected abstract void setValuesFromTable();
	
	protected abstract void setValuesFromAction(Action action);
	
	/**
	 * Creates or edits a given record for the passed dialog.
	 * @param dlg the subclass of {@link AbstractModifyDialog} that will be affected.
	 * @param edit <code>true</code> if it's an edit, <code>false</code> to create new.
	 * @throws Exception if something goes awry.
	 */
	protected abstract void makeOrUpdate(AbstractModifyDialog dlg, boolean edit)
		throws Exception;
	
	protected void initPanels() {
		MigLayout layout = new MigLayout(
				"wrap,fill",
				"[grow]",
				"[top,grow]10[]");
		this.internalPanel = new JPanel(layout);
		this.tabbedPane = new JTabbedPane();
		this.tabbedPane.addChangeListener(this);
		this.panel = new JPanel(getMigLayout());
		this.tabbedPane.addTab(I18N.getMsg("msg.common"), panel);
	}
	
	private void internalInit(){
		addWindowListener(this);
	}
	
	/**
	 * Sets some common elements shared across all GUIs.
	 */
	protected void initInternalGUI(){
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		initGUI();
		
		// OK button
		JButton btOk = new JButton();
		btOk.setAction(getOkAction());
		SwingTools.addEnterAction(btOk, getOkAction());
		btOk.setText(I18N.getMsg("msg.common.ok"));
		btOk.setName(ComponentName.OK_BUTTON.toString());
		
		// cancel button
		JButton btCancel = new JButton();
		btCancel.setAction(getCancelAction());
		SwingTools.addEscAction(btCancel, getCancelAction());
		btCancel.setText(I18N.getMsg("msg.common.cancel"));
		btCancel.setIcon(I18N.getIcon("icon.small.close"));
		btCancel.setVerifyInputWhenFocusTarget(false);
		btCancel.setName(ComponentName.CANCEL_BUTTON.toString());
		
		if (useScrollPane) {
			JScrollPane scrollPane = new JScrollPane(tabbedPane);
			internalPanel.add(scrollPane, "grow");
		} else {
			internalPanel.add(tabbedPane, "grow");
		}
		
		internalPanel.add(lbError, "split 3");
		internalPanel.add(btOk, "sg,gap push");
		internalPanel.add(btCancel, "sg");
		add(internalPanel);	
	}
	
	public UndoableTextArea createNotesTextArea(){
		UndoableTextArea taNotes = new UndoableTextArea();
		taNotes.setLineWrap(true);
		taNotes.setWrapStyleWord(true);
		taNotes.setDragEnabled(true);
		SwingTools.addCtrlEnterAction(taNotes, getOkAction());
		JPopupMenu popup = new JPopupMenu();
		SwingTools.addCopyPasteToPopupMenu(popup, taNotes);
		taNotes.setComponentPopupMenu(popup);
		return taNotes;
	}
	
	public JPanel createDescrPanel(JTextArea textArea) {
		MigLayout layout = new MigLayout("fill", "", "[top]");
		JPanel panel = new JPanel(layout);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		JPopupMenu popup = new JPopupMenu();
		SwingTools.addCopyPasteToPopupMenu(popup, textArea);
		textArea.setComponentPopupMenu(popup);
		SwingTools.addCtrlEnterAction(textArea, getOkAction());
		JScrollPane scroller = new JScrollPane(textArea);
		scroller.setPreferredSize(new Dimension(400, 400));
		panel.add(scroller, "grow");
		return panel;
	}
	
	/**
	 * Returns the label to be shown on an error.
	 * @return the {@link JLabel} to be shown on an error.
	 */
	public JLabel getErrorLabel(){
		return this.lbError;
	}
	
	/**
	 * Handles the "okay" button being pressed.
	 * @return an {@link AbstractAction} capable of handling the event.
	 */
	protected AbstractAction getOkAction() {
		if (okAction == null) {
			okAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					try {
						if (!SwingTools.checkInputComponents(getThis().inputComponentList)) {
							return;
						}
						getThis().finish();
						getThis().makeOrUpdate(getThis(), getThis().edit);
					} catch (SQLException e) {
						SwingTools.showException(e);
						return;
					} catch(Exception e){
						e.printStackTrace();
					}
					getThis().dispose();
				}
			};
		}
		return okAction;
	}
	
	/**
	 * Handles the pressing of the "cancel" button.
	 * @return an {@link AbstractAction} capable of processing the event.
	 */
	protected AbstractAction getCancelAction() {
		if (this.cancelAction == null) {
			this.cancelAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					getThis().finish();
					getThis().canceled = true;
					getThis().dispose();
				}
			};
		}
		return cancelAction;
	}
	
	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		if (!(e.getSource() instanceof JTabbedPane)) {
			return;
		}
		// avoid changing if the input check fails
		JTabbedPane p = (JTabbedPane) e.getSource();
		if (!SwingTools.checkInputComponents(this.inputComponentList)) {
			p.setSelectedIndex(this.lastSelectedIndex);
			return;
		}
		this.lastSelectedIndex = p.getSelectedIndex();
	}

	@Override
	public void refresh() {
		panel.removeAll();
		internalPanel.removeAll();
		initInternalGUI();
		internalPanel.validate();
		internalPanel.repaint();
		panel.validate();
		panel.repaint();
		pack();
	}
	
	/**
	 * Returns its self for use within anonymous objects that require
	 * references to this object without being able to use <code>this</code>
	 * keyword.
	 */
	protected AbstractModifyDialog getThis() {
		return this;
	}

	/**
	 * Returns whether the dialog was canceled or not.
	 * @return <code>true</code> if the dialog action was canceled, <code>false</code> otherwise.
	 */
	public boolean getCanceled() {
		return canceled;
	}
	
	/**
	 * Sets the component which requests the focus after
	 * the window opened.
	 * @param focusComponent the focus component
	 */
	public void setFocusComponent(JComponent focusComponent) {
		this.focusComponent = focusComponent;
	}
	
	@Override
	public void setTitle(String resourceKey) {
		super.setTitle(I18N.getMsg(resourceKey));
	}
	
	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		canceled = true;
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
		if (this.focusComponent == null) {
			return;
		}
		this.focusComponent.requestFocus();
	}

	public boolean isUseScrollPane() {
		return useScrollPane;
	}

	public void setUseScrollPane(boolean useScrollPane) {
		this.useScrollPane = useScrollPane;
	}
}
