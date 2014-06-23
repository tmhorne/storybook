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

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;

import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.TagLink;
import ch.intertec.storybook.model.TagLinkPeer;
import ch.intertec.storybook.view.AbstractModifyDialog;
import ch.intertec.storybook.view.IRefreshable;

/**
 * Dialog for editing an tag link.
 * 
 * @author martin
 * 
 */

@SuppressWarnings("serial")
public class TagLinksDialog extends AbstractModifyDialog implements IRefreshable {

	protected ArrayList<TagLinkPanel> tagLinkPanels;

	public TagLinksDialog() {
		super();
	}

	public TagLinksDialog(JFrame frame) {
		super(frame);
	}

	public TagLinksDialog(JFrame frame, TagLink link) {
		super(frame, link);
	}

	public TagLinksDialog(JFrame frame, ArrayList<TagLink> links) {
		super(frame, links);
	}

	@Override
	public void init() {
		this.tagLinkPanels = new ArrayList<TagLinkPanel>();
		this.setModal(true);
		this.setUseScrollPane(true);
		if (this.tagLinks == null) {
			this.tagLinks = new ArrayList<TagLink>();
		}
		Tag.setToStringCategory(true);
	}

	@Override
	protected MigLayout getMigLayout() {
		return new MigLayout("flowy", "", "");
	}

	@Override
	protected void initGUI() {
		try {
			if (edit) {
				setTitle("msg.common.edit");
			} else {
				setTitle("msg.common.new");
			}
			
			int i = 0;
			for (TagLink link : this.tagLinks) {
				TagLinkPanel tagLinkPanel = new TagLinkPanel(link, i + 1);
				tagLinkPanels.add(i, tagLinkPanel);
				panel.add(tagLinkPanels.get(i));
				++i;
			}
			
			int maxh = MainFrame.getInstance().getHeight();
			int h = (260 * i) + 120;
			if (h > maxh) {
				h = maxh - 20;
			}
			setPreferredSize(new Dimension(850, h));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void finish(){
		Tag.setToStringCategory(false);
	}
	
	@Override
	protected void setValuesFromTable() {
		// SwingTools.checkInputComponents(inputComponentList);
	}

	@Override
	protected void setValuesFromAction(Action action) {
		logger.fatal("not implemented");
	}

	@Override
	protected void makeOrUpdate(AbstractModifyDialog dlg, boolean edit)
			throws Exception {
		TagLinkPeer.makeOrUpdateTagLinks((TagLinksDialog) getThis(), edit);
	}

	public TagLink getTagLink() {
		return (TagLink) table;
	}
	
	public ArrayList<TagLinkPanel> getTagLinkPanels() {
		return this.tagLinkPanels;
	}
}
