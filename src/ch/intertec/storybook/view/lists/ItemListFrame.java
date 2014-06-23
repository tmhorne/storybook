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

package ch.intertec.storybook.view.lists;

import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.view.model.dbtable.ItemDbTable;

@SuppressWarnings("serial")
public class ItemListFrame extends AbstractListFrame {

	@Override
	void init() {
		table = new ItemDbTable();
		setTitle("msg.items.manage");
	}

	@Override
	int getPreferredWidth() {
		return 800;
	}

	@Override
	protected void addListeners() {
		PCSDispatcher.getInstance().addPropertyChangeListener(Property.ITEM,
				this);
	}

	@Override
	protected void removeListeners() {
		PCSDispatcher.getInstance().removeAllPropertyChangeListener(this);
	}
}
