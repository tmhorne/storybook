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

package ch.intertec.storybook.view.content;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.action.ActionManager.SbAction;
import ch.intertec.storybook.action.ActionRegistry;
import ch.intertec.storybook.action.ViewPartAction;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.main.MainSplitPane;
import ch.intertec.storybook.main.MainSplitPane.ContentPanelType;
import ch.intertec.storybook.main.toolbar.ViewItem;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;
import ch.intertec.storybook.view.IconButton;

@SuppressWarnings("serial")
public class ViewControlPanel extends JPanel implements IRefreshable,
		PropertyChangeListener, ChangeListener {

	private JToggleButton btChrono;
	private JToggleButton btMange;
	private JToggleButton btBook;
	private JSlider scaleSlider;
	
	public ViewControlPanel() {
		initGUI();
		
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.addPropertyChangeListener(PCSDispatcher.Property.PROJECT, this);
		pcs.addPropertyChangeListener(PCSDispatcher.Property.PART, this);
		pcs.addPropertyChangeListener(PCSDispatcher.Property.ACTIVE_PART, this);
		pcs.addPropertyChangeListener(PCSDispatcher.Property.VIEW, this);
		pcs.addPropertyChangeListener(PCSDispatcher.Property.REFRESH_ALL, this);
		pcs.addPropertyChangeListener(PCSDispatcher.Property.SCALE, this);
	}

	private void initGUI() {
		setLayout(new MigLayout("ins 8 4 2 6,fillx"));
		ActionRegistry ar = ActionRegistry.getInstance();
		
		add(createOptionsPanel(), "w 320");
		
		ViewItem[] views = new ViewItem[3];
		views[0] = new ViewItem("msg.menu.view.chrono",
				"icon.small.chrono.view", ar.getAction(SbAction.VIEW_CHRONO));
		views[1] = new ViewItem("msg.menu.view.manage",
				"icon.small.manage.view", ar.getAction(SbAction.VIEW_MANAGE));
		views[2] = new ViewItem("msg.menu.view.book", "icon.small.book.view",
				ar.getAction(SbAction.VIEW_BOOK));
		btChrono = views[0].getToggleButton(false);
		btMange = views[1].getToggleButton(false);
		btBook = views[2].getToggleButton(false);
		ButtonGroup group = new ButtonGroup();
		group.add(btChrono);
		add(btChrono, "sg,split 3");
		group.add(btMange);
		add(btMange, "sg");
		group.add(btBook);
		add(btBook, "sg");
		
		add(getPartNaviRefreshPanel(), "gap push");
		
		if (MainFrame.isReady()) {
			updateButtonStates(MainFrame.getInstance().getContentPanelType());
		}
	}

	private JPanel createOptionsPanel() {
		JPanel panel = new JPanel(new MigLayout("ins 0"));

		if (MainFrame.isReady()) {
			MainFrame mainFrame = MainFrame.getInstance();
			
			// scale
			int min = 1;
			int max = 16;
			MainSplitPane splitPane = mainFrame.getSplitPane();
			if (splitPane.getContentPanelType() == ContentPanelType.CHRONO) {
				min = 2;
			}
			int tick = splitPane.getContentPanelType().getScale();
			scaleSlider = SwingTools.createSafeSlider(JSlider.HORIZONTAL, min,
					max, tick);
			scaleSlider.setMinorTickSpacing(1);
			scaleSlider.setMajorTickSpacing(2);
			scaleSlider.setPaintTicks(false);
			scaleSlider.setPaintLabels(false);
			scaleSlider.setSnapToTicks(false);
			scaleSlider.setPreferredSize(new Dimension(200, 10));
			scaleSlider.setToolTipText(I18N.getMsg("msg.common.scale.layout"));
			scaleSlider.addChangeListener(this);
			panel.add(scaleSlider);
			
			// additional options
			JPanel optionsPanel = new JPanel(new MigLayout("ins 0"));
			AbstractContentPanel cp = mainFrame.getContentPanel();
			cp.initOptionsPanel(optionsPanel);
			panel.add(optionsPanel);
		}
		return panel;
	}
	
	private JPanel getPartNaviRefreshPanel() {
		JPanel panel = new JPanel(new MigLayout("ins 0"));
		
		if (PartPeer.doCount() > 1) {
			MainFrame mainFrame = MainFrame.getInstance();
			int activePartId = mainFrame.getActivePartId();
			
			// previous part
			int previousId = PartPeer.getIdOfPreviousPart(activePartId);
			AbstractAction action = new ViewPartAction();
			action.putValue(ViewPartAction.ACTION_KEY_PART_ID, previousId);
			IconButton  btPreviousPart = new IconButton("icon.small.previous",
					"msg.common.part.previous", action);
			btPreviousPart.setSize32x20();
			if (previousId == -1) {
				btPreviousPart.setEnabled(false);
			}
			
			// next part
			int nextId = PartPeer.getIdOfNextPart(activePartId);
			action = new ViewPartAction();
			action.putValue(ViewPartAction.ACTION_KEY_PART_ID, nextId);
			IconButton btNextPart = new IconButton("icon.small.next", "msg.common.part.next",
					action);
			btNextPart.setSize32x20();
			if (nextId == -1) {
				btNextPart.setEnabled(false);
			}
			
			// part label
			Part activePart = PartPeer.doSelectById(activePartId);
			StringBuffer buf = new StringBuffer();
			buf.append(activePart.getNumberStr());
			buf.append(" / ");
			List<Part> partList = PartPeer.doSelectAll();
			Part lastPart = partList.get(partList.size() - 1);
			buf.append(lastPart.getNumberStr());
			JLabel lbPart = new JLabel(buf.toString());
			
			panel.add(btPreviousPart);
			panel.add(lbPart);
			panel.add(btNextPart);
		}
		
		IconButton btRefresh = new IconButton("icon.small.refresh",
				getRefreshPanelAction());
		btRefresh.setSize32x20();
		panel.add(btRefresh, "gap 10");

		return panel; 
	}

	private AbstractAction getRefreshPanelAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractContentPanel cp = MainFrame.getInstance()
						.getContentPanel();
				cp.refresh();
			}
		};
	}
	
	@Override
	public void refresh() {
		removeAll();
		initGUI();
		revalidate();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (PCSDispatcher.isPropertyFired(Property.SCALE, evt)) {
			int val = (Integer) evt.getNewValue();
			scaleSlider.setValue(val);
			return;
		}
		
		refresh();
		try {
			if (PCSDispatcher.isPropertyFired(Property.VIEW, evt)) {
				ContentPanelType newType = (ContentPanelType) evt.getNewValue();
				updateButtonStates(newType);
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	private void updateButtonStates(ContentPanelType type) {
		if (type == ContentPanelType.CHRONO) {
			btChrono.setSelected(true);
		} else if (type == ContentPanelType.MANAGE) {
			btMange.setSelected(true);
		} else if (type == ContentPanelType.BOOK) {
			btBook.setSelected(true);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting()) {
			adjustViewSize(source.getValue());
		}
	}
	
	private void adjustViewSize(int pos) {
		SwingTools.setWaitCursor(this);
		ContentPanelType contentPanelType = MainFrame.getInstance()
				.getSplitPane().getContentPanelType();
		Integer old = contentPanelType.getScale();
		contentPanelType.setScale(pos);
		PCSDispatcher.getInstance().firePropertyChange(
				PCSDispatcher.Property.SCALE.toString(), old, new Integer(pos));
		SwingTools.setDefaultCursor(this);
	}

	public void zoomIn() {
		SwingTools.setWaitCursor(this);
		ContentPanelType contentPanelType = MainFrame.getInstance()
				.getSplitPane().getContentPanelType();
		Integer old = contentPanelType.getScale();
		int pos = old + 1;
		contentPanelType.setScale(pos);
		PCSDispatcher.getInstance().firePropertyChange(
				PCSDispatcher.Property.SCALE.toString(), old, new Integer(pos));
		SwingTools.setDefaultCursor(this);
	}

	public void zoomOut() {
		ContentPanelType contentPanelType = MainFrame.getInstance()
				.getSplitPane().getContentPanelType();
		Integer old = contentPanelType.getScale();
		int pos = old - 1;
		if (pos < 1) {
			pos = 1;
		}
		contentPanelType.setScale(pos);
		PCSDispatcher.getInstance().firePropertyChange(
				PCSDispatcher.Property.SCALE.toString(), old, new Integer(pos));
		SwingTools.setDefaultCursor(this);
	}
}
