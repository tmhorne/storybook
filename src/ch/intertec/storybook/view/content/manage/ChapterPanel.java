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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.dnd.Autoscroll;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.action.AbstractTableAction;
import ch.intertec.storybook.action.ActionManager.SbAction;
import ch.intertec.storybook.action.ActionRegistry;
import ch.intertec.storybook.action.TableDeleteAction;
import ch.intertec.storybook.action.TableEditAction;
import ch.intertec.storybook.action.TableNewAction;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;
import ch.intertec.storybook.view.content.manage.dnd.DTScenePanel;
import ch.intertec.storybook.view.content.manage.dnd.SceneTransferHandler;

@SuppressWarnings("serial")
public class ChapterPanel extends JPanel implements MouseListener,
		IRefreshable, Autoscroll {

	private Chapter chapter;
	private SceneTransferHandler sceneTransferHandler;

	public ChapterPanel() {
		this(null);
	}

	public ChapterPanel(Chapter chapter) {
		super();
		this.chapter = chapter;
		initGUI();
		setFocusable(true);
		addMouseListener(this);
		setAutoscrolls(true);
	}

	public boolean isForUnassignedScene() {
		return chapter == null;
	}
	
	private void initGUI() {
		try {
			MigLayout layout = new MigLayout(
					"flowy",
					"[grow]",
					"[]4[]0[]"
			);
			setLayout(layout);
			setBorder(SwingTools.getBorderDefault());
			setMinimumSize(new Dimension(40, 40));
			setComponentPopupMenu(createPopupMenu());

			JLabel lbChapter = new JLabel();
			StringBuffer buf = new StringBuffer();
			if (chapter == null) {
				buf.append(I18N.getMsg("msg.unassigned.scenes"));
			} else {
				buf.append(chapter.getChapterNoStr());
				buf.append(" ");
				buf.append(chapter.getTitle());
			}
			lbChapter.setText(buf.toString());
			add(lbChapter);

			sceneTransferHandler = new SceneTransferHandler();

			if (chapter == null) {
				// show all unassigned scenes (having a chapter id of -1)
				for (Scene scene : ScenePeer
						.doSelectByChapterId(ScenePeer.UNASIGNED_CHAPTER_ID)) {
					DTScenePanel dtScene = new DTScenePanel(scene,
							DTScenePanel.TYPE_UNASSIGNED);
					dtScene.setTransferHandler(sceneTransferHandler);
					add(dtScene, "growx,gapbottom 10");
				}

				// unassigned chapters
				DTScenePanel makeUnassigned = new DTScenePanel(
						DTScenePanel.TYPE_MAKE_UNASSIGNED);
				makeUnassigned.setTransferHandler(sceneTransferHandler);
				add(makeUnassigned, "push,grow,gapbottom 10");

			} else {
				List<Scene> sceneList = ScenePeer.doSelectByChapter(chapter);

				DTScenePanel begin = new DTScenePanel(DTScenePanel.TYPE_BEGIN);
				begin.setTransferHandler(sceneTransferHandler);
				if (sceneList.isEmpty()) {
					add(begin, "grow,gap 15 15");
				} else {
					add(begin, "growx,gap 15 15");
				}

				int i = 0;
				for (Scene scene : sceneList) {
					// scene
					DTScenePanel dtScene = new DTScenePanel(scene);
					dtScene.setTransferHandler(sceneTransferHandler);
					add(dtScene, "growx");

					// move next
					DTScenePanel next = new DTScenePanel(DTScenePanel.TYPE_NEXT);
					next.setPreviousNumber(scene.getSceneNo());
					next.setTransferHandler(sceneTransferHandler);
					if (i < sceneList.size() - 1) {
						add(next, "growx,gap 15 15");
					} else {
						add(next, "push,grow,gap 15 15");
					}
					++i;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JPopupMenu createPopupMenu() {
		JPopupMenu menu = new JPopupMenu();

		if (chapter != null) {
			// edit
			AbstractTableAction action = new TableEditAction(chapter);
			menu.add(action);

			// delete
			action = new TableDeleteAction(chapter);
			action.putValue(AbstractTableAction.ActionKey.CONTAINER.toString(),
					getThis());
			menu.add(action);

			// new chapter
			action = new TableNewAction(chapter);
			menu.add(action);

			menu.add(new Separator());

			// new scene
			action = new TableNewAction(new Scene());
			Date date = ChapterPeer.getLastDate(chapter);
			action.putValue(AbstractTableAction.ActionKey.DATE.toString(), date);
			action.putValue(AbstractTableAction.ActionKey.CHAPTER.toString(),
					chapter);
			menu.add(action);

			menu.add(new Separator());

			// renumber scenes
			menu.add(new AbstractAction(I18N.getMsg("msg.renumber.scenes")) {
				public void actionPerformed(ActionEvent evt) {
					ChapterPeer.renumberScenes(chapter);
				}
			});

			menu.add(new Separator());

			// generate chapters
			ActionRegistry ar = ActionRegistry.getInstance();
			menu.add(ar.getAction(SbAction.CHAPTER_GENERATE));

		} else {
			// new scene
			AbstractTableAction action = new TableNewAction(new Scene());
			menu.add(action);
		}

		return menu;
	}

	@Override
	public void refresh() {
		removeAll();
		initGUI();
		validate();
		repaint();
	}

	protected ChapterPanel getThis() {
		return this;
	}

	public Chapter getChapter() {
		return chapter;
	}

	/**
	 * Gets all {@link DTScenePanel} that have a scene assigned.
	 * 
	 * @return a list of all {@link DTScenePanel}
	 * @see DTScenePanel
	 */
	public List<DTScenePanel> getDTScenePanels() {
		List<DTScenePanel> list = new ArrayList<DTScenePanel>();
		for (Component comp : getComponents()) {
			if (comp instanceof DTScenePanel
					&& ((DTScenePanel) comp).getScene() != null) {
				list.add((DTScenePanel) comp);
			}
		}
		return list;
	}
	
	@Override
	public void mouseClicked(MouseEvent evt) {
		requestFocusInWindow();
		if (chapter != null && evt.getClickCount() == 2) {
			AbstractTableAction action = new TableEditAction(chapter);
			action.actionPerformed(null);
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

	@Override
	public Insets getAutoscrollInsets() {
		System.out.println("ChapterPanel.getAutoscrollInsets(): ");
		return null;
	}

	@Override
	public void autoscroll(Point cursorLocn) {
		System.out.println("ChapterPanel.autoscroll(): ");
	}
}
