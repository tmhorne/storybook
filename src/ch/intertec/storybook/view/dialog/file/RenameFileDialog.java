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

package ch.intertec.storybook.view.dialog.file;

import java.io.File;

import javax.swing.JFrame;

import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;

@SuppressWarnings("serial")
public class RenameFileDialog extends AbstractFileDialog {

	public RenameFileDialog(JFrame frame) {
		super(frame);
		setTitle(I18N.getMsg("msg.common.project.rename"));
		setHideDir(true);
		initGUI();
		File file = ProjectTools.getCurrentFile();
		setFilename(ProjectTools.getProjectName());
		setDir(file.getParent());
	}
}
