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
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.TagLink;
import ch.intertec.storybook.model.TagLinkPeer;
import ch.intertec.storybook.model.TagPeer;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Scene;

public class TagCharacterLinksFactory extends AbstractLinksFactory {

	public TagCharacterLinksFactory(List<Object> linksList, DbTable table) {
		super(linksList, table);
	}

	@Override
	public List<TagLink> doSelect(int id1, int id2) {
		try {
			return TagLinkPeer.doSelect(id1, id2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Tag> doSelectAll() {
		return TagPeer.doSelectAll();
	}

	@Override
	public List<? extends DbTable> doSelectByCharacter(SbCharacter character) {
		List<Tag> list = new ArrayList<Tag>();
		List<TagLink> linkList = TagLinkPeer.doSelectByCharacterId(character.getId());
		for (TagLink link : linkList) {
			list.add(link.getTag());
		}
		return list;
	}
	
	@Override
	public List<? extends DbTable> doSelectByScene(Scene scene) {
		List<Tag> list = new ArrayList<Tag>();
		List<TagLink> linkList = TagLinkPeer.doSelectBySceneId(scene.getId());
		for (TagLink link : linkList) {
			list.add(link.getTag());
		}
		return list;
	}
}
