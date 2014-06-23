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

package ch.intertec.storybook.toolkit;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Preference;
import ch.intertec.storybook.model.PreferencePeer;
import ch.intertec.storybook.toolkit.swing.SwingTools;

public class PrefManager {

	private static Logger logger = Logger.getLogger(PrefManager.class);

	public static final String PREFERENCES_DB_NAME = "internal";
	private static final String DB_FILE_ENDING = "data.db";
	
	private static PrefManager thePreferenceManager;
	private String databaseName;
	private Connection connection;
	
	private PrefManager() {
		// make the constructor private
		connection = null;
		try {
			getPrefDir().mkdir();
			databaseName = getDBName();
			getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("init db, databaseName=" + databaseName);		
	}
	
	public static PrefManager getInstance() {
		if (thePreferenceManager == null) {
			thePreferenceManager = new PrefManager();
		}
		return thePreferenceManager;
	}

	public void init() {
		try {
			// create table
			PreferencePeer.createTable();
			// set default values
			PrefManager.getInstance().saveDefaultValues();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() throws Exception {
		if (connection == null) {
			String connectionStr = "jdbc:h2:" + databaseName;
			logger.info("connect to: " + connectionStr);
			try {
				Class.forName("org.h2.Driver");
				connection = DriverManager.getConnection(
						connectionStr, "sa", "");
			} catch (Exception e) {
				logger.error(e);
				SwingTools.showException(e);
			}
		}
		return connection;
	}

	public void closeConnection() {
		if (!isConnectionOpen()) {
			return;
		}
		try {
			connection.close();
			connection = null;
			databaseName = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnectionOpen() {
		return connection != null;
	}

	private static File getPrefDir() {
		File home = FileTools.getHomeDir();
		return new File(home + File.separator + ".storybook");
	}
	
	private String getDBName(){
		return getPrefDir() + File.separator + PREFERENCES_DB_NAME;
	}

	private static String getFullDBName() {
		return PREFERENCES_DB_NAME + "." + DB_FILE_ENDING;
	}

	public boolean isKeyExisting(String key) {
		return getStringValue(key) == null ? false : true;
	}

	public void setValue(ch.intertec.storybook.toolkit.Constants.Preference pref,
			Object value, boolean overwrite) throws Exception {
		setValue(pref.toString(), value, overwrite);
	}
	
	public void setValue(String key, Object value, boolean overwrite)
			throws Exception {
		if (!overwrite && isKeyExisting(key)) {
			return;
		}
		setValue(key, value);
	}

	public static String toCsString(List<String> list) {
		StringBuffer ret = new StringBuffer();
		Iterator<String> i = list.iterator();
		while (i.hasNext()) {
			String s = i.next();
			ret.append(s);
			ret.append("@@@");
		}
		return ret.toString();
	}

	public static ArrayList<String> toStringList(String str) {
		ArrayList<String> list = new ArrayList<String>();
		if (str == null) {
			return list;
		}
		String[] items = str.split("@@@");
		for (int i = 0; i < items.length; ++i) {
			if (items[i].isEmpty()) {
				continue;
			}
			list.add(items[i]);
		}
		return list;
	}
	
	public void setValue(ch.intertec.storybook.toolkit.Constants.Preference pref,
			Object value) throws Exception {
		setValue(pref.toString(), value);
	}
	
	public void setValue(String key, Object value) throws Exception {
		Preference pref = PreferencePeer.doSelectByKey(key);
		if (pref == null) {
			// new preference
			pref = new Preference();
			pref.setKey(key);
		}
		if (value instanceof String) {
			pref.setStringValue((String) value);
		} else if (value instanceof Integer) {
			pref.setIntegerValue((Integer) value);
		} else if (value instanceof Boolean) {
			pref.setBooleanValue((Boolean) value);
		} else {
			throw new Exception("wrong object type");
		}
		pref.save();
	}

	public String getStringValue(
			ch.intertec.storybook.toolkit.Constants.Preference pref) {
		return getStringValue(pref.toString());
	}
	
	public String getStringValue(String key) {
		try {
			Preference pref = PreferencePeer.doSelectByKey(key);
			if (pref == null) {
				return null;
			} else {
				return pref.getStringValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Integer getIntegerValue(
			ch.intertec.storybook.toolkit.Constants.Preference pref) {
		return getIntegerValue(pref.toString());
	}
	
	public Integer getIntegerValue(String key) {
		try {
			Preference pref = PreferencePeer.doSelectByKey(key);
			if (pref == null) {
				return null;
			} else {
				return pref.getIntegerValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Boolean getBooleanValue(
			ch.intertec.storybook.toolkit.Constants.Preference pref) {
		return getBooleanValue(pref.toString());
	}
	
	public Boolean getBooleanValue(String key) {
		try {
			Preference pref = PreferencePeer.doSelectByKey(key);
			if (pref == null) {
				return null;
			} else {
				return pref.getBooleanValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void saveDefaultValues() {
		try {
			setValue(Constants.Preference.LANG,
					Constants.Language.en_US.name(), false);
			setValue(Constants.Preference.LAF,
					Constants.LookAndFeel.system.name(), false);
			setValue(Constants.Preference.START,
					Constants.StartOption.donothing.name(), false);
			setValue(Constants.Preference.CONFIRM_EXIT, true, false);
			setValue(Constants.Preference.CHECK_UPDATES, true, false);
			setValue(Constants.Preference.WINDOW_WIDTH,
					MainFrame.PREFFERED_WIDTH, false);
			setValue(Constants.Preference.WINDOW_HEIGHT,
					MainFrame.PREFFERED_HEIGHT, false);
			setValue(Constants.Preference.WINDOW_X, 20, false);
			setValue(Constants.Preference.WINDOW_Y, 20, false);
			setValue(Constants.Preference.WINDOW_MAXIMIZE, false, false);
			setValue(Constants.Preference.LAST_OPENED_DIRECTORY,
					System.getProperty("user.home"), false);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public static boolean isDbExisting() {
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(DB_FILE_ENDING);
			}
		};
		File[] files = getPrefDir().listFiles(filter);
		if(files == null || files.length == 0){
			return false;
		}
		for(File file : Arrays.asList(files)){
			String fullname = file.getName();
			String fullDBName = getFullDBName();
			if(fullname.equals(fullDBName)){
				return true;
			}			
		}
		return false;
	}
}
