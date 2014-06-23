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

package ch.intertec.storybook.view.content.chrono.cell;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.action.ActionManager.SbAction;
import ch.intertec.storybook.action.ActionRegistry;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.SceneLinkLocation;
import ch.intertec.storybook.model.SceneLinkLocationPeer;
import ch.intertec.storybook.model.SceneLinkSbCharacter;
import ch.intertec.storybook.model.SceneLinkSbCharacterPeer;
import ch.intertec.storybook.model.SceneLinkStrandPeer;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextArea;
import ch.intertec.storybook.view.IRefreshable;
import ch.intertec.storybook.view.LinkComponentFactory;
import ch.intertec.storybook.view.content.AbstractScenePanel;

@SuppressWarnings("serial")
public class ChronoScenePanel extends AbstractScenePanel implements
		MouseListener, FocusListener, IRefreshable, PropertyChangeListener {

	private static final String COMP_NAME = "chrono_scene_panel";
	public static final String COMP_NAME_TA_TITLE = "ta:title";
	public static final String COMP_NAME_TA_SUMMARY = "ta:summary";
	public static final String COMP_NAME_BT_NEW = "bt:new";
	public static final String COMP_NAME_BT_REMOVE = "bt:remove";
	public static final String COMP_NAME_BT_EDIT = "bt:edit";
	
	private Color color;
	private JPanel upperPanel;
	private JScrollPane locationLinksScroller;
	private JPanel characterLinksPanel;

	public ChronoScenePanel(Scene scene, Color color) {
		super(scene, MainFrame.getInstance().showBgGradient(),
				Color.white, color);
		this.scene = scene;
		this.color = color;
		initGUI();
		
		// listeners
		addMouseListener(this);
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.addPropertyChangeListener(this);
	}

	@Override
	protected void init() {
	}

	@Override
	protected void initGUI() {
		try {
			int size = MainFrame.getInstance().getContentPanelType()
			.getCalculatedScale();
			boolean showSummary = true;
			if (size < 180) {
				showSummary = false;
			}

			MigLayout layout = new MigLayout(
					"fill,flowy,insets 4",
					"[]",
					"[][grow]");
			setLayout(layout);
			setPreferredSize(new Dimension(size, size));
			setName(createComponentName(scene));
			// doesn't work...
			// setFocusable(true);
			// addFocusListener(this);

			removeAll();
			
			try {
				// set dot border for scenes which belong to
				// another part then the active one
				int partId = getScene().getChapter().getPart().getId();
				if (MainFrame.getInstance().getActivePartId() == partId) {
					setBorder(SwingTools.getBorderDefault());
				} else {
					setBorder(SwingTools.getBorderDot());
				}
			} catch (NullPointerException e) {
				setBorder(SwingTools.getBorderDefault());
			}

			// strand links
			JPanel strandLinksPanel = LinkComponentFactory
					.createStrandLinksPanel(getScene(), true);

			// character links
			characterLinksPanel = LinkComponentFactory
					.createCharacterLinksPanel(getScene());

			// location links
			locationLinksScroller = LinkComponentFactory
					.createLocationLinksScroller(scene);
			
			// button new
			btNew = getNewButton();
			btNew.setSize20x20();
			btNew.setName(COMP_NAME_BT_NEW);
			
			// button remove
			btDelete = getDeleteButton();
			btDelete.setSize20x20();
			btDelete.setName(COMP_NAME_BT_REMOVE);

			// button edit
			btEdit = getEditButton();
			btEdit.setSize20x20();
			btEdit.setName(COMP_NAME_BT_EDIT);

			// chapter and scene number
			JLabel lbSceneNo = new JLabel("", SwingConstants.CENTER);
			lbSceneNo.setName("chapter_scene_no");
			lbSceneNo.setText(scene.getChapterAndSceneNumber());
			if (scene.getChapter() != null) {
				StringBuffer buf = new StringBuffer("<html>");
				buf.append(I18N.getMsgColon("msg.common.chapter"));
				buf.append(" " + scene.getChapter().getLabelText());
				buf.append("<br>");
				if (scene.getChapter().getPart() != null) {
					buf.append(I18N.getMsgColon("msg.common.part"));
					buf.append(" "
							+ scene.getChapter().getPart().getLabelText());
					buf.append("<br>");
				}
				lbSceneNo.setToolTipText(buf.toString());
			}
			lbSceneNo.setOpaque(true);
			lbSceneNo.setBackground(Color.white);

			// title
			UndoableTextArea taTitle = new UndoableTextArea();
			taTitle.setName(COMP_NAME_TA_TITLE);
			taTitle.setText(scene.getTitle());
			taTitle.setLineWrap(true);
			taTitle.setWrapStyleWord(true);
			taTitle.setDragEnabled(true);
			taTitle.setCaretPosition(0);
			taTitle.getUndoManager().discardAllEdits();
			taTitle.addFocusListener(this);
			taTitle.addMouseListener(this);
			SwingTools.addCtrlEnterAction(taTitle, getEditActionForHotkey());
			JScrollPane spTitle = new JScrollPane(taTitle);
			spTitle.setPreferredSize(new Dimension(50, 35));
			
			// summary
			UndoableTextArea taSummary = new UndoableTextArea();
			taSummary.setName(COMP_NAME_TA_SUMMARY);
			taSummary.setText(scene.getText());
			taSummary.setLineWrap(true);
			taSummary.setWrapStyleWord(true);
			taSummary.setDragEnabled(true);
			taSummary.setCaretPosition(0);
			taSummary.getUndoManager().discardAllEdits();
			taSummary.addFocusListener(this);
			taSummary.addMouseListener(this);
			SwingTools.addCtrlEnterAction(taSummary, getEditActionForHotkey());
			JScrollPane spSummary = new JScrollPane(taSummary);
			spSummary.setVerticalScrollBarPolicy(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			spSummary.setPreferredSize(new Dimension(Integer.MAX_VALUE,
					Integer.MAX_VALUE));
						
			// layout
			
			// button panel
			JPanel buttonPanel = new JPanel(new MigLayout("flowy,insets 0"));
			buttonPanel.setName("buttonpanel");
			buttonPanel.setOpaque(false);
			buttonPanel.add(btEdit);
			buttonPanel.add(btDelete);
			buttonPanel.add(btNew);

			upperPanel = new JPanel(new MigLayout(
					"insets 0",
					"[][grow][]",
					"[top][top][top]"
			));
			upperPanel.setName("upperpanel");
			upperPanel.setOpaque(false);
			upperPanel.add(lbSceneNo, "grow,width pref+10px,split 2");
			upperPanel.add(scene.getStatusLabel());
			upperPanel.add(strandLinksPanel, "grow");
			upperPanel.add(buttonPanel, "spany 3,wrap");
			JScrollPane scroller = new JScrollPane(
					characterLinksPanel,
					JScrollPane.VERTICAL_SCROLLBAR_NEVER,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
			);
			scroller.setName("characterlinksscroller");
			scroller.setMinimumSize(new Dimension(20, 16));
			scroller.setOpaque(false);
			scroller.getViewport().setOpaque(false);
			scroller.setBorder(null);
			upperPanel.add(scroller, "spanx 2,growx,wrap");
			if (locationLinksScroller != null) {
				upperPanel.add(locationLinksScroller, "spanx 2,grow,wrap");
			}
			
			// main panel
			add(upperPanel, "growx");
			if (showSummary) {
				add(spTitle, "growx, h 35!");
				add(spSummary, "grow");
			} else {
				add(spTitle, "grow");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JPopupMenu createPopupMenu(JComponent comp) {
		ActionRegistry ar = ActionRegistry.getInstance();
		JPopupMenu menu = new JPopupMenu();

		if (comp instanceof JTextArea) {
			SwingTools.addCopyPasteToPopupMenu(menu, comp);
			menu.add(new JSeparator());
		}
		
		Scene sc = getScene();
		AbstractAction action = ar.getAction(SbAction.SHOW_IN_BOOK_VIEW);
		action.putValue(Constants.ActionKey.CHAPTER_OR_SCENE.toString(), sc);
		menu.add(action);
		action = ar.getAction(SbAction.SHOW_IN_MANAGE_VIEW);
		action.putValue(Constants.ActionKey.CHAPTER_OR_SCENE.toString(), sc);
		menu.add(action);
		action = ar.getAction(SbAction.SHOW_IN_MEMORIA);
		action.putValue(Constants.ActionKey.MEMORIA_DBOBJ.toString(), sc);
		menu.add(action);
		
		return menu;
	}
	
	public static String createComponentName(Scene scene) {
		return COMP_NAME + "_" + scene.getId();
	}
	
	protected ChronoScenePanel getThis() {
		return this;
	}
	
	@Override
	public void focusGained(FocusEvent evt) {
		// nothing to do
	}

	@Override
	public void focusLost(FocusEvent evt) {
		JComponent opComp = null;
		try {
			opComp = (JComponent) evt.getOppositeComponent();
		} catch (ClassCastException e) {
			// ignore
			return;
		}
		if (opComp == null || opComp instanceof JRootPane) {
			// do nothing if source is a popup menu
			return;
		}
		
		try {
			JComponent comp = (JComponent) evt.getSource();
			if (comp instanceof JTextArea) {
				JTextArea ta = (JTextArea) comp;
				if (COMP_NAME_TA_TITLE.equals(ta.getName())) {
					scene.setTitle(ta.getText());
				} else if (COMP_NAME_TA_SUMMARY.equals(ta.getName())) {
					scene.setText(ta.getText());
				}
				scene.save();
				ta.setCaretPosition(0);
				Scene old = ScenePeer.doSelectById(scene.getId());
				PCSDispatcher.getInstance().firePropertyChange(
						Property.SCENE_TITLE_SUMMARY, old, scene);
				ta.setBackground(Color.white);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Scene getScene(){
		return this.scene;
	}	

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (PCSDispatcher.isPropertyFired(Property.CHARACTER, evt)) {
			pcCharacter(evt);
			return;
		}

		if (PCSDispatcher.isPropertyFired(Property.LOCATION, evt)) {
			pcLocation(evt);
			return;
		}

		if (PCSDispatcher.isPropertyFired(Property.STRAND, evt)) {
			pcStrand(evt);
			return;
		}
		
		if (PCSDispatcher.isPropertyFired(Property.CHAPTER, evt)) {
			pcChapter(evt);
			return;
		}
	}

	private void pcChapter(PropertyChangeEvent evt) {
		if (PCSDispatcher.isPropertyEdited(evt)) {
			Chapter newChapter = (Chapter) evt.getNewValue();
			if (getScene().getChapterId() == newChapter.getId()) {
				refresh();
				return;
			}
		}
		if (PCSDispatcher.isPropertyRemoved(evt)) {
			Chapter oldChapter = (Chapter) evt.getOldValue();
			if (getScene().getChapterId() == oldChapter.getId()) {
				refresh();
				return;
			}
		}
	}

	private void pcStrand(PropertyChangeEvent evt) {
		Strand strand = (Strand) evt.getNewValue();
		if (strand == null) {
			// strand has been deleted
			return;
		}
		if (scene.getStrand().getId() == strand.getId()) {
			setColor(strand.getColor());
			setEndBgColor(getColor());
			refresh();
			repaint();
		} else if(SceneLinkStrandPeer.hasLinks(scene, strand)){
			refresh();
			repaint();				
		}
	}

	@SuppressWarnings("unchecked")
	private void pcLocation(PropertyChangeEvent evt) {
		if (locationLinksScroller == null) {
			// no links, no refresh needed
			return;
		}
		Location location = (Location) evt.getNewValue();
		if (location == null) {
			// location has been deleted
			location = (Location) evt.getOldValue();
			Object o = locationLinksScroller.getClientProperty(
					LinkComponentFactory.PROPERTY_LOCATION_LINK_LIST);
			if (o != null && o instanceof List) {
				// why give this a unchecked cast warning?
				List<SceneLinkLocation> list = (List<SceneLinkLocation>) o;
				for (SceneLinkLocation link : list) {
					if (link.getLocation() == null) {
						refresh();
						return;
					}
				}
			}
		}
		if (SceneLinkLocationPeer.hasLinks(scene, location)) {
			refresh();
		}
	}

	@SuppressWarnings("unchecked")
	private void pcCharacter(PropertyChangeEvent evt) {
		SbCharacter character = (SbCharacter) evt.getNewValue();
		if (character == null) {
			// character has been deleted
			character = (SbCharacter) evt.getOldValue();
			Object o = characterLinksPanel.getClientProperty(
					LinkComponentFactory.PROPERTY_CHARACTER_LINK_LIST);
			if (o != null && o instanceof List) {
				List<SceneLinkSbCharacter> list = (List<SceneLinkSbCharacter>) o;
				for (SceneLinkSbCharacter link : list) {
					if (link.getCharacter() == null) {
						refresh();
						return;
					}
				}
			}			
		}
		if (SceneLinkSbCharacterPeer.hasLinks(scene, character)) {
			refresh();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JComponent comp = (JComponent) e.getSource();
		// requestFocusInWindow();
		if (SwingUtilities.isRightMouseButton(e)) {
			Point p = SwingUtilities.convertPoint(comp, e.getPoint(), this);
			JPopupMenu menu = createPopupMenu(comp);
			menu.show(this, p.x, p.y);
		}
		AbstractAction a = ActionRegistry.getInstance().getAction(
				SbAction.MENU_REFRESH_EDIT);
		a.actionPerformed(null);
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
}
