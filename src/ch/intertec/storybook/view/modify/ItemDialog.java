package ch.intertec.storybook.view.modify;

import javax.swing.JFrame;

import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.ItemPeer;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.Tag.TagType;
import ch.intertec.storybook.view.AbstractModifyDialog;

@SuppressWarnings("serial")
public class ItemDialog extends TagDialog {

	public ItemDialog() {
		super();
	}

	public ItemDialog(JFrame frame) {
		super(frame);
	}

	public ItemDialog(JFrame frame, Tag tag) {
		super(frame, tag);
	}

	@Override
	protected void makeOrUpdate(AbstractModifyDialog dlg, boolean edit)
			throws Exception {
		ItemPeer.makeOrUpdateItem((ItemDialog) getThis(), edit);
	}
	
	public Item getItem() {
		return (Item) table;
	}
	
	public TagType getTagType(){
		return TagType.ITEM;
	}
}
