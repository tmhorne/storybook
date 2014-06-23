package ch.intertec.storybook.jasper;

import java.awt.Dimension;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JRViewer;

@SuppressWarnings("serial")
public class ExportPreview extends JRViewer {

	public ExportPreview(JasperPrint jrPrint) {
		super(jrPrint);
		super.setPreferredSize(new Dimension(420, 600));
	}

	@Override
	public void loadReport(JasperPrint jrPrint){
		super.loadReport(jrPrint);
		if (jrPrint == null) {
			return;
		}
		super.forceRefresh();
		super.setFitPageZoomRatio();
	}
}
