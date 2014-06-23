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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.thin.ThinLocation;
import ch.intertec.storybook.model.thin.ThinScene;
import ch.intertec.storybook.toolkit.I18N;

@SuppressWarnings("serial")
class SidebarTreeCellRenderer extends DefaultTreeCellRenderer {
	private Icon locationIcon;
	private Icon strandIcon;
	private Icon partIcon;
	private Icon chapterIcon;
	private Icon itemIcon;
	private Icon tagIcon;
	
	public SidebarTreeCellRenderer(){
		locationIcon = I18N.getIcon("icon.small.location");
		strandIcon = I18N.getIcon("icon.small.strand");
		partIcon = I18N.getIcon("icon.small.part");
		chapterIcon = I18N.getIcon("icon.small.chapter");
		tagIcon = I18N.getIcon("icon.small.tag");
		itemIcon = I18N.getIcon("icon.small.item");
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (leaf) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object userObject = node.getUserObject();
			if (userObject instanceof SbCharacter) {
				setLeafIcon(((SbCharacter) userObject).getGender().getIcon());
			} else if (userObject instanceof Location
					|| userObject instanceof ThinLocation) {
				setLeafIcon(locationIcon);
			} else if (userObject instanceof Strand) {
				setLeafIcon(strandIcon);
			} else if (userObject instanceof Part) {
				setLeafIcon(partIcon);
			} else if (userObject instanceof Scene
					|| userObject instanceof ThinScene) {
				setLeafIcon(chapterIcon);
			} else if (userObject instanceof Item) {
				setLeafIcon(itemIcon);
			} else if (userObject instanceof Tag) {
				setLeafIcon(tagIcon);
			} else {
				setLeafIcon(null);
			}
		}
		super.getTreeCellRendererComponent(tree, value, sel, expanded,
				leaf, row, hasFocus);
		return this;
	};
}
