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

package ch.intertec.storybook.view.content.manage;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.action.ActionManager.SbAction;
import ch.intertec.storybook.action.ActionRegistry;
import ch.intertec.storybook.action.TableNewAction;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.InternalPeer;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.crchooser.ColRowChooser;
import ch.intertec.storybook.toolkit.swing.crchooser.IColRowChooserService;
import ch.intertec.storybook.view.IconButton;
import ch.intertec.storybook.view.content.AbstractContentPanel;

@SuppressWarnings("serial")
public class ManageContentPanel extends AbstractContentPanel implements
		PropertyChangeListener, DragSourceMotionListener,
		IColRowChooserService, ITextLengthChooserService {

	public ManageContentPanel() {
		super();
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.addPropertyChangeListener(Property.CHAPTER, this);
		pcs.addPropertyChangeListener(Property.SCENE, this);
		pcs.addPropertyChangeListener(Property.STRAND, this);

		DragSource.getDefaultDragSource().addDragSourceMotionListener(this);
	}

	public int getCols() {
		return InternalPeer.getManageCols();
	}

	public void setCols(int cols) {
		InternalPeer.saveManageCols(cols);
	}

	public void setTextLength(int textLength) {
		InternalPeer.saveManageViewTextLength(textLength);
		refreshPanel();
	}

	@Override
	protected void init() {
	}

	@Override
	public void initGUI() {
		MigLayout layout = new MigLayout(
				"wrap " + getCols(),
				"[]10[]10[]10[]",
				"[]10"
		);
		setLayout(layout);
		setBackground(SwingTools.getBackgroundColor());
		setComponentPopupMenu(createPopupMenu());
		refreshPanel();
	}

	private void refreshPanel() {
		JScrollPane scroller = MainFrame.getInstance().getScroller();
		// set scroll unit increments
		scroller.getVerticalScrollBar().setUnitIncrement(20);
		scroller.getHorizontalScrollBar().setUnitIncrement(20);
		// save values
		int vsb = scroller.getVerticalScrollBar().getValue();
		int hsb = scroller.getHorizontalScrollBar().getValue();

		removeAll();

		int c = 0;
		int row = 0;
		// unassigned scenes
		add(new ChapterPanel(), "grow,sg " + row);
		++c;

		List<Chapter> chapterList = ChapterPeer.doSelectAll(true);
		for (Chapter chapter : chapterList) {
			row = c / getCols();
			ChapterPanel chapterPanel = new ChapterPanel(chapter);
			add(chapterPanel, "grow,sg " + row);
			++c;
		}

		scroller.setViewportView(this);
		scroller.getVerticalScrollBar().setValue(vsb);
		scroller.getHorizontalScrollBar().setValue(hsb);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();

		if (PCSDispatcher.isPropertyFired(Property.CHAPTER, evt)) {
			if (oldValue == null && newValue != null) {
				Chapter chapter = (Chapter) newValue;
				if (chapter.getPcsState() == true) {
					// created a new chapter
					refreshPanel();
					return;
				}
			}

			if (oldValue == null && newValue == null) {
				// created multiple chapters
				refreshPanel();
				return;
			}

			if (oldValue != null && newValue == null) {
				if (((Chapter) oldValue).isMarkedAsExpired()) {
					// deleted a chapter
					refreshPanel();
					return;
				}
			}

			Chapter dirtyChapter = (Chapter) (oldValue != null ? oldValue
					: newValue);
			List<Component> list = getChapterPanels();
			for (Component comp : list) {
				ChapterPanel chPanel = (ChapterPanel) comp;
				if (dirtyChapter == null) {
					if (chPanel.isForUnassignedScene()) {
						chPanel.refresh();
						continue;
					}
					continue;
				}
				if (dirtyChapter.isForUnassignedScenes()
						&& chPanel.isForUnassignedScene()) {
					chPanel.refresh();
					continue;
				}
				if (chPanel.isForUnassignedScene()) {
					continue;
				}
				if (dirtyChapter.getId() == -1) {
					int newId = ((Chapter) newValue).getId();
					if (chPanel.getChapter().getId() == newId) {
						chPanel.refresh();
						continue;
					}
				}
				if (chPanel.getChapter().getId() != dirtyChapter.getId()) {
					continue;
				}
				chPanel.refresh();
			}
			validate();
			repaint();
			return;
		}

		if (PCSDispatcher.isPropertyFired(Property.SCENE, evt)) {
			Scene oldScene = (Scene) oldValue;
			Scene newScene = (Scene) newValue;
			List<Component> list = getChapterPanels();
			for (Component comp : list) {
				ChapterPanel chPanel = (ChapterPanel) comp;
				if (chPanel.isForUnassignedScene()) {
					chPanel.refresh();
					continue;
				}
				if (oldScene != null) {
					if (chPanel.getChapter().getId() == oldScene.getChapterId()) {
						chPanel.refresh();
						continue;
					}
				}
				if (newScene != null) {
					if (chPanel.getChapter().getId() == newScene.getChapterId()) {
						chPanel.refresh();
						continue;
					}
				}
			}
			return;
		}

		if (PCSDispatcher.isPropertyFired(Property.STRAND, evt)) {
			refreshPanel();
			return;
		}

		// everything else is handled by super class
		super.propertyChange(evt);
	}

	private List<Component> getChapterPanels() {
		List<Component> list = new ArrayList<Component>();
		SwingTools.findComponentsByClass(this, ChapterPanel.class, list);
		return list;
	}

	private JPopupMenu createPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new TableNewAction(new Scene()));
		menu.add(new TableNewAction(new Chapter()));
		menu.add(new Separator());
		menu.add(new TableNewAction(new SbCharacter()));
		menu.add(new TableNewAction(new Location()));
		menu.add(new TableNewAction(new Tag()));
		menu.add(new Separator());
		menu.add(new TableNewAction(new Strand()));
		menu.add(new TableNewAction(new Part()));
		menu.add(new Separator());
		// generate chapters
		ActionRegistry ar = ActionRegistry.getInstance();
		menu.add(ar.getAction(SbAction.CHAPTER_GENERATE));
		return menu;
	}

	private ManageContentPanel getThis(){
		return this;
	}
	
	private Action getSetColsAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int cols = getCols();
				ColRowChooser chooser = new ColRowChooser(getThis(), 16, 1,
						cols, 1);
				JComponent comp = (JComponent) e.getSource();
				Point p = comp.getLocationOnScreen();
				p.translate(0, comp.getHeight() + 2);
				PopupFactory factory = PopupFactory.getSharedInstance();
				final Popup popup = factory.getPopup(comp, chooser, p.x, p.y);
				chooser.setPopup(popup);
				popup.show();
			}
		};
	}

	private Action getSetTextLengthAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextLengthChooser chooser = new TextLengthChooser(getThis());
				JComponent comp = (JComponent) e.getSource();
				Point p = comp.getLocationOnScreen();
				p.translate(0, comp.getHeight() + 2);
				PopupFactory factory = PopupFactory.getSharedInstance();
				final Popup popup = factory.getPopup(comp, chooser, p.x, p.y);
				chooser.setPopup(popup);
				popup.show();
			}
		};
	}

	@Override
	public void dragMouseMoved(DragSourceDragEvent dsde) {
		// p is in screen coordinates
		Point dsdePoint = new Point(dsde.getX(), dsde.getY());
		JScrollPane scroller = MainFrame.getInstance().getSplitPane()
				.getScroller();
		Rectangle vr = scroller.getVisibleRect();
		Point p = new Point((int) vr.getMinX(), (int) vr.getMinY());
		SwingUtilities.convertPointToScreen(p, scroller);
		Rectangle pScreen = new Rectangle(p);
		pScreen.setSize(vr.width, vr.height - 20);
		if (pScreen.contains(dsdePoint)) {
			// inside the visible rectangle
			return;
		}

		JScrollBar sbVertical = scroller.getVerticalScrollBar();
		int valVertical = sbVertical.getValue();
		JScrollBar sbHorizontal = scroller.getHorizontalScrollBar();
		int valHorizontal = sbHorizontal.getValue();
		// int dy = 0;
		// int dx = 0;
		int d = 4;
		if (dsdePoint.y < pScreen.y) {
			// scroll up
			// dy = scr.y - p.y;
			sbVertical.setValue(valVertical - d);
		} else if (dsdePoint.y > pScreen.y) {
			// scroll down
			// dy = p.y - scr.y - scr.height;
			sbVertical.setValue(valVertical + d);
		}

		if (dsdePoint.x < pScreen.x) {
			// scroll left
			// dx = scr.x - p.x;
			sbHorizontal.setValue(valHorizontal - d);
		} else if (dsdePoint.x > pScreen.x) {
			// scroll right
			// dx = p.x - scr.x - scr.width;
			sbHorizontal.setValue(valHorizontal + d);
		}
	}

	@Override
	public void setRow(int row) {
	}

	@Override
	public void setCol(int col) {
		setCols(col);
		refresh();
	}

	@Override
	protected void initOptionsPanel(JPanel optionsPanel) {
		IconButton btSetCols = new IconButton("icon.small.columns",
				getSetColsAction());
		btSetCols.setSize32x20();
		btSetCols.setToolTipText(I18N
				.getMsg("msg.statusbar.set.displayed.cols"));
		
		IconButton btSetTextLength = new IconButton("icon.small.size",
				getSetTextLengthAction());
		btSetTextLength.setSize32x20();
		
		optionsPanel.add(btSetTextLength);
		optionsPanel.add(btSetCols);
	};
}
