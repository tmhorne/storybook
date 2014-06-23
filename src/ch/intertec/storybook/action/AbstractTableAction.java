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

import java.sql.Date;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import ch.intertec.storybook.main.sidebar.City;
import ch.intertec.storybook.main.sidebar.Country;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.SbCharacter;

@SuppressWarnings("serial")
abstract public class AbstractTableAction extends AbstractAction {
	
	private JFrame parentFrame;
	
	public enum ActionKey {
		CHAPTER("chapter"),
		SCENE("scene"),
		DATE("date"),
		CATEGORY("category"),
		CONTAINER("container"),
		COUNTRY("country"),
		CITY("city"),
		PART("part");
		final private String key;
		private ActionKey(String key) { this.key = key; }
		public String getKey() { return key; };
		public String toString(){ return key; };
	}
	
	protected DbTable dbObj;
	
	public AbstractTableAction(Icon icon, DbTable table) {
		this("", icon, table);
	}

	public AbstractTableAction(String name, Icon icon, DbTable dbObj) {
		super(name, icon);
		this.dbObj = dbObj;
	}
	
	public void setParentFrame(JFrame parentFrame) {
		this.parentFrame = parentFrame;
	}
	
	public JFrame getParentFrame() {
		return parentFrame;
	}
		
	public static void putDateToAction(AbstractAction action, Date date) {
		action.putValue(ActionKey.DATE.toString(), date);
	}

	public static void putChapterToAction(AbstractAction action, Chapter chapter) {
		action.putValue(ActionKey.CHAPTER.toString(), chapter);
	}

	public static void putPartToAction(AbstractAction action, Part part) {
		action.putValue(ActionKey.PART.toString(), part);
	}

	public static void putCityToAction(AbstractAction action, City city) {
		if (!city.isEmtpy()) {
			action.putValue(ActionKey.CITY.toString(), city);
		}
	}

	public static void putCountryToAction(AbstractAction action, Country country) {
		if (!country.isEmtpy()) {
			action.putValue(ActionKey.COUNTRY.toString(), country);
		}
	}

	public static void putCategoryToAction(AbstractAction action,
			DbTable dbTable) {
		if (dbTable instanceof SbCharacter) {
			SbCharacter ch = (SbCharacter) dbTable;
			action.putValue(ActionKey.CATEGORY.toString(), ch.getCategory());
		}
	}
}
