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

package ch.intertec.storybook.view.content;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
public class BlankPanel extends AbstractContentPanel {
	
	@Override
	protected void init() {
	}
	
	@Override
	protected void initGUI() {
		setLayout(new MigLayout(
				"fill,wrap 1",
				"[center]",
				"[grow][]"));
		removeAll();

		JLabel lbSlogan = new JLabel();
		if (Constants.Application.IS_PRO_VERSION.toBoolean()) {
			lbSlogan.setText(Constants.Application.SLOGAN_AND_VERSION_PRO
					.toString());
		} else {
			lbSlogan.setText(Constants.Application.SLOGAN_AND_VERSION
					.toString());
		}
		lbSlogan.setForeground(new Color(0x8a, 0xb0, 0x11));
		
		JLabel lbLogo = new JLabel(I18N.getIcon("icon.logo.500.blurred"));

		add(lbLogo);
		add(lbSlogan);

		setBackground(SwingTools.getBackgroundColor());
		setBorder(BorderFactory.createLoweredBevelBorder());
		setMinimumSize(new Dimension(600, 400));
		setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	}

	@Override
	public void refresh() {
		// not used
	}

	@Override
	protected void initOptionsPanel(JPanel optionsPanel) {
		// not used
	}
}
