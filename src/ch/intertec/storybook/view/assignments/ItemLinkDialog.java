package ch.intertec.storybook.view.assignments;

import java.util.ArrayList;

import javax.swing.JFrame;

import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.ItemLink;
import ch.intertec.storybook.model.ItemLinkPeer;
import ch.intertec.storybook.model.TagLink;
import ch.intertec.storybook.view.AbstractModifyDialog;

@SuppressWarnings("serial")
public class ItemLinkDialog extends TagLinkDialog {

	public ItemLinkDialog() {
		super();
	}

	public ItemLinkDialog(JFrame frame, ArrayList<TagLink> links) {
		super(frame, links);
	}

	public ItemLinkDialog(JFrame frame, TagLink link) {
		super(frame, link);
	}

	public ItemLinkDialog(JFrame frame) {
		super(frame);
	}

	@Override
	public void init() {
		this.setModal(true);
		Item.setToStringCategory(true);
	}
	
	@Override
	protected void initGUI() {
		try {
			if (edit) {
				setTitle("msg.common.edit");
			} else {
				setTitle("msg.common.new");
			}
			if (table != null) {
				linkPanel = new ItemLinkPanel((ItemLink) table, 1);
			} else {
				ItemLink link = new ItemLink();
				linkPanel = new ItemLinkPanel(link, 1);
			}
			panel.add(linkPanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void finish(){
		Item.setToStringCategory(false);
	}

	@Override
	protected void makeOrUpdate(AbstractModifyDialog dlg, boolean edit)
			throws Exception {
		ItemLinkPeer.makeOrUpdateItemLink((ItemLinkDialog) getThis(), edit);
	}
}
