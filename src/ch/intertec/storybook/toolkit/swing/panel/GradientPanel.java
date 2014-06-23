/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008-2009 Martin Mustun

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

package ch.intertec.storybook.toolkit.swing.panel;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import ch.intertec.storybook.toolkit.swing.ColorUtil;


@SuppressWarnings("serial")
public class GradientPanel extends JPanel {
	private Color startBgColor = Color.white;
	private Color endBgColor = Color.black;
	private boolean showBgGradient = true;

	public GradientPanel() {
		showBgGradient = false;
	}

	public GradientPanel(boolean showBgGradient, Color startBgColor,
			Color endBgColor) {
		super();
		this.showBgGradient = showBgGradient;
		this.startBgColor = startBgColor;
		this.endBgColor = endBgColor;
	}

	@Override
	public void paintComponent(Graphics g) {
		if (showBgGradient) {
			Graphics2D g2d = (Graphics2D) g;
			GradientPaint gradient = new GradientPaint(
					0, 0, startBgColor,
					this.getWidth(), this.getHeight(),
					ColorUtil.blend(Color.white, endBgColor));
			g2d.setPaint(gradient);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		} else {
			super.paintComponent(g);
		}
	}

	public Color getEndBgColor() {
		return endBgColor;
	}

	public Color getStartBgColor() {
		return startBgColor;
	}

	public void setStartBgColor(Color startBgColor) {
		this.startBgColor = startBgColor;
	}

	public void setEndBgColor(Color endBgColor) {
		this.endBgColor = endBgColor;
	}
}
