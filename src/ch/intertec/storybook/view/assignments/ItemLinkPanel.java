package ch.intertec.storybook.view.assignments;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.ItemLink;
import ch.intertec.storybook.model.ItemPeer;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.TagLink;

@SuppressWarnings("serial")
public class ItemLinkPanel extends TagLinkPanel {

	public ItemLinkPanel(TagLink link, int number) {
		super(link, number);
	}

	public ItemLinkPanel(ItemLink link, int number) {
		super(link, number);
	}

	@Override
	protected JComboBox createTagCombo() {
		try {
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			DbTable selected = null;
			ItemLink link = (ItemLink) this.link;
			for (Tag t : ItemPeer.doSelectAll()) {
				if (t.getId() == link.getTagId()) {
					selected = t;
				}
				model.addElement(t);
			}
			JComboBox combo = new JComboBox();
			combo.setModel(model);
			if (selected != null) {
				combo.setSelectedItem(selected);
			}
			return combo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
