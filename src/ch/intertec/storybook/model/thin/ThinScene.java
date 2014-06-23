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

package ch.intertec.storybook.model.thin;

import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;

public class ThinScene extends ThinDbTable {
	private String title;
	
	public ThinScene(Scene scene){
		id = scene.getId();
		title = scene.toString();
	}

	public Scene getScene(){
		return ScenePeer.doSelectById(id);
	}

	public String getTitle() {
		return title;
	}

	@Override
	public DbTable getDbTable() {
		return getScene();
	}
	
	@Override
	public String toString() {
		return getScene().toString();
	}
}
