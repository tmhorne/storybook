/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008-2009 Martin Mustun

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

package ch.intertec.storybook.main;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import ch.intertec.storybook.action.ActionManager;
import ch.intertec.storybook.main.MainSplitPane.ContentPanelType;
import ch.intertec.storybook.main.sidebar.Sidebar;
import ch.intertec.storybook.main.toolbar.MainToolBar;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.PersistenceManager;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.PrefManager;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.net.NetTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.content.AbstractContentPanel;
import ch.intertec.storybook.view.content.BlankPanel;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(MainFrame.class);

	private static MainFrame theInstance;
	private MainSplitPane splitPane;
	
	private boolean translatorMode = false;
	private Font defaultFont;
	private boolean showBgGradient = true;
	private int activePartId = 1;	

	public static final int PREFFERED_WIDTH = 960;
	public static final int PREFFERED_HEIGHT = 720;
	
	public static final String COMP_NAME = "main_frame";
	
	private static boolean finished = false;

	public static MainFrame getInstance() {
		if (theInstance == null) {
			theInstance = new MainFrame();
		}
		return theInstance;
	}
	
	private MainFrame(){
		super(Constants.Application.NAME.toString());
		// calling init() here would end up in a endless loop
	}
	
	public void init() {
		init(false);
	}
	
	public void init(boolean refresh) {
		finished = false;
		
		// initialize PCS dispatcher
		PCSDispatcher.getInstance();

		if (!refresh) {
			setSize(PREFFERED_WIDTH, PREFFERED_HEIGHT);
		}
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		ImageIcon icon = (ImageIcon) I18N.getIcon("icon.sb");
		setIconImage(icon.getImage());
		setName(COMP_NAME);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				// confirm exit?
				Boolean confirm = PrefManager.getInstance().getBooleanValue(
						Constants.Preference.CONFIRM_EXIT);				
				if (confirm) {
					int n = JOptionPane.showConfirmDialog(
							null,
							I18N.getMsg("msg.mainframe.want.exit"),
							I18N.getMsg("msg.common.exit"),
							JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.NO_OPTION
							|| n == JOptionPane.CLOSED_OPTION) {
						return;
					}
				}
				savePreferences();
				dispose();
				System.exit(0);
			}
		});

		// initialize action manager
		ActionManager.init();

		// recent files
		ProjectTools.updateRecentFiles();
		
		// initialize GUI
		setLayout(new MigLayout("fill"));
		setTitle();
		setJMenuBar(MainMenuBar.getInstance());		
		initGUI();
		
		// add listeners
		addListeners();
		
		if (!refresh) {
			// restore preferences
			restorePreferences();
		}
		
		finished = true;
	}

	public static boolean isReady(){
		return finished;
	}
	
	private void addListeners() {
		// listeners
		PCSDispatcher pcs = PCSDispatcher.getInstance();
		pcs.addPropertyChangeListener(Property.PROJECT, this);
		pcs.addPropertyChangeListener(Property.PART, this);
		pcs.addPropertyChangeListener(Property.ACTIVE_PART, this);
		pcs.addPropertyChangeListener(Property.REFRESH_ALL, this);
	}

	public void initGUI() {
		// clean up
		getContentPane().removeAll();
		
		// set up the tool bar
		add(new MainToolBar(), "north");
		
		if (ProjectTools.isProjectOpen()) {
			// set up the content
			add(getSplitPane(), "grow");
		} else {
			add(new BlankPanel(), "grow");
		}
		
		// set up the status bar
		add(new MainStatusBar(), "south");
	}

	public MainSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new MainSplitPane();
		}
		return splitPane;
	}

	public JScrollPane getScroller() {
		return getSplitPane().getScroller();
	}
	
	public AbstractContentPanel getContentPanel() {
		return getSplitPane().getContentPanel();
	}

	public ContentPanelType getContentPanelType() {
		return getSplitPane().getContentPanelType();
	}

	public boolean isBookPanelActive() {
		return getSplitPane().isBookPanelActive();
	}

	public boolean isChronoPanelActive() {
		return getSplitPane().isChronoPanelActive();
	}

	public boolean isManagePanelActive() {
		return getSplitPane().isManagePanelActive();
	}
	
	public void setBookPanel() {
		getSplitPane().setBookPanel();
	}

	public void setChronoPanel() {
		getSplitPane().setChronoPanel();
	}

	public void setManagePanel() {
		getSplitPane().setManagePanel();
	}
	
	public void setBlankPanel() {
		getSplitPane().setBlankPanel();
	}

	private void savePreferences() {
		try {
			// close project
			ProjectTools.closeFile();

			// save window maximize
			int xoff = 0;
			int yoff = 0;
			if ((getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
				PrefManager.getInstance().setValue(
						Constants.Preference.WINDOW_MAXIMIZE, true);
				xoff = 200;
				yoff = 50;
			} else {
				PrefManager.getInstance().setValue(
						Constants.Preference.WINDOW_MAXIMIZE, false);
			}

			// save window location
			PrefManager.getInstance().setValue(Constants.Preference.WINDOW_X,
					getLocation().x + xoff / 2);
			PrefManager.getInstance().setValue(Constants.Preference.WINDOW_Y,
					getLocation().y + yoff / 2);

			// save window size
			PrefManager.getInstance().setValue(
					Constants.Preference.WINDOW_WIDTH, getWidth() - xoff);
			PrefManager.getInstance().setValue(
					Constants.Preference.WINDOW_HEIGHT, getHeight() - yoff);

			// close connection
			PersistenceManager.getInstance().closeConnection();

			// say good bye
			logger.info("Bye.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void restorePreferences(){
		restoreShowBgGradient();
		restoreGoogleMapUrl();
		restoreDefaultFont();
		restoreWindowPosition();
	}
	
	private void restoreDefaultFont() {
		// get font name
		String name = PrefManager.getInstance().getStringValue(
				Constants.Preference.FONT_DEFAULT_NAME);
		if (name == null || name.isEmpty()) {
			name = "Dialog";
		}

		// get font style
		Integer style = PrefManager.getInstance().getIntegerValue(
				Constants.Preference.FONT_DEFAULT_STYLE);
		if (style == null) {
			style = Font.PLAIN;
		}

		// get font size
		Integer size = PrefManager.getInstance().getIntegerValue(
				Constants.Preference.FONT_DEFAULT_SIZE);
		if (size == null) {
			size = 12;
		}

		// set default font
		setDefaultFont(new Font(name, style, size));
	}

	private void restoreWindowPosition() {
		// restore window location
		int x = PrefManager.getInstance().getIntegerValue(
				Constants.Preference.WINDOW_X);
		int y = PrefManager.getInstance().getIntegerValue(
				Constants.Preference.WINDOW_Y);
		setLocationRelativeTo(null);
		setLocation(x, y);

		// restore window size
		int width = PrefManager.getInstance().getIntegerValue(
				Constants.Preference.WINDOW_WIDTH);
		int height = PrefManager.getInstance().getIntegerValue(
				Constants.Preference.WINDOW_HEIGHT);
		if (width != 0) {
			setSize(width, height);
		} else {
			setSize(MainFrame.PREFFERED_WIDTH, MainFrame.PREFFERED_HEIGHT);
		}

		// restore window maximize state
		boolean maximize = PrefManager.getInstance().getBooleanValue(
				Constants.Preference.WINDOW_MAXIMIZE);
		if (maximize) {
			setExtendedState(Frame.MAXIMIZED_BOTH);
		}
	}

	private void restoreGoogleMapUrl() {
		String url = PrefManager.getInstance().getStringValue(
				Constants.Preference.GOOGLE_MAP_URL);
		if (url == null || url.isEmpty()) {
			url = Constants.Preference.GOOGLE_MAP_DEFAULT_URL.toString();
		}
		NetTools.setGoogleMapUrl(url);
	}

	private void restoreShowBgGradient() {
		Boolean gradient = PrefManager.getInstance().getBooleanValue(
				Constants.Preference.SHOW_BG_GRADIENT);
		if (gradient == null) {
			gradient = true;
		}
		setShowBgGrandient(gradient);
	}

	public void exit() {
		processEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
	
	public void setTitle() {
		String nameAndVersion = "";
		if (Constants.Application.IS_PRO_VERSION.toBoolean()) {
			nameAndVersion = Constants.Application.NAME_PRO + " "
					+ Constants.Application.VERSION_PRO;
		} else {
			nameAndVersion = Constants.Application.NAME + " "
					+ Constants.Application.VERSION;
		}

		if (!ProjectTools.isProjectOpen()) {
			setTitle(nameAndVersion);
			return;
		}
		StringBuffer buf = new StringBuffer();
		Part part = PartPeer.doSelectById(getActivePartId());
		buf.append(ProjectTools.getProjectName());
		buf.append(" - ");
		buf.append(I18N.getMsg("msg.common.part"));
		buf.append(" ");
		buf.append(part.getNumberStr());
		buf.append(": ");
		buf.append(part.getName());
		buf.append(" - ");
		buf.append(nameAndVersion);
		setTitle(buf.toString());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (PCSDispatcher.isPropertyFired(Property.PART, evt)) {
			setTitle();
			return;
		}

		if (PCSDispatcher.isPropertyFired(Property.ACTIVE_PART, evt)) {
			setTitle();
			return;
		}

		if (PCSDispatcher.isPropertyFired(Property.PROJECT, evt)) {
			setTitle();
			initGUI();
			return;
		}
		
		if (PCSDispatcher.isPropertyFired(Property.REFRESH_ALL, evt)) {
			init(true);
			getSplitPane().refresh();
			int width = getWidth() - Sidebar.PREFERRED_WIDTH;
			getSplitPane().setDividerLocation(width);
			validate();
			return;
		}
	}
	
	public void setShowBgGrandient(boolean showBgGradient){
		this.showBgGradient = showBgGradient;
	}
	
	public boolean showBgGradient(){
		return showBgGradient;
	}
	
	public Font getDefaultFont() {
		return defaultFont;
	}

	public void setDefaultFont(Font font) {
		if (font == null) {
			return;
		}
		defaultFont = font;
		SwingTools.setUIFont(new javax.swing.plaf.FontUIResource(
				font.getName(), font.getStyle(), font.getSize()));
	}
	
	public void setTranslatorMode(boolean translatorMode) {
		this.translatorMode = translatorMode;
	}

	public boolean isInTranslatorMode() {
		return translatorMode;
	}
	
	public int getActivePartId() {
		return activePartId;
	}

	public void setActivePartId(int activePartId) {
		if (this.activePartId == activePartId) {
			return;
		}
		int old = this.activePartId;
		this.activePartId = activePartId;
		PCSDispatcher.getInstance().firePropertyChange(
				Property.ACTIVE_PART.toString(), old, activePartId);
	}
}
