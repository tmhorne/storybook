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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.log4j.Logger;

import com.inet.jortho.FileUserDictionary;
import com.inet.jortho.SpellChecker;

public class SpellCheckerTools {

	private static Logger logger = Logger.getLogger(SpellCheckerTools.class);
	
	private static File userDictDir = null;
	
	public static File getDictionaryDir() throws IOException {
		try {
			File dir = new File(".");
			File file = new File(
					dir.getCanonicalPath()
					+ File.separator
					+ Constants.ProgramDirectory.DICTS);
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static URL getDictionaryDirAsURL() throws MalformedURLException,
			IOException {
		URI uri = getDictionaryDir().toURI();
		return uri.toURL();
	}
	
	public static void registerDictionaries(){
		try {
			logger.debug("registering dictionaries");
			URL url = getDictionaryDirAsURL();
			logger.debug("dictionary dir: " + url);
			String spelling = PrefManager.getInstance().getStringValue(
					Constants.Preference.SPELLING);
			if (spelling == null || spelling.isEmpty()) {
				spelling = Constants.Spelling.en_US.name();
				PrefManager.getInstance().setValue(
						Constants.Preference.SPELLING, spelling);
			}
			String lang = spelling.substring(0, 2);
			SpellChecker.registerDictionaries(url,
					"en,de,es,fr,it,nl,pl", lang);
			
			// user dictionary directory
			File userDictDir = initUserDictDir();
			logger.debug("user dict dir: " + userDictDir);
			FileUserDictionary fud =
				new FileUserDictionary(userDictDir.toString());
			SpellChecker.setUserDictionaryProvider(fud);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static File initUserDictDir() {
		if (userDictDir == null) {
			File dir = new File(System.getProperty("user.home"));
			userDictDir = new File(
					dir
					+ File.separator
					+ ".storybook"
					+ File.separator
					+ Constants.ProjectDirectory.USER_DICTS);
			userDictDir.mkdir();
		}
		return userDictDir;
	}
	
	public static boolean isSpellCheckActive() {
		String spelling = PrefManager.getInstance().getStringValue(
				Constants.Preference.SPELLING);
		if (spelling.equals(Constants.Spelling.none.name())) {
			return false;
		}
		return true;
	}
}
