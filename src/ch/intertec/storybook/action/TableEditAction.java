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

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;

import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Gender;
import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.AbstractModifyDialog;
import ch.intertec.storybook.view.IRefreshable;
import ch.intertec.storybook.view.modify.ChapterDialog;
import ch.intertec.storybook.view.modify.CharacterDialog;
import ch.intertec.storybook.view.modify.GenderDialog;
import ch.intertec.storybook.view.modify.ItemDialog;
import ch.intertec.storybook.view.modify.LocationDialog;
import ch.intertec.storybook.view.modify.PartDialog;
import ch.intertec.storybook.view.modify.StrandDialog;
import ch.intertec.storybook.view.modify.TagDialog;
import ch.intertec.storybook.view.modify.scene.SceneDialog;

@SuppressWarnings("serial")
public class TableEditAction extends AbstractTableAction {

	private int showClockDelay = -1;
	
	public TableEditAction(DbTable dbObj) {
		super(getTitle(dbObj), I18N.getIcon("icon.small.edit"), dbObj);
	}

	public TableEditAction(DbTable dbObj, boolean noName) {
		super(I18N.getIcon("icon.small.edit"), dbObj);
	}

	public void actionPerformed(ActionEvent evt) {
		ProjectTools.saveFile(showClockDelay);
		showEditDialog(dbObj);
	}

	private void showEditDialog(DbTable dbObj) {
		JFrame parentFrame;
		if (getParentFrame() == null) {
			parentFrame = MainFrame.getInstance();
		} else {
			parentFrame = getParentFrame();
		}
		AbstractModifyDialog dlg = null;
		Container container = (Container) getValue(AbstractTableAction.ActionKey.CONTAINER
				.toString());
		if (dbObj instanceof SbCharacter) {
			dlg = new CharacterDialog(parentFrame, (SbCharacter) dbObj);
		} else if (dbObj instanceof Location) {
			dlg = new LocationDialog(parentFrame, (Location) dbObj);
		} else if (dbObj instanceof Strand) {
			dlg = new StrandDialog(parentFrame, (Strand) dbObj);
		} else if (dbObj instanceof Chapter) {
			dlg = new ChapterDialog(parentFrame, (Chapter) dbObj);
		} else if (dbObj instanceof Scene) {
			dlg = new SceneDialog(parentFrame, (Scene) dbObj);
		} else if (dbObj instanceof Part) {
			dlg = new PartDialog(parentFrame, (Part) dbObj);
		} else if (dbObj instanceof Item) {
			dlg = new ItemDialog(parentFrame, (Item) dbObj);
		} else if (dbObj instanceof Tag) {
			dlg = new TagDialog(parentFrame, (Tag) dbObj);
		} else if (dbObj instanceof Gender) {
			dlg = new GenderDialog(parentFrame, (Gender) dbObj);
		}

		if (dlg == null) {
			return;
		}

		SwingTools.showModalDialog(dlg, parentFrame);
		if (dlg.getCanceled()) {
			if (container != null) {
				if (container instanceof IRefreshable) {
					((IRefreshable) container).refresh();
				}
			}
			return;
		}
	}

	private static String getTitle(DbTable dbObj) {
		if (dbObj instanceof Chapter) {
			return I18N.getMsg("msg.common.chapter.edit");
		} else if (dbObj instanceof Location) {
			return I18N.getMsg("msg.common.location.edit");
		} else if (dbObj instanceof SbCharacter) {
			return I18N.getMsg("msg.common.person.edit");
		} else if (dbObj instanceof Scene) {
			return I18N.getMsg("msg.common.scene.edit");
		} else if (dbObj instanceof Strand) {
			return I18N.getMsg("msg.common.strand.edit");
		} else if (dbObj instanceof Part) {
			return I18N.getMsg("msg.common.part.edit");
		} else if (dbObj instanceof Item) {
			return I18N.getMsg("msg.item.edit");
		} else if (dbObj instanceof Tag) {
			return I18N.getMsg("msg.tag.edit");
		}
		return I18N.getMsg("msg.common.edit");
	}
	
	public void setShowClockDelay(int showClockDelay) {
		this.showClockDelay = showClockDelay;
	}

	public int getShowClockDelay() {
		return showClockDelay;
	}
}
