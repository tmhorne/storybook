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
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.Tag.TagType;
import ch.intertec.storybook.model.TagLink;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
public class TagAssignmentsFrame extends AbstractAssignmentsFrame {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(TagAssignmentsFrame.class);

	public TagAssignmentsFrame() {
		super();
		PCSDispatcher.getInstance().addPropertyChangeListener(
				Property.TAG, this);
	}

	@Override
	protected AbstractAction getAddAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (getThis().table == null) {
					return;
				}
				TagLinkDialog dlg = null;
				int row = getThis().table.getSelectedRow();
				if (row != -1) {
					int rowModel = table.convertRowIndexToModel(row);
					TagTableModel model = (TagTableModel) table.getModel();
					Tag tag = model.getTag(rowModel);
					TagLink link = new TagLink();
					link.setTag(tag);
					dlg = new TagLinkDialog(getThis(), link);
				} else {
					if (getThis().table.getRowCount() > 0) {
						dlg = new TagLinkDialog(getThis());
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
		Tag tag = model.getTag(rowModel);
		ArrayList<TagLink> links = tag.getLinks();
		if (links.isEmpty()) {
			TagLink link = new TagLink();
			link.setTag(tag);
			links.add(link);
		}
		TagLinksDialog dlg = new TagLinksDialog(getThis(), links);
		SwingTools.showDialog(dlg, getThis());
	}
	
	@Override
	protected TagType getTagType() {
		return TagType.TAG;
	}

	@Override
	public String getTitle() {
		return I18N.getMsg("msg.tag.assignments");
	}

	@Override
	protected String[] getColumnNames() {
		String[] columnNames = { I18N.getMsg("msg.tag"),
				I18N.getMsg("msg.tag.category"),
				I18N.getMsg("msg.tags.links"),
				I18N.getMsg("msg.tags.periods") };
		return columnNames;
	}

	@Override
	protected JButton getAddTagButton() {
		JButton bt = new JButton();
		TableNewAction addAction = new TableNewAction(new Tag());
		addAction.setParentFrame(this);
		bt.setAction(addAction);
		bt.setIcon(I18N.getIcon("icon.medium.new.tag"));
		return bt;
	}
	
	@Override
	protected JButton getManageTagsButton() {
		JButton bt = new JButton();
		bt.setAction(ActionRegistry.getInstance()
				.getAction(SbAction.TAG_MANAGE));
		bt.setText(I18N.getMsg("msg.tags.manage"));
		bt.setIcon(I18N.getIcon("icon.medium.manage.tags"));
		return bt;
	}

	@Override
	protected JButton getDeleteButton() {
		JButton bt = new JButton();
		bt.setAction(getDeleteAction());
		bt.setText(I18N.getMsg("msg.tag.delete.assigments"));
		bt.setIcon(I18N.getIcon("icon.small.delete"));
		return bt;
	}
	
	@Override
	protected int showConfirmDialog(Tag tag) {
		return JOptionPane.showConfirmDialog(getThis(),
				I18N.getMsg("msg.tags.links.delete.all", tag),
				I18N.getMsg("msg.common.delete"), JOptionPane.YES_NO_OPTION);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (PCSDispatcher.isPropertyFired(Property.TAG, evt)) {
			refresh();
			return;
		}
	}
}
