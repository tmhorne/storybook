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

package ch.intertec.storybook.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import ch.intertec.storybook.view.assignments.ItemLinkDialog;
import ch.intertec.storybook.view.assignments.ItemLinkPanel;
import ch.intertec.storybook.view.assignments.ItemLinksDialog;

public class ItemLinkPeer {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ItemLinkPeer.class);

	public static ArrayList<ItemLink> doSelectAll() {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + ItemLink.TABLE_NAME);
			sql.append(" order by " + ItemLink.COLUMN_ID);
			Statement stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());
			return makeLinksFromResultSet(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<ItemLink> doSelectByItemId(int id) throws Exception {
		String sql = "select * from " + TagLink.TABLE_NAME + " where "
				+ TagLink.COLUMN_TAG_ID + " = ?";
		PreparedStatement stmt = PersistenceManager.getInstance()
				.getConnection().prepareStatement(sql);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		ArrayList<ItemLink> links = new ArrayList<ItemLink>();
		while (rs.next()) {
			links.add(makeLink(rs));
		}
		return links;
	}

	public static ItemLink makeLink(ResultSet rs) throws SQLException {
		ItemLink link = new ItemLink(rs.getInt(TagLink.COLUMN_ID));
		link.setTagId(rs.getInt(TagLink.COLUMN_TAG_ID));
		link.setStartSceneId(rs.getInt(TagLink.COLUMN_START_SCENE_ID));
		link.setEndSceneId(rs.getInt(TagLink.COLUMN_END_SCENE_ID));
		link.setCharacterId(rs.getInt(TagLink.COLUMN_CHARACTER_ID));
		link.setLocationId(rs.getInt(TagLink.COLUMN_LOCATION_ID));
		return link;
	}

	public static ArrayList<TagLink> toTagLinks(ArrayList<ItemLink> in) {
		ArrayList<TagLink> out = new ArrayList<TagLink>();
		for (ItemLink link : in) {
			out.add(link);
		}
		return out;
	}

	public static void makeOrUpdateItemLink(ItemLinkDialog dlg, boolean edit)
			throws Exception {
		TagLinkPeer.makeOrUpdateTagLink(dlg, edit);
	}

	public static void makeOrUpdateItemLinks(ItemLinksDialog dlg, boolean edit)
			throws Exception {
		ArrayList<ItemLinkPanel> panels = dlg.getItemLinkPanels();
		for (ItemLinkPanel panel : panels) {
			TagLinkPeer.makeOrUpdate(panel);
		}
	}

	public static ArrayList<ItemLink> doSelectByCharacterId(int characterId) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + ItemLink.TABLE_NAME);
			sql.append(" where " + ItemLink.COLUMN_CHARACTER_ID + " = ?");
			sql.append(" order by " + ItemLink.COLUMN_TAG_ID);
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, characterId);
			ResultSet rs = stmt.executeQuery();
			return makeLinksFromResultSet(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<ItemLink>();
	}

	public static ArrayList<ItemLink> doSelectByLocationId(int locationId) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + ItemLink.TABLE_NAME);
			sql.append(" where " + ItemLink.COLUMN_LOCATION_ID + " = ?");
			sql.append(" order by " + ItemLink.COLUMN_TAG_ID);
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, locationId);
			ResultSet rs = stmt.executeQuery();
			return makeLinksFromResultSet(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<ItemLink>();
	}

	public static ArrayList<ItemLink> doSelectBySceneId(int sceneId) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + ItemLink.TABLE_NAME);
			sql.append(" where " + ItemLink.COLUMN_START_SCENE_ID + " = ?");
			sql.append(" order by " + ItemLink.COLUMN_TAG_ID);
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, sceneId);
			ResultSet rs = stmt.executeQuery();
			return makeLinksFromResultSet(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<ItemLink>();
	}

	public static void doDeleteByItemId(int itemId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + ItemLink.TABLE_NAME);
		sql.append(" where " + ItemLink.COLUMN_TAG_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
				.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, itemId);
		stmt.execute();
	}

	private static ArrayList<ItemLink> makeLinksFromResultSet(ResultSet rs)
			throws SQLException {
		ArrayList<ItemLink> list = new ArrayList<ItemLink>();
		while (rs.next()) {
			ItemLink link = makeLink(rs);
			if (link == null) {
				continue;
			}
			list.add(link);
		}
		return list;
	}
}
