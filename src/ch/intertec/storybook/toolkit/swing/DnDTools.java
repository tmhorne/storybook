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

package ch.intertec.storybook.toolkit.swing;

import java.awt.dnd.DnDConstants;

public class DnDTools {
	public static String showActions(int action) {
		String actions = "";
		if ((action & (DnDConstants.ACTION_LINK | DnDConstants.ACTION_COPY_OR_MOVE)) == 0) {
			return "None";
		}
		if ((action & DnDConstants.ACTION_COPY) != 0) {
			actions += "Copy ";
		}
		if ((action & DnDConstants.ACTION_MOVE) != 0) {
			actions += "Move ";
		}
		if ((action & DnDConstants.ACTION_LINK) != 0) {
			actions += "Link";
		}
		return actions;
	}
}
