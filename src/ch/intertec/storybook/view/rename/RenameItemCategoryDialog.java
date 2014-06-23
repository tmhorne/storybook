package ch.intertec.storybook.view.rename;

import java.util.List;

import javax.swing.JFrame;

import ch.intertec.storybook.model.ItemPeer;
import ch.intertec.storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class RenameItemCategoryDialog extends AbstractRenameDialog {

	public RenameItemCategoryDialog(JFrame frame) {
		super(frame);
	}

	@Override
	protected List<String> getList() {
		return ItemPeer.doSelectDistinctCategory();
	}

	@Override
	protected void rename(String oldValue, String newValue) {
		ItemPeer.renameCategory(oldValue, newValue);
	}

	@Override
	protected String getDlgTitle() {
		return I18N.getMsg("msg.item.rename.category");
	}
}
