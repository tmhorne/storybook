package ch.intertec.storybook.chart.jfreechart;

import java.awt.Color;

import org.jfree.data.xy.XYDataItem;

public class ColorXYDataItem extends XYDataItem {

	private static final long serialVersionUID = 4159997772748578566L;
	private Color color;

	public ColorXYDataItem(Number x, Number y, Color color) {
		super(x, y);
		this.color = color;
	}

	public ColorXYDataItem(double x, double y, Color color) {
		super(x, y);
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
}
