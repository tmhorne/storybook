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

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;

import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.SbCharacterPeer;
import ch.intertec.storybook.model.LocationPeer;
import ch.intertec.storybook.toolkit.I18N;

public class ChartTools {

	public static final String COMP_NAME_CHB_CHARACTER = "chb:character";

	public static List<JCheckBox> createCharacterCheckBoxes(
			List<JCheckBox> chbCategoryList, ActionListener comp) {
		List<JCheckBox> list = new ArrayList<JCheckBox>();
		for (JCheckBox chb : chbCategoryList) {
			if (chb.isSelected()) {
				Integer category = (Integer) chb
						.getClientProperty(SbCharacter.CATEGORY_KEY);
				List<SbCharacter> characterList = SbCharacterPeer
						.doSelectByCategory(category);
				for (SbCharacter character : characterList) {
					JCheckBox chbCharacter = new JCheckBox(character.getName());
					chbCharacter.setName(COMP_NAME_CHB_CHARACTER);
					chbCharacter.putClientProperty(
							SbCharacter.SB_CHARACTER_ID_KEY, character.getId());
					chbCharacter.addActionListener(comp);
					list.add(chbCharacter);
				}
			}
		}
		return list;
	}

	public static List<SbCharacter> getCharactersBySelectedCategories(
			List<JCheckBox> chbCategoryList) {
		// get characters by category
		List<SbCharacter> list = new ArrayList<SbCharacter>();
		for (JCheckBox chb : chbCategoryList) {
			if (chb.isSelected()) {
				Integer category = (Integer) chb
						.getClientProperty(SbCharacter.CATEGORY_KEY);
				list.addAll(SbCharacterPeer.doSelectByCategory(category));
			}
		}
		return list;
	}

	public static List<SbCharacter> getCharacterListBySelectedCharacters(
			List<JCheckBox> chbCharacterList) {
		// get characters by category
		List<SbCharacter> list = new ArrayList<SbCharacter>();
		for (JCheckBox chb : chbCharacterList) {
			if (chb.isSelected()) {
				Integer id = (Integer) chb
						.getClientProperty(SbCharacter.SB_CHARACTER_ID_KEY);
				list.add(SbCharacterPeer.doSelectById(id));
			}
		}
		return list;
	}

	public static List<JCheckBox> createCategoryCheckBoxes(ActionListener comp) {
		List<JCheckBox> list = new ArrayList<JCheckBox>();

		// central characters
		JCheckBox chb = new JCheckBox(
				I18N.getMsg("msg.category.central.characters"));
		chb.putClientProperty(SbCharacter.CATEGORY_KEY,
				SbCharacter.CATEGORY_CENTRAL);
		chb.setOpaque(false);
		chb.addActionListener(comp);
		chb.setSelected(true);
		list.add(chb);

		// minor characters
		chb = new JCheckBox(I18N.getMsg("msg.category.minor.characters"));
		chb.putClientProperty(SbCharacter.CATEGORY_KEY,
				SbCharacter.CATEGORY_MINOR);
		chb.setOpaque(false);
		chb.addActionListener(comp);
		chb.setSelected(true);
		list.add(chb);

		return list;
	}

	public static List<JCheckBox> createCountryCheckBoxes(ActionListener comp) {
		List<JCheckBox> list = new ArrayList<JCheckBox>();
		for (String str : LocationPeer.doSelectDistinctCountry()) {
			JCheckBox chb = new JCheckBox(str.isEmpty() ? "-" : str);
			chb.setOpaque(false);
			chb.addActionListener(comp);
			chb.setSelected(true);
			list.add(chb);
		}
		return list;
	}
}
