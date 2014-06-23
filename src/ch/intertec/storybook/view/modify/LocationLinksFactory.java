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

package ch.intertec.storybook.view.modify;

import java.util.ArrayList;
import java.util.List;

import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.LocationPeer;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.SceneLinkLocation;
import ch.intertec.storybook.model.SceneLinkLocationPeer;

public class LocationLinksFactory extends AbstractLinksFactory {

	public LocationLinksFactory(List<Object> linksList, DbTable table) {
		super(linksList, table);
	}

	@Override
	public List<SceneLinkLocation> doSelect(int id1, int id2) {
		try {
			return SceneLinkLocationPeer.doSelect(id1, id2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Location> doSelectAll() {
		return LocationPeer.doSelectAll();
	}

	@Override
	public List<? extends DbTable> doSelectByScene(Scene scene) {
		List<Location> list = new ArrayList<Location>();
		List<SceneLinkLocation> linkList = SceneLinkLocationPeer
				.doSelectBySceneId(scene.getId());
		for (SceneLinkLocation link : linkList) {
			list.add(link.getLocation());
		}
		return list;
	}
}
