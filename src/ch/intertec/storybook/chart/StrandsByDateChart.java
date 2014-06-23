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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.sql.Date;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;

import ch.intertec.storybook.chart.jfreechart.JFreeChartTools;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.ColorUtil;

@SuppressWarnings("serial")
public class StrandsByDateChart extends AbstractChartFrame implements
		ActionListener {
	
	private final static String CHART_NAME = "StrandsByDate";
	
	private ChartPanel chartPanel;
	private double average;
	private JCheckBox chbUseStrandColor;
	
	public StrandsByDateChart(){
		super();
		PCSDispatcher.getInstance().addPropertyChangeListener(
				Property.ACTIVE_PART, this);
	}
	
	@Override
	protected void init() {
		// use strand colors
		chbUseStrandColor = new JCheckBox();
		chbUseStrandColor.setText(
				I18N.getMsg("msg.chart.common.use.strand.color"));
		chbUseStrandColor.setOpaque(false);
		chbUseStrandColor.addActionListener(this);
	}
	
	@Override
	protected void initGUI() {
		// title
		Object[] objs = new Object[1];
		objs[0] = ProjectTools.getProjectName();
		setTitle(I18N.getMsg("msg.report.strand.date.title", objs));

		// chart
		CategoryDataset dataset = createDataset();
		JFreeChart chart = createChart(dataset);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(400, 280));
		
		// options panel
		JPanel optionsPanel = new JPanel();
		optionsPanel.setOpaque(false);
		optionsPanel.add(chbUseStrandColor);
		
		// main panel
		panel.add(chartPanel, "grow");
		panel.add(optionsPanel);
	}

	private CategoryDataset createDataset() {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		double i = 0;
		List<Strand> strandList = StrandPeer.doSelectAll();
		Set<Date> dateSet = ScenePeer.doSelectDistinctDate();
		for (Strand strand : strandList) {
			for (Date date : dateSet) {
				int count = StrandPeer.doCountByDate(date, strand);
				dataset.addValue(count, strand, date);
				i += count;
			}
		}
		average = i / (strandList.size() + dateSet.size());
		
		return dataset;
	}
	
	private JFreeChart createChart(CategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createBarChart(
				I18N.getMsg("msg.menu.tools.charts.overall.strand.date"),
				"", // x axis label
				"", // y axis label
				dataset, // dataset
				PlotOrientation.VERTICAL, // orientation
				true, // legend
				true, // tool tips
				false // URLs
				);
	
		final CategoryPlot plot = (CategoryPlot)chart.getPlot();

		// average marker
		plot.addRangeMarker(JFreeChartTools.getAverageMarker(average),
				Layer.FOREGROUND);
		
		// set strand colors
		List<Strand> strandList = StrandPeer.doSelectAll();
		Color[] colors = ColorUtil.getDarkColors(
				ColorUtil.getPastelColors(), 0.35);
		Color[] strandColors = new Color[strandList.size()];
		int i = 0;
		for (Strand strand : strandList) {
			strandColors[i] = ColorUtil.darker(strand.getColor(), 0.25);
			++i;
		}
		BarRenderer renderer = (BarRenderer)plot.getRenderer();
		for (int s = 0; s < dataset.getRowCount(); ++s) {
			Color color;
			if(chbUseStrandColor.isSelected()){
				color = strandColors[s % strandColors.length];
			} else {
				color = colors[s % colors.length];
			}
			renderer.setSeriesPaint(s, color);
		}

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
	public void propertyChange(PropertyChangeEvent evt) {
		if (PCSDispatcher.isPropertyFired(Property.ACTIVE_PART, evt)) {
			refresh();
			return;
		}
		super.propertyChange(evt);
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
