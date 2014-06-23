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

package ch.intertec.storybook.playground.jfreechart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 * A simple introduction to using JFreeChart. This demo is described in the
 * JFreeChart Developer Guide.
 */
public class PgFreeChart1 {
	/**
	 * The starting point for the demo.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {
		// create a dataset...
		DefaultPieDataset data = new DefaultPieDataset();
		data.setValue("Category 1", 43.2);
		data.setValue("Category 2", 27.9);
		data.setValue("Category 3", 79.5);
		// create a chart...
		JFreeChart chart = ChartFactory.createPieChart("Sample Pie Chart",
				data, true, // legend?
				true, // tooltips?
				false // URLs?
				);
		// create and display a frame...
		ChartFrame frame = new ChartFrame("Sample Pie Chart", chart);
		frame.pack();
		frame.setVisible(true);
	}
}
