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

package ch.intertec.storybook.model;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Item extends Tag {

	public Item() {
		this.type = TagType.ITEM;
	}

	public Item(boolean isVolatile) {
		super(isVolatile);
		this.type = TagType.ITEM;
	}

	public Item(int id) {
		super(id);
		this.type = TagType.ITEM;
	}
	
	public ArrayList<TagLink> getLinks() {
		try {
			return ItemLinkPeer.toTagLinks(ItemLinkPeer.doSelectByItemId(this
					.getId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<ItemLink> getItemLinks() {
		try {
			return ItemLinkPeer.doSelectByItemId(this.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Item clone() {
		try {
			Item clone = (Item) super.clone();
			clone.id = cloneId--;
			clone.realId = id;
			return clone;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return null;
	}
}
