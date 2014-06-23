package ch.intertec.storybook.view.rename;

import java.util.List;

import javax.swing.JFrame;

import ch.intertec.storybook.model.LocationPeer;
import ch.intertec.storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class RenameLocationCountryDialog extends AbstractRenameDialog {

	public RenameLocationCountryDialog(JFrame frame) {
		super(frame);
	}

	@Override
	protected List<String> getList() {
		return LocationPeer.doSelectDistinctCountry();
	}

	@Override
	protected void rename(String oldValue, String newValue) {
		LocationPeer.renameCountry(oldValue, newValue);
	}

	@Override
	protected String getDlgTitle() {
		return I18N.getMsg("msg.location.rename.country");
	}
}
