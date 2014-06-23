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

package ch.intertec.storybook.chart.jfreechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.Date;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.ColorUtil;

public class JFreeChartTools {
	public static void hideLegend(CategoryPlot plot) {
		plot.setFixedLegendItems(new LegendItemCollection());
	}
	
	public static void hideDomainAxis(CategoryPlot plot) {
		CategoryAxis axis = (CategoryAxis) plot.getDomainAxis();
		axis.setTickMarksVisible(false);
		axis.setTickLabelsVisible(false);
	}

	public static void hideRangeAxis(CategoryPlot plot) {
		ValueAxis axis = (ValueAxis) plot.getRangeAxis();
		axis.setTickMarksVisible(false);
		axis.setTickLabelsVisible(false);
	}

	public static ItemLabelPosition getNiceItemLabelPosition(){
		// label position
		ItemLabelAnchor itemLabelAnchor = ItemLabelAnchor.OUTSIDE6;
		TextAnchor textAnchor = TextAnchor.BOTTOM_LEFT;
		TextAnchor rotationAnchor = TextAnchor.TOP_LEFT;
		double angle = Math.toRadians(270);
		return new ItemLabelPosition(
				itemLabelAnchor, textAnchor, rotationAnchor, angle);		
	}
	
	public static void setNiceSeriesColors(CategoryDataset dataset,
			AbstractRenderer renderer) {
		Color[] colors = ColorUtil.getDarkColors(
				ColorUtil.getPastelColors(), 0.35);
		for (int s = 0; s < dataset.getRowCount(); ++s) {
			Color color = colors[s % colors.length];
			renderer.setSeriesPaint(s, color);
		}
	}
	
	public static Marker getAverageMarker(double value){
		final Marker marker = new ValueMarker(
				value, Color.red, new BasicStroke(0.3f));
		marker.setLabel(I18N.getMsg("msg.common.average"));
		marker.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
		marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
		marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
		return marker;
	}
	
	public static Marker getDateMarker(Date date) {
		return getDateMarker(date, date.toString());
	}

	public static Marker getDateMarker(Date date, String str) {
		return getDateMarker(date, str, false);
	}

	public static Marker getDateMarker(Date date, String str, boolean start) {
		double value = date.getTime();
		final Marker marker = new ValueMarker(value, Color.red,
				new BasicStroke(0.3f));
		marker.setLabel(str);
		marker.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
		marker.setLabelAnchor(RectangleAnchor.BOTTOM);
		if (start) {
			marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
		} else {
			marker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
		}
		return marker;
	}
	
	public static Marker getDateIntervalMarker(Date startDate, Date endDate) {
		String str = startDate.toString() + " - " + endDate.toString();
		return getDateIntervalMarker(startDate, endDate, str);
	}
	
	public static Marker getDateIntervalMarker(Date startDate, Date endDate, String str){
		double start = startDate.getTime();
		double end = endDate.getTime();
		Stroke stroke = new BasicStroke(0.3f);
		final Marker marker = new IntervalMarker(
				start, end,
				Color.pink, stroke,
				Color.black, stroke,
				0.5f
				);
		marker.setLabel(str);
		marker.setLabelAnchor(RectangleAnchor.BOTTOM);
		marker.setLabelTextAnchor(TextAnchor.BOTTOM_CENTER);
		return marker;
	}
}
