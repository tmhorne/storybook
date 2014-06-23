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

package ch.intertec.storybook.main.sidebar;

import javax.swing.tree.DefaultMutableTreeNode;

import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.ItemPeer;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.TagPeer;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.LocationPeer;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.SbCharacterPeer;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.model.thin.ThinLocation;
import ch.intertec.storybook.model.thin.ThinScene;
import ch.intertec.storybook.toolkit.I18N;

public class NodeFactory {

	public static void createCharacterNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode category = null;
		DefaultMutableTreeNode character = null;

		// central characters
		SbCharacter catCharacter = new SbCharacter();
		catCharacter.setCategory(SbCharacter.CATEGORY_CENTRAL);
		DbTableNode node = new DbTableNode("msg.category.central.characters",
				catCharacter);
		category = new DefaultMutableTreeNode(node);
		top.add(category);
		for (SbCharacter ch : SbCharacterPeer
				.doSelectByCategory(SbCharacter.CATEGORY_CENTRAL)) {
			character = new DefaultMutableTreeNode(ch);
			category.add(character);
		}

		// minor characters
		catCharacter = new SbCharacter();
		catCharacter.setCategory(SbCharacter.CATEGORY_MINOR);
		node = new DbTableNode("msg.category.minor.characters", catCharacter);
		category = new DefaultMutableTreeNode(node);
		top.add(category);
		for (SbCharacter ch : SbCharacterPeer
				.doSelectByCategory(SbCharacter.CATEGORY_MINOR)) {
			character = new DefaultMutableTreeNode(ch);
			category.add(character);
		}
	}

	public static void createChapterNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode node = null;
		DefaultMutableTreeNode partNode = null;

		// unassigned scenes
		node = new DefaultMutableTreeNode(I18N.getMsg("msg.unassigned.scenes"));
		for (Scene scene : ScenePeer
				.doSelectByChapterId(ScenePeer.UNASIGNED_CHAPTER_ID)) {
			ThinScene tscene = new ThinScene(scene);
			DefaultMutableTreeNode sceneNode = new DefaultMutableTreeNode(
					tscene);
			node.add(sceneNode);
		}
		top.add(node);

		for (Part part : PartPeer.doSelectAll()) {
			partNode = new DefaultMutableTreeNode(part);
			for (Chapter chapter : ChapterPeer.doSelectByPart(part)) {
				node = new DefaultMutableTreeNode(chapter);
				for (Scene scene : ScenePeer.doSelectByChapter(chapter)) {
					ThinScene tscene = new ThinScene(scene);
					DefaultMutableTreeNode sceneNode = new DefaultMutableTreeNode(
							tscene);
					node.add(sceneNode);
				}
				partNode.add(node);
			}
			top.add(partNode);
		}
	}

	public static void createStrandNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode node = null;
		for (Strand strand : StrandPeer.doSelectAll()) {
			node = new DefaultMutableTreeNode(strand);
			top.add(node);
		}
	}

	public static void createPartNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode node = null;
		for (Part strand : PartPeer.doSelectAll()) {
			node = new DefaultMutableTreeNode(strand);
			top.add(node);
		}
	}

	public static void createLocationNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode countryNode = null;
		DefaultMutableTreeNode cityNode = null;
		DefaultMutableTreeNode node = null;

		for (String country : LocationPeer.doSelectDistinctCountry()) {
			Country cntr = new Country(country);
			countryNode = new DefaultMutableTreeNode(cntr);
			for (String city : LocationPeer.doSelectDistinctCity(country)) {
				cityNode = new DefaultMutableTreeNode(new City(city, cntr));
				for (Location location : LocationPeer.doSelectByCountryAndCity(
						country, city)) {
					ThinLocation thinLocation = new ThinLocation(location);
					node = new DefaultMutableTreeNode(thinLocation);
					cityNode.add(node);
				}
				countryNode.add(cityNode);
			}
			top.add(countryNode);
		}
	}

	public static void createTagNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode categoryNode = null;
		DefaultMutableTreeNode node = null;

		for (String category : TagPeer.doSelectDistinctCategory()) {
			Category cat = new Category(category, true);
			categoryNode = new DefaultMutableTreeNode(cat);
			for (Tag tag : TagPeer.doSelectByCategory(category)) {
				node = new DefaultMutableTreeNode(tag);
				categoryNode.add(node);
			}
			top.add(categoryNode);
		}
	}

	public static void createItemNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode categoryNode = null;
		DefaultMutableTreeNode node = null;

		for (String category : ItemPeer.doSelectDistinctCategory()) {
			Category cat = new Category(category, false);
			categoryNode = new DefaultMutableTreeNode(cat);
			for (Item item : ItemPeer.doSelectByCategory(category)) {
				node = new DefaultMutableTreeNode(item);
				categoryNode.add(node);
			}
			top.add(categoryNode);
		}
	}
}
