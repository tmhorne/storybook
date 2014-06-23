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

package ch.intertec.storybook.main.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JToolBar;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.action.ActionManager.SbAction;
import ch.intertec.storybook.action.ActionRegistry;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;
import ch.intertec.storybook.view.IconButton;

@SuppressWarnings("serial")
public class MainToolBar extends JToolBar implements ActionListener,
		PropertyChangeListener, IRefreshable {

	public final static String COMP_NAME = "toolbar";
	public final static String COMP_NAME_BT_NEW_CHARACTER = "bt:new_character";
	public final static String COMP_NAME_BT_NEW_TAG = "bt:new_tag";
	public final static String COMP_NAME_BT_NEW_ITEM = "bt:new_item";
	public final static String COMP_NAME_BT_NEW_LOCATION = "bt:new_location";
	public final static String COMP_NAME_BT_NEXT_PART = "bt:next_part";
	public final static String COMP_NAME_BT_PREV_PART = "bt:prev_part";
	public final static String COMP_NAME_COB_VIEW = "cob:view";
	
	private JComboBox cobView;
	private IconButton btShowSidebar;
	
	public MainToolBar(){
		initGUI();
		
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.addPropertyChangeListener(PCSDispatcher.Property.PROJECT, this);
//		pcs.addPropertyChangeListener(PCSDispatcher.Property.PART, this);
//		pcs.addPropertyChangeListener(PCSDispatcher.Property.ACTIVE_PART, this);
//		pcs.addPropertyChangeListener(PCSDispatcher.Property.VIEW, this);
	}
	
	private void initGUI(){
		MigLayout layout = new MigLayout(
				"insets 0 2 0 2",
				"",
				"[center]"); 
		setLayout(layout);		
		setFloatable(false);
		setName(COMP_NAME);

		boolean active = ProjectTools.isProjectOpen();
		ActionRegistry ar = ActionRegistry.getInstance();

		// new file
		IconButton btNewFile = new IconButton(
				"icon.small.file.new",
				"msg.file.new",
				ar.getAction(SbAction.FILE_NEW)
				);
		
		// open file
		IconButton btOpenFile = new IconButton(
				"icon.small.open",
				"msg.common.open",
				ar.getAction(SbAction.FILE_OPEN));
		
		// save file
		IconButton btSaveFile = new IconButton(
				"icon.small.save",
				"msg.file.save",
				ar.getAction(SbAction.FILE_SAVE));
		btSaveFile.setEnabled(active);

		// print / export
		IconButton btExportPrint = new IconButton(
				"icon.small.print",
				"msg.menu.file.export_print",
				ar.getAction(SbAction.EXPORT_PRINT));
		btExportPrint.setEnabled(active);

		// new scene
		IconButton btNewScene = new IconButton(
				"icon.small.new",
				"msg.common.scene.add",
				ar.getAction(SbAction.SCENE_NEW));
		btNewScene.setEnabled(active);
		
		// new chapter
		IconButton btNewChapter = new IconButton(
				"icon.small.chapter.new",
				"msg.common.chapter.add",
				ar.getAction(SbAction.CHAPTER_NEW));
		btNewChapter.setEnabled(active);		
		
		// manage chapters
		IconButton btManageChapters = new IconButton(
				"icon.medium.manage.chapters",
				"msg.menu.chapters.manage",
				ar.getAction(SbAction.CHAPTER_MANAGE));
		btManageChapters.setEnabled(active);
		
		// new location
		IconButton btNewLocation = new IconButton(
				"icon.medium.new.location",
				"msg.common.location.new",
				ar.getAction(SbAction.LOCATION_NEW));
		btNewLocation.setEnabled(active);
		btNewLocation.setName(COMP_NAME_BT_NEW_LOCATION);
		
		// new character
		IconButton btNewCharacter = new IconButton(
				"icon.medium.new.person",
				"msg.common.person.new",
				ar.getAction(SbAction.CHARACTER_NEW));
		btNewCharacter.setEnabled(active);
		btNewCharacter.setName(COMP_NAME_BT_NEW_CHARACTER);

		// manage locations
		IconButton btManageLocations = new IconButton(
				"icon.medium.manage.locations",
				"msg.dlg.mng.loc.title",
				ar.getAction(SbAction.LOCATION_MANAGE));
		btManageLocations.setEnabled(active);
		
		// manage characters
		IconButton btManageCharacters = new IconButton(
				"icon.medium.manage.persons",
				"msg.dlg.mng.persons.title",
				ar.getAction(SbAction.CHARACTER_MANAGE));
		btManageCharacters.setEnabled(active);

		// new tag
		IconButton btNewTag = new IconButton(
				"icon.medium.new.tag",
				"msg.tag.new",
				ar.getAction(SbAction.TAG_NEW));
		btNewTag.setEnabled(active);
		btNewTag.setName(COMP_NAME_BT_NEW_TAG);

		// manage tags
		IconButton btManageTags = new IconButton(
				"icon.medium.manage.tags",
				"msg.tags.manage",
				ar.getAction(SbAction.TAG_MANAGE));
		btManageTags.setEnabled(active);

		// tag assignments
		IconButton btTagAssignments = new IconButton(
				"icon.medium.manage.tag_links",
				"msg.tag.assignments",
				ar.getAction(SbAction.TAG_ASSIGNMENTS));
		btTagAssignments.setEnabled(active);

		// new item
		IconButton btNewItem = new IconButton(
				"icon.medium.new.item",
				"msg.item.new",
				ar.getAction(SbAction.ITEM_NEW));
		btNewItem.setEnabled(active);
		btNewItem.setName(COMP_NAME_BT_NEW_ITEM);

		// manage item
		IconButton btManageItems = new IconButton(
				"icon.medium.manage.items",
				"msg.items.manage",
				ar.getAction(SbAction.ITEM_MANAGE));
		btManageItems.setEnabled(active);

		// item assignments
		IconButton btItemAssignments = new IconButton(
				"icon.medium.manage.item_links",
				"msg.item.assignments",
				ar.getAction(SbAction.ITEM_ASSIGNMENTS));
		btItemAssignments.setEnabled(active);

		// flash of inspiration
		IconButton btIdeas = new IconButton(
				"icon.small.bulb",
				"msg.foi.title",
				ar.getAction(SbAction.IDEAS_FOI));
		btIdeas.setEnabled(active);
		
		// memoria
		IconButton btMemoria = new IconButton(
				"icon.small.memoria",
				"msg.menu.view.pov",
				ar.getAction(SbAction.MEMORIA));
		btMemoria.setEnabled(active);
		
		// task list
		IconButton btTaskList = new IconButton(
				"icon.small.tasklist",
				"msg.tasklist.title",
				ar.getAction(SbAction.TASK_LIST));
		btTaskList.setEnabled(active);
		
		// refresh
		IconButton btRefresh = new IconButton(
				"icon.small.refresh",
				"msg.common.refresh",
				ar.getAction(SbAction.REFRESH));
		btRefresh.setEnabled(active);
		
		btShowSidebar = new IconButton(
				"icon.small.show.infos",
				"msg.menu.view.show.infos",
				ar.getAction(SbAction.SIDEBAR_TOGGLE));
		btShowSidebar.setEnabled(active);		

		// layout
		add(btNewFile);
		add(btOpenFile);
		add(btSaveFile);
		add(btExportPrint);
		
		add(SwingTools.createMenuBarSpacer());
		
		add(btNewScene);
		add(btNewChapter);
		add(btManageChapters);
		add(btNewCharacter);
		add(btManageCharacters);
		add(btNewLocation);
		add(btManageLocations);
		add(btNewTag);
		add(btManageTags);
		add(btTagAssignments);
		add(btNewItem);
		add(btManageItems);
		add(btItemAssignments);
				
		add(SwingTools.createMenuBarSpacer());
		
		add(btMemoria);
		add(btIdeas);
		add(btTaskList);
		
		add(btRefresh, "pushx,right");
		add(btShowSidebar);
	}

	@Override
	public void refresh(){
		removeAll();
		initGUI();
		revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == cobView) {
			ViewItem item = (ViewItem) cobView.getSelectedItem();
			item.getViewAction().actionPerformed(evt);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		refresh();
	}
}
