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

package ch.intertec.storybook.main;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import org.apache.commons.io.FilenameUtils;

import ch.intertec.storybook.action.ActionManager;
import ch.intertec.storybook.action.ActionManager.SbAction;
import ch.intertec.storybook.action.ActionRegistry;
import ch.intertec.storybook.action.OpenBrowserSbPageAction;
import ch.intertec.storybook.action.ViewPartAction;
import ch.intertec.storybook.chart.ChartManager;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.PrefManager;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.Constants.ActionKey;
import ch.intertec.storybook.toolkit.Constants.Preference;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;

@SuppressWarnings("serial")
public class MainMenuBar extends JMenuBar implements IRefreshable,
		PropertyChangeListener {

	private final String COMP_NAME_PART_MENU = "menu:part";
	private final String COMP_NAME_VIEW_MENU = "menu:view";
	
	private static MainMenuBar theInstance;
	
	private boolean isProjectOpen;
	private static ActionRegistry actionRegistry; 
	
	public static MainMenuBar getInstance() {
		if (theInstance == null) {
			actionRegistry = ActionRegistry.getInstance();
			theInstance = new MainMenuBar();
		}
		return theInstance;
	}
	
	private MainMenuBar(){
		initGUI();
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.addPropertyChangeListener(Property.PART, this);
		pcs.addPropertyChangeListener(Property.ACTIVE_PART, this);
		pcs.addPropertyChangeListener(Property.PROJECT, this);
		pcs.addPropertyChangeListener(Property.VIEW, this);
		pcs.addPropertyChangeListener(Property.REFRESH_ALL, this);
	}

	private void initGUI(){
		isProjectOpen = ProjectTools.isProjectOpen();

		removeAll();

		// add menus
		addFileMenu();
		addEditMenu();
		addCharactersMenu();
		addLocationsMenu();
		addTagsMenu();
		addItemsMenu();
		addChaptersMenu();
		addScenesMenu();
		addStrandsMenu();
		add(createPartMenu());
		add(createViewMenu());
		addToolsMenu();
		if (MainFrame.getInstance().isInTranslatorMode()) {
			addTranslatorMenu();
		}		
		addHelpMenu();
	}
	
	public void refreshEditMenu() {
		JMenu editMenu = getMenu(1);
		editMenu.removeAll();
		JComponent comp = null;
		try {
			comp = (JComponent) MainFrame.getInstance().getFocusOwner();
		} catch (ClassCastException e) {
			// ignore
		}
		SwingTools.addCopyPasteToMenu(editMenu, comp);
		
		editMenu.add(new JSeparator());
		
		// find date
		JMenuItem miFindDate = new JMenuItem();
		miFindDate.setAction(actionRegistry.getAction(SbAction.GOTO_DATE_DLG));
		miFindDate.setText(I18N.getMsgDot("msg.menu.navigate.goto.date"));
		miFindDate.setIcon(I18N.getIcon("icon.small.search"));
		I18N.setMnemonic(miFindDate, KeyEvent.VK_D, KeyEvent.VK_D);
		SwingTools.setAccelerator(miFindDate, KeyEvent.VK_F, Event.CTRL_MASK);
		miFindDate.setEnabled(isProjectOpen);
		editMenu.add(miFindDate);
		
		// find chapter
		JMenuItem miFindChapter = new JMenuItem();
		miFindChapter.setAction(actionRegistry.getAction(SbAction.GOTO_CHAPTER_DLG));
		miFindChapter.setText(I18N.getMsgDot("msg.menu.navigate.goto.chapter"));
		miFindChapter.setIcon(I18N.getIcon("icon.small.search"));
		I18N.setMnemonic(miFindChapter, KeyEvent.VK_C, KeyEvent.VK_K);
		SwingTools.setAccelerator(miFindChapter, KeyEvent.VK_F, Event.CTRL_MASK
				| Event.SHIFT_MASK);
		miFindChapter.setEnabled(isProjectOpen);
		editMenu.add(miFindChapter);
	}
	
	@Override
	public void refresh(){
		initGUI();
	}
	
	private void addScenesMenu() {
		// scenes menu
		JMenu scenesMenu = new JMenu();
		scenesMenu.setText(I18N.getMsg("msg.common.scenes"));
		I18N.setMnemonic(scenesMenu, KeyEvent.VK_S, KeyEvent.VK_S);
		add(scenesMenu);

		// new scene
		JMenuItem newSceneMI = new JMenuItem();
		newSceneMI.setAction(actionRegistry.getAction(SbAction.SCENE_NEW));
		newSceneMI.setText(I18N.getMsg("msg.menu.scene.new"));
		newSceneMI.setEnabled(isProjectOpen);
		I18N.setMnemonic(newSceneMI, KeyEvent.VK_C, KeyEvent.VK_K);
		newSceneMI.setIcon(I18N.getIcon("icon.small.new"));
		scenesMenu.add(newSceneMI);

	}

	private void addChaptersMenu() {
		// chapters menu
		JMenu chaptersMenu = new JMenu();
		chaptersMenu.setText(I18N.getMsg("msg.menu.chapters"));
		I18N.setMnemonic(chaptersMenu, KeyEvent.VK_C, KeyEvent.VK_K);
		add(chaptersMenu);

		// new chapter
		JMenuItem newChapterMI = new JMenuItem();
		newChapterMI.setAction(actionRegistry.getAction(SbAction.CHAPTER_NEW));
		newChapterMI.setText(I18N.getMsg("msg.menu.chapters.new"));
		newChapterMI.setEnabled(isProjectOpen);
		I18N.setMnemonic(newChapterMI, KeyEvent.VK_N);
		newChapterMI.setIcon(I18N.getIcon("icon.small.chapter.new"));
		chaptersMenu.add(newChapterMI);

		// manage chapters
		JMenuItem manageChaptersMI = new JMenuItem();
		manageChaptersMI.setAction(actionRegistry.getAction(SbAction.CHAPTER_MANAGE));
		manageChaptersMI.setText(I18N.getMsg("msg.menu.chapters.manage"));
		manageChaptersMI.setEnabled(isProjectOpen);
		I18N.setMnemonic(manageChaptersMI, KeyEvent.VK_M, KeyEvent.VK_V);
		manageChaptersMI.setIcon(I18N.getIcon("icon.medium.manage.chapters"));
		chaptersMenu.add(manageChaptersMI);

		chaptersMenu.add(new JSeparator());
		
		// generate chapters
		JMenuItem generateChaptersMI = new JMenuItem();
		generateChaptersMI.setAction(actionRegistry.getAction(SbAction.CHAPTER_GENERATE));
		generateChaptersMI.setText(I18N.getMsgDot("msg.generate.chapters"));
		generateChaptersMI.setEnabled(isProjectOpen);
		I18N.setMnemonic(generateChaptersMI, KeyEvent.VK_C, KeyEvent.VK_M);
		chaptersMenu.add(generateChaptersMI);
	}

	private void addToolsMenu() {
		JMenu toolsMenu = new JMenu();
		toolsMenu.setText(I18N.getMsg("msg.menu.tools"));
		I18N.setMnemonic(toolsMenu, KeyEvent.VK_T, KeyEvent.VK_T);
		add(toolsMenu);

		// memoria
		JMenuItem miMemoria = new JMenuItem();
		miMemoria.setAction(actionRegistry.getAction(SbAction.MEMORIA));
		miMemoria.setText(I18N.getMsgDot("msg.menu.view.pov"));
		miMemoria.setIcon(I18N.getIcon("icon.small.memoria"));
		miMemoria.setEnabled(isProjectOpen);
		I18N.setMnemonic(miMemoria, KeyEvent.VK_M);
		SwingTools.setAccelerator(miMemoria, KeyEvent.VK_M, KeyEvent.CTRL_MASK);
		toolsMenu.add(miMemoria);

		// task list
		JMenuItem miTasks = new JMenuItem();
		miTasks.setAction(actionRegistry.getAction(SbAction.TASK_LIST));
		miTasks.setText(I18N.getMsgDot("msg.tasklist.title"));
		miTasks.setIcon(I18N.getIcon("icon.small.tasklist"));
		I18N.setMnemonic(miTasks, KeyEvent.VK_T, KeyEvent.VK_A);
		SwingTools.setAccelerator(miTasks, KeyEvent.VK_K, KeyEvent.CTRL_MASK);
		miTasks.setEnabled(isProjectOpen);
		toolsMenu.add(miTasks);
		
		// separator
		toolsMenu.add(new JSeparator());
		
		// flash of inspiration
		JMenuItem miFoi = new JMenuItem();
		miFoi.setAction(actionRegistry.getAction(SbAction.IDEAS_FOI));
		miFoi.setText(I18N.getMsg("msg.foi.title"));
		miFoi.setIcon(I18N.getIcon("icon.small.bulb"));
		miFoi.setEnabled(isProjectOpen);
		toolsMenu.add(miFoi);
		
		// ideas
		JMenuItem miIdeas = new JMenuItem();
		miIdeas.setAction(actionRegistry.getAction(SbAction.IDEAS));
		miIdeas.setText(I18N.getMsg("msg.ideas.title"));
		miIdeas.setIcon(I18N.getIcon("icon.small.bulb"));
		miIdeas.setEnabled(isProjectOpen);
		toolsMenu.add(miIdeas);

		// separator
		toolsMenu.add(new JSeparator());

		ChartManager chartManager = ChartManager.getInstance();

		// part related charts
		JMenu chartsPartMenu = new JMenu();
		chartsPartMenu.setText(I18N.getMsg("msg.menu.tools.charts.part"));
		I18N.setMnemonic(chartsPartMenu, KeyEvent.VK_P, KeyEvent.VK_T);
		chartsPartMenu.setIcon(I18N.getIcon("icon.small.chart"));
		toolsMenu.add(chartsPartMenu);

		// who is when where
		JMenuItem miWiww = new JMenuItem();
		miWiww.setAction(chartManager.getWhoIsWhereWhenAction());
		miWiww.setText(I18N
				.getMsg("msg.menu.tools.charts.overall.whoIsWhereWhen"));
		I18N.setMnemonic(miWiww, KeyEvent.VK_W, KeyEvent.VK_W);
		miWiww.setEnabled(isProjectOpen);
		chartsPartMenu.add(miWiww);

		// character appearance by scene
		JMenuItem miChByScene = new JMenuItem();
		miChByScene.setAction(chartManager.getCharacterSceneAction());
		miChByScene.setText(I18N
				.getMsg("msg.menu.tools.charts.part.character.scene"));
		I18N.setMnemonic(miChByScene, KeyEvent.VK_C, KeyEvent.VK_K);
		miChByScene.setEnabled(isProjectOpen);
		chartsPartMenu.add(miChByScene);

		// character appearance by date
		JMenuItem miChByDate = new JMenuItem();
		miChByDate.setAction(chartManager.getCharacterDateAction());
		miChByDate.setText(I18N
				.getMsg("msg.menu.tools.charts.overall.character.date"));
		I18N.setMnemonic(miChByDate, KeyEvent.VK_D, KeyEvent.VK_D);
		miChByDate.setEnabled(isProjectOpen);
		chartsPartMenu.add(miChByDate);

		// usage of strands by date
		JMenuItem miStrandsByDate = new JMenuItem();
		miStrandsByDate.setAction(chartManager.getStrandDateAction());
		miStrandsByDate.setText(I18N
				.getMsg("msg.menu.tools.charts.overall.strand.date"));
		I18N.setMnemonic(miStrandsByDate, KeyEvent.VK_S, KeyEvent.VK_S);
		miStrandsByDate.setEnabled(isProjectOpen);
		chartsPartMenu.add(miStrandsByDate);

		// overall charts
		JMenu chartsOverallMenu = new JMenu();
		chartsOverallMenu.setText(I18N.getMsg("msg.menu.tools.charts.overall"));
		I18N.setMnemonic(chartsOverallMenu, KeyEvent.VK_O, KeyEvent.VK_G);
		chartsOverallMenu.setIcon(I18N.getIcon("icon.small.chart"));
		toolsMenu.add(chartsOverallMenu);

		// Gantt chart of characters
		JMenuItem miGantt = new JMenuItem();
		miGantt.setAction(chartManager.getCharacterGanttChartAction());
		miGantt.setText(I18N.getMsg("msg.chart.gantt.characters.title"));
		miGantt.setEnabled(isProjectOpen);
		chartsOverallMenu.add(miGantt);

		// occurrence of characters
		JMenuItem miOccCharacters = new JMenuItem();
		miOccCharacters.setAction(chartManager.getCharacterOccurrenceAction());
		miOccCharacters.setText(I18N
				.getMsg("msg.menu.tools.charts.overall.character.occurrence"));
		I18N.setMnemonic(miOccCharacters, KeyEvent.VK_O, KeyEvent.VK_H);
		miOccCharacters.setEnabled(isProjectOpen);
		chartsOverallMenu.add(miOccCharacters);

		// occurrence of locations
		JMenuItem miOccLocations = new JMenuItem();
		miOccLocations.setAction(chartManager.getLocationOccurrenceAction());
		miOccLocations.setText(I18N
				.getMsg("msg.menu.tools.charts.overall.location.occurrence"));
		I18N.setMnemonic(miOccLocations, KeyEvent.VK_C, KeyEvent.VK_L);
		miOccLocations.setEnabled(isProjectOpen);
		chartsOverallMenu.add(miOccLocations);
	}

	private void addHelpMenu() {
		// help menu
		JMenu helpMenu = new JMenu();
		helpMenu.setText(I18N.getMsg("msg.menu.help"));
		helpMenu.setMnemonic(KeyEvent.VK_H);
		add(helpMenu);

		// manual
		JMenuItem miManual = new JMenuItem();
		miManual.setAction(actionRegistry.getAction(SbAction.MANUAL));
		miManual.setText(I18N.getMsgDot("msg.menu.help.manual"));
		SwingTools.setAccelerator(miManual, KeyEvent.VK_F1, 0);
		helpMenu.add(miManual);

		// online help
		JMenuItem miHelp = new JMenuItem();
		OpenBrowserSbPageAction action = new OpenBrowserSbPageAction();
		action.putValue(OpenBrowserSbPageAction.ACTION_KEY_PAGE,
				I18N.getMsg("msg.url.documents"));
		miHelp.setAction(action);
		miHelp.setText(I18N.getMsgDot("msg.menu.help.online"));
		SwingTools.setAccelerator(miHelp, KeyEvent.VK_F1, KeyEvent.CTRL_MASK);
		helpMenu.add(miHelp);

		// FAQ
		JMenuItem miFaq = new JMenuItem();
		action = new OpenBrowserSbPageAction();
		action.putValue(OpenBrowserSbPageAction.ACTION_KEY_PAGE,
				I18N.getMsg("msg.url.faq"));
		miFaq.setAction(action);
		miFaq.setText(I18N.getMsgDot("msg.menu.faq.online"));
		helpMenu.add(miFaq);

		if (!Constants.Application.IS_PRO_VERSION.toBoolean()) {
			JMenuItem miGoPro = new JMenuItem();
			action = new OpenBrowserSbPageAction();
			action.putValue(OpenBrowserSbPageAction.ACTION_KEY_PAGE,
					I18N.getMsg("msg.url.gopro"));
			miGoPro.setAction(action);
			miGoPro.setText(I18N.getMsg("msg.menu.help.donate"));
			helpMenu.add(miGoPro);
		} else {
			// contact
			JMenuItem miContact = new JMenuItem();
			action = new OpenBrowserSbPageAction();
			action.putValue(OpenBrowserSbPageAction.ACTION_KEY_PAGE,
					I18N.getMsg("msg.url.contact"));
			miContact.setAction(action);
			miContact.setText(I18N.getMsg("msg.menu.help.contact"));
			helpMenu.add(miContact);
		}

		helpMenu.add(new JSeparator());

		if(!Constants.Application.IS_PRO_VERSION.toBoolean()){			
			// check for updates
			JMenuItem miUpdate = new JMenuItem();
			miUpdate.setAction(actionRegistry.getAction(SbAction.UPDATE));
			miUpdate.setText(I18N.getMsg("msg.menu.help.update"));
			helpMenu.add(miUpdate);
			
			helpMenu.add(new JSeparator());
		}

		// home page
		JMenuItem miHomepage = new JMenuItem();		
		action = new OpenBrowserSbPageAction();
		action.putValue(OpenBrowserSbPageAction.ACTION_KEY_PAGE,
				I18N.getMsg("msg.url.home"));
		miHomepage.setAction(action);
		miHomepage.setText(I18N.getMsg("msg.menu.help.homepage"));
		helpMenu.add(miHomepage);

		// Facebook
		JMenuItem miFacebook = new JMenuItem();		
		action = new OpenBrowserSbPageAction();
		action.putValue(OpenBrowserSbPageAction.ACTION_KEY_PAGE,
				I18N.getMsg("msg.url.facebook"));
		miFacebook.setAction(action);
		miFacebook.setText(I18N.getMsg("msg.menu.help.facebook"));
		helpMenu.add(miFacebook);

		helpMenu.add(new JSeparator());
		
		// about
		JMenuItem miAbout = new JMenuItem();
		miAbout.setAction(actionRegistry.getAction(SbAction.ABOUT));
		miAbout.setText(I18N.getMsg("msg.menu.help.about"));
		helpMenu.add(miAbout);
	}
	
	private JMenu createViewMenu(){
		// view menu
		JMenu viewMenu = new JMenu();
		viewMenu.setName(COMP_NAME_VIEW_MENU);
		viewMenu.setText(I18N.getMsg("msg.menu.view"));
		I18N.setMnemonic(viewMenu, KeyEvent.VK_V, KeyEvent.VK_A);
		add(viewMenu);
		
		ButtonGroup viewGroup = new ButtonGroup();
		
		// chronological view
		JRadioButtonMenuItem rbmiChronoView = new JRadioButtonMenuItem();
		rbmiChronoView.setAction(
				actionRegistry.getAction(SbAction.VIEW_CHRONO));
		rbmiChronoView.setText(I18N.getMsg("msg.menu.view.chrono"));
		rbmiChronoView.setIcon(I18N.getIcon("icon.small.chrono.view"));
		rbmiChronoView.setEnabled(isProjectOpen);
		I18N.setMnemonic(rbmiChronoView, KeyEvent.VK_C);
		SwingTools.setAccelerator(rbmiChronoView, KeyEvent.VK_1,
				Event.CTRL_MASK);
		if (MainFrame.getInstance().isChronoPanelActive()) {
			rbmiChronoView.setSelected(true);
		}
		viewGroup.add(rbmiChronoView);
		viewMenu.add(rbmiChronoView);

		// manage view
		JRadioButtonMenuItem rbmiManageView = new JRadioButtonMenuItem();
		rbmiManageView.setAction(
				actionRegistry.getAction(SbAction.VIEW_MANAGE));
		rbmiManageView.setText(I18N.getMsg("msg.menu.view.manage"));
		rbmiManageView.setIcon(I18N.getIcon("icon.small.manage.view"));
		rbmiManageView.setEnabled(isProjectOpen);
		I18N.setMnemonic(rbmiManageView, KeyEvent.VK_M);
		SwingTools.setAccelerator(rbmiManageView, KeyEvent.VK_2,
				Event.CTRL_MASK);
		if (MainFrame.getInstance().isManagePanelActive()) {
			rbmiManageView.setSelected(true);
		}
		viewGroup.add(rbmiManageView);
		viewMenu.add(rbmiManageView);

		// book view
		JRadioButtonMenuItem rbmiBookView = new JRadioButtonMenuItem();
		rbmiBookView.setAction(
				actionRegistry.getAction(SbAction.VIEW_BOOK));
		rbmiBookView.setText(I18N.getMsg("msg.menu.view.book"));
		rbmiBookView.setIcon(I18N.getIcon("icon.small.book.view"));
		rbmiBookView.setEnabled(isProjectOpen);
		I18N.setMnemonic(rbmiBookView, KeyEvent.VK_B);
		SwingTools.setAccelerator(rbmiBookView, KeyEvent.VK_3,
				Event.CTRL_MASK);
		if (MainFrame.getInstance().isBookPanelActive()) {
			rbmiBookView.setSelected(true);
		}
		viewGroup.add(rbmiBookView);
		viewMenu.add(rbmiBookView);

		viewMenu.add(new JSeparator());

		// show info panel
		JMenuItem miShowInfos = new JMenuItem();
		miShowInfos.setAction(actionRegistry.getAction(SbAction.SIDEBAR_TOGGLE));
		miShowInfos.setText(I18N.getMsg("msg.menu.view.show.infos"));
		miShowInfos.setIcon(I18N.getIcon("icon.small.show.infos"));
		miShowInfos.setEnabled(isProjectOpen);
		I18N.setMnemonic(miShowInfos, KeyEvent.VK_I);
		SwingTools.setAccelerator(miShowInfos, KeyEvent.VK_0,
				KeyEvent.CTRL_MASK);
		viewMenu.add(miShowInfos);

		viewMenu.add(new JSeparator());

		// refresh view
		JMenuItem miRefresh = new JMenuItem();
		miRefresh.setAction(actionRegistry.getAction(SbAction.REFRESH));
		miRefresh.setText(I18N.getMsg("msg.menu.view.refresh"));
		miRefresh.setEnabled(isProjectOpen);
		I18N.setMnemonic(miRefresh, KeyEvent.VK_R, KeyEvent.VK_A);
		SwingTools.setAccelerator(miRefresh, KeyEvent.VK_F5, 0);
		miRefresh.setIcon(I18N.getIcon("icon.small.refresh"));
		viewMenu.add(miRefresh);
		
		return viewMenu;
	}

	private JMenu createPartMenu() {
		// part menu
		JMenu partMenu = new JMenu();
		partMenu.setName(COMP_NAME_PART_MENU);
		partMenu.setText(I18N.getMsg("msg.menu.parts"));
		I18N.setMnemonic(partMenu, KeyEvent.VK_P, KeyEvent.VK_T);
		
		for (Part part : PartPeer.doSelectAll()) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem();
			AbstractAction action = new ViewPartAction();
			action.putValue(ViewPartAction.ACTION_KEY_PART_ID,
					part.getId());
			item.setAction(action);

			Object[] args = { part.getNumberStr(), part.getName() };
			String text = I18N.getMsg("msg.menu.parts.view", args);
			item.setText(text);
			if (MainFrame.getInstance().getActivePartId() == part.getId()) {
				item.setSelected(true);
			}
			SwingTools.setAccelerator(item, KeyEvent.VK_0 + part.getNumber(),
					Event.ALT_MASK);
			partMenu.add(item);
		}

		partMenu.add(new JSeparator());

		// new part
		JMenuItem newPartMI = new JMenuItem();
		newPartMI.setAction(actionRegistry.getAction(SbAction.PART_NEW));
		newPartMI.setText(I18N.getMsg("msg.menu.parts.new"));
		newPartMI.setEnabled(isProjectOpen);
		I18N.setMnemonic(newPartMI, KeyEvent.VK_N);
		newPartMI.setIcon(I18N.getIcon("icon.medium.new.part"));
		partMenu.add(newPartMI);

		// manage parts
		JMenuItem manageStrandsMI = new JMenuItem();
		manageStrandsMI.setAction(actionRegistry.getAction(SbAction.PART_MANAGE));
		manageStrandsMI.setText(I18N.getMsg("msg.menu.parts.manage"));
		manageStrandsMI.setEnabled(isProjectOpen);
		I18N.setMnemonic(manageStrandsMI, KeyEvent.VK_M, KeyEvent.VK_V);
		manageStrandsMI.setIcon(I18N.getIcon("icon.medium.manage.parts"));
		partMenu.add(manageStrandsMI);
		
		return partMenu;
	}

	private void addStrandsMenu() {
		// strand menu
		JMenu strandMenu = new JMenu();
		strandMenu.setText(I18N.getMsg("msg.menu.strands"));
		I18N.setMnemonic(strandMenu, KeyEvent.VK_R, KeyEvent.VK_S);
		add(strandMenu);

		// new strand
		JMenuItem newStrandMI = new JMenuItem();
		newStrandMI.setAction(actionRegistry.getAction(SbAction.STRAND_NEW));
		newStrandMI.setText(I18N.getMsg("msg.menu.strands.new"));
		newStrandMI.setEnabled(isProjectOpen);
		I18N.setMnemonic(newStrandMI, KeyEvent.VK_S);
		newStrandMI.setIcon(I18N.getIcon("icon.medium.new.strand"));
		strandMenu.add(newStrandMI);

		// manage strands
		JMenuItem manageStrandsMI = new JMenuItem();
		manageStrandsMI.setAction(actionRegistry.getAction(SbAction.STRAND_MANAGE));
		manageStrandsMI.setText(I18N.getMsg("msg.menu.strands.manage"));
		manageStrandsMI.setEnabled(isProjectOpen);
		I18N.setMnemonic(manageStrandsMI, KeyEvent.VK_M, KeyEvent.VK_V);
		manageStrandsMI.setIcon(I18N.getIcon("icon.medium.manage.strands"));
		strandMenu.add(manageStrandsMI);
	}

	private void addCharactersMenu() {
		// character menu
		JMenu menu = new JMenu();
		menu.setText(I18N.getMsg("msg.menu.persons"));
		I18N.setMnemonic(menu, KeyEvent.VK_A, KeyEvent.VK_F);
		add(menu);

		// new character
		JMenuItem miNewCharacter = new JMenuItem();
		miNewCharacter.setAction(actionRegistry.getAction(SbAction.CHARACTER_NEW));
		miNewCharacter.setText(I18N.getMsg("msg.menu.persons.new"));
		miNewCharacter.setEnabled(isProjectOpen);
		I18N.setMnemonic(miNewCharacter, KeyEvent.VK_N);
		SwingTools.setAccelerator(miNewCharacter, KeyEvent.VK_B,
				KeyEvent.CTRL_MASK);
		miNewCharacter.setIcon(I18N.getIcon("icon.medium.new.person"));
		menu.add(miNewCharacter);

		// manage characters
		JMenuItem miManageCharacters = new JMenuItem();
		miManageCharacters.setAction(actionRegistry.getAction(SbAction.CHARACTER_MANAGE));
		miManageCharacters.setText(I18N.getMsg("msg.menu.persons.manage"));
		miManageCharacters.setEnabled(isProjectOpen);
		I18N.setMnemonic(miManageCharacters, KeyEvent.VK_M);
		SwingTools.setAccelerator(miManageCharacters, KeyEvent.VK_B,
				KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
		miManageCharacters.setIcon(I18N.getIcon("icon.medium.manage.persons"));
		menu.add(miManageCharacters);
		
		menu.add(new JSeparator());
		
		// manage genders
		JMenuItem miGenders = new JMenuItem();
		miGenders.setAction(actionRegistry.getAction(SbAction.GENDER_MANAGE));
		miGenders.setText(I18N.getMsgDot("msg.dlg.mng.genders.title"));
		miGenders.setEnabled(isProjectOpen);
		I18N.setMnemonic(miGenders, KeyEvent.VK_G);
		miGenders.setIcon(I18N.getIcon("icon.medium.manage.genders"));
		menu.add(miGenders);
	}

	private void addLocationsMenu() {
		// location menu
		JMenu locationMenu = new JMenu();
		locationMenu.setText(I18N.getMsg("msg.menu.locations"));
		I18N.setMnemonic(locationMenu, KeyEvent.VK_L, KeyEvent.VK_L);
		add(locationMenu);

		// new location
		JMenuItem miNewLocation = new JMenuItem();
		miNewLocation
				.setAction(actionRegistry.getAction(SbAction.LOCATION_NEW));
		miNewLocation.setText(I18N.getMsg("msg.menu.locations.new"));
		miNewLocation.setEnabled(isProjectOpen);
		I18N.setMnemonic(miNewLocation, KeyEvent.VK_N);
		SwingTools.setAccelerator(miNewLocation, KeyEvent.VK_L,
				KeyEvent.CTRL_MASK);
		miNewLocation.setIcon(I18N.getIcon("icon.medium.new.location"));
		locationMenu.add(miNewLocation);

		// manage locations
		JMenuItem miManageLocations = new JMenuItem();
		miManageLocations.setAction(actionRegistry
				.getAction(SbAction.LOCATION_MANAGE));
		miManageLocations.setText(I18N.getMsg("msg.menu.locations.manage"));
		miManageLocations.setEnabled(isProjectOpen);
		I18N.setMnemonic(miManageLocations, KeyEvent.VK_M, KeyEvent.VK_V);
		SwingTools.setAccelerator(miManageLocations, KeyEvent.VK_L,
				KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
		miManageLocations.setIcon(I18N.getIcon("icon.medium.manage.locations"));
		locationMenu.add(miManageLocations);
		
		locationMenu.add(new JSeparator());
		
		// rename city
		JMenuItem renameCityMI = new JMenuItem();
		renameCityMI.setAction(actionRegistry.getAction(SbAction.LOCATION_RENAME_CITY));
		renameCityMI.setText(I18N.getMsgDot("msg.location.rename.city"));
		renameCityMI.setEnabled(isProjectOpen);
		I18N.setMnemonic(renameCityMI, KeyEvent.VK_C);
		renameCityMI.setIcon(I18N.getIcon("icon.small.rename"));
		locationMenu.add(renameCityMI);
		
		// rename country
		JMenuItem renameCountryMI = new JMenuItem();
		renameCountryMI.setAction(actionRegistry.getAction(SbAction.LOCATION_RENAME_COUNTRY));
		renameCountryMI.setText(I18N.getMsgDot("msg.location.rename.country"));
		renameCountryMI.setEnabled(isProjectOpen);
		I18N.setMnemonic(renameCountryMI, KeyEvent.VK_N);
		renameCountryMI.setIcon(I18N.getIcon("icon.small.rename"));
		locationMenu.add(renameCountryMI);
	}

	private void addTagsMenu() {
		// tag menu
		JMenu tagMenu = new JMenu();
		tagMenu.setText(I18N.getMsg("msg.tags.title"));
		I18N.setMnemonic(tagMenu, KeyEvent.VK_T);
		add(tagMenu);

		// new tag
		JMenuItem miNewTag = new JMenuItem();
		miNewTag.setAction(actionRegistry.getAction(SbAction.TAG_NEW));
		miNewTag.setText(I18N.getMsgDot("msg.tag.new"));
		miNewTag.setEnabled(isProjectOpen);
		I18N.setMnemonic(miNewTag, KeyEvent.VK_N);
		SwingTools.setAccelerator(miNewTag, KeyEvent.VK_T, KeyEvent.CTRL_MASK);
		miNewTag.setIcon(I18N.getIcon("icon.medium.new.tag"));
		tagMenu.add(miNewTag);
		
		// manage tags
		JMenuItem miManageTags = new JMenuItem();
		miManageTags.setAction(actionRegistry.getAction(SbAction.TAG_MANAGE));
		miManageTags.setText(I18N.getMsgDot("msg.tags.manage"));
		miManageTags.setEnabled(isProjectOpen);
		I18N.setMnemonic(miManageTags, KeyEvent.VK_M, KeyEvent.VK_V);
		SwingTools.setAccelerator(miManageTags, KeyEvent.VK_T, KeyEvent.CTRL_MASK
				| KeyEvent.SHIFT_MASK);
		miManageTags.setIcon(I18N.getIcon("icon.medium.manage.tags"));
		tagMenu.add(miManageTags);
		
		// tag assignments
		JMenuItem miTagAssignments = new JMenuItem();
		miTagAssignments.setAction(actionRegistry.getAction(SbAction.TAG_ASSIGNMENTS));
		miTagAssignments.setText(I18N.getMsgDot("msg.tag.assignments"));
		miTagAssignments.setEnabled(isProjectOpen);
		I18N.setMnemonic(miTagAssignments, KeyEvent.VK_A);
		SwingTools.setAccelerator(miTagAssignments, KeyEvent.VK_T,
				KeyEvent.SHIFT_MASK | KeyEvent.ALT_MASK);
		miTagAssignments.setIcon(I18N.getIcon("icon.medium.manage.tag_links"));
		tagMenu.add(miTagAssignments);

		tagMenu.add(new JSeparator());
		
		// rename category
		JMenuItem miRenameCategory = new JMenuItem();
		miRenameCategory.setAction(actionRegistry.getAction(SbAction.TAG_RENAME_CATEGORY));
		miRenameCategory.setText(I18N.getMsgDot("msg.tag.rename.category"));
		miRenameCategory.setEnabled(isProjectOpen);
		I18N.setMnemonic(miRenameCategory, KeyEvent.VK_R);
		miRenameCategory.setIcon(I18N.getIcon("icon.small.rename"));
		tagMenu.add(miRenameCategory);
		
		tagMenu.add(new JSeparator());
		
		// tag collections
		JMenuItem miTagCollections = new JMenuItem();
		miTagCollections.setAction(actionRegistry.getAction(SbAction.TAG_COLLECTIONS));
		miTagCollections.setText(I18N.getMsgDot("msg.tag.collections"));
		miTagCollections.setEnabled(isProjectOpen);
		I18N.setMnemonic(miTagCollections, KeyEvent.VK_C);
		tagMenu.add(miTagCollections);
	}
	
	private void addItemsMenu() {
		// item menu
		JMenu itemMenu = new JMenu();
		itemMenu.setText(I18N.getMsg("msg.items.title"));
		I18N.setMnemonic(itemMenu, KeyEvent.VK_I);
		add(itemMenu);

		// new item
		JMenuItem miNewItem = new JMenuItem();
		miNewItem.setAction(actionRegistry.getAction(SbAction.ITEM_NEW));
		miNewItem.setText(I18N.getMsgDot("msg.item.new"));
		miNewItem.setIcon(I18N.getIcon("icon.medium.new.item"));
		miNewItem.setEnabled(isProjectOpen);
		I18N.setMnemonic(miNewItem, KeyEvent.VK_N);
		SwingTools.setAccelerator(miNewItem, KeyEvent.VK_I, KeyEvent.CTRL_MASK);
		itemMenu.add(miNewItem);

		// manage items
		JMenuItem miManageItems = new JMenuItem();
		miManageItems.setAction(actionRegistry.getAction(SbAction.ITEM_MANAGE));
		miManageItems.setText(I18N.getMsgDot("msg.items.manage"));
		miManageItems.setIcon(I18N.getIcon("icon.medium.manage.items"));
		miManageItems.setEnabled(isProjectOpen);
		I18N.setMnemonic(miManageItems, KeyEvent.VK_M, KeyEvent.VK_V);
		SwingTools.setAccelerator(miManageItems, KeyEvent.VK_I, KeyEvent.CTRL_MASK
				| KeyEvent.SHIFT_MASK);
		itemMenu.add(miManageItems);

		// item assignments
		JMenuItem miItemAssignments = new JMenuItem();
		miItemAssignments.setAction(actionRegistry
				.getAction(SbAction.ITEM_ASSIGNMENTS));
		miItemAssignments.setText(I18N.getMsgDot("msg.item.assignments"));
		miItemAssignments
				.setIcon(I18N.getIcon("icon.medium.manage.item_links"));
		miItemAssignments.setEnabled(isProjectOpen);
		I18N.setMnemonic(miItemAssignments, KeyEvent.VK_A);
		SwingTools.setAccelerator(miItemAssignments, KeyEvent.VK_I,
				KeyEvent.SHIFT_MASK | KeyEvent.ALT_MASK);
		itemMenu.add(miItemAssignments);

		itemMenu.add(new JSeparator());

		// rename category
		JMenuItem miRenameCategory = new JMenuItem();
		miRenameCategory.setAction(actionRegistry
				.getAction(SbAction.ITEM_RENAME_CATEGORY));
		miRenameCategory.setText(I18N.getMsgDot("msg.item.rename.category"));
		miRenameCategory.setEnabled(isProjectOpen);
		I18N.setMnemonic(miRenameCategory, KeyEvent.VK_R);
		miRenameCategory.setIcon(I18N.getIcon("icon.small.rename"));
		itemMenu.add(miRenameCategory);

		itemMenu.add(new JSeparator());

		// item collections
		JMenuItem miItemCollections = new JMenuItem();
		miItemCollections.setAction(actionRegistry
				.getAction(SbAction.ITEM_COLLECTIONS));
		miItemCollections.setText(I18N.getMsgDot("msg.item.collections"));
		miItemCollections.setEnabled(isProjectOpen);
		I18N.setMnemonic(miItemCollections, KeyEvent.VK_C);
		itemMenu.add(miItemCollections);
	}

	private void addFileMenu() {
		// file menu
		JMenu fileMenu = new JMenu();
		add(fileMenu);
		fileMenu.setText(I18N.getMsg("msg.menu.file"));
		I18N.setMnemonic(fileMenu, KeyEvent.VK_F, KeyEvent.VK_D);

		// new file
		JMenuItem miNewFile = new JMenuItem();
		miNewFile.setAction(actionRegistry.getAction(SbAction.FILE_NEW));
		miNewFile.setText(I18N.getMsg("msg.file.new"));
		I18N.setMnemonic(miNewFile, KeyEvent.VK_O);
		SwingTools.setAccelerator(miNewFile, KeyEvent.VK_N, Event.CTRL_MASK);
		miNewFile.setIcon(I18N.getIcon("icon.small.open"));
		fileMenu.add(miNewFile);
		
		// create demo file
		JMenuItem miCreateDemo = new JMenuItem();
		miCreateDemo.setAction(actionRegistry.getAction(SbAction.FILE_CREATE_DEMO));
		miCreateDemo.setText(I18N.getMsgDot("msg.file.create.demo"));
		fileMenu.add(miCreateDemo);
		
		fileMenu.add(new JSeparator());
		
		// open file
		JMenuItem miopenFile = new JMenuItem();
		miopenFile.setAction(actionRegistry.getAction(SbAction.FILE_OPEN));
		miopenFile.setText(I18N.getMsg("msg.file.open"));
		I18N.setMnemonic(miopenFile, KeyEvent.VK_O);
		SwingTools.setAccelerator(miopenFile, KeyEvent.VK_O, Event.CTRL_MASK);
		miopenFile.setIcon(I18N.getIcon("icon.small.open"));
		fileMenu.add(miopenFile);

		// recent files
		JMenu openRecentMenu = new JMenu();
		openRecentMenu.setText(I18N.getMsg("msg.file.open.recent"));
		I18N.setMnemonic(openRecentMenu, KeyEvent.VK_R);
		fileMenu.add(openRecentMenu);
		
		ArrayList<String> files = PrefManager.toStringList(PrefManager
				.getInstance().getStringValue(Preference.RECENT_FILES));
		Collections.reverse(files);
		Iterator<String> i = files.iterator();
		while (i.hasNext()) {
			String fileName = i.next();
			JMenuItem mi = new JMenuItem();
			AbstractAction action = ActionManager.getFileOpenRecentAction();
			action.putValue(ActionKey.FILE.toString(), fileName);
			mi.setAction(action);
			File file = new File(fileName);
			mi.setText(file.getName() + " ["
					+ FilenameUtils.getPath(file.getPath()) + "]");
			openRecentMenu.add(mi);
		}
		
		openRecentMenu.add(new JSeparator());

		// clear recent files
		JMenuItem miClearRecent = new JMenuItem();
		miClearRecent.setAction(actionRegistry.getAction(SbAction.FILE_CLEAR_RECENT));
		miClearRecent.setText(I18N.getMsg("msg.file.clear.recent"));
		openRecentMenu.add(miClearRecent);
		
		fileMenu.add(new JSeparator());

		// save file
		JMenuItem miSaveFile = new JMenuItem();
		miSaveFile.setAction(actionRegistry.getAction(SbAction.FILE_SAVE));
		miSaveFile.setText(I18N.getMsg("msg.file.save"));
		miSaveFile.setEnabled(isProjectOpen);
		I18N.setMnemonic(miSaveFile, KeyEvent.VK_S);
		SwingTools.setAccelerator(miSaveFile, KeyEvent.VK_S, Event.CTRL_MASK);
		miSaveFile.setIcon(I18N.getIcon("icon.small.save"));
		fileMenu.add(miSaveFile);

		// save file as
		JMenuItem miSaveFileAs = new JMenuItem();
		miSaveFileAs.setAction(actionRegistry.getAction(SbAction.FILE_SAVE_AS));
		miSaveFileAs.setText(I18N.getMsg("msg.file.save.as"));
		miSaveFileAs.setEnabled(isProjectOpen);
		miSaveFileAs.setIcon(I18N.getIcon("icon.small.save.as"));
		fileMenu.add(miSaveFileAs);

		// rename file
		JMenuItem miRenameFile = new JMenuItem();
		miRenameFile.setAction(actionRegistry.getAction(SbAction.FILE_RENAME));
		miRenameFile.setText(I18N.getMsgDot("msg.common.project.rename"));
		miRenameFile.setEnabled(isProjectOpen);
		miRenameFile.setIcon(I18N.getIcon("icon.small.rename"));
		SwingTools.setAccelerator(miRenameFile, KeyEvent.VK_R, Event.CTRL_MASK);
		fileMenu.add(miRenameFile);
		
		fileMenu.add(new JSeparator());
		
		// export and print
		JMenuItem miPrint = new JMenuItem();
		miPrint.setAction(actionRegistry.getAction(SbAction.EXPORT_PRINT));
		miPrint.setText(I18N.getMsg("msg.menu.file.export_print"));
		miPrint.setEnabled(isProjectOpen);
		I18N.setMnemonic(miPrint, KeyEvent.VK_P);
		SwingTools.setAccelerator(miPrint, KeyEvent.VK_P, Event.CTRL_MASK);
		miPrint.setIcon(I18N.getIcon("icon.small.print"));
		fileMenu.add(miPrint);

		// raw export
		JMenuItem miRawExport = new JMenuItem();
		miRawExport.setAction(actionRegistry.getAction(SbAction.RAW_EXPORT));
		miRawExport.setText(I18N.getMsgDot("msg.export.raw"));
		miRawExport.setEnabled(isProjectOpen);
		I18N.setMnemonic(miRawExport, KeyEvent.VK_E);
		SwingTools.setAccelerator(miRawExport, KeyEvent.VK_E, Event.CTRL_MASK);
		miRawExport.setIcon(I18N.getIcon("icon.small.export"));
		fileMenu.add(miRawExport);
		
		fileMenu.add(new JSeparator());

		// file information
		JMenuItem miFileInfo = new JMenuItem();
		miFileInfo.setAction(actionRegistry.getAction(SbAction.FILE_INFO));
		miFileInfo.setText(I18N.getMsgDot("msg.file.info"));
		miFileInfo.setEnabled(isProjectOpen);
		fileMenu.add(miFileInfo);
		
		fileMenu.add(new JSeparator());
		
		// close file
		JMenuItem miCloseFile = new JMenuItem();
		miCloseFile.setAction(actionRegistry.getAction(SbAction.FILE_CLOSE));
		miCloseFile.setText(I18N.getMsg("msg.file.close"));
		miCloseFile.setIcon(I18N.getIcon("icon.small.close"));
		miCloseFile.setEnabled(isProjectOpen);
		I18N.setMnemonic(miCloseFile, KeyEvent.VK_C);
		SwingTools.setAccelerator(miCloseFile, KeyEvent.VK_W, Event.CTRL_MASK);
		fileMenu.add(miCloseFile);	
		
		fileMenu.add(new JSeparator());
		
		// preferences
		JMenuItem miPref = new JMenuItem();
		miPref.setAction(actionRegistry.getAction(SbAction.PREFERENCES));
		miPref.setText(I18N.getMsg("msg.menu.file.preferences"));
		miPref.setIcon(I18N.getIcon("icon.small.preferences"));
		I18N.setMnemonic(miPref, KeyEvent.VK_P, KeyEvent.VK_E);
		fileMenu.add(miPref);

		fileMenu.add(new JSeparator());

		// exit
		JMenuItem miExit = new JMenuItem();
		miExit.setAction(actionRegistry.getAction(SbAction.EXIT));
		miExit.setText(I18N.getMsg("msg.common.exit"));
		miExit.setIcon(I18N.getIcon("icon.small.exit"));
		I18N.setMnemonic(miExit, KeyEvent.VK_E, KeyEvent.VK_B);
		SwingTools.setAccelerator(miExit, KeyEvent.VK_Q, Event.CTRL_MASK);
		fileMenu.add(miExit);
	}

	private void addEditMenu() {
		// edit menu
		JMenu editMenu = new JMenu();
		editMenu.setText(I18N.getMsg("msg.menu.edit"));
		I18N.setMnemonic(editMenu, KeyEvent.VK_E, KeyEvent.VK_E);
		add(editMenu);
		refreshEditMenu();
	}

	private void addTranslatorMenu() {
		// help menu
		JMenu helpMenu = new JMenu();
		helpMenu.setText("Translator Tools");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		add(helpMenu);

		// refresh property files
		JMenuItem refreshMI = new JMenuItem();
		AbstractAction action = actionRegistry.getAction(SbAction.REFRESH);
		refreshMI.setAction(action);
		refreshMI.setText("Refresh Localization Files");
		helpMenu.add(refreshMI);
		
		// run Attesoro
		JMenuItem attesoroMI = new JMenuItem();
		action = actionRegistry.getAction(SbAction.RUN_ATTESORO);
		attesoroMI.setAction(action);
		attesoroMI.setText("Run Attesoro");
		helpMenu.add(attesoroMI);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (PCSDispatcher.isPropertyFired(Property.PART, evt)) {
			refreshPartMenu();
			return;
		}

		if (PCSDispatcher.isPropertyFired(Property.PROJECT, evt)) {
			refresh();
			return;
		}

		if (PCSDispatcher.isPropertyFired(Property.ACTIVE_PART, evt)) {
			refreshPartMenu();
			return;
		}

		if (PCSDispatcher.isPropertyFired(Property.VIEW, evt)) {
			refreshViewMenu();
			return;
		}
		
		if (PCSDispatcher.isPropertyFired(Property.REFRESH_ALL, evt)) {
			theInstance = null;
			refresh();
			return;
		}
	}
	
	private void refreshPartMenu() {
		for (Component comp : getComponents()) {
			if (COMP_NAME_PART_MENU.equals(comp.getName())) {
				Component menu = SwingTools.findComponentByName(this,
						COMP_NAME_PART_MENU);
				int index = getComponentIndex(menu);
				remove(index);
				add(createPartMenu(), index);
				validate();
			}
		}
	}
	
	private void refreshViewMenu() {
		for (Component comp : getComponents()) {
			if (COMP_NAME_VIEW_MENU.equals(comp.getName())) {
				Component menu = SwingTools.findComponentByName(this,
						COMP_NAME_VIEW_MENU);
				int index = getComponentIndex(menu);
				remove(index);
				add(createViewMenu(), index);
				validate();
			}
		}
	}
}
