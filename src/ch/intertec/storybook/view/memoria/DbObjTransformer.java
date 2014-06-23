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

package ch.intertec.storybook.view.memoria;

import org.apache.commons.collections15.Transformer;

import ch.intertec.storybook.model.DbPeer;
import ch.intertec.storybook.model.DbTable;

public class DbObjTransformer implements Transformer<DbTable, String> {

	@Override
	public String transform(DbTable dbObj) {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		buf.append("<table><tr><td width='400'>");
		buf.append(DbPeer.getInfoText(dbObj, true));
		buf.append("</td></tr></table>");
		return buf.toString();
	}
}
