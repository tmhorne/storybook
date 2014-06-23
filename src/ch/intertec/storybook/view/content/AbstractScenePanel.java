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

package ch.intertec.storybook.view.content;

import java.awt.Color;

import ch.intertec.storybook.action.AbstractTableAction;
import ch.intertec.storybook.action.TableDeleteAction;
import ch.intertec.storybook.action.TableEditAction;
import ch.intertec.storybook.action.TableNewAction;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.panel.GradientPanel;
import ch.intertec.storybook.view.IRefreshable;
import ch.intertec.storybook.view.IconButton;

@SuppressWarnings("serial")
abstract public class AbstractScenePanel extends GradientPanel implements
		IRefreshable {
	protected Scene scene;

	protected AbstractTableAction editAction;
	protected AbstractTableAction deleteAction;
	protected AbstractTableAction newAction;
	
	protected IconButton btNew;
	protected IconButton btEdit;
	protected IconButton btDelete;
	
	public AbstractScenePanel() {
		super();
		init();
		initGUI();
	}

	public AbstractScenePanel(Scene scene) {
		super();
		this.scene = scene;
		init();
		initGUI();
	}
	
	public AbstractScenePanel(Scene scene, boolean showBgGradient,
			Color startBgcolor, Color endBgColor) {
		super(showBgGradient, startBgcolor, endBgColor);
		this.scene = scene;
		init();
		initGUI();
	}

	public AbstractScenePanel(int sceneId){
		super();
		this.scene = ScenePeer.doSelectById(sceneId);
		init();
		initGUI();		
	}

	abstract protected void init();
	
	abstract protected void initGUI();
	
	@Override
	public void refresh() {
		initGUI();
		revalidate();
		repaint();
	}
	
	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	protected AbstractTableAction getEditActionForHotkey() {
		TableEditAction act = new TableEditAction(scene, true);
		act.setShowClockDelay(50);
		act.putValue(AbstractTableAction.ActionKey.CONTAINER.toString(), this);
		return act;
	}

	protected AbstractTableAction getEditAction() {
		if (editAction != null) {
			return editAction;
		}
		editAction = new TableEditAction(scene, true);
		editAction.putValue(AbstractTableAction.ActionKey.CONTAINER.toString(),
				this);
		return editAction;
	}

	protected AbstractTableAction getDeleteAction() {
		if (deleteAction != null) {
			return deleteAction;
		}
		deleteAction = new TableDeleteAction(scene, true);
		deleteAction.putValue(
				AbstractTableAction.ActionKey.CONTAINER.toString(), this);
		return deleteAction;
	}
	
	protected AbstractTableAction getNewAction() {
		if (newAction != null) {
			return newAction;
		}
		newAction = new TableNewAction(new Scene(), true);
		newAction.putValue(AbstractTableAction.ActionKey.SCENE.toString(),
				scene);
		newAction.putValue(AbstractTableAction.ActionKey.DATE.toString(),
				scene.getDate());
		return newAction;
	}
	
	protected IconButton getEditButton() {
		if (btEdit != null) {
			return btEdit;
		}
		btEdit = new IconButton("icon.small.edit", getEditAction());
		btEdit.setSize32x20();
		btEdit.setToolTipText(I18N.getMsg("msg.common.edit"));
		return btEdit;
	}
	
	protected IconButton getDeleteButton() {
		if (btDelete != null) {
			return btDelete;
		}
		btDelete = new IconButton("icon.small.delete", getDeleteAction());
		btDelete.setSize32x20();
		btDelete.setToolTipText(I18N.getMsg("msg.common.delete"));
		return btDelete;
	}

	protected IconButton getNewButton() {
		if (btNew != null) {
			return btNew;
		}
		btNew = new IconButton("icon.small.new", getNewAction());
		btNew.setSize32x20();
		btNew.setToolTipText(I18N.getMsg("msg.common.new"));
		return btNew;
	}
}
