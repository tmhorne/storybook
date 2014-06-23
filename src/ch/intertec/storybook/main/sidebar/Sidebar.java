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

package ch.intertec.storybook.main.sidebar;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import ch.intertec.storybook.action.AbstractTableAction;
import ch.intertec.storybook.action.AbstractTableAction.ActionKey;
import ch.intertec.storybook.action.ActionManager.SbAction;
import ch.intertec.storybook.action.ActionRegistry;
import ch.intertec.storybook.action.GoogleMapsAction;
import ch.intertec.storybook.action.TableCopyAction;
import ch.intertec.storybook.action.TableDeleteAction;
import ch.intertec.storybook.action.TableEditAction;
import ch.intertec.storybook.action.TableNewAction;
import ch.intertec.storybook.action.ViewPartAction;
import ch.intertec.storybook.main.InfoPanel;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.DbPeer;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PersistenceManager;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.thin.ThinDbTable;
import ch.intertec.storybook.model.thin.ThinLocation;
import ch.intertec.storybook.model.thin.ThinScene;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;
import ch.intertec.storybook.view.IconButton;
import ch.intertec.storybook.view.net.BrowserPanel;

@SuppressWarnings("serial")
public class Sidebar extends JSplitPane implements ActionListener,
		MouseListener, TreeSelectionListener, PropertyChangeListener,
		IRefreshable {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(Sidebar.class);

	private static final String CMD_COPY = "copy";
	private static final String CMD_DELETE = "delete";
	
	public static final int PREFERRED_WIDTH = 350;

	private static Sidebar theInstance;

	private InfoPanel infoPanel;
	private JTree tree;
	private JScrollPane scroller;
	private BrowserPanel browser;

	private IconButton btToogleCharacters;
	private IconButton btToogleLocations;
	private IconButton btToogleTags;
	private IconButton btToogleItems;
	private IconButton btToogleChapters;
	private IconButton btToogleStrands;
	private IconButton btToogleParts;

	private List<IconButton> toggleButtonList;

	private Sidebar() {
		super(JSplitPane.VERTICAL_SPLIT);
		setResizeWeight(0.6);
		theInstance = null;

		toggleButtonList = new ArrayList<IconButton>();

		btToogleCharacters = new IconButton("icon.small.character",
				"msg.tree.show.characters", getToggleCharactersAction());
		toggleButtonList.add(btToogleCharacters);

		btToogleLocations = new IconButton("icon.small.location",
				"msg.tree.show.locations", getToggleLocationsAction());
		toggleButtonList.add(btToogleLocations);

		btToogleTags = new IconButton("icon.small.tag",
				"msg.tree.show.tags", getToggleTagsAction());
		toggleButtonList.add(btToogleTags);

		btToogleItems = new IconButton("icon.small.item",
				"msg.tree.show.items", getToggleItemsAction());
		toggleButtonList.add(btToogleItems);

		btToogleChapters = new IconButton("icon.small.chapter",
				"msg.tree.show.chapters", getToggleChaptersAction());
		toggleButtonList.add(btToogleChapters);

		btToogleStrands = new IconButton("icon.small.strand",
				"msg.tree.show.strands", getToggleStrandsAction());
		toggleButtonList.add(btToogleStrands);

		btToogleParts = new IconButton("icon.small.part",
				"msg.tree.show.parts", getTogglePartsAction());
		toggleButtonList.add(btToogleParts);

		// select all buttons and add a right-click handler
		for (IconButton button : toggleButtonList) {
			button.setSelected(true);
			button.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						toggle((IconButton) e.getSource());
					}
				}
			});
		}

		// listeners
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.addPropertyChangeListener(this);

		initGUI();
	}

	public static Sidebar getInstance() {
		if (theInstance == null) {
			theInstance = new Sidebar();
			theInstance.initGUI();
		}
		return theInstance;
	}

	private void initGUI() {
		if (!PersistenceManager.getInstance().isConnectionOpen()) {
			// return if no connection is open
			return;
		}

		setPreferredSize(new Dimension(PREFERRED_WIDTH, 400));

		// save divider location
		int location = getDividerLocation();

		// top component
		scroller = createTreeScrollPane();
		LayoutManager layout = new MigLayout("wrap,insets 0", "[grow]",
				"[][grow]");
		JPanel panel = new JPanel(layout);
		panel.add(createToolbar(), "growx");
		panel.add(scroller, "grow");
		setTopComponent(panel);

		// bottom component
		infoPanel = new InfoPanel();

		if (!Constants.Application.IS_PRO_VERSION.toBoolean()) {
			JPanel goProPanel = new JPanel(new MigLayout("flowy,fill"));
			goProPanel.add(infoPanel, "grow 70");
			try {
				if (browser == null) {
					String locale = Locale.getDefault().toString();
					URL url = new URL(
							Constants.Application.GOPRO_URL.toString()
									+ "/?locale=" + locale);
					browser = new BrowserPanel(url.toString(), 200, 180);
					browser.setBackground(Color.white);
				}
				goProPanel.add(browser, "grow 30");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			setBottomComponent(goProPanel);
		} else {
			setBottomComponent(infoPanel);
		}

		// restore divider location
		setDividerLocation(location);

		if (!Constants.Application.IS_PRO_VERSION.toBoolean()) {
			setDividerLocation(250);
		}
	}
	
	private JToolBar createToolbar() {
		JToolBar toolbar = new JToolBar();
		LayoutManager layout = new MigLayout(
				"insets 0, gapx 2",
				"[][][][][][][]push[][][][]",
				"");
		toolbar.setLayout(layout);
		toolbar.setFloatable(false);

		// toggle buttons
		for (IconButton button : toggleButtonList) {
			toolbar.add(button);
		}

		// tree control buttons
		IconButton ib = new IconButton("icon.small.all", "msg.tree.show.all",
				getShowAllAction());
		ib.setControlButton();
		toolbar.add(ib, "top");
		ib = new IconButton("icon.small.none", "msg.tree.show.none",
				getShowNoneAction());
		ib.setControlButton();
		toolbar.add(ib, "top");
		ib = new IconButton("icon.small.expand", "msg.tree.expand.all",
				getExpandAction());
		ib.setControlButton();
		toolbar.add(ib, "top");
		ib = new IconButton("icon.small.collapse", "msg.tree.collapse.all",
				getCollapseAction());
		ib.setControlButton();
		toolbar.add(ib, "top");
		return toolbar;
	}

	protected Sidebar getThis() {
		return this;
	}

	private JScrollPane createTreeScrollPane() {
		try {
			// top node
			DefaultMutableTreeNode top = new DefaultMutableTreeNode(
					ProjectTools.getProjectName());

			DbTableNode rootNode;
			DefaultMutableTreeNode node;

			// create character nodes
			if (btToogleCharacters.isSelected()) {
				rootNode = new DbTableNode("msg.common.persons",
						new SbCharacter());
				node = new DefaultMutableTreeNode(rootNode);
				NodeFactory.createCharacterNodes(node);
				top.add(node);
			}

			// create location nodes
			if (btToogleLocations.isSelected()) {
				rootNode = new DbTableNode("msg.common.locations",
						new Location());
				node = new DefaultMutableTreeNode(rootNode);
				NodeFactory.createLocationNodes(node);
				top.add(node);
			}

			// create tag nodes
			if (btToogleTags.isSelected()) {
				rootNode = new DbTableNode("msg.tags", new Tag());
				node = new DefaultMutableTreeNode(rootNode);
				NodeFactory.createTagNodes(node);
				top.add(node);
			}

			// create item nodes
			if (btToogleItems.isSelected()) {
				rootNode = new DbTableNode("msg.items", new Item());
				node = new DefaultMutableTreeNode(rootNode);
				NodeFactory.createItemNodes(node);
				top.add(node);
			}

			// create chapter nodes
			if (btToogleChapters.isSelected()) {
				rootNode = new DbTableNode("msg.common.chapters", new Chapter());
				node = new DefaultMutableTreeNode(rootNode);
				NodeFactory.createChapterNodes(node);
				top.add(node);
			}

			// create strand nodes
			if (btToogleStrands.isSelected()) {
				rootNode = new DbTableNode("msg.common.strands", new Strand());
				node = new DefaultMutableTreeNode(rootNode);
				NodeFactory.createStrandNodes(node);
				top.add(node);
			}

			// create part nodes
			if (btToogleParts.isSelected()) {
				rootNode = new DbTableNode("msg.common.parts", new Part());
				node = new DefaultMutableTreeNode(rootNode);
				NodeFactory.createPartNodes(node);
				top.add(node);
			}

			// create tree
			tree = new JTree(top);
			tree.getSelectionModel().setSelectionMode(
					TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.setCellRenderer(new SidebarTreeCellRenderer());
			KeyStroke copy = SwingTools.getKeyStrokeCopy();
			tree.registerKeyboardAction(this, CMD_COPY, copy,
					JComponent.WHEN_FOCUSED);
			KeyStroke delete = SwingTools.getKeyStrokeDelete();
			tree.registerKeyboardAction(this, CMD_DELETE, delete,
					JComponent.WHEN_FOCUSED);

			// add listeners
			tree.addTreeSelectionListener(this);
			tree.addMouseListener(this);

			JScrollPane scroller = new JScrollPane(tree);
			return scroller;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JScrollPane();
	}

	@Override
	public void refresh() {
		if (tree == null) {
			return;
		}

		// save tree state
		Boolean[] rowExp = new Boolean[tree.getRowCount()];
		for (int i = 0; i < tree.getRowCount(); i++) {
			rowExp[i] = tree.isExpanded(i);
		}
		int selectedRow = -1;
		if (tree.getSelectionCount() > 0) {
			selectedRow = tree.getSelectionRows()[0];
		}
		Point point = scroller.getViewport().getViewPosition();

		// re-build tree
		initGUI();

		// restore tree state
		try {
			for (int i = 0; i < tree.getRowCount(); i++) {
				if (rowExp[i]) {
					tree.expandRow(i);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// ignore
		}
		if (selectedRow > -1) {
			tree.setSelectionRow(selectedRow);
		}
		scroller.getViewport().setViewPosition(point);

		validate();
	}

	@Override
	public void mouseClicked(MouseEvent evt) {

		TreePath selectedPath = tree.getPathForLocation(evt.getX(), evt.getY());
		DefaultMutableTreeNode selectedNode = null;
		try {
			selectedNode = (DefaultMutableTreeNode) selectedPath
					.getLastPathComponent();
		} catch (Exception e) {
			// ignore
		}
		if (selectedNode == null) {
			return;
		}
		tree.setSelectionPath(selectedPath);

		AbstractAction refEditMenuAction = ActionRegistry.getInstance()
				.getAction(SbAction.MENU_REFRESH_EDIT);
		refEditMenuAction.actionPerformed(null);
		
		// handle double click
		if (evt.getClickCount() == 2) {
			if (selectedNode.isLeaf()) {
				Object value = selectedNode.getUserObject();
				if (value instanceof DbTable) {
					if (value instanceof Part) {
						// show part
						Part part = (Part) value;
						AbstractAction action = new ViewPartAction();
						action.putValue(ViewPartAction.ACTION_KEY_PART_ID, part
								.getId());
						action.actionPerformed(null);
					} else {
						// edit
						createEditAction((DbTable) value).actionPerformed(null);
					}
				} else if (value instanceof ThinDbTable) {
					if (value instanceof ThinScene) {
						// goto scene
						Scene scene = ((ThinScene) value).getScene();
						AbstractAction action = ActionRegistry.getInstance()
								.getAction(SbAction.SCROLL_TO_CHAPTER_OR_SCENE);
						action.putValue(Constants.ActionKey.CHAPTER_OR_SCENE
								.toString(), scene);
						action.actionPerformed(null);
					} else {
						// edit
						DbTable dbTable = ((ThinDbTable) value).getDbTable();
						createEditAction(dbTable).actionPerformed(null);
					}
				}
			}
		}		
	}

	private static TableEditAction createEditAction(DbTable dbObj) {
		return new TableEditAction(dbObj);
	}

	private static TableCopyAction createCopyAction(DbTable dbObj) {
		return new TableCopyAction(dbObj);
	}

	private static TableDeleteAction createDeleteAction(DbTable dbObj) {
		return new TableDeleteAction(dbObj);
	}

	private TableNewAction createNewAction(DbTable dbObj) {
		TableNewAction action = new TableNewAction(dbObj);
		action.putValue(AbstractTableAction.ActionKey.CONTAINER.toString(),
				this);

		// pass values to the action
		if (dbObj instanceof SbCharacter) {
			AbstractTableAction.putCategoryToAction(action, dbObj);
		} else if (dbObj instanceof Location) {
			Location location = (Location) dbObj;
			Country country = new Country(location);
			AbstractTableAction.putCountryToAction(action, country);
			City city = new City(location);
			AbstractTableAction.putCityToAction(action, city);
		} else if (dbObj instanceof Chapter) {
			Chapter chapter = (Chapter) dbObj;
			AbstractTableAction.putPartToAction(action, chapter.getPart());
		} else if (dbObj instanceof Scene) {
			Scene scene = (Scene) dbObj;
			AbstractTableAction.putChapterToAction(action, scene.getChapter());
			AbstractTableAction.putDateToAction(action, scene.getDate());
		}
		return action;
	}

	@Override
	public void mouseEntered(MouseEvent evt) {
		// not used
	}

	@Override
	public void mouseExited(MouseEvent evt) {
		// not used
	}

	@Override
	public void mousePressed(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			showPopupMenu(evt);
		}
	}

	@Override
	public void mouseReleased(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			showPopupMenu(evt);
		}
	}

	private void showPopupMenu(MouseEvent evt) {
		TreePath selectedPath = tree.getPathForLocation(evt.getX(), evt.getY());
		DefaultMutableTreeNode selectedNode = null;
		try {
			selectedNode = (DefaultMutableTreeNode) selectedPath
					.getLastPathComponent();
		} catch (Exception e) {
			// ignore
		}
		if (selectedNode == null) {
			return;
		}
		JPopupMenu menu = createPopupMenu(selectedNode.getUserObject());
		if (menu == null) {
			return;
		}
		tree.setSelectionPath(selectedPath);
		JComponent comp = (JComponent) tree.getComponentAt(evt.getPoint());
		Point p = SwingUtilities.convertPoint(comp, evt.getPoint(), this);
		menu.show(this, p.x, p.y);
		evt.consume();
	}

	private JPopupMenu createPopupMenu(Object value) {
		JPopupMenu menu = new JPopupMenu();
		if (value == null) {
			return menu;
		}
		if (value instanceof DbTable || value instanceof ThinDbTable) {
			DbTable dbObj;
			if (value instanceof ThinDbTable) {
				dbObj = ((ThinDbTable) value).getDbTable();
			} else {
				dbObj = (DbTable) value;
			}

			// edit
			menu.add(createEditAction(dbObj));
			if (!(value instanceof Scene || value instanceof ThinScene)) {
				// copy
				menu.add(createCopyAction(dbObj));
			}
			// delete
			menu.add(createDeleteAction(dbObj));

			// new
			menu.add(new JPopupMenu.Separator());
			menu.add(createNewAction(dbObj));
			
			// rename tag / item category
			if (dbObj instanceof Tag) {
				ActionRegistry ar = ActionRegistry.getInstance();
				AbstractAction renameAction = null;
				if (dbObj instanceof Item) {
					renameAction = ar.getAction(SbAction.ITEM_RENAME_CATEGORY);
				} else if (dbObj instanceof Tag) {
					renameAction = ar.getAction(SbAction.TAG_RENAME_CATEGORY);
				}
				renameAction.putValue(ActionKey.CATEGORY.toString(),
						((Tag) dbObj).getCategory());
				menu.add(renameAction);
			}
			
			
			// show chapter in view
			if (dbObj instanceof Chapter) {
				Chapter chapter = (Chapter) dbObj;
				menu.add(new JPopupMenu.Separator());
				addShowInMenu(menu, chapter);
			}
			// show scene in view
			if (dbObj instanceof Scene) {
				Scene scene = (Scene) dbObj;
				menu.add(new JPopupMenu.Separator());
				addShowInMenu(menu, scene);
			}
			
			// show in memoria
			if (dbObj instanceof Scene || dbObj instanceof SbCharacter
					|| dbObj instanceof Location || dbObj instanceof Tag) {
				menu.add(new JPopupMenu.Separator());
				AbstractAction action = ActionRegistry.getInstance().getAction(
						SbAction.SHOW_IN_MEMORIA);
				action.putValue(Constants.ActionKey.MEMORIA_DBOBJ.toString(),
						dbObj);
				menu.add(action);
			}
			
			// Google Maps
			if (value instanceof Location) {
				menu.add(new JPopupMenu.Separator());
				menu.add(new GoogleMapsAction((Location) value));
			}
			if (value instanceof ThinLocation) {
				menu.add(new JPopupMenu.Separator());
				Location location = ((ThinLocation) value).getLocation();
				menu.add(new GoogleMapsAction(location));
			}

			// manage
			addManageMenus(menu, dbObj);
			
			// tag / item assignments
			if (value instanceof Tag) {
				addAssignmentsMenu(menu, dbObj);
			}
		} else if (value instanceof DbTableNode) {
			DbTableNode node = (DbTableNode) value;
			DbTable dbObj = node.getDbTable();
			TableNewAction action = new TableNewAction(dbObj);
			AbstractTableAction.putCategoryToAction(action, dbObj);
			menu.add(action);
			
			// manage objects
			addManageMenus(menu, dbObj);
			
			// tag and item assignments
			if (dbObj instanceof Item || dbObj instanceof Tag) {
				addAssignmentsMenu(menu, dbObj);
			}
		} else if (value instanceof Country) {
			Country country = (Country) value;
			TableNewAction action = new TableNewAction(new Location());
			AbstractTableAction.putCountryToAction(action, country);
			menu.add(action);
			AbstractAction renameAction = ActionRegistry.getInstance()
					.getAction(SbAction.LOCATION_RENAME_COUNTRY);
			renameAction.putValue(ActionKey.COUNTRY.toString(),
					country.getCountry());
			menu.add(renameAction);
		} else if (value instanceof City) {
			City city = (City) value;
			TableNewAction action = new TableNewAction(new Location());
			AbstractTableAction.putCityToAction(action, city);
			AbstractTableAction.putCountryToAction(action, city.getCountry());
			menu.add(action);
			AbstractAction renameAction = ActionRegistry.getInstance()
					.getAction(SbAction.LOCATION_RENAME_CITY);
			renameAction.putValue(ActionKey.CITY.toString(), city.getCity());
			menu.add(renameAction);
		} else if (value instanceof Category) {
			Category cat = (Category) value;
			ActionRegistry ar = ActionRegistry.getInstance();
			AbstractAction renameAction = null;
			if (cat.isTag()) {
				renameAction = ar.getAction(SbAction.TAG_RENAME_CATEGORY);
			} else {
				renameAction = ar.getAction(SbAction.ITEM_RENAME_CATEGORY);
			}
			renameAction.putValue(ActionKey.CATEGORY.toString(),
					cat.getCategory());
			menu.add(renameAction);
		}
		
		if (menu.getComponents().length == 0) {
			return null;
		}
		
		return menu;
	}

	private void addAssignmentsMenu(JPopupMenu menu, DbTable dbObj) {
		ActionRegistry ar = ActionRegistry.getInstance();
		JMenuItem mi = new JMenuItem();
		if (dbObj instanceof Item) {
			mi.setAction(ar.getAction(SbAction.ITEM_ASSIGNMENTS));
			mi.setText(I18N.getMsgDot("msg.item.assignments"));
			mi.setIcon(I18N.getIcon("icon.medium.manage.item_links"));
		} else if (dbObj instanceof Tag) {
			mi.setAction(ar.getAction(SbAction.TAG_ASSIGNMENTS));
			mi.setText(I18N.getMsgDot("msg.tag.assignments"));
			mi.setIcon(I18N.getIcon("icon.medium.manage.tag_links"));
		}
		menu.add(new JPopupMenu.Separator());
		menu.add(mi);
	}
	
	private void addManageMenus(JPopupMenu menu, DbTable dbObj) {
		ActionRegistry ar = ActionRegistry.getInstance();
		JMenuItem mi = new JMenuItem();
		if (dbObj instanceof SbCharacter) {
			mi.setAction(ar.getAction(SbAction.CHARACTER_MANAGE));
			mi.setText(I18N.getMsg("msg.menu.persons.manage"));
			mi.setIcon(I18N.getIcon("icon.medium.manage.persons"));
		} else if (dbObj instanceof Location) {
			mi.setAction(ar.getAction(SbAction.LOCATION_MANAGE));
			mi.setText(I18N.getMsg("msg.menu.locations.manage"));
			mi.setIcon(I18N.getIcon("icon.medium.manage.locations"));
		} else if (dbObj instanceof Item) {
			mi.setAction(ar.getAction(SbAction.ITEM_MANAGE));
			mi.setText(I18N.getMsg("msg.items.manage"));
			mi.setIcon(I18N.getIcon("icon.medium.manage.items"));
		} else if (dbObj instanceof Tag) {
			mi.setAction(ar.getAction(SbAction.TAG_MANAGE));
			mi.setText(I18N.getMsg("msg.tags.manage"));
			mi.setIcon(I18N.getIcon("icon.medium.manage.tags"));
		} else if (dbObj instanceof Chapter || dbObj instanceof Scene) {
			mi.setAction(ar.getAction(SbAction.CHAPTER_MANAGE));
			mi.setText(I18N.getMsg("msg.menu.chapters.manage"));
			mi.setIcon(I18N.getIcon("icon.medium.manage.chapters"));
		} else if (dbObj instanceof Strand) {
			mi.setAction(ar.getAction(SbAction.STRAND_MANAGE));
			mi.setText(I18N.getMsg("msg.menu.strands.manage"));
			mi.setIcon(I18N.getIcon("icon.medium.manage.strands"));
		} else if (dbObj instanceof Part) {
			mi.setAction(ar.getAction(SbAction.PART_MANAGE));
			mi.setText(I18N.getMsg("msg.menu.parts.manage"));
			mi.setIcon(I18N.getIcon("icon.medium.manage.parts"));
		}

		menu.add(new JPopupMenu.Separator());
		menu.add(mi);
	}
	
	private void addShowInMenu(JPopupMenu menu, DbTable dbObj) {
		AbstractAction action = ActionRegistry.getInstance().getAction(
				SbAction.SHOW_IN_CHRONO_VIEW);
		action.putValue(Constants.ActionKey.CHAPTER_OR_SCENE.toString(),
				dbObj);
		menu.add(action);

		action = ActionRegistry.getInstance().getAction(
				SbAction.SHOW_IN_BOOK_VIEW);
		action.putValue(Constants.ActionKey.CHAPTER_OR_SCENE.toString(),
				dbObj);
		menu.add(action);

		action = ActionRegistry.getInstance().getAction(
				SbAction.SHOW_IN_MANAGE_VIEW);
		action.putValue(Constants.ActionKey.CHAPTER_OR_SCENE.toString(),
				dbObj);
		menu.add(action);
	}

	private void setInfoText(DbTable dbObj) {
		String str = DbPeer.getInfoText(dbObj);
		infoPanel.setText(str);
	}

	private AbstractAction getToggleCharactersAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btToogleCharacters);
			}
		};
	}

	private AbstractAction getToggleLocationsAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btToogleLocations);
			}
		};
	}

	private AbstractAction getToggleTagsAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btToogleTags);
			}
		};
	}

	private AbstractAction getToggleItemsAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btToogleItems);
			}
		};
	}

	private AbstractAction getToggleChaptersAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btToogleChapters);
			}
		};
	}

	private AbstractAction getToggleStrandsAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btToogleStrands);
			}
		};
	}

	private AbstractAction getTogglePartsAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btToogleParts);
			}
		};
	}

	private void toggle(IconButton button) {
		boolean toggle = button.isSelected();
		button.setSelected(!toggle);
		int count = 0;
		for (IconButton bt : toggleButtonList) {
			if (bt.isSelected()) {
				++count;
			}
		}
		initGUI();
		if (count == 1) {
			getExpandAction().actionPerformed(null);
		}
		button.requestFocus();
	}

	private void toggleSingle(IconButton button) {
		getShowNoneAction().actionPerformed(null);
		toggle(button);
	}

	private AbstractAction getShowAllAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				for (IconButton button : toggleButtonList) {
					button.setSelected(true);
				}
				refresh();
			}
		};
	}

	private AbstractAction getShowNoneAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				for (IconButton button : toggleButtonList) {
					button.setSelected(false);
				}
				refresh();
			}
		};
	}

	private AbstractAction getExpandAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				for (int i = 0; i < tree.getRowCount(); i++) {
					tree.expandRow(i);
				}
			}
		};
	}

	private AbstractAction getCollapseAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				for (int i = 0; i < tree.getRowCount(); i++) {
					TreePath path = tree.getPathForRow(i);
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
							.getLastPathComponent();
					Object value = node.getUserObject();
					if (value instanceof DbTableNode) {
						collapse(tree, path);
						tree.collapsePath(path);
					}
				}
			}
		};
	}

	private static void collapse(JTree tree, TreePath path) {
		Enumeration<TreePath> paths = tree.getExpandedDescendants(path);
		if (paths == null) {
			return;
		}
		while (paths.hasMoreElements()) {
			TreePath path2 = paths.nextElement();
			collapse(tree, path2);
			tree.collapsePath(path2);
		}
	}

	public InfoPanel getInfoPanel(){
		return infoPanel;
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent evt) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		if (node == null) {
			return;
		}
		Object value = node.getUserObject();
		if (value instanceof DbTable) {
			DbTable dbObj = (DbTable) value;
			setInfoText(dbObj);
		} else if (value instanceof ThinDbTable) {
			ThinDbTable thinDbObj = (ThinDbTable) value;
			setInfoText(thinDbObj.getDbTable());
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (PCSDispatcher.isPropertyFired(Property.CHARACTER, evt)
				|| PCSDispatcher.isPropertyFired(Property.GENDER, evt)) {
			if (btToogleCharacters.isSelected()) {
				refresh();
				return;
			}
		}

		if (PCSDispatcher.isPropertyFired(Property.LOCATION, evt)) {
			if (btToogleLocations.isSelected()) {
				refresh();
				return;
			}
		}

		if (PCSDispatcher.isPropertyFired(Property.TAG, evt)) {
			if (btToogleTags.isSelected()) {
				refresh();
				return;
			}
		}

		if (PCSDispatcher.isPropertyFired(Property.ITEM, evt)) {
			if (btToogleItems.isSelected()) {
				refresh();
				return;
			}
		}

		if (PCSDispatcher.isPropertyFired(Property.STRAND, evt)) {
			if (btToogleStrands.isSelected()) {
				refresh();
				return;
			}
		}

		if (PCSDispatcher.isPropertyFired(Property.PART, evt)) {
			if (btToogleParts.isSelected()) {
				refresh();
				return;
			}
		}

		if (PCSDispatcher.isPropertyFired(Property.CHAPTER, evt)
				|| PCSDispatcher.isPropertyFired(Property.SCENE, evt)
				|| PCSDispatcher.isPropertyFired(Property.SCENE_TITLE_SUMMARY,
						evt)) {
			if (btToogleChapters.isSelected()) {
				refresh();
				return;
			}
		}

		if (PCSDispatcher.isPropertyFired(Property.PROJECT, evt)) {
			if (PCSDispatcher.isPropertyRemoved(evt)) {
				// project closed
				// upper component: JPanel containing the tree
				((Container) getComponent(1)).removeAll();
				// lower component: Info Panel
				((Container) getComponent(2)).removeAll();
				validate();
			} else {
				// project opened
				initGUI();
				refresh();
			}
		}

		if (PCSDispatcher.isPropertyFired(Property.REFRESH_ALL, evt)) {
			theInstance = null;
			refresh();
			return;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		if (node == null) {
			return;
		}
		Object value = node.getUserObject();
		DbTable dbObj = null;
		if (value instanceof DbTable) {
			dbObj = (DbTable) value;
		} else if (value instanceof ThinDbTable) {
			ThinDbTable thinDbObj = (ThinDbTable) value;
			if (thinDbObj instanceof ThinLocation) {
				dbObj = ((ThinLocation) thinDbObj).getLocation();
			}
		}
		if (dbObj == null) {
			return;
		}
		if (e.getActionCommand().compareTo(CMD_COPY) == 0) {
			TableCopyAction action = new TableCopyAction(dbObj);
			action.actionPerformed(e);
			return;
		}
		if (e.getActionCommand().compareTo(CMD_DELETE) == 0) {
			TableDeleteAction action = new TableDeleteAction(dbObj);
			action.actionPerformed(e);
			return;
		}
	}
}
