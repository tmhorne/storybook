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

package ch.intertec.storybook.view.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.Internal;
import ch.intertec.storybook.model.InternalPeer;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.ExportTools;
import ch.intertec.storybook.toolkit.FileTools;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.filefilter.TXTFileFilter;
import ch.intertec.storybook.toolkit.swing.ProGlassPane;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
public class RawExportDialog extends JDialog implements CaretListener,
		ChangeListener {

	private static final int previewLimit = 2000;

	private JTextField tfFileName;
	private JCheckBox cbExpChapterWord;
	private JCheckBox cbExpChapterNumbers;
	private JCheckBox cbExpChapterDescr;
	private JCheckBox cbExpParts;
	private JCheckBox cbExpSceneTitles;
	private JButton btExport;
	private JTextArea taPreview;

	public RawExportDialog(JFrame frame) {
		super(frame);
		initGUI();
	}

	private void initGUI() {
		setTitle(I18N.getMsg("msg.export.raw"));

		MigLayout layout = new MigLayout(
				"wrap 3,fill",
				"[][]20[grow]",
				"[]20[][][top]40[]");
		setLayout(layout);

		JLabel lbIcon = new JLabel(
				(ImageIcon) I18N.getIcon("icon.large.export"));
		JLabel lbInfo = new JLabel(I18N.getMsg("msg.export.raw.info"));

		JLabel lbPreview = new JLabel(I18N.getMsg("msg.common.preview"));
		taPreview = new JTextArea();
		String text = ExportTools.getExportText(false, false, false, false,
				false, previewLimit);
		taPreview.setText(text);
		taPreview.setCaretPosition(0);
		taPreview.setEditable(false);
		taPreview.setLineWrap(true);
		taPreview.setWrapStyleWord(true);
		taPreview.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		JScrollPane scroller = new JScrollPane(taPreview);
		scroller.setPreferredSize(new Dimension(300, 200));

		JLabel lbFile = new JLabel(I18N.getMsgColon("msg.export.filename"));
		tfFileName = new JTextField(30);
		
		try {
			Internal internal = InternalPeer
					.doSelectByKey(Constants.ProjectSetting.RAW_EXPORT_LAST_FILENAME);
			String lastFilename = internal.getStringValue();
			if (lastFilename != null && !lastFilename.isEmpty()) {
				tfFileName.setText(lastFilename);
			}
		} catch (Exception e) {
			// ignore exception
		}
		if (tfFileName.getText().isEmpty()) {
			tfFileName.setText(FileTools.getHomeDir().getAbsolutePath());
		}
		tfFileName.addCaretListener(this);
		JButton btChooseFile = new JButton();
		btChooseFile.setAction(getChooseFileAction());
		btChooseFile.setText(I18N.getMsg("msg.export.choose.file"));

		// export parts
		cbExpParts = new JCheckBox();
		cbExpParts.setText(I18N.getMsg("msg.export.parts"));
		cbExpParts.addChangeListener(this);

		// export chapter numbers
		cbExpChapterNumbers = new JCheckBox();
		cbExpChapterNumbers.setText(I18N.getMsg("msg.export.chapter.numbers"));
		cbExpChapterNumbers.addChangeListener(this);

		// export chapter numbers
		cbExpChapterWord = new JCheckBox();
		cbExpChapterWord.setText(I18N.getMsg("msg.export.chapter.word"));
		cbExpChapterWord.addChangeListener(this);

		// export chapter descriptions
		cbExpChapterDescr = new JCheckBox();
		cbExpChapterDescr.setText(I18N.getMsg("msg.export.chapter.descr"));
		cbExpChapterDescr.addChangeListener(this);

		// export scene titles
		cbExpSceneTitles = new JCheckBox();
		cbExpSceneTitles.setText(I18N.getMsg("msg.export.scene.titles"));
		cbExpSceneTitles.addChangeListener(this);

		// export button
		btExport = new JButton();
		btExport.setAction(getExportAction());
		btExport.setText(I18N.getMsg("msg.common.export"));
		btExport.setIcon(I18N.getIcon("icon.small.export"));
		if (tfFileName.getText().isEmpty()) {
			btExport.setEnabled(false);
		}
		SwingTools.addEnterAction(btExport, getExportAction());

		// close button
		JButton btClose = new JButton();
		btClose.setAction(getCloseAction());
		btClose.setText(I18N.getMsg("msg.common.close"));
		btClose.setIcon(I18N.getIcon("icon.small.close"));
		SwingTools.addEscAction(btClose, getCloseAction());

		if (!Constants.Application.IS_PRO_VERSION.toBoolean()) {
			ProGlassPane glassPane = new ProGlassPane(this, true);
			setGlassPane(glassPane);
			glassPane.setVisible(true);
		}
		
		// layout
		add(lbIcon);
		add(lbInfo);

		JPanel p = new JPanel();
		p.setBorder(SwingTools.getBorderGray());
		p.setLayout(new MigLayout("wrap,fill", "", "[][fill,grow]"));
		p.add(lbPreview);
		p.add(scroller, "grow");
		add(p, "grow,spany 4");

		add(lbFile);
		add(tfFileName);

		add(btChooseFile, "span 2,gap push");

		JPanel p2 = new JPanel(new MigLayout("wrap 2"));
		p2.add(cbExpParts);
		p2.add(cbExpSceneTitles);
		p2.add(cbExpChapterNumbers);
		p2.add(cbExpChapterWord);
		p2.add(cbExpChapterDescr);
		add(p2, "span 2");

		add(btExport, "sg,span,split");
		add(btClose, "sg,gap push");

		stateChanged(null);
	}

	private JDialog getThis() {
		return this;
	}

	private AbstractAction getExportAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				String filename = tfFileName.getText();
				File file = new File(filename);

				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (!file.canWrite()) {
					JOptionPane.showMessageDialog(getThis(),
							I18N.getMsgColon("msg.export.not.writable") + "\n"
									+ filename,
							I18N.getMsg("msg.common.warning"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				file.delete();
				
//				// create writable file
//				Path path = Paths.get(filename);
//				try {
//					Files.deleteIfExists(path);
//					Files.createFile(path);
//				} catch (IOException e1) {
//					// ignore
//				}
//				if (!Files.isWritable(path)) {
//					JOptionPane.showMessageDialog(getThis(),
//							I18N.getMsgColon("msg.export.not.writable") + "\n"
//									+ filename,
//							I18N.getMsg("msg.common.warning"),
//							JOptionPane.WARNING_MESSAGE);
//					return;
//				}

				SwingTools.setWaitCursor(getThis());

				// get text
				String text = ExportTools.getExportText(
						cbExpParts.isSelected(),
						cbExpChapterNumbers.isSelected(),
						cbExpChapterWord.isSelected(),
						cbExpChapterDescr.isSelected(),
						cbExpSceneTitles.isSelected());

				// export text to file
				ExportTools.exportTextToFile(file, text);

				SwingTools.setDefaultCursor(getThis());

				try {
					Internal internal = InternalPeer
							.doSelectByKey(Constants.ProjectSetting.RAW_EXPORT_LAST_FILENAME);
					if (internal == null) {
						internal = new Internal();
					}
					internal.setKey(Constants.ProjectSetting.RAW_EXPORT_LAST_FILENAME);
					internal.setStringValue(filename);
					internal.save();
				} catch (Exception e) {
					e.printStackTrace();
				}

				JOptionPane.showMessageDialog(getThis(),
						I18N.getMsg("msg.common.export.success") + "\n"
								+ filename, I18N.getMsg("msg.common.export"),
						JOptionPane.INFORMATION_MESSAGE);
			}
		};
	}

	private AbstractAction getChooseFileAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				File currentDir = new File(tfFileName.getText());
				JFileChooser fc = new JFileChooser(currentDir);
				fc.setFileFilter(new TXTFileFilter());
				int ret = fc.showOpenDialog(getThis());
				if (ret == JFileChooser.CANCEL_OPTION) {
					return;
				}
				File file = fc.getSelectedFile();
				if (!file.getName().endsWith(".txt")) {
					file = new File(file.getPath() + ".txt");
				}
				tfFileName.setText(file.getAbsolutePath());
			}
		};
	}

	private AbstractAction getCloseAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				getThis().dispose();
			}
		};
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		if (tfFileName.getText().isEmpty()) {
			btExport.setEnabled(false);
			return;
		}
		btExport.setEnabled(true);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (cbExpChapterNumbers.isSelected()) {
			cbExpChapterWord.setEnabled(true);
		} else {
			cbExpChapterWord.setEnabled(false);
		}
		String text = ExportTools.getExportText(cbExpParts.isSelected(),
				cbExpChapterNumbers.isSelected(),
				cbExpChapterWord.isSelected(), cbExpChapterDescr.isSelected(),
				cbExpSceneTitles.isSelected(), previewLimit);
		taPreview.setText(text);
		taPreview.setCaretPosition(0);
	}
}
