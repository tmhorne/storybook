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

package ch.intertec.storybook.chart;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;

import ch.intertec.storybook.chart.jfreechart.DbTableCategoryItemLabelGenerator;
import ch.intertec.storybook.chart.jfreechart.JFreeChartTools;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.LocationPeer;
import ch.intertec.storybook.model.SceneLinkLocationPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;

@SuppressWarnings("serial")
public class OccurrenceOfLocationsChart extends AbstractChartFrame implements
		ActionListener {
	
	private final static String CHART_NAME = "OccurrenceOfLocations";
	
	private ChartPanel chartPanel;
	private double average;
	private List<JCheckBox> chbCountryList;

	@Override
	protected void init() {
		chbCountryList = ChartTools.createCountryCheckBoxes(this);
	}
	
	@Override
	protected void initGUI() {
		// title
		setTitle(I18N.getMsg("msg.report.location.occurrence.title",
				ProjectTools.getProjectName()));
		
		// chart
		CategoryDataset dataset = createDataset();
		JFreeChart chart = createChart(dataset);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(400, 280));

		// options panel
		for (JCheckBox chb : chbCountryList) {
			optionsPanel.add(chb);
		}

		// main panel
		panel.add(chartPanel, "grow");
	}
	
    private CategoryDataset createDataset() {
    	final List<Location> list = new ArrayList<Location>();
		for (JCheckBox chb : chbCountryList) {
			if (chb.isSelected()) {
				String str = chb.getText().equals("-") ? "" : chb.getText();
				list.addAll(LocationPeer.doSelectByCountry(str));
			}
		}
    	
        // create dataset
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int i = 1;
		double sum = 0;
		for (Location location : list) {
			int count = SceneLinkLocationPeer.doCountByLocation(location);
			dataset.addValue(count, location, new Integer(1));		
			sum += count;
			++i;
		}
		
		average = sum / list.size();
        return dataset;
    }
	
	private JFreeChart createChart(CategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createBarChart(
				I18N.getMsg("msg.menu.tools.charts.overall.location.occurrence"),
				"", // x axis label
				"", // y axis label
				dataset, // dataset
				PlotOrientation.VERTICAL, // orientation
				true, // legend
				true, // tool tips
				false // URLs
				);
		
		final CategoryPlot plot = (CategoryPlot)chart.getPlot();

		// hide domain axis
		JFreeChartTools.hideDomainAxis(plot);
		
		// average marker
		plot.addRangeMarker(JFreeChartTools.getAverageMarker(average),
				Layer.FOREGROUND);
		
		BarRenderer renderer = (BarRenderer)plot.getRenderer();
		
		// set nice colors
		JFreeChartTools.setNiceSeriesColors(dataset, renderer);
		
		// item labels
		CategoryItemLabelGenerator generator = new DbTableCategoryItemLabelGenerator();
		renderer.setBaseItemLabelGenerator(generator);
		renderer.setBaseItemLabelsVisible(true);
		
		// set label positions
		ItemLabelPosition pos = JFreeChartTools.getNiceItemLabelPosition();		
		renderer.setBasePositiveItemLabelPosition(pos);
		renderer.setPositiveItemLabelPositionFallback(pos);
		
		return chart;
	}

	@Override
	public String getChartName() {
		return CHART_NAME;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		refresh();
	}

	@Override
	protected void beforeRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void afterRefresh() {
		// TODO Auto-generated method stub
		
	}
}
