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
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

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
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.SceneLinkSbCharacterPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.ColorUtil;

@SuppressWarnings("serial")
public class OccurrenceOfCharactersChart extends AbstractChartFrame implements
		ActionListener {
	
	private final static String CHART_NAME = "OccurrenceOfCharacters";
	
	private ChartPanel chartPanel;
	private List<JCheckBox> chbCategoryList;
	private JCheckBox chbUseCharacterColor;
	
	private double average;
	
	@Override
	protected void init() {
		// category check boxes
		chbCategoryList = ChartTools.createCategoryCheckBoxes(this);
		
		// use character colors
		chbUseCharacterColor = new JCheckBox();
		chbUseCharacterColor.setText(
				I18N.getMsg("msg.chart.common.use.character.color"));
		chbUseCharacterColor.setOpaque(false);
		chbUseCharacterColor.addActionListener(this);
	}
	
	@Override
	protected void initGUI() {
		// title
		Object[] objs = new Object[1];
		objs[0] = ProjectTools.getProjectName();
		setTitle(I18N.getMsg("msg.report.person.occurrence.title", objs));
		
		// chart
		CategoryDataset dataset = createDataset();
		JFreeChart chart = createChart(dataset);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(400, 280));

		// character category
		JLabel lbCategory = new JLabel(I18N.getMsgColon("msg.common.category"));
		
		// options panel
		optionsPanel.add(lbCategory);
		for (JCheckBox chb : chbCategoryList) {
			optionsPanel.add(chb);
		}
		optionsPanel.add(chbUseCharacterColor, "gap left 20");
		
		// main panel
		panel.add(chartPanel, "grow");
	}

	@Override
	public String getChartName() {
		return CHART_NAME;
	}
	
    private CategoryDataset createDataset() {
    	// get characters by category
    	final List<SbCharacter> list =
    		ChartTools.getCharactersBySelectedCategories(chbCategoryList);
    	
        // create dataset
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int i = 1;
		double sum = 0;
		for (SbCharacter character : list) {
			int count = SceneLinkSbCharacterPeer.doCountByCharacter(character);
			dataset.addValue(count, character, new Integer(1));
			sum += count;
			++i;
		}
		average = sum / list.size();
        return dataset;
    }
	
	private JFreeChart createChart(CategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createBarChart(
				I18N.getMsg("msg.menu.tools.charts.overall.character.occurrence"),
				"", // x axis label
				"", // y axis label
				dataset, // dataset
				PlotOrientation.VERTICAL, // orientation
				true, // legend
				true, // tool tips
				false // URLs
				);
		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		
		// hide domain axis
		JFreeChartTools.hideDomainAxis(plot);
	
		// average marker
		plot.addRangeMarker(JFreeChartTools.getAverageMarker(average),
				Layer.FOREGROUND);
		
		BarRenderer renderer = (BarRenderer)plot.getRenderer();

		// item labels
		CategoryItemLabelGenerator generator = new DbTableCategoryItemLabelGenerator();
		renderer.setBaseItemLabelGenerator(generator);
		renderer.setBaseItemLabelsVisible(true);
		
		// set label positions
		ItemLabelPosition pos = JFreeChartTools.getNiceItemLabelPosition();		
		renderer.setBasePositiveItemLabelPosition(pos);
		renderer.setPositiveItemLabelPositionFallback(pos);
		
		// set nice colors
		Color[] colors = ColorUtil.getDarkColors(
				ColorUtil.getPastelColors(), 0.35);
		for (int s = 0; s < dataset.getRowCount(); ++s) {
			SbCharacter character = (SbCharacter) dataset.getRowKey(s);
			Color color = character.getColor();
			
			// bar color
			if (chbUseCharacterColor.isSelected() && color != null) {
				color = ColorUtil.darker(color, 0.2);
			} else {
				color = colors[s % colors.length];
			}
			renderer.setSeriesPaint(s, color);
			
			// font color
			if (ColorUtil.isDark(color)) {
				renderer.setSeriesItemLabelPaint(0, Color.white);
			}
		}
		
		return chart;
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
