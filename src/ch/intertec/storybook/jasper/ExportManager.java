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

import java.io.File;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.view.JasperViewer;
import ch.intertec.storybook.model.PersistenceManager;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;

/**
 * Loads the manager as a singleton instance with the reports to be displayed.
 *
 * @author Martin
 */
public class ExportManager implements ReportConstants {
	
	private List<ExportReport> reportList;
	private static JasperPrint jasperPrint;
	private HashMap<String, String> formatMap;

	private static ExportManager theInstance;
	
	private ExportManager(){
		ConfigLoader loader = ConfigLoader.getInstance();
		// initialize available reports
		reportList = loader.getReports();
		
		// initialize available formats
		formatMap = new HashMap<String, String>();
		formatMap.put(KEY_PDF, "PDF");
		formatMap.put(KEY_HTML, "HTML");
		formatMap.put(KEY_CSV, "CSV (comma-separated values)");
		formatMap.put(KEY_TEXT, "Text (UTF-8)");
		formatMap.put(KEY_RTF, "RTF (Rich Text Format)");
		formatMap.put(KEY_ODT, "ODT (OpenDocument Text)");
	}
	
	/**
	 * Returns an instance of the {@link ExportManager}.
	 * @return a singleton instance of {@link ExportManager}
	 */
	public static ExportManager getInstance(){
		if(theInstance == null){
			theInstance = new ExportManager();
		}
		return theInstance;
	}
	
	/**
	 * Returns all of the loaded reports.
	 * @return a {@link List} of {@link ExportReport} objects containing report 
	 * 			descriptors.
	 */
	public List<ExportReport> getReportList() {
		return reportList;
	}

	/**
	 * Returns a {@link HashMap} of possible formats available for reporting.
	 * @return a {@link HashMap} of possible formats available for reporting.
	 */
	public HashMap<String, String> getFormatMap() {
		return formatMap;
	}
		
	/**
	 * Runs the requested report through Jasper.
	 * @param er the {@link ExportReport} instance containing the report information.
	 */
	public static void fillReport(ExportReport er) {
		try {
			Connection connection = PersistenceManager.getInstance()
					.getConnection();
			String sourceFileName = REPORT_DIR + File.separator
					+ er.getJasperReportName() + ".jasper";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PARAM_TITLE,
					er.getName() + " - " + I18N.getMsg("msg.common.project")
							+ ": \"" + ProjectTools.getProjectName() + "\"");
			params.put(PARAM_SUBREPORT_DIR, REPORT_DIR + File.separator);
			jasperPrint = JasperFillManager.fillReport(sourceFileName, params,
					connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static JasperPrint getJasperPrint(){
		return jasperPrint;
	}
	
	/**
	 * Exports the report into the given place in the given format. 
	 * @param key the format the report is to be returned in.
	 * @param er the {@link ExportReport} descriptor instance to run.
	 * @return the file name of the report created.
	 * @see ReportConstants#KEY_CSV
	 * @see ReportConstants#KEY_HTML
	 * @see ReportConstants#KEY_ODT
	 * @see ReportConstants#KEY_PDF
	 * @see ReportConstants#KEY_PREVIEW
	 * @see ReportConstants#KEY_RTF
	 * @see ReportConstants#KEY_TEXT
	 */
	public static String export(File outputDir, String key, ExportReport er) {
		try {
			String exportFileName = "";
			if (!KEY_PREVIEW.equals(key)) {
				outputDir.mkdirs();
			}
			ExportManager.fillReport(er);
			
			if(KEY_PDF.equals(key)){
				exportFileName = getExportFileName(outputDir, er.getName(), "pdf");
				JasperExportManager.exportReportToPdfFile(
				jasperPrint, exportFileName);
			} else if(KEY_PREVIEW.equals(key)){
				JasperViewer.viewReport(jasperPrint, false);
			} else if(KEY_HTML.equals(key)){
				exportFileName = getExportFileName(outputDir, er.getName(), "html");
				JasperExportManager.exportReportToHtmlFile(
						jasperPrint, exportFileName);
			} else if(KEY_CSV.equals(key)){
				exportFileName = getExportFileName(outputDir, er.getName(), "csv");
				JRCsvExporter csv = new JRCsvExporter();
				csv.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				csv.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, exportFileName);
				csv.exportReport();
			} else if(KEY_TEXT.equals(key)){
				exportFileName = getExportFileName(outputDir, er.getName(), "txt");
				JRTextExporter text = new JRTextExporter();
				text.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				text.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, exportFileName);
				text.setParameter(JRTextExporterParameter.PAGE_WIDTH, new Integer(80));
				text.setParameter(JRTextExporterParameter.PAGE_HEIGHT, new Integer(100));
				text.exportReport();
			} else if(KEY_RTF.equals(key)){
				exportFileName = getExportFileName(outputDir, er.getName(), "rtf");
				JRRtfExporter rtf = new JRRtfExporter();
				rtf.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				rtf.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, exportFileName);
				rtf.exportReport();				
			} else if(KEY_ODT.equals(key)){
				exportFileName = getExportFileName(outputDir, er.getName(), "odt");
				JROdtExporter odt = new JROdtExporter();
				odt.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				odt.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, exportFileName);
				odt.exportReport();								
			}
			return exportFileName;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * Creates the file name of the report.
	 * @param outputDir The directory into which the report will be placed.
	 * @param name the name of the report.
	 * @param extension the extension the report is to be given.
	 * @return the concatonated file name.
	 */
	private static String getExportFileName(File outputDir, String name, String extension){
		StringBuffer buf = new StringBuffer();
		buf.append(outputDir);
		buf.append(File.separator);
		buf.append(name);
		buf.append("_");
		buf.append(SwingTools.getTimestamp(new Date()));
		buf.append(".");
		buf.append(extension);
		return buf.toString();
	}
}
