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


/**
 * Contains the constants required in this package.
 * Abstracted out of the {@link ExportManager} class to be reused.
 * @author Martin
 */
public interface ReportConstants {
	public static final String PARAM_TITLE = "PARAM_TITLE";
	public static final String PARAM_SUBREPORT_DIR = "SUBREPORT_DIR";
	
	/**
	 * The directory to look for reports in.
	 */
	public static final String REPORT_DIR = "reports";
	
	/**
	 * The key for PDF formated documents.
	 */
	public static final String KEY_PDF = "pdf";
	
	/**
	 * The key for HTML formated documents.
	 */
	public static final String KEY_HTML = "html";
	
	/**
	 * The key for Comma Separated documents.
	 */
	public static final String KEY_CSV = "csv";
	
	/**
	 * The key for text documents.
	 */
	public static final String KEY_TEXT = "text";
	
	/**
	 * The key for documents that are generated for previewed only.
	 */
	public static final String KEY_PREVIEW = "preview";
	
	/**
	 * The key for Rich Text formated documents.
	 */
	public static final String KEY_RTF = "rtf";
	
	/**
	 * The key for Open Document formated documents.
	 */
	public static final String KEY_ODT = "odt";
}
