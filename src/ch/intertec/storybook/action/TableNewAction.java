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

import javax.swing.JFrame;

import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Gender;
import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.AbstractModifyDialog;
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
public class TableNewAction extends AbstractTableAction {

	public TableNewAction(DbTable table) {
		super(getTitle(table), I18N.getIcon("icon.small.new"), table);
	}
	
	public TableNewAction(DbTable table, boolean noName) {
		super(I18N.getIcon("icon.small.new"), table);
	}

	public void actionPerformed(ActionEvent evt) {
		showAddDialog(dbObj);
	}

	private void showAddDialog(DbTable table) {
		JFrame parentFrame;
		if (getParentFrame() == null) {
			parentFrame = MainFrame.getInstance();
		} else {
			parentFrame = getParentFrame();
		}
		AbstractModifyDialog dlg = null;
		if (table instanceof SbCharacter) {
			dlg = new CharacterDialog(this);
		} else if (table instanceof Location) {
			dlg = new LocationDialog(this);
		} else if (table instanceof Strand) {
			dlg = new StrandDialog(parentFrame);
		} else if (table instanceof Chapter) {
			dlg = new ChapterDialog(this);
		} else if (table instanceof Part) {
			dlg = new PartDialog(parentFrame);
		} else if (table instanceof Scene) {
			dlg = new SceneDialog(this);
		} else if (table instanceof Item) {
			dlg = new ItemDialog(parentFrame);
		} else if (table instanceof Tag) {
			dlg = new TagDialog(parentFrame);
		} else if (table instanceof Gender) {
			dlg = new GenderDialog(parentFrame);
		}
		
		if (dlg == null) {
			return;
		}
		
		SwingTools.showModalDialog(dlg, parentFrame);
	}

	private static String getTitle(DbTable table) {
		if (table instanceof Chapter) {
			return I18N.getMsg("msg.common.chapter.add");
		} else if (table instanceof Location) {
			return I18N.getMsg("msg.common.location.new");
		} else if (table instanceof SbCharacter) {
			return I18N.getMsg("msg.common.person.new");
		} else if (table instanceof Scene) {
			return I18N.getMsg("msg.common.scene.add");
		} else if (table instanceof Strand) {
			return I18N.getMsg("msg.common.strand.new");
		} else if (table instanceof Part) {
			return I18N.getMsg("msg.common.part.new");
		} else if (table instanceof Item) {
			return I18N.getMsg("msg.item.new");
		} else if (table instanceof Tag) {
			return I18N.getMsg("msg.tag.new");
		}
		return I18N.getMsg("msg.common.new");
	}
}
