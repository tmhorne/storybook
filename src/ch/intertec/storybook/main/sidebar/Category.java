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

package ch.intertec.storybook.main.sidebar;

import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.Tag;

public class Category {
	private String category;

	private boolean isTag = true;

	public Category(String category) {
		this(category, true);
	}

	public Category(String category, boolean isTag) {
		this.isTag = isTag;
		this.category = category;
	}

	public Category(Tag tag) {
		this.category = tag.getCategory();
	}

	public Category(Item item) {
		this.isTag = false;
		this.category = item.getCategory();
	}

	public boolean isEmtpy() {
		return category.isEmpty();
	}

	public String getCategory() {
		return category;
	}

	public boolean isTag() {
		return isTag;
	}

	@Override
	public String toString() {
		return category.isEmpty() ? "-" : category;
	}
}
