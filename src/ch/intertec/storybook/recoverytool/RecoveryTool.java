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

package ch.intertec.storybook.recoverytool;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.BasicConfigurator;

import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.toolkit.PrefManager;
import ch.intertec.storybook.toolkit.Constants.Preference;
import ch.intertec.storybook.toolkit.filefilter.H2FileFilter;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
public class RecoveryTool extends JFrame {

	public static final String VERSION = "0.2";

	private static RecoveryTool theInstance;
	private LoggerPanel loggerPanel;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					// log4j
					BasicConfigurator.configure();

					// initialize preference manager
					PrefManager.getInstance();
					PrefManager.getInstance().init();

					theInstance = new RecoveryTool();
					Recovery.doRecovery(null);
				} catch (Exception e) {
					e.printStackTrace();
					SwingTools.showException(e);
				}
			}
		});
	}

	public RecoveryTool() {
		super("Storybook Recovery Tool, Version " + VERSION);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initGUI();
		setVisible(true);
	}

	private void initGUI() {
		setLayout(new MigLayout("wrap,fill", "", "[grow][]"));
		setSize(800, 600);

		loggerPanel = new LoggerPanel();
		JButton btOpen = new JButton(getOpenAction());
		JButton btExit = new JButton(getExitAction());

		add(loggerPanel, "grow");
		add(btOpen, "split 2");
		add(btExit);
	}

	private AbstractAction getOpenAction() {
		return new AbstractAction("Open file") {
			public void actionPerformed(ActionEvent evt) {
				File file = null;
				final JFileChooser fc = new JFileChooser();
				File dir = new File(PrefManager.getInstance().getStringValue(
						Preference.LAST_OPENED_DIRECTORY));
				fc.setCurrentDirectory(dir);
				fc.addChoosableFileFilter(new H2FileFilter());
				int ret = fc.showOpenDialog(MainFrame.getInstance());
				if (ret == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					if (!file.exists()) {
						JOptionPane.showMessageDialog(getThis(), 
								"file doesn't exits or is not readable: " + file,
								"file doesn't extts",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				Recovery.doRecovery(file);
			}
		};
	}

	private AbstractAction getExitAction() {
		return new AbstractAction("Exit") {
			public void actionPerformed(ActionEvent evt) {
				getThis().dispose();
				System.exit(0);
			}
		};
	}

	private JFrame getThis() {
		return this;
	}

	public LoggerPanel getLoggerPanel() {
		return loggerPanel;
	}

	public static RecoveryTool getInstance() {
		return theInstance;
	}
}
