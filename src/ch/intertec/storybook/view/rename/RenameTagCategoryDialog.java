package ch.intertec.storybook.view.rename;

import java.util.List;

import javax.swing.JFrame;

import ch.intertec.storybook.model.TagPeer;
import ch.intertec.storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class RenameTagCategoryDialog extends AbstractRenameDialog {

	public RenameTagCategoryDialog(JFrame frame) {
		super(frame);
	}

	@Override
	protected List<String> getList() {
		return TagPeer.doSelectDistinctCategory();
	}

	@Override
	protected void rename(String oldValue, String newValue) {
		TagPeer.renameCategory(oldValue, newValue);
	}

	@Override
	protected String getDlgTitle() {
		return I18N.getMsg("msg.tag.rename.category");
	}
}
