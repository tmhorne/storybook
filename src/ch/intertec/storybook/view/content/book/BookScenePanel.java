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

package ch.intertec.storybook.view.content.book;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.action.ActionManager.SbAction;
import ch.intertec.storybook.action.ActionRegistry;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.label.CleverLabel;
import ch.intertec.storybook.toolkit.swing.label.StrandDateLabel;
import ch.intertec.storybook.toolkit.swing.undo.UndoableTextArea;
import ch.intertec.storybook.view.LinkComponentFactory;
import ch.intertec.storybook.view.content.AbstractScenePanel;

@SuppressWarnings("serial")
public class BookScenePanel extends AbstractScenePanel implements
		FocusListener, MouseListener {

	private final static String CN_TITLE = "title";
	private final static String CN_SUMMARY = "summary";

	private JPanel cmdPanel;
	private JPanel textPanel;
	
	public BookScenePanel(Scene scene) {
		super(scene);
		addMouseListener(this);
	}
	
	@Override
	protected void init() {
	}

	@Override
	protected void initGUI() {
		MainFrame mainFrame = MainFrame.getInstance();
		int width = mainFrame.getContentPanelType().getCalculatedScale();
		int scale = mainFrame.getContentPanelType().getScale();
		MigLayout layout = new MigLayout(
				"wrap 3,fill",
				"[]",
				"[top]"
		);
		setLayout(layout);
		setOpaque(false);
		
		removeAll();
		
		// info panel
		Strand strand = StrandPeer.doSelectById(scene.getStrandId());
		JPanel infoPanel = createInfoPanel(strand);

		// text panel
		textPanel = createTextPanel(scale, width);

		// command panel
		cmdPanel = createCommandPanel();

		// layout
		add(infoPanel, "w 300");
		add(textPanel, "grow,gap 10");
		add(cmdPanel);
	}
	
	private JPanel createTextPanel(int scale, int width) {
		MigLayout layout = new MigLayout(
				"wrap,fill",
				"[]",
				"[]"
				);
		JPanel panel = new JPanel(layout);
		panel.setBorder(SwingTools.getBorderDefault());
		
		// scene number
		JLabel lbSceneNo = new JLabel(scene.getChapterAndSceneNumber());
		lbSceneNo.setFont(SwingTools.getFontBold(12));
		JLabel lbStatus = scene.getStatusLabel();
		JLabel lbStatusText = new JLabel(scene.getStatusStr());
		
		// title
		UndoableTextArea taTitle = new UndoableTextArea();
		taTitle.setName(CN_TITLE);
		taTitle.setLineWrap(true);
		taTitle.setWrapStyleWord(true);
		taTitle.setText(scene.getTitle());
		taTitle.setCaretPosition(0);
		taTitle.setDragEnabled(true);
		taTitle.getUndoManager().discardAllEdits();
		JScrollPane titleScroller = new JScrollPane(taTitle);
		taTitle.addFocusListener(this);
		taTitle.addMouseListener(this);
		SwingTools.addCtrlEnterAction(taTitle, getEditActionForHotkey());

		// summary
		UndoableTextArea taSummary = new UndoableTextArea();
		taSummary.setName(CN_SUMMARY);
		taSummary.setLineWrap(true);
		taSummary.setWrapStyleWord(true);
		taSummary.setText(scene.getText());
		taSummary.setCaretPosition(0);
		taSummary.setDragEnabled(true);
		taSummary.getUndoManager().discardAllEdits();
		JScrollPane summaryScroller = new JScrollPane(taSummary);
		summaryScroller.setMinimumSize(new Dimension(width, 100 + scale * 10));
		summaryScroller.setMaximumSize(new Dimension(width, 300 + scale * 20));
		summaryScroller.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		taSummary.addFocusListener(this);
		taSummary.addMouseListener(this);
		SwingTools.addCtrlEnterAction(taSummary, getEditActionForHotkey());

		panel.add(lbSceneNo, "split 3,grow");
		panel.add(lbStatusText, "gap right");
		panel.add(lbStatus);
		panel.add(titleScroller, "grow");
		panel.add(summaryScroller, "grow");
		
		return panel;
	}
	
	private JPanel createInfoPanel(Strand strand){
		LayoutManager layout = new MigLayout(
				"flowy",
				"grow",
				"[]10");
		JPanel panel = new JPanel(layout);
		panel.setOpaque(false);
		panel.setBorder(SwingTools.getBorderDefault());

		// date
		StrandDateLabel lbDate = new StrandDateLabel(strand, scene.getDate());
		lbDate.setOpaque(false);
		
		// strand
		CleverLabel lbStrand = new CleverLabel(strand.toString(), JLabel.CENTER);
		lbStrand.setBackground(strand.getColor());

		// strand links
		JPanel strandLinksPanel = LinkComponentFactory.createStrandLinksPanel(
				scene, false);
		strandLinksPanel.setOpaque(false);

		// character links
		JPanel characterLinksPanel = LinkComponentFactory
				.createCharacterLinksPanel(scene);

		// location links
		JScrollPane locationLinksScroller = LinkComponentFactory
				.createLocationLinksScroller(scene, false);
		
		// layout
		panel.add(lbStrand, "grow");
		panel.add(lbDate);
		panel.add(strandLinksPanel);
		Icon characterIcon = (ImageIcon) I18N.getIcon("icon.small.character");
		panel.add(new JLabel(characterIcon), "aligny top,flowx,split 2");
		panel.add(characterLinksPanel);
		if (locationLinksScroller != null) {
			Icon icon = (ImageIcon) I18N.getIcon("icon.small.location");
			panel.add(new JLabel(icon), "aligny top,flowx,split 2");
			panel.add(locationLinksScroller, "grow");
		}
		return panel;
	}	
	
	private JPanel createCommandPanel() {
		JPanel panel = new JPanel(new MigLayout("flowy,insets 0"));
		panel.setOpaque(false);

		// layout
		panel.add(getEditButton());
		panel.add(getDeleteButton());
		panel.add(getNewButton());

		return panel;
	}
	
	private JPopupMenu createPopupMenu(JComponent comp) {
		ActionRegistry ar = ActionRegistry.getInstance();
		JPopupMenu menu = new JPopupMenu();
		
		if (comp instanceof JTextArea) {
			SwingTools.addCopyPasteToPopupMenu(menu, comp);
			menu.add(new JSeparator());
		}

		Scene sc = getScene();
		AbstractAction action = ar.getAction(SbAction.SHOW_IN_CHRONO_VIEW);
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
	
	protected BookScenePanel getThis() {
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
			// save scene
			Component comp = (Component) evt.getSource();
			if (comp instanceof JTextArea) {
				JTextArea ta = (JTextArea) comp;
				if (CN_TITLE.equals(ta.getName())) {
					scene.setTitle(ta.getText());
				} else if (CN_SUMMARY.equals(ta.getName())) {
					scene.setText(ta.getText());
				}
				try {
					scene.save();
					ta.setCaretPosition(0);
					Scene old = ScenePeer.doSelectById(scene.getId());
					PCSDispatcher.getInstance().firePropertyChange(
							Property.SCENE_TITLE_SUMMARY, old, scene);
				} catch (SQLException e) {
					SwingTools.showException(e);
					ta.setBackground(SwingTools.getNiceRed());
					return;
				}
				ta.setBackground(Color.white);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public Scene getScene() {
		return scene;
	}

	@Override
	public void mouseClicked(MouseEvent evt) {
		AbstractAction refEditMenuAction = ActionRegistry.getInstance()
				.getAction(SbAction.MENU_REFRESH_EDIT);
		refEditMenuAction.actionPerformed(null);

		// requestFocusInWindow();
		if (SwingUtilities.isRightMouseButton(evt)) {
			JComponent comp = (JComponent) evt.getSource();
			Point p = SwingUtilities.convertPoint(comp, evt.getPoint(), this);
			JPopupMenu menu = createPopupMenu(comp);
			menu.show(this, p.x, p.y);
		}
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
