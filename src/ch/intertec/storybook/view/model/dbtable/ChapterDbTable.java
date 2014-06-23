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

package ch.intertec.storybook.view.model.dbtable;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.IDbColumn;
import ch.intertec.storybook.model.Chapter.Column;

public class ChapterDbTable extends AbstractDbTable {
	
	private Vector<Column> columns;
	
	@Override
	protected void init() {
		columns = new Vector<Column>();
		columns.add(Column.CHAPTER_NO);
		columns.add(Column.TITLE);
		columns.add(Column.PART_ID);
		dbTableColumnIndex = 1;
	}
	
	@Override
	protected void fillData() {
		list = ChapterPeer.doSelectAll();
		data = new DefaultTableModel(columns, list.size());
		for (int rowIndex = 0; rowIndex < list.size(); ++rowIndex) {
			Chapter chapter = (Chapter)list.get(rowIndex);
			for (int columnIndex = 0; columnIndex < columns.size(); ++columnIndex) {
				Object value = null;
				switch (columns.get(columnIndex)) {
				case CHAPTER_NO:
					value = chapter.getChapterNoStr();
					break;
				case TITLE:
					value = chapter.getTitle();
					break;
				case PART_ID:
					value = chapter.getPart().toString();
					break;					
				}
				data.setValueAt(value, rowIndex, columnIndex);
			}
		}		
	}
	
	@Override
	protected Vector<? extends IDbColumn> getColumns() {
		return columns;
	}
}
