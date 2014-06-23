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

package ch.intertec.storybook.chart.legend;

import java.awt.Color;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;

@SuppressWarnings("serial")
public abstract class AbstractLegendPanel extends JPanel implements
		IRefreshable {

	public AbstractLegendPanel() {
		init();
	}

	public AbstractLegendPanel(boolean noInit) {
	}

	protected abstract void initGUI();
	
	protected void init(){
		internalInitGUI();
		initGUI();
	}

	private void internalInitGUI() {
		MigLayout layout = new MigLayout("");
		setLayout(layout);
		setBackground(Color.white);
		setBorder(SwingTools.getBorderDefault());
	}
	
	@Override
	public void refresh() {
		removeAll();
		init();
	}
}
