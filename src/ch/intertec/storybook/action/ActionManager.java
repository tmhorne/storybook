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

package ch.intertec.storybook.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdesktop.jdic.desktop.Desktop;

import ch.intertec.storybook.action.AbstractTableAction.ActionKey;
import ch.intertec.storybook.jasper.ExportPrintDialog;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.main.MainMenuBar;
import ch.intertec.storybook.main.MainSplitPane.ContentPanelType;
import ch.intertec.storybook.main.sidebar.Sidebar;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.PrefManager;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.Constants.Preference;
import ch.intertec.storybook.toolkit.net.Updater;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.ViewTools;
import ch.intertec.storybook.view.assignments.ItemAssignmentsFrame;
import ch.intertec.storybook.view.assignments.TagAssignmentsFrame;
import ch.intertec.storybook.view.collections.ItemCollectionsDialog;
import ch.intertec.storybook.view.collections.TagCollectionsDialog;
import ch.intertec.storybook.view.dialog.AboutDialog;
import ch.intertec.storybook.view.dialog.FileInfoDialog;
import ch.intertec.storybook.view.dialog.GenerateChaptersDialog;
import ch.intertec.storybook.view.dialog.PreferenceDialog;
import ch.intertec.storybook.view.dialog.RawExportDialog;
import ch.intertec.storybook.view.dialog.TaskListFrame;
import ch.intertec.storybook.view.ideas.FoiDialog;
import ch.intertec.storybook.view.ideas.IdeasFrame;
import ch.intertec.storybook.view.lists.ChapterListFrame;
import ch.intertec.storybook.view.lists.CharacterListFrame;
import ch.intertec.storybook.view.lists.GenderListFrame;
import ch.intertec.storybook.view.lists.ItemListFrame;
import ch.intertec.storybook.view.lists.LocationListFrame;
import ch.intertec.storybook.view.lists.PartListFrame;
import ch.intertec.storybook.view.lists.StrandListFrame;
import ch.intertec.storybook.view.lists.TagListFrame;
import ch.intertec.storybook.view.memoria.MemoriaFrame;
import ch.intertec.storybook.view.modify.ChapterDialog;
import ch.intertec.storybook.view.modify.CharacterDialog;
import ch.intertec.storybook.view.modify.ItemDialog;
import ch.intertec.storybook.view.modify.LocationDialog;
import ch.intertec.storybook.view.modify.PartDialog;
import ch.intertec.storybook.view.modify.StrandDialog;
import ch.intertec.storybook.view.modify.TagDialog;
import ch.intertec.storybook.view.modify.scene.SceneDialog;
import ch.intertec.storybook.view.navigation.FindChapterDialog;
import ch.intertec.storybook.view.navigation.FindDateDialog;
import ch.intertec.storybook.view.net.GoogleMapsDialog;
import ch.intertec.storybook.view.rename.RenameItemCategoryDialog;
import ch.intertec.storybook.view.rename.RenameLocationCityDialog;
import ch.intertec.storybook.view.rename.RenameLocationCountryDialog;
import ch.intertec.storybook.view.rename.RenameTagCategoryDialog;

@SuppressWarnings("serial")
public class ActionManager {

	public static final String CHAPTER_KEY = "chapter";
	
	private static MainFrame mainFrame;
	
	public static enum SbAction {
		FILE_NEW, FILE_OPEN, FILE_CREATE_DEMO, FILE_CLEAR_RECENT,
		FILE_CLOSE, FILE_SAVE, FILE_SAVE_AS, FILE_RENAME,
		FILE_INFO,
		PREFERENCES, EXPORT_PRINT, RAW_EXPORT, GOOGLE_MAPS,
		CHAPTER_NEW, CHAPTER_MANAGE, CHAPTER_GENERATE,
		PART_NEW, PART_MANAGE,
		STRAND_NEW, STRAND_MANAGE,
		SCENE_NEW, SCENE_RENUMBER,
		CHARACTER_NEW, CHARACTER_MANAGE, GENDER_MANAGE,
		LOCATION_NEW, LOCATION_MANAGE, LOCATION_RENAME_CITY, LOCATION_RENAME_COUNTRY,
		TAG_NEW, TAG_MANAGE, TAG_ASSIGNMENTS, TAG_COLLECTIONS, TAG_RENAME_CATEGORY,
		ITEM_NEW, ITEM_MANAGE, ITEM_ASSIGNMENTS, ITEM_COLLECTIONS, ITEM_RENAME_CATEGORY,
		VIEW_BOOK, VIEW_CHRONO, VIEW_MANAGE, MEMORIA,
		SIDEBAR_TOGGLE, REFRESH,
		SCROLL_TO_CHAPTER_OR_SCENE,
		SHOW_IN_CHRONO_VIEW, SHOW_IN_BOOK_VIEW, SHOW_IN_MANAGE_VIEW, SHOW_IN_MEMORIA,
		GOTO_CHAPTER_DLG, GOTO_DATE_DLG, TASK_LIST,
		UPDATE, ABOUT, EXIT, MANUAL,
		RUN_ATTESORO,
		IDEAS, IDEAS_FOI,
		MENU_REFRESH_EDIT;
	}
	
	public static void init(){
		ActionRegistry ar = ActionRegistry.getInstance();
		ar.clear();
		mainFrame = MainFrame.getInstance();
		
		// register actions
		ar.addAction(SbAction.FILE_NEW, getFileNewAction());
		ar.addAction(SbAction.FILE_OPEN, getFileOpenAction());
		ar.addAction(SbAction.FILE_CREATE_DEMO, getFileCreateDemoAction());
		ar.addAction(SbAction.FILE_CLEAR_RECENT, getFileClearRecentAction());
		ar.addAction(SbAction.FILE_CLOSE, getFileCloseAction());
		ar.addAction(SbAction.FILE_SAVE, getFileSaveAction());
		ar.addAction(SbAction.FILE_SAVE_AS, getFileSaveAsAction());
		ar.addAction(SbAction.FILE_RENAME, getFileRenameAction());
		ar.addAction(SbAction.FILE_INFO, getFileInfoAction());
		ar.addAction(SbAction.PREFERENCES, getPreferencesAction());
		ar.addAction(SbAction.EXPORT_PRINT, getExportAction());
		ar.addAction(SbAction.RAW_EXPORT, getRawExportAction());
		ar.addAction(SbAction.GOOGLE_MAPS, getGoogleMapsAction());
		ar.addAction(SbAction.CHAPTER_NEW, getChapterNewAction());
		ar.addAction(SbAction.CHAPTER_MANAGE, getChapterManageAction());
		ar.addAction(SbAction.CHAPTER_GENERATE, getChapterGenerateAction());
		ar.addAction(SbAction.PART_NEW, getPartNewAction());
		ar.addAction(SbAction.PART_MANAGE, getPartManageAction());
		ar.addAction(SbAction.STRAND_NEW, getStrandNewAction());
		ar.addAction(SbAction.STRAND_MANAGE, getStrandManageAction());
		ar.addAction(SbAction.SCENE_NEW, getSceneNewAction());
		ar.addAction(SbAction.SCENE_RENUMBER, getSceneRenumberAction());
		ar.addAction(SbAction.CHARACTER_NEW, getCharacterNewAction());
		ar.addAction(SbAction.CHARACTER_MANAGE, getCharacterManageAction());
		ar.addAction(SbAction.GENDER_MANAGE, getGenderManageAction());
		ar.addAction(SbAction.LOCATION_NEW, getLocationNewAction());
		ar.addAction(SbAction.LOCATION_MANAGE, getLocationManageAction());
		ar.addAction(SbAction.LOCATION_RENAME_CITY, getLocationRenameCityAction());
		ar.addAction(SbAction.LOCATION_RENAME_COUNTRY, getLocationRenameCountryAction());
		ar.addAction(SbAction.TAG_NEW, getTagNewAction());
		ar.addAction(SbAction.TAG_MANAGE, getTagManageAction());
		ar.addAction(SbAction.TAG_ASSIGNMENTS, getTagAssignmentsAction());
		ar.addAction(SbAction.TAG_COLLECTIONS, getTagCollectionsAction());
		ar.addAction(SbAction.TAG_RENAME_CATEGORY, getTagRenameCategoryAction());
		ar.addAction(SbAction.ITEM_NEW, getItemNewAction());
		ar.addAction(SbAction.ITEM_MANAGE, getItemManageAction());
		ar.addAction(SbAction.ITEM_ASSIGNMENTS, getItemAssignmentsAction());
		ar.addAction(SbAction.ITEM_COLLECTIONS, getItemCollectionsAction());
		ar.addAction(SbAction.ITEM_RENAME_CATEGORY, getItemRenameCategoryAction());		
		ar.addAction(SbAction.VIEW_BOOK, getViewBookAction());
		ar.addAction(SbAction.VIEW_CHRONO, getViewChronoAction());
		ar.addAction(SbAction.VIEW_MANAGE, getViewManageAction());
		ar.addAction(SbAction.MEMORIA, getMemoriaAction());
		ar.addAction(SbAction.SIDEBAR_TOGGLE, getSidebarToggleAction());
		ar.addAction(SbAction.REFRESH, getRefreshAction());
		ar.addAction(SbAction.SCROLL_TO_CHAPTER_OR_SCENE, getScrollToChapterOrSceneAction());
		ar.addAction(SbAction.SHOW_IN_CHRONO_VIEW, getShowInChronoViewAction());
		ar.addAction(SbAction.SHOW_IN_BOOK_VIEW, getShowInBookViewAction());
		ar.addAction(SbAction.SHOW_IN_MANAGE_VIEW, getShowInManageViewAction());
		ar.addAction(SbAction.SHOW_IN_MEMORIA, getShowInMemoriaAction());		
		ar.addAction(SbAction.GOTO_CHAPTER_DLG, getGotoChapterDlgAction());
		ar.addAction(SbAction.GOTO_DATE_DLG, getGotoDateDlgAction());
		ar.addAction(SbAction.TASK_LIST, getTaskListAction());
		ar.addAction(SbAction.UPDATE, getUpdateAction());
		ar.addAction(SbAction.ABOUT, getAboutAction());
		ar.addAction(SbAction.EXIT, getExitAction());
		ar.addAction(SbAction.MANUAL, getManualAction());
		ar.addAction(SbAction.RUN_ATTESORO, getRunAttesoroAction());
		ar.addAction(SbAction.IDEAS, getIdeasAction());
		ar.addAction(SbAction.IDEAS_FOI, getFOIAction());
		ar.addAction(SbAction.MENU_REFRESH_EDIT, getMenuRefreshEditAction());
	}

	private static AbstractAction getFileNewAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				ProjectTools.createNewFile();
			}
		};
	}
	
	private static AbstractAction getFileOpenAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				ProjectTools.openFile();
			}
		};
	}

	public static AbstractAction getFileOpenRecentAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				Object fileName = getValue(Constants.ActionKey.FILE.toString());
				if (fileName == null) {
					return;
				}
				ProjectTools.openFile((String)fileName);
			}
		};
	}
	
	private static AbstractAction getFileClearRecentAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				try {
					PrefManager.getInstance().setValue(Preference.RECENT_FILES,
							"");
					PCSDispatcher.getInstance().firePropertyChange(
							PCSDispatcher.Property.REFRESH_ALL.toString(), true,
							null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	private static AbstractAction getFileCreateDemoAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				ProjectTools.createDemoFile();
			}
		};
	}

	private static AbstractAction getFileCloseAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				// SwingTools.disposeWindows();
				SwingTools.disposeOpenedDialogs();
				ProjectTools.closeFile();
			}
		};
	}
	
	private static AbstractAction getFileSaveAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				ProjectTools.saveFile();
			}
		};
	}

	private static AbstractAction getFileSaveAsAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				ProjectTools.saveFileAs();
			}
		};
	}

	private static AbstractAction getFileRenameAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				ProjectTools.renameFile();
			}
		};
	}
	
	private static AbstractAction getFileInfoAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				FileInfoDialog dlg = new FileInfoDialog();
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};
	}

	private static AbstractAction getPreferencesAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				PreferenceDialog dlg = new PreferenceDialog();
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};
	}
	
	private static AbstractAction getExportAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				SwingTools.showDialog(new ExportPrintDialog(mainFrame),
						mainFrame);
			}
		};
	}

	private static AbstractAction getRawExportAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				SwingTools.showDialog(
						new RawExportDialog(mainFrame), mainFrame);
			}
		};
	}

	private static AbstractAction getGoogleMapsAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				SwingTools.showDialog(
						new GoogleMapsDialog(), mainFrame);
			}
		};
	}	
	
	private static AbstractAction getChapterNewAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				ChapterDialog dlg = new ChapterDialog(mainFrame);
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};

	}

	private static AbstractAction getChapterManageAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (!SwingTools.showWindowIfOpened(ChapterListFrame.class)) {
					ChapterListFrame dlg = new ChapterListFrame();
					SwingTools.showFrame(dlg, mainFrame);
				}
			}
		};
	}
	
	private static AbstractAction getChapterGenerateAction() {
		return new AbstractAction(I18N.getMsg("msg.generate.chapters")) {
			public void actionPerformed(ActionEvent evt) {
				SwingTools.showModalDialog(
						new GenerateChaptersDialog(mainFrame), mainFrame);
			}
		};
	}
	
	private static AbstractAction getPartNewAction(){
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				PartDialog dlg = new PartDialog(mainFrame);
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};
	}
	
	private static AbstractAction getPartManageAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (!SwingTools.showWindowIfOpened(PartListFrame.class)) {
					PartListFrame dlg = new PartListFrame();
					SwingTools.showFrame(dlg, mainFrame);
				}
				int activePartId = mainFrame.getActivePartId();
				if (!PartPeer.checkIfPartIsValid(activePartId)) {
					activePartId = PartPeer.getFirstPart().getId();
				}
			}
		};
	}

	private static AbstractAction getStrandNewAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				StrandDialog dlg = new StrandDialog(mainFrame);
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};
	}

	private static AbstractAction getStrandManageAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (!SwingTools.showWindowIfOpened(StrandListFrame.class)) {
					StrandListFrame dlg = new StrandListFrame();
					SwingTools.showFrame(dlg, mainFrame);
				}
			}
		};
	}
	
	private static AbstractAction getSceneNewAction(){
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				// check if at least one strand if defined
				if (StrandPeer.doCount() == 0) {
					JOptionPane.showMessageDialog(
							mainFrame,
							I18N.getMsg("msg.dlg.no.strand.text"),
							I18N.getMsg("msg.dlg.no.strand.title"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				SceneDialog dlg = new SceneDialog(mainFrame);
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};
	}
	
	private static AbstractAction getSceneRenumberAction(){
		return new AbstractAction(
				I18N.getMsg("msg.renumber.scenes")) {
			public void actionPerformed(ActionEvent evt) {
				Chapter chapter = (Chapter) getValue(CHAPTER_KEY);
				ChapterPeer.renumberScenes(chapter);
			}
		};
	}
	
	private static AbstractAction getCharacterNewAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				CharacterDialog dlg = new CharacterDialog(mainFrame);
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};
	}

	private static AbstractAction getCharacterManageAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (!SwingTools.showWindowIfOpened(CharacterListFrame.class)) {
					CharacterListFrame dlg = new CharacterListFrame();
					SwingTools.showFrame(dlg, mainFrame);
				}
			}
		};
	}

	private static AbstractAction getGenderManageAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (!SwingTools.showWindowIfOpened(GenderListFrame.class)) {
					GenderListFrame dlg = new GenderListFrame();
					SwingTools.showFrame(dlg, mainFrame);
				}
			}
		};
	}

	private static AbstractAction getLocationNewAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				LocationDialog dlg = new LocationDialog(mainFrame);
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};
	}

	private static AbstractAction getLocationManageAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (!SwingTools.showWindowIfOpened(LocationListFrame.class)) {
					LocationListFrame dlg = new LocationListFrame();
					SwingTools.showFrame(dlg, mainFrame);
				}
			}
		};
	}

	private static AbstractAction getLocationRenameCityAction() {
		return new AbstractAction(I18N.getMsgDot("msg.location.rename.city"),
				I18N.getIcon("icon.small.rename")) {
			public void actionPerformed(ActionEvent evt) {
				RenameLocationCityDialog dlg = new RenameLocationCityDialog(mainFrame);
				if (getValue(ActionKey.CITY.toString()) != null) {
					dlg.setValue((String) getValue(ActionKey.CITY.toString()));
				}
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};
	}

	private static AbstractAction getLocationRenameCountryAction() {
		return new AbstractAction(
				I18N.getMsgDot("msg.location.rename.country"),
				I18N.getIcon("icon.small.rename")) {
			public void actionPerformed(ActionEvent evt) {
				RenameLocationCountryDialog dlg = new RenameLocationCountryDialog(mainFrame);
				if (getValue(ActionKey.COUNTRY.toString()) != null) {
					dlg.setValue((String) getValue(ActionKey.COUNTRY.toString()));
				}
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};
	}

	private static AbstractAction getTagNewAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				TagDialog dlg = new TagDialog(mainFrame);
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};
	}

	private static AbstractAction getItemNewAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				ItemDialog dlg = new ItemDialog(mainFrame);
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};
	}

	private static AbstractAction getTagManageAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (!SwingTools.showWindowIfOpened(TagListFrame.class)) {
					TagListFrame frame = new TagListFrame();
					SwingTools.showFrame(frame, mainFrame);
				}
			}
		};
	}

	private static AbstractAction getItemManageAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (!SwingTools.showWindowIfOpened(ItemListFrame.class)) {
					ItemListFrame frame = new ItemListFrame();
					SwingTools.showFrame(frame, mainFrame);
				}
			}
		};
	}

	private static AbstractAction getTagAssignmentsAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (!SwingTools.showWindowIfOpened(TagAssignmentsFrame.class)) {
					TagAssignmentsFrame frame = new TagAssignmentsFrame();
					SwingTools.showFrame(frame, mainFrame);
				}
			}
		};
	}

	private static AbstractAction getItemAssignmentsAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (!SwingTools.showWindowIfOpened(ItemAssignmentsFrame.class)) {
					ItemAssignmentsFrame frame = new ItemAssignmentsFrame();
					SwingTools.showFrame(frame, mainFrame);
				}
			}
		};
	}

	private static AbstractAction getTagCollectionsAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				SwingTools.showModalDialog(new TagCollectionsDialog(),
						mainFrame);
			}
		};
	}

	private static AbstractAction getItemCollectionsAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				SwingTools.showModalDialog(new ItemCollectionsDialog(),
						mainFrame);
			}
		};
	}

	private static AbstractAction getTagRenameCategoryAction() {
		return new AbstractAction(I18N.getMsg("msg.tag.rename.category"),
				I18N.getIcon("icon.small.rename")) {
			public void actionPerformed(ActionEvent evt) {
				RenameTagCategoryDialog dlg = new RenameTagCategoryDialog(
						mainFrame);
				if (getValue(ActionKey.CATEGORY.toString()) != null) {
					dlg.setValue((String) getValue(ActionKey.CATEGORY
							.toString()));
				}
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};
	}

	private static AbstractAction getItemRenameCategoryAction() {
		return new AbstractAction(I18N.getMsg("msg.item.rename.category"),
				I18N.getIcon("icon.small.rename")) {
			public void actionPerformed(ActionEvent evt) {
				RenameItemCategoryDialog dlg = new RenameItemCategoryDialog(
						mainFrame);
				if (getValue(ActionKey.CATEGORY.toString()) != null) {
					dlg.setValue((String) getValue(ActionKey.CATEGORY
							.toString()));
				}
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};
	}

	private static AbstractAction getViewBookAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (mainFrame.isBookPanelActive()) {
					return;
				}
				SwingTools.setWaitCursor();
				ContentPanelType old = mainFrame.getContentPanelType();
				mainFrame.setBookPanel();
				PCSDispatcher.getInstance().firePropertyChange(
						PCSDispatcher.Property.VIEW, old,
						ContentPanelType.BOOK);
				getMenuRefreshEditAction().actionPerformed(null);
				SwingTools.setDefaultCursor();
			}
		};
	}

	private static AbstractAction getViewChronoAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (mainFrame.isChronoPanelActive()) {
					return;
				}
				SwingTools.setWaitCursor();
				ContentPanelType old = mainFrame.getContentPanelType();
				mainFrame.setChronoPanel();
				PCSDispatcher.getInstance().firePropertyChange(
						PCSDispatcher.Property.VIEW, old,
						ContentPanelType.CHRONO);
				getMenuRefreshEditAction().actionPerformed(null);
				SwingTools.setDefaultCursor();
			}
		};
	}
	
	private static AbstractAction getViewManageAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (mainFrame.isManagePanelActive()) {
					return;
				}
				SwingTools.setWaitCursor();
				ContentPanelType old = mainFrame.getContentPanelType();
				mainFrame.setManagePanel();
				PCSDispatcher.getInstance().firePropertyChange(
						PCSDispatcher.Property.VIEW, old,
						ContentPanelType.MANAGE);
				getMenuRefreshEditAction().actionPerformed(null);
				SwingTools.setDefaultCursor();
			}
		};
	}

	private static AbstractAction getSidebarToggleAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				mainFrame.getSplitPane().toggleDivider();
			}
		};
	}
	
	private static AbstractAction getRefreshAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (MainFrame.getInstance().isInTranslatorMode()) {
					I18N.initResourceBundles();
				}
				PCSDispatcher pcs = PCSDispatcher.getInstance();
				pcs.firePropertyChange(Property.REFRESH_ALL, null, null);
			}
		};
	}

	private static AbstractAction getScrollToChapterOrSceneAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				Object value = getValue(
						Constants.ActionKey.CHAPTER_OR_SCENE.toString());
				if (value == null) {
					return;
				}
				ViewTools.scrollToChapterOrScene((DbTable) value);
			}
		};
	}
	
	private static AbstractAction getShowInMemoriaAction() {
		AbstractAction action = new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (!MemoriaFrame.checkIfAvailable()) {
					return;
				}
				Object value = getValue(Constants.ActionKey.MEMORIA_DBOBJ
						.toString());
				if (value == null || !(value instanceof DbTable)) {
					return;
				}
				MemoriaFrame frame = new MemoriaFrame();
				frame.getMemoriaPanel().setDbObj((DbTable) value);
				SwingTools.showFrame(frame, mainFrame);
			}
		};
		action.putValue(AbstractAction.NAME,
				I18N.getMsg("msg.show.in.memoria"));
		action.putValue(AbstractAction.SMALL_ICON,
				I18N.getIcon("icon.small.memoria"));
		return action;
	}

	private static AbstractAction getShowInChronoViewAction() {
		AbstractAction action = new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				Object value = getValue(Constants.ActionKey.CHAPTER_OR_SCENE.toString());
				if (value == null) {
					return;
				}
				MainFrame.getInstance().setVisible(true);
				getViewChronoAction().actionPerformed(evt);
				ViewTools.scrollToChapterOrScene((DbTable)value);
			}
		};
		action.putValue(AbstractAction.NAME,
				I18N.getMsg("msg.show.in.chrono.view"));
		action.putValue(AbstractAction.SMALL_ICON,
				I18N.getIcon("icon.small.arrow.right"));
		return action;
	}

	private static AbstractAction getShowInBookViewAction() {
		AbstractAction action = new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				Object value = getValue(Constants.ActionKey.CHAPTER_OR_SCENE.toString());
				if (value == null) {
					return;
				}
				MainFrame.getInstance().setVisible(true);
				getViewBookAction().actionPerformed(evt);
				ViewTools.scrollToChapterOrScene((DbTable)value);
			}
		};
		action.putValue(AbstractAction.NAME,
				I18N.getMsg("msg.show.in.book.view"));
		action.putValue(AbstractAction.SMALL_ICON,
				I18N.getIcon("icon.small.arrow.right"));
		return action;
	}
	
	private static AbstractAction getShowInManageViewAction() {
		AbstractAction action = new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				Object value = getValue(Constants.ActionKey.CHAPTER_OR_SCENE.toString());
				if (value == null) {
					return;
				}
				MainFrame.getInstance().setVisible(true);
				getViewManageAction().actionPerformed(evt);
				ViewTools.scrollToChapterOrScene((DbTable)value);
			}
		};
		action.putValue(AbstractAction.NAME,
				I18N.getMsg("msg.show.in.manage.view"));
		action.putValue(AbstractAction.SMALL_ICON,
				I18N.getIcon("icon.small.arrow.right"));
		return action;
	}

	private static AbstractAction getGotoChapterDlgAction(){
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				FindChapterDialog dlg = new FindChapterDialog(mainFrame);
				JPanel infoPanel = Sidebar.getInstance().getInfoPanel();
				SwingTools.showDialog(dlg, infoPanel);
			}
		};
	}

	private static AbstractAction getGotoDateDlgAction(){
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				FindDateDialog dlg = new FindDateDialog(mainFrame);
				JPanel infoPanel = Sidebar.getInstance().getInfoPanel();
				SwingTools.showDialog(dlg, infoPanel);
			}
		};
	}

	private static AbstractAction getIdeasAction(){
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (!SwingTools.showWindowIfOpened(IdeasFrame.class)) {
					IdeasFrame taskListFrame = new IdeasFrame();
					SwingTools.showFrame(taskListFrame, mainFrame);
				}
			}
		};
	}

	private static AbstractAction getFOIAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				FoiDialog dlg = new FoiDialog();
				SwingTools.showModalDialog(dlg, mainFrame);
			}
		};
	}

	private static AbstractAction getTaskListAction(){
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (!SwingTools.showWindowIfOpened(TaskListFrame.class)) {
					TaskListFrame taskListFrame = new TaskListFrame();
					SwingTools.showFrame(taskListFrame, mainFrame);
				}
			}
		};
	}

	private static AbstractAction getMemoriaAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (!MemoriaFrame.checkIfAvailable()) {
					return;
				}
				MemoriaFrame frame = new MemoriaFrame();
				SwingTools.showFrame(frame, mainFrame);
			}
		};
	}

	private static AbstractAction getMenuRefreshEditAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				((MainMenuBar) mainFrame.getJMenuBar()).refreshEditMenu();
			}
		};
	}
	
	private static AbstractAction getUpdateAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (Updater.checkForUpdate()) {
					JOptionPane.showMessageDialog(mainFrame,
							I18N.getMsg("msg.update.no.text"),
							I18N.getMsg("msg.update.no.title"),
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
	}
	
	private static AbstractAction getAboutAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				SwingTools.showModalDialog(
						new AboutDialog(mainFrame), mainFrame);
			}
		};
	}
	
	public static AbstractAction getExitAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				MainFrame.getInstance().exit();
			}
		};
	}

	public static AbstractAction getManualAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				try {
					Desktop.open(new File("resources/"
							+ I18N.getMsg("msg.menu.help.manual.file")));
				} catch (Exception e) {
					// linux crashes here
					// ignore
				} catch (Error er){
					// linux crashes here
					// ignore
				}
			}
		};
	}

	public static AbstractAction getRunAttesoroAction() {
		return new AbstractAction() {
			private final static String ATTESORO_JAR = "attesoro_1_8.jar";
			public void actionPerformed(ActionEvent evt) {
				try {
					String cmd =
						"java -Duser.language=en"
						+ " -jar "
						+ Constants.ProgramDirectory.LIB.toString()
						+ "/" + ATTESORO_JAR
						+ " resources/messages.properties";
					Runtime.getRuntime().exec(cmd);
				} catch (Exception e) {
					e.printStackTrace();
					SwingTools.showException(e);
				}
			}
		};
	}
		
	public static void performAction(SbAction action) {
		performAction(action, null);
	}
	
	public static void performAction(SbAction action, ActionEvent evt){
		ActionRegistry.getInstance().getAction(action).actionPerformed(evt);
	}	
}
