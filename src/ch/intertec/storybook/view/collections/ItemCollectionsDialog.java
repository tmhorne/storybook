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
package ch.intertec.storybook.view.collections;

import ch.intertec.storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class ItemCollectionsDialog extends AbstractCollectionsDialog {

	public ItemCollectionsDialog() {
		setTitle(I18N.getMsg("msg.item.collections"));
	}

	@Override
	protected void addData() {
		String[] transItems = { "msg.ic.transportation.car",
				"msg.ic.transportation.motorcycle",
				"msg.ic.transportation.bike", "msg.ic.transportation.train",
				"msg.ic.transportation.airplane",
				"msg.ic.transportation.by_foot", "msg.ic.transportation.horse",
				"msg.ic.transportation.ship" };
		add("msg.ic.transportation", transItems, "icon.large.car");

		String[] weaponItems = { "msg.ic.weapon.knife", "msg.ic.weapon.gun",
				"msg.ic.weapon.revolver", "msg.ic.weapon.candle_stick",
				"msg.ic.weapon.sword", "msg.ic.weapon.rifle" };
		add("msg.ic.weapon", weaponItems, "icon.large.gun");

		String[] mmItems = { "msg.ic.murder_mystery.DNA",
				"msg.ic.murder_mystery.blood_pattern",
				"msg.ic.murder_mystery.fingerprint",
				"msg.ic.murder_mystery.super_glue",
				"msg.ic.murder_mystery.human_hair",
				"msg.ic.murder_mystery.animal_hair",
				"msg.ic.murder_mystery.textile_fibers" };
		add("msg.ic.murder_mystery", mmItems, "icon.large.murder_mystery");

		String[] clothesItems = { "msg.ic.clothes.pullover",
				"msg.ic.clothes.sweater", "msg.ic.clothes.shirt",
				"msg.ic.clothes.chemise", "msg.ic.clothes.pants",
				"msg.ic.clothes.coat", "msg.ic.clothes.skirt",
				"msg.ic.clothes.shorts" };
		add("msg.ic.clothes", clothesItems, "icon.large.shirt");

		String[] toolItems = { "msg.ic.tool.rope", "msg.ic.tool.spanner",
				"msg.ic.tool.ax", "msg.ic.tool.saw", "msg.ic.tool.chain_saw",
				"msg.ic.tool.duct_tape", "msg.ic.tool.drill",
				"msg.ic.tool.hammer", "msg.ic.tool.flashlight",
				"msg.ic.tool.plunger", "msg.ic.tool.screwdriver",
				"msg.ic.tool.staple_gun", "msg.ic.tool.wrench" };
		add("msg.ic.tool", toolItems, "icon.large.hammer");

		String[] ftItems = { "msg.ic.fairytale.armor",
				"msg.ic.fairytale.shield", "msg.ic.fairytale.sword",
				"msg.ic.fairytale.magic_ring", "msg.ic.fairytale.potion",
				"msg.ic.fairytale.cloak_of_invisibility",
				"msg.ic.fairytale.golden_apple",
				"msg.ic.fairytale.white_shirt", "msg.ic.fairytale.iron_shoes",
				"msg.ic.fairytale.ball_of_string",
				"msg.ic.fairytale.spinning_wheel", "msg.ic.fairytale.candle",
				"msg.ic.fairytale.magic_mirror", "msg.ic.fairytale.ax",
				"msg.ic.fairytale.ruby_slippers",
				"msg.ic.fairytale.glass_slippers",
				"msg.ic.fairytale.magic_lamp", "msg.ic.fairytale.pot_of_gold" };
		add("msg.ic.fairytale", ftItems, "icon.large.snow_white");
	}

	@Override
	protected String getAddTextKey() {
		return "msg.items.add.collection";
	}
}
