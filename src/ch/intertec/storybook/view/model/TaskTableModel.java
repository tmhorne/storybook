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

package ch.intertec.storybook.view.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class TaskTableModel extends AbstractTableModel {

	private String[] columnNames;
	private List<Scene> sceneList;
	private Object[][] data;

	public TaskTableModel() {
		columnNames = new String[4];
		columnNames[0] = I18N.getMsg("msg.common.scene");
		columnNames[1] = I18N.getMsg("msg.common.notes");
		columnNames[2] = I18N.getMsg("msg.status");
		columnNames[3] = I18N.getMsg("msg.common.part");
		sceneList = ScenePeer.doSelectTasks();
		data = new Object[sceneList.size()][4];
		int c = 0;
		for (Scene scene : sceneList) {
			data[c][0] = scene.getChapterAndSceneNumber() + " "
					+ scene.getTitle();
			data[c][1] = scene.getNotes();
			data[c][2] = new Integer(scene.getStatus());
			if (scene.getChapter() != null) {
				data[c][3] = scene.getChapter().getPart().getNumberStr();
			} else {
				data[c][3] = "";
			}
			++c;
		}
	}

	public Scene getSceneAt(int rowIndex) {
		return sceneList.get(rowIndex);
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}
}
