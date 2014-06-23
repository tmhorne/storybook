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
import java.util.List;

import org.apache.log4j.Logger;

import ch.intertec.storybook.view.assignments.TagLinkDialog;
import ch.intertec.storybook.view.assignments.TagLinkPanel;
import ch.intertec.storybook.view.assignments.TagLinksDialog;

public class TagLinkPeer {

	static Logger logger = Logger.getLogger(TagLinkPeer.class);

	/**
	 * Has to be package private!
	 * 
	 * @throws Exception
	 */
	public static void createTable() throws Exception {
		String sql;
		Statement stmt;
		// drop if exists
		logger.debug("createTable: drop table " + TagLink.TABLE_NAME);
		sql = "drop table " + TagLink.TABLE_NAME + " if exists";
		stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);

		// create
		logger.debug("createTable: create table " + TagLink.TABLE_NAME);
		sql = "create table "
			+ TagLink.TABLE_NAME
			+ " ("
			+ TagLink.COLUMN_ID + " identity primary key,"
			+ TagLink.COLUMN_TAG_ID + " int,"
			+ TagLink.COLUMN_START_SCENE_ID + " int,"
			+ TagLink.COLUMN_END_SCENE_ID+ " int,"
			+ TagLink.COLUMN_CHARACTER_ID + " int,"
			+ TagLink.COLUMN_LOCATION_ID + " int"
			+ ")";
		stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);
	}
	
	private static ArrayList<TagLink> makeLinksFromResultSet(ResultSet rs)
			throws SQLException {
		ArrayList<TagLink> list = new ArrayList<TagLink>();
		while (rs.next()) {
			TagLink link = makeLink(rs);
			if (link == null) {
				continue;
			}
			list.add(link);
		}
		return list;
	}
	
	public static ArrayList<TagLink> doSelectAll() {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + TagLink.TABLE_NAME);
			sql.append(" order by " + TagLink.COLUMN_ID);
			Statement stmt = PersistenceManager.getInstance()
				.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());
			return makeLinksFromResultSet(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<TagLink> doSelectByTagId(int id) throws Exception {
		String sql = "select * from " + TagLink.TABLE_NAME + " where "
				+ TagLink.COLUMN_TAG_ID + " = ?";
		PreparedStatement stmt = PersistenceManager.getInstance()
				.getConnection().prepareStatement(sql);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		return makeLinksFromResultSet(rs);
	}
	
	public static TagLink doSelectById(int id) throws Exception {
		String sql = "select * from " + TagLink.TABLE_NAME + " where "
				+ TagLink.COLUMN_ID + " = ?";
		PreparedStatement stmt = PersistenceManager.getInstance()
				.getConnection().prepareStatement(sql);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		return makeLink(rs);
	}
	
	public static ArrayList<TagLink> doSelect(int tagId, int sceneId) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + TagLink.TABLE_NAME);
			sql.append(" where " + TagLink.COLUMN_TAG_ID + " = ?");
			sql.append(" and " + TagLink.COLUMN_START_SCENE_ID + " = ?");
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, tagId);
			stmt.setInt(2, sceneId);
			ResultSet rs = stmt.executeQuery();
			return makeLinksFromResultSet(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<TagLink> doSelectBySceneId(int sceneId) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + TagLink.TABLE_NAME);
			sql.append(" where " + TagLink.COLUMN_START_SCENE_ID + " = ?");
			sql.append(" order by " + TagLink.COLUMN_TAG_ID);
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, sceneId);
			ResultSet rs = stmt.executeQuery();
			return makeLinksFromResultSet(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<TagLink>();
	}

	public static ArrayList<TagLink> doSelectByLocationId(int locationId) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + TagLink.TABLE_NAME);
			sql.append(" where " + TagLink.COLUMN_LOCATION_ID + " = ?");
			sql.append(" order by " + TagLink.COLUMN_TAG_ID);
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, locationId);
			ResultSet rs = stmt.executeQuery();
			return makeLinksFromResultSet(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<TagLink>();
	}

	public static ArrayList<TagLink> doSelectByCharacterId(int characterId) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + TagLink.TABLE_NAME);
			sql.append(" where " + TagLink.COLUMN_CHARACTER_ID + " = ?");
			sql.append(" order by " + TagLink.COLUMN_TAG_ID);
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, characterId);
			ResultSet rs = stmt.executeQuery();
			return makeLinksFromResultSet(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<TagLink>();
	}

	public static void doDelete(TagLink link) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + TagLink.TABLE_NAME);
		sql.append(" where " + TagLink.COLUMN_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
				.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, link.getId());
		stmt.execute();
	}
	
	public static void doDeleteBySceneId(int sceneId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + TagLink.TABLE_NAME);
		sql.append(" where " + TagLink.COLUMN_START_SCENE_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
				.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, sceneId);
		stmt.execute();
	}

	public static void doDeleteByLocationId(int locationId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + TagLink.TABLE_NAME);
		sql.append(" where " + TagLink.COLUMN_LOCATION_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
				.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, locationId);
		stmt.execute();
	}

	public static void doDeleteByCharacterId(int characterId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + TagLink.TABLE_NAME);
		sql.append(" where " + TagLink.COLUMN_CHARACTER_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
				.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, characterId);
		stmt.execute();
	}

	public static void doDeleteByTagId(int tagId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + TagLink.TABLE_NAME);
		sql.append(" where " + TagLink.COLUMN_TAG_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
				.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, tagId);
		stmt.execute();
	}

	public static TagLink makeLink(ResultSet rs) throws SQLException {
		TagLink link = new TagLink(rs.getInt(TagLink.COLUMN_ID));
		link.setTagId(rs.getInt(TagLink.COLUMN_TAG_ID));
		link.setStartSceneId(rs.getInt(TagLink.COLUMN_START_SCENE_ID));
		link.setEndSceneId(rs.getInt(TagLink.COLUMN_END_SCENE_ID));
		link.setCharacterId(rs.getInt(TagLink.COLUMN_CHARACTER_ID));
		link.setLocationId(rs.getInt(TagLink.COLUMN_LOCATION_ID));
		return link;
	}

	public static void deleteAssignments(ArrayList<TagLink> links) {
		for (TagLink link : links) {
			try {
				doDelete(link);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void makeOrUpdateTagLinks(TagLinksDialog dlg, boolean edit)
			throws Exception {
		ArrayList<TagLinkPanel> panels = dlg.getTagLinkPanels();
		for (TagLinkPanel panel : panels) {
			makeOrUpdate(panel);
		}
	}

	public static void makeOrUpdateTagLink(TagLinkDialog dlg, boolean edit)
			throws Exception {
		TagLinkPanel panel = dlg.getTagLinkPanel();
		makeOrUpdate(panel);
	}
	
	protected static void makeOrUpdate(TagLinkPanel panel) {
		try {
			TagLink link = panel.getTagLink();
			if (link.isMarkedForDeletion()) {
				TagLinkPeer.doDelete(link);
				return;
			}
			Tag tag = (Tag) panel.getTagCombo().getSelectedItem();
			link.setTag(tag);
			Location location = (Location) panel.getLocationCombo()
					.getSelectedItem();
			link.setLocation(location);
			SbCharacter character = (SbCharacter) panel.getCharacterCombo()
					.getSelectedItem();
			link.setCharacter(character);
			Scene startScene = (Scene) panel.getStartSceneCombo()
					.getSelectedItem();
			if (startScene != null) {
				link.setStartScene(startScene);
			}
			Scene endScene = (Scene) panel.getEndSceneCombo().getSelectedItem();
			if (endScene != null) {
				link.setEndScene(endScene);
			}
			link.save();
			
			// clean empty links
			List<TagLink> list = doSelectAll();
			for (TagLink link2 : list) {
				if (link2.isEmtpy()) {
					TagLinkPeer.doDelete(link2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
