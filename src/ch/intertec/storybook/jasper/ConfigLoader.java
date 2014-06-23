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

/*
 * Date				Author			Changes
 * Sep 14, 2008		Colin Ferm		Created
 */

package ch.intertec.storybook.jasper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.intertec.storybook.toolkit.Constants;

/**
 * Loads the report xml file.
 * 
 * @author Colin Ferm
 */
public class ConfigLoader implements ReportConstants {
	private static ConfigLoader loader = null;

	private List<ExportReport> reports = new ArrayList<ExportReport>();

	protected final Log log = LogFactory.getLog(this.getClass());

	/**
	 * Runs through the report configuration file and adds {@link ExportReport}
	 * objects to the internal array.
	 */
	private ConfigLoader() {
		try {
			File config = new File(
					Constants.Application.SETTINGS_XML_FILE.toString());
			log.info("File path: " + config.getAbsolutePath());
			log.info("File: " + config.isFile());
			// File config = new File("config.xml");
			// URL config =
			// this.getClass().getClassLoader().getResource("config.xml");
			InputStream is = new FileInputStream(config);

			Digester digester = new Digester();
			digester.push(this);
			digester.addCallMethod("storybook/jasper-reports/report",
					"addReport", 2);
			digester.addCallParam("storybook/jasper-reports/report/file", 0);
			digester.addCallParam("storybook/jasper-reports/report/key", 1);

			digester.parse(is);

			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the singleton instance of {@link ConfigLoader}.
	 * 
	 * @return the singleton instance of {@link ConfigLoader}.
	 */
	public static ConfigLoader getInstance() {
		if (ConfigLoader.loader == null) {
			ConfigLoader.loader = new ConfigLoader();
		}
		return ConfigLoader.loader;
	}

	/**
	 * Builds an {@link ExportReport} object and adds to the internal array.
	 * 
	 * @param name
	 *            the file name of the report.
	 * @param key
	 *            the resource key to use when displaying it on the UI.
	 */
	public void addReport(String name, String key) {
		ExportReport report = new ExportReport(name, key);
		this.reports.add(report);
	}

	/**
	 * Returns all of the {@link ExportReport} objects defined. The returned
	 * {@link List} is an unmodifiable list since it is to be read for
	 * configuration purposes only.
	 * 
	 * @return a {@link List} of associated {@link ExportReport} objects.
	 */
	public List<ExportReport> getReports() {
		return Collections.unmodifiableList(this.reports);
	}
}
