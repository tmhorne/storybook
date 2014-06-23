package ch.intertec.storybook.model;

@SuppressWarnings("serial")
public class ItemLink extends TagLink {

	public ItemLink() {
		super();
	}

	public ItemLink(int id) {
		super(id);
	}

	public Item getItem() {
		try {
			return ItemPeer.doSelectById(getTagId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getItemId() {
		return super.getTagId();
	}

	public void setItem(Item item) {
		this.tagId = item.getId();
	}
}
