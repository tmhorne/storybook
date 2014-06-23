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
package ch.intertec.storybook.view.assignments;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import ch.intertec.storybook.action.ActionManager.SbAction;
import ch.intertec.storybook.action.ActionRegistry;
import ch.intertec.storybook.action.TableNewAction;
import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.ItemLink;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.Tag.TagType;
import ch.intertec.storybook.model.TagLink;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
public class ItemAssignmentsFrame extends AbstractAssignmentsFrame {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(ItemAssignmentsFrame.class);

	public ItemAssignmentsFrame() {
		super();
		PCSDispatcher.getInstance().addPropertyChangeListener(Property.ITEM,
				this);
	}

	@Override
	protected AbstractAction getAddAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (getThis().table == null) {
					return;
				}
				ItemLinkDialog dlg = null;
				int row = getThis().table.getSelectedRow();
				if (row != -1) {
					int rowModel = table.convertRowIndexToModel(row);
					TagTableModel model = (TagTableModel) table.getModel();
					Item item = model.getItem(rowModel);
					ItemLink link = new ItemLink();
					link.setItem(item);
					dlg = new ItemLinkDialog(getThis(), link);
				} else {
					if (getThis().table.getRowCount() > 0) {
						dlg = new ItemLinkDialog(getThis());
					}
				}
				if (dlg != null) {
					SwingTools.showDialog(dlg, getThis());
				}
			}
		};
	}
	
	@Override
	protected void editTagLink(int row) {
		int rowModel = table.convertRowIndexToModel(row);
		TagTableModel model = (TagTableModel) table.getModel();
		Item item = model.getItem(rowModel);
		ArrayList<TagLink> tagLinks = item.getLinks();
		ArrayList<ItemLink> itemLinks = new ArrayList<ItemLink>();
		for (TagLink link : tagLinks) {
			if (link instanceof ItemLink) {
				itemLinks.add((ItemLink) link);
			}
		}
		if (itemLinks.isEmpty()) {
			ItemLink link = new ItemLink();
			link.setTag(item);
			itemLinks.add(link);
		}
		ItemLinksDialog dlg = new ItemLinksDialog(getThis(), itemLinks);
		SwingTools.showDialog(dlg, getThis());
	}
	
	@Override
	protected TagType getTagType() {
		return TagType.ITEM;
	}
	
	@Override
	public String getTitle() {
		return I18N.getMsg("msg.item.assignments");
	}

	@Override
	protected String[] getColumnNames() {
		String[] columnNames = { I18N.getMsg("msg.item"),
				I18N.getMsg("msg.item.category"),
				I18N.getMsg("msg.items.links"),
				I18N.getMsg("msg.items.periods") };
		return columnNames;
	}

	@Override
	protected JButton getManageTagsButton() {
		JButton bt = new JButton();
		bt.setAction(ActionRegistry.getInstance().getAction(
				SbAction.ITEM_MANAGE));
		bt.setText(I18N.getMsg("msg.items.manage"));
		bt.setIcon(I18N.getIcon("icon.medium.manage.items"));
		return bt;
	}

	@Override
	protected JButton getAddTagButton() {
		JButton bt = new JButton();
		TableNewAction addAction = new TableNewAction(new Item());
		addAction.setParentFrame(this);
		bt.setAction(addAction);
		bt.setIcon(I18N.getIcon("icon.medium.new.item"));
		return bt;
	}

	@Override
	protected JButton getDeleteButton() {
		JButton bt = new JButton();
		bt.setAction(getDeleteAction());
		bt.setText(I18N.getMsg("msg.item.delete.assigments"));
		bt.setIcon(I18N.getIcon("icon.small.delete"));
		return bt;
	}

	@Override
	protected int showConfirmDialog(Tag tag) {
		return JOptionPane.showConfirmDialog(getThis(),
				I18N.getMsg("msg.items.links.delete.all", tag),
				I18N.getMsg("msg.common.delete"), JOptionPane.YES_NO_OPTION);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (PCSDispatcher.isPropertyFired(Property.ITEM, evt)) {
			refresh();
			return;
		}
	}
}
