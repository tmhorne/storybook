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

import java.awt.Component;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.label.StrandDateLabel;
import ch.intertec.storybook.view.IRefreshable;
import ch.intertec.storybook.view.content.book.BookContentPanel;
import ch.intertec.storybook.view.content.book.BookScenePanel;
import ch.intertec.storybook.view.content.chrono.AbstractStrandDatePanel;
import ch.intertec.storybook.view.content.chrono.ChronoContentPanel;
import ch.intertec.storybook.view.content.chrono.cell.ChronoScenePanel;
import ch.intertec.storybook.view.content.chrono.cell.SpacePanel;
import ch.intertec.storybook.view.content.manage.ChapterPanel;
import ch.intertec.storybook.view.content.manage.ManageContentPanel;
import ch.intertec.storybook.view.content.manage.ScenePanel;

@SuppressWarnings("serial")
public abstract class AbstractContentPanel extends JPanel implements
		IRefreshable, PropertyChangeListener {
	
	public final static String COMP_NAME ="content_panel";
	
	public AbstractContentPanel() {
		setName(COMP_NAME);

		// remove listeners
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		removeListeners();
		// remove content panel listeners
		pcs.removeListenersByClass(this.getClass());
		pcs.removeListenersByClass(ChronoContentPanel.class);
		pcs.removeListenersByClass(BookContentPanel.class);
		pcs.removeListenersByClass(ManageContentPanel.class);
		
		// initialize GUI
		init();
		initGUI();
		if (MainFrame.isReady()) {
			MainFrame.getInstance().getSplitPane().resetViewPosition();
		}
		registerKeyboardAction();
		
		// add property change listeners
		pcs.addPropertyChangeListener(
				PCSDispatcher.Property.ACTIVE_PART, this);
		pcs.addPropertyChangeListener(
				PCSDispatcher.Property.VIEW, this);
		pcs.addPropertyChangeListener(
				PCSDispatcher.Property.SCALE, this);
		pcs.addPropertyChangeListener(
				PCSDispatcher.Property.STRAND, this);
	}

	protected abstract void init();
	
	protected abstract void initGUI();
	
	protected abstract void initOptionsPanel(JPanel optionsPanel);
	
	@Override
	public void refresh() {
		removeAll();
		initGUI();
		revalidate();
		repaint();
	}
	
	protected void refreshScene(Scene scene) {
		List<Component> list = findScenePanels();
		for (Component comp : list) {
			AbstractScenePanel scenePanel = (AbstractScenePanel) comp;
			if (scenePanel.getScene().getId() == scene.getId()) {
				scenePanel.refresh();
				return;
			}
		}
	}

	protected void refreshStrandDate(Scene scene) {
		refreshStrandDate(scene, null);
	}

	protected void refreshStrandDate(Scene scene, Date sceneDate) {
		Strand sceneStrand = scene.getStrand();
		if (sceneDate == null) {
			sceneDate = scene.getDate();
		}
		List<Component> list = findStrandDatePanels();
		for (Component comp : list) {
			AbstractStrandDatePanel sdPanel = (AbstractStrandDatePanel) comp;
			Strand strand = sdPanel.getStrand();
			Date date = sdPanel.getDate();
			if (date.compareTo(sceneDate) == 0
					&& strand.getId() == sceneStrand.getId()) {
				sdPanel.refresh();
				return;
			}
		}
		
		// strand-date panel not found
		refresh();
	}

	protected void refreshStrands(Scene scene, Strand oldStrand, Strand newStrand) {
		Date sceneDate = scene.getDate();
		List<Component> list = findStrandDatePanels();
		for (Component comp : list) {
			AbstractStrandDatePanel sdPanel = (AbstractStrandDatePanel) comp;
			Date date = sdPanel.getDate();
			if (date.compareTo(sceneDate) != 0) {
				continue;
			}
			Strand strand = sdPanel.getStrand();
			if (strand.getId() == oldStrand.getId()
					|| strand.getId() == newStrand.getId()) {
				sdPanel.refresh();
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {		
		if (PCSDispatcher.isPropertyFired(Property.SCALE, evt)) {
			removeListeners();
			refresh();
			return;
		}

		if (PCSDispatcher.isPropertyFired(Property.ACTIVE_PART, evt)) {
			removeListeners();
			refresh();
			return;
		}

		if (PCSDispatcher.isPropertyFired(Property.VIEW, evt)) {
			removeListeners();
			refresh();
			return;
		}

		if (PCSDispatcher.isPropertyFired(Property.STRAND, evt)) {
			if (!PCSDispatcher.isPropertyEdited(
					evt.getOldValue(), evt.getNewValue())) {
				// strand added or removed, but not edited
				refresh();
			}
		}
	}
	
	private void removeListeners(){
		// remove listeners
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		
		// for chrono panel
		pcs.removeListenersByClass(ChronoScenePanel.class);
		pcs.removeListenersByClass(SpacePanel.class);
		
		// for book panel
		pcs.removeListenersByClass(BookScenePanel.class);
		
		// for manage panel
		pcs.removeListenersByClass(ChapterPanel.class);
		pcs.removeListenersByClass(ScenePanel.class);
	}
	
	public List<Component> findScenePanels() {
		List<Component> list = new ArrayList<Component>();
		SwingTools.findComponentsByClass(this, AbstractScenePanel.class, list);
		return list;
	}

	public List<Component> findStrandDatePanels() {
		List<Component> list = new ArrayList<Component>();
		SwingTools.findComponentsByClass(this, AbstractStrandDatePanel.class, list);
		return list;
	}

	public List<Component> findStrandDateLabels() {
		List<Component> list = new ArrayList<Component>();
		SwingTools.findComponentsByClass(this, StrandDateLabel.class, list);
		return list;
	}
	
	protected void scrollHorizontal(int amount, int rotation) {
		JScrollPane scroller = MainFrame.getInstance().getScroller();
		JScrollBar sb = scroller.getHorizontalScrollBar();
		int val = sb.getValue();
		sb.setValue(val + amount * rotation * sb.getUnitIncrement());
	}

	protected void scrollVertical(int amount, int rotation) {
		JScrollPane scroller = MainFrame.getInstance().getScroller();
		JScrollBar sb = scroller.getVerticalScrollBar();
		int val = sb.getValue();
		sb.setValue(val + amount * rotation * sb.getUnitIncrement());
	}

	private void registerKeyboardAction(){
		registerKeyboardAction(new ScrollToRightAction(),
				KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.ALT_MASK),
				WHEN_IN_FOCUSED_WINDOW);
		registerKeyboardAction(new ScrollToLeftAction(),
				KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.ALT_MASK),
				WHEN_IN_FOCUSED_WINDOW);
		registerKeyboardAction(new ScrollUpAction(),
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, Event.ALT_MASK),
				WHEN_IN_FOCUSED_WINDOW);
		registerKeyboardAction(new ScrollDownAction(),
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Event.ALT_MASK),
				WHEN_IN_FOCUSED_WINDOW);
		registerKeyboardAction(new ZoomInAction(),
				KeyStroke.getKeyStroke(KeyEvent.VK_ADD, Event.CTRL_MASK), 
				WHEN_IN_FOCUSED_WINDOW);
		registerKeyboardAction(new ZoomOutAction(),
				KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, Event.CTRL_MASK),
				WHEN_IN_FOCUSED_WINDOW);
	}
	
	private class ScrollToRightAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			scrollHorizontal(6, 1);
		}
	}

	private class ScrollToLeftAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			scrollHorizontal(6, -1);
		}
	}
	
	private class ScrollUpAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			scrollVertical(6, -1);
		}
	}
	
	private class ScrollDownAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			scrollVertical(6, 1);
		}
	}
	
	private class ZoomInAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			MainFrame.getInstance().getSplitPane().getViewControlPanel()
					.zoomIn();
		}
	}

	private class ZoomOutAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			MainFrame.getInstance().getSplitPane().getViewControlPanel()
					.zoomOut();
		}
	}
}
