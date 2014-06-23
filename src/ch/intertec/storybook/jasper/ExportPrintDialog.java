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

package ch.intertec.storybook.jasper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.desktop.DesktopException;

import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Internal;
import ch.intertec.storybook.model.InternalPeer;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.FileTools;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.ProGlassPane;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IconButton;

@SuppressWarnings("serial")
public class ExportPrintDialog extends javax.swing.JDialog implements ActionListener {

	private JTextField tfDir;
	private ExportPreview previewPanel;
	private List<JRadioButton> formatList;
	private String formatKey;
	private JComboBox reportCombo;
	
	private Timer timer;
	
	public ExportPrintDialog(JFrame frame) {
		super(frame);
		formatKey = ExportManager.KEY_PDF;
		initGUI();
		timer = new Timer(50, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				preview();
			}
		});
		timer.start();
	}

	private void initGUI() {
		MigLayout layout = new MigLayout(
				"wrap 2",
				"",
				"20[]20[top]");
		
		setTitle(I18N.getMsg("msg.dlg.export.title"));
		setLayout(layout);
		
		// export directory
		JLabel lbDir = new JLabel(I18N.getMsgColon("msg.dlg.export.folder"));
		tfDir = new JTextField(20);
		
		try {
			Internal internal = InternalPeer
					.doSelectByKey(Constants.ProjectSetting.EXPORT_LAST_DIRECTORY);
			String lastDir = internal.getStringValue();
			if (lastDir != null && !lastDir.isEmpty()) {
				tfDir.setText(lastDir);
			}
		} catch (Exception e) {
			// ignore exception
		}
		if (tfDir.getText().isEmpty()) {
			tfDir.setText(FileTools.getHomeDir().getAbsolutePath());
		}
		
		JButton btChooseDir = new JButton();
		btChooseDir.setAction(getChooseFolderAction());
		btChooseDir.setText(I18N.getMsg("msg.common.choose.folder"));
		
		JPanel leftPanel = new JPanel(new MigLayout("flowy"));
		JPanel rightPanel = new JPanel(new MigLayout("flowy"));

		// report label
		JLabel reportLb = new JLabel(I18N.getMsgColon("msg.dlg.export.report"));
		
		// report combo box
		DefaultComboBoxModel reportModel = new DefaultComboBoxModel();
		for (ExportReport report : ExportManager.getInstance().getReportList()) {
			reportModel.addElement(report);
		}
		reportCombo = new JComboBox(reportModel);
		reportCombo.setMaximumRowCount(15);
		reportCombo.addActionListener(this);
		
		// format label
		JLabel lbFormat = new JLabel(I18N.getMsgColon("msg.dlg.export.format"));
		// formats
		formatList = new ArrayList<JRadioButton>();
		HashMap<String, String> map = ExportManager.getInstance().getFormatMap();
		ButtonGroup group = new ButtonGroup();
		for (String key : map.keySet()) {
			JRadioButton rb = new JRadioButton(map.get(key));
			rb.setActionCommand(key);
			rb.addActionListener(this);
			if (ExportManager.KEY_PDF.equals(key)) {
				rb.setSelected(true);
			}
			formatList.add(rb);
			group.add(rb);
		}
				
		// export button
		JButton btExport = new JButton();
		btExport.setAction(getExportAction());
		btExport.setText(I18N.getMsg("msg.common.export"));
		btExport.setIcon(I18N.getIcon("icon.small.export"));
		SwingTools.addEnterAction(btExport, getExportAction());

		// preview panel
		previewPanel = new ExportPreview(null);
		
		// refresh button
		IconButton btRefresh = new IconButton("icon.small.refresh",
				getPreviewAction());
		btRefresh.setToolTipText(I18N.getMsg("msg.dlg.export.refresh.preview"));
		
		// enlarge button
		JButton btEnlarge = new JButton();
		btEnlarge.setAction(getEnlargeAction());
		btEnlarge.setText(I18N.getMsg("msg.dlg.export.enlarge.preview"));

		// close button
		JButton btClose = new JButton();
		btClose.setAction(getCloseAction());
		btClose.setText(I18N.getMsg("msg.common.close"));
		btClose.setIcon(I18N.getIcon("icon.small.close"));
		SwingTools.addEscAction(btClose, getCloseAction());
		
		// layout left panel
		leftPanel.add(reportLb);
		leftPanel.add(reportCombo, "gapbottom 20");
		leftPanel.add(lbFormat);
		for(JRadioButton rb: formatList){
			leftPanel.add(rb);
		}
				
		// layout right panel
		rightPanel.add(previewPanel);
		
		if (!Constants.Application.IS_PRO_VERSION.toBoolean()) {
			ProGlassPane glassPane = new ProGlassPane(this, true);
			setGlassPane(glassPane);
			glassPane.setVisible(true);
		}
		
		// layout
		add(lbDir, "span,split 3");
		add(tfDir,"grow");
		add(btChooseDir);
		add(leftPanel);
		add(rightPanel);
		add(btExport, "span,split 4");
		add(btEnlarge);
		add(btRefresh, "gap 20");
		add(btClose, "gap push");
	}

	private JDialog getThis() {
		return this;
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
			}
		};
	}
	
	private AbstractAction getExportAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				SwingTools.setWaitCursor(getThis());
				File dir = new File(tfDir.getText());
				String fileName = ExportManager.export(dir, formatKey,
						(ExportReport) reportCombo.getSelectedItem());
				SwingTools.setDefaultCursor(getThis());
				if (fileName.length() > 0) {
					SwingTools.showModalDialog(new ConfirmDialog(fileName),
							getThis());
				}
			}
		};
	}

	private AbstractAction getEnlargeAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				SwingTools.setWaitCursor(getThis());
				ExportManager.export(null, ExportManager.KEY_PREVIEW,
						(ExportReport) reportCombo.getSelectedItem());
				SwingTools.setDefaultCursor(getThis());
			}
		};
	}

	private AbstractAction getPreviewAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				preview();
			}
		};
	}

	private AbstractAction getCloseAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				try {
					Internal internal = InternalPeer
							.doSelectByKey(Constants.ProjectSetting.EXPORT_LAST_DIRECTORY);
					if (internal == null) {
						internal = new Internal();
					}
					internal.setKey(Constants.ProjectSetting.EXPORT_LAST_DIRECTORY);
					internal.setStringValue(tfDir.getText());
					internal.save();
				} catch (Exception e) {
					e.printStackTrace();
				}
				getThis().dispose();
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JRadioButton) {
			formatKey = e.getActionCommand();
		}
		if (e.getSource() == reportCombo) {
			preview();
		}
	}
	
	private void preview(){
		SwingTools.setWaitCursor(getThis());
		ExportManager.fillReport(
				(ExportReport) reportCombo.getSelectedItem());
		previewPanel.loadReport(ExportManager.getJasperPrint());
		SwingTools.setDefaultCursor(getThis());
		timer.stop();
	}
	
	private class ConfirmDialog extends JDialog {
		String fileName;

		public ConfirmDialog(String fileName) {
			this.fileName = fileName;
			initGUI();
		}

		private void initGUI() {
			MigLayout layout = new MigLayout(
					"wrap");
			setLayout(layout);
			setTitle(I18N.getMsg("msg.dlg.export.done.title"));
			
			JTextArea ta = new JTextArea(I18N.getMsg(
					"msg.dlg.export.done.text", fileName));
			ta.setEditable(false);

			// open button
			JButton btOpen = new JButton();
			btOpen.setAction(getOpenAction());
			btOpen.setText(I18N.getMsg("msg.common.open"));
			btOpen.setIcon(I18N.getIcon("icon.small.open"));
			SwingTools.addEnterAction(btOpen, getOpenAction());

			// close button
			JButton btClose = new JButton();
			btClose.setAction(getCloseAction());
			btClose.setText(I18N.getMsg("msg.common.close"));
			btClose.setIcon(I18N.getIcon("icon.small.close"));
			SwingTools.addEscAction(btClose, getCloseAction());
			
			add(ta);
			add(btOpen, "split 2,gap push,sg");
			add(btClose, "sg");
		}
		
		private AbstractAction getOpenAction() {
			return new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					try {
						Desktop.open(new File(fileName));
						getCloseAction().actionPerformed(evt);
					} catch (DesktopException e) {
						e.printStackTrace();
					}
				}
			};
		}

		private AbstractAction getCloseAction() {
			return new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					getThat().dispose();
				}
			};
		}

		private JDialog getThat() {
			return this;
		}
	}
}
