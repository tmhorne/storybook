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

package ch.intertec.storybook.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.Constants.Application;

/**
 * Loads settings from the XML file.
 *
 * @author Martin Mustun
 */
public class SettingManager {
	private final Log log = LogFactory.getLog(this.getClass());
	
	private static SettingManager theInstance;
	private Settings settings;

	private SettingManager() {
		init();
	}
	
	/**
	 * Returns the singleton instance.
	 * @return the singleton instance.
	 * @see SettingManager
	 */
	public static SettingManager getInstance() {
		if (SettingManager.theInstance == null) {
			SettingManager.theInstance = new SettingManager();
		}
		return SettingManager.theInstance;
	}

	/**
	 * Reloads the settings from the XML file.
	 */
	public void reload(){
		init();
	}
	
	private void init(){
		try {
			File config = new File(
					Application.SETTINGS_XML_FILE.toString());
			InputStream is = new FileInputStream(config);
			Digester digester = new Digester();
			digester.addObjectCreate("storybook/settings", Settings.class);
			
			digester.addObjectCreate("storybook/settings/setting", Setting.class);
			digester.addBeanPropertySetter("storybook/settings/setting/key", "key");
			digester.addBeanPropertySetter("storybook/settings/setting/value", "value");
			digester.addSetNext("storybook/settings/setting", "add");

			settings = (Settings)digester.parse(is);
			is.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void log() {
		log.debug("settings read from "
				+ Constants.Application.SETTINGS_XML_FILE.toString());
		for (String key : settings.get().keySet()) {
			log.debug("key=" + key + ", value=" + settings.get(key));
		}
	}
	
	public String get(String key){
		return settings.get(key);
	}
}
