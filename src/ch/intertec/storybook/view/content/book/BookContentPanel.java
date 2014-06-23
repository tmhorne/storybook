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
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

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
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.HtmlTools;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IconButton;
import ch.intertec.storybook.view.content.AbstractContentPanel;
import ch.intertec.storybook.view.content.AbstractScenePanel;
import ch.intertec.storybook.view.content.chrono.cell.SpacePanel;

@SuppressWarnings("serial")
public class BookContentPanel extends AbstractContentPanel implements
		PropertyChangeListener, HyperlinkListener, KeyListener, ItemListener {

	private boolean readingView;
	private JTextPane tpText;
	private JScrollPane tpScroller;
	private HashSet<Integer> strandIds;
	
	public BookContentPanel(){
		super();
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.addPropertyChangeListener(Property.SCENE, this);
		pcs.addPropertyChangeListener(Property.CHAPTER, this);
	}
	
	private void addAllStrands(){
		List<Strand> list = StrandPeer.doSelectAll();
		for (Strand strand : list) {
			strandIds.add(strand.getId());
		}
	}
	
	@Override
	protected void init() {
		readingView = getSavedReadingView();
		strandIds = new HashSet<Integer>();
		addAllStrands();
	}
	
	@Override
	public void initGUI() {
		MigLayout layout  = new MigLayout(
				"flowy",
				"[grow,center]",
				""
		);
		setLayout(layout);
		if (readingView) {
			setReadingLayout();
		} else {
			setEditingLayout();
		}
		setComponentPopupMenu(createPopupMenu());
	}

	public void setEditingLayout() {
		readingView = false;
		saveReadingView();
		setBackground(SwingTools.getBackgroundColor());
		refreshPanel();
	}

	public void setReadingLayout() {
		readingView = true;
		saveReadingView();
		setBackground(SwingTools.getNiceGray());
		refreshPanel();
	}
	
	public boolean getReadingLayout() {
		return readingView;
	}
	
	private void refreshPanel() {
		// remove listeners
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.removeListenersByClass(BookScenePanel.class);
		pcs.removeListenersByClass(SpacePanel.class);
		
		if (readingView) {
			refreshPanelReading();
			return;
		}
		refreshPanelEditing();
	}
	
	private void refreshPanelEditing() {
        JScrollPane scroller = MainFrame.getInstance().getScroller();
        // set scroll unit increments
        scroller.getVerticalScrollBar().setUnitIncrement(20);
        scroller.getHorizontalScrollBar().setUnitIncrement(20);
        // save values
		int vsb = scroller.getVerticalScrollBar().getValue();
		int hsb = scroller.getHorizontalScrollBar().getValue();

		removeAll();
		
		List<Chapter> chapterList = ChapterPeer.doSelectAll(true);
		for (Chapter chapter : chapterList) {
			List<Scene> sceneList = ScenePeer.doSelectByChapterId(chapter
					.getId());

			for (Scene scene : sceneList) {
				BookScenePanel panel = new BookScenePanel(scene);
				add(panel, "sgx");
			}
			// space after each chapter
			JPanel panel = new JPanel();
			panel.setOpaque(false);
			panel.setPreferredSize(new Dimension(10, 15));
			add(panel);
		}

		// space panel shows an "add scene" button
		SpacePanel spacePanel = new SpacePanel();
		add(spacePanel, "sgx");
		
		validate();
		repaint();
		scroller.setViewportView(this);
		scroller.getVerticalScrollBar().setValue(vsb);
		scroller.getHorizontalScrollBar().setValue(hsb);
	}
	
	private void refreshPanelReading() {
		MainFrame mainFrame = MainFrame.getInstance();
		int scale = mainFrame.getContentPanelType().getScale();

		JScrollPane scroller = MainFrame.getInstance().getScroller();
        
		removeAll();

		// strand panel
		add(createStrandPanel(), "wrap,aligny top");
		
		// text
		tpText = new JTextPane();
		if (!Constants.Application.IS_PRO_VERSION.toBoolean()) {
			tpText.setHighlighter(null);
		}
		tpText.setContentType("text/html");
		tpText.setEditable(false);
		tpText.addKeyListener(this);
		
		StringBuffer buf = new StringBuffer(HtmlTools.getHeadWithCSS());
		buf.append("<body>\n");

		// title
		buf.append("<h1>" + ProjectTools.getProjectName() + "</h1>\n");

		// part
		if (PartPeer.doCount() > 1) {
			buf.append("<p style='font-weight:bold;text-align:center;'>");
			buf.append(I18N.getMsg("msg.common.part"));
			Part part = PartPeer.doSelectById(MainFrame.getInstance()
					.getActivePartId());
			buf.append(" "+part + "</p>\n");
			buf.append("<p style=''/>");
		}
		
		// table of contents
		buf.append("<p style='font-weight:bold'>");
		buf.append("<a name='toc'>" + I18N.getMsg("msg.table.of.contents")
				+ "</a></p>\n");
		List<Chapter> chapterList = ChapterPeer.doSelectAll(true);
		for (Chapter chapter : chapterList) {
			String no = chapter.getChapterNoStr();
			buf.append("<p><a href='#" + no + "'>");
			buf.append(no + ": " + chapter.getTitle() + "</a>");
			String descr = chapter.getDescription();
			if (descr != null) {
				if (!descr.isEmpty()) {
					buf.append(": " + chapter.getDescription());
				}
			}
			buf.append("</p>\n");
		}
		
		for (Chapter chapter : chapterList) {
			List<Scene> sceneList = ScenePeer.doSelectByChapterId(chapter
					.getId());
			buf.append("<h2><a name='" + chapter.getChapterNoStr() + "'>");
			String no = chapter.getChapterNoStr();
			buf.append(no + ": " + chapter.getTitle());
			buf.append("</a></h2>\n");
			for (Scene scene : sceneList) {
				int strandId = scene.getStrandId();
				if (!strandIds.contains(strandId)) {
					continue;
				}
				buf.append("<p>");
				buf.append("<span style='background-color:#");
				Strand strand = scene.getStrand();
				String clr = strand.getHTMLColor();
				buf.append(clr+";'>&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;");
				buf.append("<span><b>" + scene.getTitle() + "</b> \n");
				buf.append(scene.getText());
				buf.append("</span>\n");
				buf.append("</p>");
			}
			buf.append("<p style='font-size:8px;text-align:left;'><a href='#toc'>"
					+ I18N.getMsg("msg.table.of.contents") + "</a></p>");
		}
		buf.append("<p>&nbsp;</body></html>\n");
		tpText.setText(buf.toString());
		tpText.setCaretPosition(0);
		
		tpScroller = new JScrollPane(tpText);
		// Dimension dim = new Dimension(300 + scale * 50,
		// scroller.getHeight() - 20);
		Dimension dim = new Dimension(300 + scale * 50,
				mainFrame.getHeight() - 170);
		tpScroller.setPreferredSize(dim);
		tpScroller.getVerticalScrollBar().setUnitIncrement(10);
		tpScroller.getHorizontalScrollBar().setUnitIncrement(10);
		add(tpScroller);
		
		validate();
		repaint();
		scroller.setViewportView(this);
		
		tpText.addHyperlinkListener(this);
	}
	
	private JPanel createStrandPanel() {
		JPanel panel = new JPanel(new MigLayout("flowy"));
		panel.setBackground(Color.white);
		panel.setBorder(SwingTools.getBorderDefault());
		
		List<Strand> list = StrandPeer.doSelectAll();
		for (Strand strand : list) {
			JCheckBox cb = new JCheckBox(strand.getName());
			int id = strand.getId();
			if (strandIds.contains(id)) {
				cb.setSelected(true);
			}
			cb.setName(Integer.toString(id));
			cb.setBackground(strand.getColor());
			cb.addItemListener(this);
			panel.add(cb, "grow");
		}
		
		JButton btAll = new JButton(getSelectAllAction());
		btAll.setText(I18N.getMsg("msg.tree.show.all"));
		btAll.setName("all");
		btAll.setOpaque(false);
		panel.add(btAll, "sg,gapy 20");

		JButton cbNone = new JButton(getSelectNoneAction());
		cbNone.setText(I18N.getMsg("msg.tree.show.none"));
		cbNone.setName("none");
		cbNone.setOpaque(false);
		panel.add(cbNone, "sg");

		return panel;
	}
	
	private AbstractAction getSelectAllAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addAllStrands();
				refreshPanel();
			}
		};
	}

	private AbstractAction getSelectNoneAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				strandIds.clear();
				refreshPanel();
			}
		};
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
	
	private boolean getSavedReadingView() {
		return InternalPeer.getReadingView();
	}

	private void saveReadingView() {
		InternalPeer.saveReadingView(readingView);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();

		if (PCSDispatcher.isPropertyFired(Property.SCENE, evt)) {
			if (oldValue != null && newValue != null) {
				// scene edited
				Scene editedScene = (Scene) newValue;
				refreshScene(editedScene);
				return;
			}
			
			if (oldValue == null && newValue != null) {
				// new scene
				refreshPanel();
				return;
			}
			
			if (oldValue != null && newValue == null) {
				// scene deleted
				Scene deletedScene = (Scene) oldValue;
				List<Component> list = findScenePanels();
				for (Component comp : list) {
					AbstractScenePanel scenePanel = (AbstractScenePanel) comp;
					if (deletedScene.getId() == scenePanel.getScene().getId()) {
						remove(scenePanel);
						validate();
						return;
					}
				}
			}
		}
		
		if (PCSDispatcher.isPropertyFired(Property.CHAPTER, evt)) {
			refreshPanel();
			return;
		}

		// everything else is handled by super class
		super.propertyChange(evt);
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent evt) {
		if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				if (!evt.getDescription().isEmpty()) {
					// anchor
					tpText.scrollToReference(evt.getDescription().substring(1));
				} else {
					// external links
					tpText.setPage(evt.getURL());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource() == tpText) {
			int value = tpScroller.getVerticalScrollBar().getValue();
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				value += 20;
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				value -= 20;
			}
			tpScroller.getVerticalScrollBar().setValue(value);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	private Action getReadingLayoutAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getReadingLayout()) {
					setEditingLayout();
				} else {
					setReadingLayout();
				}
			}
		};
	}

	@Override
	protected void initOptionsPanel(JPanel optionsPanel) {
		IconButton bt = new IconButton(getReadingLayoutAction());
		bt.setSize32x20();
		bt.setIcon("icon.small.reading");
		bt.setToolTipText(I18N.getMsg("msg.statusbar.set.reading.layout"));
		optionsPanel.add(bt);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBox cb = (JCheckBox)e.getSource();
		Integer id = new Integer(cb.getName());
		if(cb.isSelected()){
			strandIds.add(id);
		}else{
			strandIds.remove(id);
		}
		refreshPanel();
	}
}
