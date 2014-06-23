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

import ch.intertec.storybook.toolkit.I18N;

/**
 * Holds a descriptor of a single export report.
 *
 * @author Martin
 */
public class ExportReport {
	private String jasperReportName;
	private String resourceKey;
	
	/**
	 * Builds an {@link ExportReport} object.
	 * @param reportName The name of the report.
	 * @param resourceKey The language resource key to display next to the report option.
	 */
	public ExportReport(String reportName, String resourceKey){
		this.jasperReportName = reportName;
		this.resourceKey = resourceKey;
	}
	
	/**
	 * A generic report constructor.
	 */
	public ExportReport() {}
	
	/**
	 * Sets the reports name.
	 * @param name the name of the jasper report file.
	 */
	public void setReportName(String name) {
		this.jasperReportName = name;
	}
	
	/**
	 * Sets the language resource key to be used in the GUI
	 * @param key the language resource key to be displayed next to the report option.
	 */
	public void setResourceKey(String key) {
		this.resourceKey = key;
	}

	@Override
	public String toString(){
		return getName();
	}
	
	/**
	 * Returns the report name of the Jasper report.
	 * @return the report name of the Jasper report.
	 */
	public String getJasperReportName() {
		return jasperReportName;
	}

	/**
	 * Returns the internationalized text of the resource.
	 * @return the internationalized text of the resource.
	 */
	public String getName() {
		return I18N.getMsg(resourceKey);
	}
}
