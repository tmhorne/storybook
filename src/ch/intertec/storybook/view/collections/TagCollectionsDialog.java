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
public class TagCollectionsDialog extends AbstractCollectionsDialog {

	public TagCollectionsDialog(){
		setTitle(I18N.getMsg("msg.tag.collections"));
	}
	
	@Override
	protected void addData() {
		String[] loveHateItems = { "msg.ic.love_hate.love",
				"msg.ic.love_hate.crush", "msg.ic.love_hate.affair",
				"msg.ic.love_hate.like", "msg.ic.love_hate.sympathy",
				"msg.ic.love_hate.antipathy", "msg.ic.love_hate.dislike",
				"msg.ic.love_hate.hate" };
		add("msg.ic.love_hate", loveHateItems, "icon.large.love");

		String[] moodItems = { "msg.ic.mood.good", "msg.ic.mood.bad",
				"msg.ic.mood.happy", "msg.ic.mood.sad",
				"msg.ic.mood.indifferent", "msg.ic.mood.satisfied",
				"msg.ic.mood.miserable", "msg.ic.mood.unhappy",
				"msg.ic.mood.overjoyed", "msg.ic.mood.depressive" };
		add("msg.ic.mood", moodItems, "icon.large.smile");
		
		String[] relationshipItems = { "msg.ic.relationship.family",
				"msg.ic.relationship.parent", "msg.ic.relationship.wife",
				"msg.ic.relationship.husband", "msg.ic.relationship.child",
				"msg.ic.relationship.sibling", "msg.ic.relationship.sister",
				"msg.ic.relationship.brother", "msg.ic.relationship.grandma",
				"msg.ic.relationship.grandpa", "msg.ic.relationship.fiance",
				"msg.ic.relationship.fiancee",
				"msg.ic.relationship.girlfriend",
				"msg.ic.relationship.boyfriend" };
		add("msg.ic.relationship", relationshipItems, "icon.large.relationship");

		String[] weatherItems = { "msg.ic.weather.sunny",
				"msg.ic.weather.cloudy", "msg.ic.weather.rainy",
				"msg.ic.weather.snowy", "msg.ic.weather.warm",
				"msg.ic.weather.cold" };
		add("msg.ic.weather", weatherItems, "icon.large.weather");		
	}

	@Override
	protected String getAddTextKey() {
		return "msg.tags.add.collection";
	}
}
