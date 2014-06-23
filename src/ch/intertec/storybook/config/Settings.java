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

import java.util.HashMap;
import java.util.Map;

import ch.intertec.storybook.toolkit.Constants;

/**
 * Holds the settings read from the configuration XML file.
 * 
 * @author Martin Mustun
 * @see SettingManager
 * @see Setting
 * @see Constants
 *
 */
public class Settings {
	private Map<String, String> map;

	public Settings() {
		// singleton doesn't work here since the constructor
		// has to be public for digester
		map = new HashMap<String, String>();
	}
	
	/***
	 * Adds a {@link Setting}.
	 * @param setting the setting to add to
	 * @see Setting
	 */
	public void add(Setting setting) {
		map.put(setting.getKey(), setting.getValue());
	}
	
	/**
	 * Gets the map containing the settings.
	 * @return the map
	 * @see Setting
	 */
	public Map<String, String> get(){
		return map;
	}
	
	/**
	 * Gets the setting value mapped to the given key.
	 * @param key the setting key
	 * @return the value
	 * @see Constants
	 */
	public String get(String key){
		return map.get(key);
	}
}
