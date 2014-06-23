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

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import ch.intertec.storybook.config.SettingManager;
import ch.intertec.storybook.main.MainFrame;


public class I18N {

	private static Logger logger = Logger.getLogger(I18N.class);
	
	private static ResourceBundle iconResourceBundle = null;
	private static ResourceBundle messageResourceBundle = null;

	public static String getCountryLanguage(Locale locale){
		return locale.getLanguage() + "_" + locale.getCountry();
	}
	
	public static DateFormat getDateFormatterWithTime() {
		DateFormat formatter;
		if (isGerman()) {
			formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		} else {
			formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		}
		return formatter;
	}

	public static DateFormat getDateFormatter() {
		DateFormat formatter;
		if (isGerman()) {
			formatter = new SimpleDateFormat("yyyy/MM/dd");
		} else {
			formatter = new SimpleDateFormat("dd.MM.yyyy");
		}
		return formatter;
	}
	
	public static boolean isEnglish() {
		Locale locale = UIManager.getDefaults().getDefaultLocale();
		if (locale == Locale.ENGLISH) {
			return true;
		}
		return false;
	}

	public static boolean isGerman() {
		Locale locale = UIManager.getDefaults().getDefaultLocale();
		if (locale == Locale.GERMAN) {
			return true;
		}
		return false;
	}

	public static final String getMsg(String resourceKey, Object arg) {
		Object[] args = new Object[]{arg};
		return getMsg(resourceKey, args);
	}
	
	public static final String getMsg(String resourceKey, Object[] args) {
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(Locale.getDefault());
		String pattern = getMessageResourceBundle().getString(resourceKey);
		formatter.applyPattern(pattern);
		return formatter.format(args);
	}
	
	public static final void setMnemonic(JMenuItem menuItem, int englishKey){
		setMnemonic(menuItem, englishKey, englishKey);
	}
	
	public static final void setMnemonic(JMenuItem menuItem, int englishKey, int germanKey){
		if(Locale.getDefault() == Locale.GERMANY){
			menuItem.setMnemonic(germanKey);
		} else {
			menuItem.setMnemonic(englishKey);	
		}
	}
	
	public static final void setMnemonic(JMenu menu, int englishKey){
		setMnemonic(menu, englishKey, englishKey);
	}
	
	public static final void setMnemonic(JMenu menu, int englishKey, int germanKey){
		if(Locale.getDefault() == Locale.GERMANY){
			menu.setMnemonic(germanKey);
		} else {
			menu.setMnemonic(englishKey);	
		}
	}
	
	/**
	 * Reads the saved language and initializes the resource bundles.
	 */
	public static final void initResourceBundles() {
		Locale locale;
		try {
			String countryLanguage = PrefManager.getInstance().getStringValue(
					Constants.Preference.LANG);
			Constants.Language lang = Constants.Language
					.valueOf(countryLanguage);
			locale = lang.getLocale();
		} catch (IllegalArgumentException e) {
			// in case this fails, set the default locale
			locale = Locale.US;
		}
		initResourceBundles(locale);
	}

	public static final void initResourceBundles(Locale locale) {
		ResourceBundle.clearCache();
		messageResourceBundle = null;
		Locale.setDefault(locale);
		UIManager.getDefaults().setDefaultLocale(locale);
	}

	/**
	 * Gets the message resource bundle.
	 * 
	 * @return the message resource bundle
	 */
	public static final ResourceBundle getMessageResourceBundle() {
		if (messageResourceBundle == null) {
			final String resourceDir = SettingManager.getInstance().get(
					Constants.Settings.resourceDir.name());
			messageResourceBundle = ResourceBundle.getBundle(
					resourceDir + ".messages",
					Locale.getDefault());
		}
		return messageResourceBundle;
	}

	public static String getMsg(String resourceKey) {
		ResourceBundle rb = getMessageResourceBundle();
		return rb.getString(resourceKey);
	}

	public static String getMsg(String resourceKey, boolean required) {
		ResourceBundle rb = getMessageResourceBundle();
		StringBuffer buf = new StringBuffer();
		if (required) {
			buf.append('*');
		}
		buf.append(rb.getString(resourceKey));
		return buf.toString();
	}

	public static String getMsgColon(String resourceKey) {
		return getMsgColon(resourceKey, false);
	}

	public static String getMsgDot(String resourceKey) {
		return getMsg(resourceKey) + "...";
	}

	public static String getMsgColon(String resourceKey, boolean required) {
		ResourceBundle rb = getMessageResourceBundle();
		StringBuffer buf = new StringBuffer();
		if (required) {
			buf.append('*');
		}
		buf.append(rb.getString(resourceKey));
		buf.append(':');
		return buf.toString();
	}
	
	public static final ResourceBundle getIconResourceBundle() {
		if (iconResourceBundle == null) {
			iconResourceBundle
				= ResourceBundle.getBundle("ch.intertec.storybook.resources.icons");
		}
		return iconResourceBundle;
	}
	
	public static Icon getIcon(String resourceKey) {
		return getImageIcon(resourceKey);
	}

	public static ImageIcon getImageIcon(String resourceKey) {
		ResourceBundle rb = getIconResourceBundle();
		String name = rb.getString(resourceKey);
		ImageIcon icon = createImageIcon(MainFrame.class, name);
		return icon;
	}
	
	public static ImageIcon createImageIcon(Class<?> c, String path) {
		java.net.URL imgURL = c.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			logger.error("Couldn't find file: " + path);
			return null;
		}
	}
}
