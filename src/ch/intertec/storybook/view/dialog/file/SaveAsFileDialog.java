package ch.intertec.storybook.view.dialog.file;

import java.io.File;

import javax.swing.JFrame;

import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;

@SuppressWarnings("serial")
public class SaveAsFileDialog extends AbstractFileDialog {

	public SaveAsFileDialog(JFrame frame) {
		super(frame);
		setTitle(I18N.getMsg("msg.file.save.as"));
		initGUI();
		File file = ProjectTools.getCurrentFile();
		setDir(file.getParent());
		setFilename(ProjectTools.getProjectName() + " ("
				+ I18N.getMsg("msg.common.copy") + ")");
	}
}
