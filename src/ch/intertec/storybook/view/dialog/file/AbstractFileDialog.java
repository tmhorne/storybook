package ch.intertec.storybook.view.dialog.file;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
public abstract class AbstractFileDialog extends JDialog implements
		CaretListener {

	protected JLabel lbWarning;
	protected JButton btOk;
	protected JTextField tfDir;
	protected JTextField tfName;
	protected JButton btChooseDir;
	protected boolean canceled;
	protected File file;
	private boolean hideDir=false;

	public AbstractFileDialog(JFrame frame) {
		super(frame);
	}

	protected void initGUI() {
		MigLayout layout = new MigLayout("wrap 2", "[]", "[]");
		setLayout(layout);

		JLabel lbName = new JLabel(
				I18N.getMsgColon("msg.dlg.mng.prjs.project.name"));
		tfName = new JTextField(30);
		tfName.setName("name");
		tfName.addCaretListener(this);

		JLabel lbDir = new JLabel(I18N.getMsgColon("msg.common.folder"));
		tfDir = new JTextField(30);
		tfDir.setName("folder");
		tfDir.addCaretListener(this);

		btChooseDir = new JButton();
		btChooseDir.setAction(getChooseFolderAction());
		btChooseDir.setText(I18N.getMsg("msg.common.choose.folder"));

		lbWarning = new JLabel(" ");

		// OK button
		btOk = new JButton();
		btOk.setAction(getOkAction());
		SwingTools.addEnterAction(btOk, getOkAction());
		btOk.setText(I18N.getMsg("msg.common.ok"));
		btOk.setEnabled(false);

		// cancel button
		JButton btCancel = new JButton();
		btCancel.setAction(getCancelAction());
		SwingTools.addEscAction(btCancel, getCancelAction());
		btCancel.setText(I18N.getMsg("msg.common.cancel"));
		btCancel.setIcon(I18N.getIcon("icon.small.close"));

		// layout
		add(lbName);
		add(tfName);
		if (!hideDir) {
			add(lbDir);
			add(tfDir, "split 2");
			add(btChooseDir);
		}
		add(lbWarning, "span,gapy 10");
		add(btOk, "sg,span,split 2,right,gapy 10");
		add(btCancel, "sg");
	}

	protected void setDir(String dir) {
		tfDir.setText(dir);
	}

	protected void setFilename(String filename) {
		tfName.setText(filename);
		tfName.selectAll();
	}

	protected AbstractFileDialog getThis() {
		return this;
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		if (e.getSource() instanceof JTextField) {
			if (tfName.getText().isEmpty() || tfDir.getText().isEmpty()) {
				btOk.setEnabled(false);
				return;
			}
			btOk.setEnabled(true);
			lbWarning.setText(" ");
		}
	}

	public JTextField getTfDir() {
		return tfDir;
	}

	public JTextField getTfName() {
		return tfName;
	}

	public void setTfName(String name) {
		tfName.setText(name);
	}

	public boolean isCanceled() {
		return canceled;
	}

	public File getFile() {
		return file;
	}
	
	public void setHideDir(boolean dirOnly){
//		tfDir.setEditable(false);
//		btChooseDir.setEnabled(false);
		hideDir=dirOnly;
	}

	private AbstractAction getOkAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (tfName.getText().isEmpty() || tfDir.getText().isEmpty()) {
					btOk.setEnabled(false);
					return;
				}
				File dir = new File(tfDir.getText());
				if (!dir.isDirectory() || !dir.canWrite() || !dir.canExecute()) {
					lbWarning.setText(I18N.getMsg("msg.new_file.not.writable"));
					return;
				}
				String name = tfName.getText();
				if (!name.endsWith("." + ProjectTools.DB_FILE_ENDING)) {
					name += "." + ProjectTools.DB_FILE_ENDING;
				}
				file = new File(tfDir.getText() + File.separator + name);
				if (file.exists()) {
					lbWarning.setText(I18N.getMsg("msg.new_file.file.exists"));
					return;
				}
				getThis().canceled = false;
				getThis().dispose();
			}
		};
	}

	private AbstractAction getChooseFolderAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				final JFileChooser fc = new JFileChooser(tfDir.getText());
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int ret = fc.showOpenDialog(MainFrame.getInstance());
				if (ret != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File dir = fc.getSelectedFile();
				tfDir.setText(dir.getAbsolutePath());
				lbWarning.setText(" ");
			}
		};
	}

	private AbstractAction getCancelAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				getThis().canceled = true;
				getThis().dispose();
			}
		};
	}
}