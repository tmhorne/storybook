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

package ch.intertec.storybook.toolkit.swing.table;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.toolkit.swing.ColorUtil;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.label.CleverLabel;
import ch.intertec.storybook.view.IRefreshable;

@SuppressWarnings("serial")
public class WiwwPanel extends JPanel implements IRefreshable {

	private WiwwContainer container;
	private boolean isSelected;

	public WiwwPanel(WiwwContainer container) {
		this(container, false);
	}

	public WiwwPanel(WiwwContainer container, boolean isSelected) {
		this.container = container;
		this.isSelected = isSelected;
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("insets 0 1 0 1"));
		
		// panel background color
		Color selected = SwingTools.getTableSelectionBackgroundColor();
		if (isSelected) {
			setBackground(ColorUtil.blend(Color.white, selected, 0.75));
		} else {
			setBackground(Color.white);
		}
		
		for (SbCharacter character : container.getCharacterList()) {
			CleverLabel lb = new CleverLabel("", SwingConstants.CENTER);
			Color characterColor = character.getColor() == null ?
					SwingTools.getCharacterDefaultColor() : character.getColor();
			Color color;
			if (isSelected) {
				color = ColorUtil.blend(characterColor, selected, 0.85);
			} else {
				color = characterColor;
			}
			lb.setText(character.getAbbreviation());
			lb.setBackground(color);
			lb.setPreferredSize(new Dimension(30, 20));
			add(lb);
		}		
	}

	@Override
	public void refresh() {
		removeAll();
		initGUI();
	}
}
