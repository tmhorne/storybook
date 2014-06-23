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

package ch.intertec.storybook.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.time.DateUtils;

import ch.intertec.storybook.action.ViewPartAction;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.label.DateLabel;
import ch.intertec.storybook.toolkit.swing.label.StrandDateLabel;
import ch.intertec.storybook.view.content.AbstractContentPanel;
import ch.intertec.storybook.view.content.AbstractScenePanel;
import ch.intertec.storybook.view.content.book.BookContentPanel;
import ch.intertec.storybook.view.content.book.BookScenePanel;
import ch.intertec.storybook.view.content.chrono.ChronoContentPanel;
import ch.intertec.storybook.view.content.chrono.cell.ChronoScenePanel;
import ch.intertec.storybook.view.content.manage.ChapterPanel;
import ch.intertec.storybook.view.content.manage.ScenePanel;
import ch.intertec.storybook.view.content.manage.dnd.DTScenePanel;

/**
 * Provides tools around the views.
 * 
 * @author martin
 * 
 */
public class ViewTools {

	public static void scrollToChapterOrScene(DbTable value){
		if (value instanceof Scene) {
			Scene scene = (Scene) value;
			ViewTools.scrollToScene(scene);
			ViewTools.scrollToScene(scene);
			return;
		}
		if (value instanceof Chapter) {
			int no = ((Chapter)value).getId();
			ViewTools.scrollToChapter(no);
			ViewTools.scrollToChapter(no);
			return;
		}
	}
	
	/**
	 * Scrolls to the given scene.
	 * 
	 * @param scene
	 *            the scene to scroll to
	 * @return true if a scene was found
	 */
	public static boolean scrollToScene(Scene scene) {
		// change the part if necessary
		if (scene.getChapter() != null) {
			int partId = scene.getChapter().getPartId();
			if (partId != MainFrame.getInstance().getActivePartId()) {
				ViewPartAction action = new ViewPartAction();
				action.putValue(ViewPartAction.ACTION_KEY_PART_ID,
						partId);
				action.actionPerformed(null);
			}
		}
		
		// scroll
		AbstractContentPanel contentPanel = MainFrame.getInstance().getContentPanel();
		JPanel panel = (JPanel)contentPanel;
		boolean found = false;
		
		// manage view
		if (MainFrame.getInstance().isManagePanelActive()) {
			List<ChapterPanel> cpList =
					ViewTools.findManageScenePanels(contentPanel);
			for (ChapterPanel chapterPanel : cpList) {
				Chapter chapter = chapterPanel.getChapter();
				// first chapter is for unassigned scenes and
				// therefore has no chapter info
				if (chapter == null) {
					continue;
				}
				if (scene.getChapter() != null
						&& scene.getChapter().getChapterNo() == chapter
								.getChapterNo()) {
					Rectangle rect = chapterPanel.getBounds();
					// scroll and repaint
					panel.scrollRectToVisible(rect);
					panel.repaint();
					// flash component
					for (DTScenePanel dtsp : chapterPanel.getDTScenePanels()) {
						if (dtsp.getScene().getId() == scene.getId()) {
							SwingTools.flashComponent(dtsp);
						}
					}
					found = true;
					break;
				}
			}
			return found;
		}
		
		// book and chrono view
		List<AbstractScenePanel> spList = 
				ViewTools.findScenePanels(contentPanel);
		for (AbstractScenePanel scenePanel : spList) {
			Scene sc = scenePanel.getScene();
			if (scene.getId() == sc.getId()) {
				System.out.println("ViewTools.scrollToScene(): found scene:"+sc);
				Rectangle rect = scenePanel.getBounds();
				System.out.println("ViewTools.scrollToScene(): rect:"+rect);
				if (scenePanel instanceof ChronoScenePanel) {
					rect = SwingUtilities.convertRectangle(
							scenePanel.getParent(), rect, panel);
				}
				SwingTools.expandRectangle(rect);
				// scroll and repaint
				panel.scrollRectToVisible(rect);
				panel.repaint();

				// flash the found component
				SwingTools.flashComponent(scenePanel);
				found = true;
				break;
			}
		}
		return found;
	}
	
	/**
	 * Scrolls to the given chapter. Stops after the first scene of the given
	 * chapter was found.
	 * 
	 * @param chapterNo
	 *            the chapter number to scroll to
	 * @return true if a chapter was found
	 */
	public static boolean scrollToChapter(int chapterNo) {
		AbstractContentPanel contentPanel = MainFrame.getInstance().getContentPanel();
		JPanel panel = (JPanel)contentPanel;
		boolean found = false;
		
		// manage view
		if (MainFrame.getInstance().isManagePanelActive()) {
			List<ChapterPanel> cpList
				= ViewTools.findManageScenePanels(contentPanel);
			for (ChapterPanel chapterPanel : cpList) {
				Chapter chapter = chapterPanel.getChapter();
				// first chapter is for unassigned scenes and
				// therefore has no chapter info
				if (chapter == null) {
					continue;
				}
				if (chapter.getChapterNo() == chapterNo) {
					Rectangle rect = chapterPanel.getBounds();

					// scroll and repaint
					panel.scrollRectToVisible(rect);
					panel.repaint();

					// show the user which scene was found
					SwingTools.flashComponent(chapterPanel);
					found = true;
					break;
				}
			}
			return found;
		}
		
		// chrono and book view
		List<AbstractScenePanel> spList
			= ViewTools.findScenePanels(contentPanel);
		for (AbstractScenePanel scenePanel : spList) {
			Chapter chapter = scenePanel.getScene().getChapter();
			if (chapter != null && chapter.getChapterNo() == chapterNo) {
				Rectangle rect = scenePanel.getBounds();
				if (scenePanel instanceof ChronoScenePanel) {
					rect = SwingUtilities.convertRectangle(
							scenePanel.getParent(), rect, panel);
				}
				// scroll and flash
				panel.requestFocus();
				panel.scrollRectToVisible(rect);
				SwingTools.flashComponent((JComponent) scenePanel);
				found = true;
				break;
			}
		}
		return found;
	}

	public static boolean scrollToStrandAndDate(Strand strand, Date date) {
		if (MainFrame.getInstance().isChronoPanelActive()) {
			return scrollToStrandAndDateChronoView(strand, date);
		} else if (MainFrame.getInstance().isBookPanelActive()) {
			return scrollToStrandAndDateBookView(strand, date);
		}
		return false;
	}

	/**
	 * Scrolls the {@link AbstractContentPanel} to the specified date.
	 * 
	 * @param date
	 *            the date to scroll to
	 * @return true if the date was found
	 */
	public static boolean scrollToDate(Date date){
		if(MainFrame.getInstance().isChronoPanelActive()){
			return scrollToStrandAndDateChronoView(date);
		} else if(MainFrame.getInstance().isBookPanelActive()){
			return scrollToStrandAndDateBookView(date);
		}
		return false;
	}
	
	private static boolean scrollToStrandAndDateChronoView(Date gotoDate){
		return scrollToStrandAndDateChronoView(null, gotoDate);
	}
	
	private static boolean scrollToStrandAndDateChronoView(Strand strand,
			Date gotoDate) {
		AbstractContentPanel contentPanel = MainFrame.getInstance().getContentPanel();
		JPanel panel = (JPanel) contentPanel;
		boolean found = false;
		
		List<Component> list = contentPanel.findStrandDateLabels();
		for (Component comp : list) {
			StrandDateLabel lbStrandDate = (StrandDateLabel) comp;
			if (lbStrandDate.getStrand().getId() != strand.getId()) {
				continue;
			}
			DateLabel lbDate = (DateLabel) comp;
			Date date = lbDate.getDate();
			if (DateUtils.isSameDay(date, gotoDate)) {
				Rectangle rect = comp.getBounds();
				rect = SwingUtilities.convertRectangle(
						comp.getParent(), rect, panel);
				SwingTools.expandRectangle(rect);
				// scroll and flash
				panel.scrollRectToVisible(rect);
				SwingTools.flashComponent((JComponent) comp.getParent());
				found = true;
				break;
			}
		}
		return found;
	}

	private static boolean scrollToStrandAndDateBookView(Date gotoDate) {
		return scrollToStrandAndDateBookView(null, gotoDate);
	}
	
	private static boolean scrollToStrandAndDateBookView(Strand strand,
			Date gotoDate) {
		AbstractContentPanel contentPanel = MainFrame.getInstance()
				.getContentPanel();
		JPanel panel = (JPanel) contentPanel;
		boolean found = false;

		List<Component> list = contentPanel.findStrandDateLabels();
		for (Component comp : list) {
			StrandDateLabel lbStrandDate = (StrandDateLabel) comp;
			if (lbStrandDate.getStrand().getId() != strand.getId()) {
				continue;
			}
			Date date = lbStrandDate.getDate();
			if (DateUtils.isSameDay(date, gotoDate)) {
				Component parent = comp.getParent().getParent();
				Rectangle rect = parent.getBounds();
				SwingTools.expandRectangle(rect);
				// scroll and flash
				panel.scrollRectToVisible(rect);
				SwingTools.flashComponent((JComponent) parent);
				found = true;
				break;
			}
		}
		return found;
	}

	/**
	 * Finds all scene panels in the given {@link AbstractContentPanel}. Note that only
	 * {@link ChronoContentPanel} and {@link BookContentPanel} are supported.
	 * For other content panels, an empty list is returned.
	 * 
	 * @param panel
	 *            the {@link AbstractContentPanel}
	 * @return a list of {@link ScenePanel} objects
	 */
	public static List<AbstractScenePanel> findScenePanels(AbstractContentPanel panel) {
		if (panel instanceof ChronoContentPanel) {
			return findChronoScenePanels(panel);
		}
		if (panel instanceof BookContentPanel) {
			return findBookScenePanels(panel);
		}
		return new ArrayList<AbstractScenePanel>();
	}

	private static List<AbstractScenePanel> findBookScenePanels(Container cont) {
		List<AbstractScenePanel> componentList = new ArrayList<AbstractScenePanel>();
		for (Component comp : cont.getComponents()) {
			if (comp instanceof BookScenePanel) {
				componentList.add((BookScenePanel) comp);
			}
		}
		return componentList;
	}

	private static List<AbstractScenePanel> findChronoScenePanels(Container cont) {
		List<Component> components = new ArrayList<Component>();
		components = SwingTools.findComponentsByClass(cont,
				ChronoScenePanel.class, components);
		List<AbstractScenePanel> scenePanels = new ArrayList<AbstractScenePanel>();
		for (Component comp : components) {
			scenePanels.add((AbstractScenePanel) comp);
		}
		return scenePanels;
	}
	
	private static List<ChapterPanel> findManageScenePanels(Container cont) {
		JPanel panel = (AbstractContentPanel) cont;
		List<ChapterPanel> componentList = new ArrayList<ChapterPanel>();
		for (Component comp : panel.getComponents()) {
			if (comp instanceof ChapterPanel) {
				componentList.add((ChapterPanel) comp);
			}
		}
		return componentList;
	}
}
