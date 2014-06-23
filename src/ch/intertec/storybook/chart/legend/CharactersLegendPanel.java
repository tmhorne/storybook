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

package ch.intertec.storybook.chart.legend;

import java.awt.Dimension;
import java.util.Collection;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.SbCharacterPeer;
import ch.intertec.storybook.toolkit.swing.ColorUtil;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.label.CleverLabel;

@SuppressWarnings("serial")
public class CharactersLegendPanel extends AbstractLegendPanel {

	private Collection<SbCharacter> collection;

	public CharactersLegendPanel() {
		// I wish I had a constructor initializer list...
		super(false);
		collection = SbCharacterPeer.doSelectAll();
		init();
	}

	public CharactersLegendPanel(Set<SbCharacter> set) {
		super(false);
		this.collection = set;
		init();
	}
	
	@Override
	public void initGUI() {
		setLayout(new MigLayout("wrap 12"));
		for (SbCharacter character : collection) {
			
			// abbreviation
			CleverLabel lbAbbr = new CleverLabel(character.getAbbreviation(),
					SwingConstants.CENTER);
			lbAbbr.setPreferredSize(new Dimension(50, 20));
			if (character.getColor() != null) {
				lbAbbr.setBackground(
						ColorUtil.darker(character.getColor(), 0.05));
			} else {
				lbAbbr.setBackground(SwingTools.getCharacterDefaultColor());
			}
			
			// name
			JLabel lbName = new JLabel();
			lbName.setText(character.getName());
			
			// layout
			add(lbAbbr, "sg");
			add(lbName, "gap after 10");
		}
	}
}
