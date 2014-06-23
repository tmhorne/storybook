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

package ch.intertec.storybook;

import java.net.URL;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import ch.intertec.storybook.action.DisposeDialogAction;
import ch.intertec.storybook.config.SettingManager;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.PreferencePeer;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.Constants.Application;
import ch.intertec.storybook.toolkit.Constants.StartOption;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.PrefManager;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.SpellCheckerTools;
import ch.intertec.storybook.toolkit.net.Updater;
import ch.intertec.storybook.toolkit.swing.SplashDialog;
import ch.intertec.storybook.toolkit.swing.SwingTools;

/**
 * The Storybook application.
 * 
 * @author martin
 * 
 */
public class StorybookApp {

	private static Logger logger = Logger.getLogger(StorybookApp.class);

	public static boolean isMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac");
		// return (System.getProperty("mrj.version") != null);
	}

	/**
	 * The one and only main. Currently no arguments are handled.
	 * 
	 * @param args
	 *            arguments
	 */
	public static void main(String[] args) {

		// for (String s : args) {
		// System.out.println(s);
		// }

//		// for the mac, this stuff has to happen first, as soon as possible
//		// System.getProperty("os.name").toLowerCase().startsWith("mac");
//		if (isMac()) {
//			// mac version
//			System.setProperty("apple.laf.useScreenMenuBar", "true");
//			System.setProperty(
//					"com.apple.mrj.application.apple.menu.about.name",
//					"Storybook");
//		}
		
		final String projectFile;
		if (args.length == 1) {
			projectFile = args[0];
		} else {
			projectFile = new String();
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// initialize
				init();

				// create the main frame and show it
				MainFrame mainFrame = MainFrame.getInstance();
				mainFrame.init();

				// splash screen
				SplashDialog dlgSplash = new SplashDialog(mainFrame);
				JLabel lbSplash = new JLabel(I18N.getIcon("splash"));
				dlgSplash.add(lbSplash);
				Timer timer = new Timer(1000,
						new DisposeDialogAction(dlgSplash));
				timer.setRepeats(false);
				timer.start();
				SwingTools.showModalDialog(dlgSplash, mainFrame);

				// if version is for journalistic review only
				// check expire date here
				if (Constants.Application.IS_FOR_REVIEW.toBoolean()) {
					Calendar now = Calendar.getInstance();
					if (now.after(Constants.expireDate)) {
						JOptionPane.showMessageDialog(mainFrame,
								"This version was built for journalistic reviews only."
										+ "\nIt has expired now.",
								"Storybook Pro Version has expired",
								JOptionPane.WARNING_MESSAGE);
						System.exit(-1);
					}
				}
				
				mainFrame.setVisible(true);

				// check for updates
				if (!Constants.Application.IS_PRO_VERSION.toBoolean()) {
					boolean check = PrefManager.getInstance().getBooleanValue(
							Constants.Preference.CHECK_UPDATES);
					if (check) {
						Updater.checkForUpdate();
					}
				}

				// handle start options
				if (!projectFile.isEmpty()) {
					ProjectTools.openFile(projectFile);
					return;
				}

				StartOption so = StartOption.donothing;
				try {
					String soPref = PrefManager.getInstance().getStringValue(
							Constants.Preference.START);
					so = StartOption.valueOf(soPref);
				} catch (IllegalArgumentException e) {
					// ignore
				}
				switch (so) {
				case openproject:
					String fileName = PrefManager.getInstance().getStringValue(
							Constants.Preference.LAST_OPENED_FILE);
					if (!fileName.isEmpty()) {
						ProjectTools.openFile(fileName);
					}
					break;
				case donothing:
					// do nothing
					break;
				}
			}
		});
	}

	private static void init() {
		// log4j
		URL configFileResource = StorybookApp.class
				.getResource(Application.LOG4J_XML_FILE.toString());
		DOMConfigurator.configure(configFileResource.getFile());

		// initialize preference manager
		PrefManager.getInstance();
		PrefManager.getInstance().init();
		try {
			PreferencePeer.updateTable();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		// load settings from XML
		SettingManager.getInstance();
		SettingManager.getInstance().log();

		// initialize resource bundles
		I18N.initResourceBundles();

		// register dictionaries
		SpellCheckerTools.registerDictionaries();

		// set look and feel
		SwingTools.setLookAndFeel();

		logger.info("starting ...");
	}
}
