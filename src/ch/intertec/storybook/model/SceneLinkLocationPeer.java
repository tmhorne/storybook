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

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.apache.log4j.Logger;

import ch.intertec.storybook.view.modify.scene.SceneDialog;

public class SceneLinkLocationPeer {

	static Logger logger = Logger.getLogger(SceneLinkLocationPeer.class);

	/**
	 * Has to be package private!
	 * 
	 * @throws Exception
	 */
	static void createTable() throws Exception {
		String sql;
		Statement stmt;
		// drop if exists
		logger.debug("createTable: drop table " + SceneLinkLocation.TABLE_NAME);
		sql = "drop table " + SceneLinkLocation.TABLE_NAME + " if exists";
		stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);

		// create
		logger.debug("createTable: create table " + SceneLinkLocation.TABLE_NAME);
		sql = "create table "
			+ SceneLinkLocation.TABLE_NAME
			+ " (" + SceneLinkLocation.COLUMN_ID + " identity primary key,"
			+ SceneLinkLocation.COLUMN_SCENE_ID + " int,"
			+ SceneLinkLocation.COLUMN_LOCATION_ID + " int)";
		stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);
	}
	
	public static Boolean hasLinks(Scene scene, Location location) {
		try {
			final String sql = "select count("
					+ SceneLinkLocation.COLUMN_ID
					+ ") from "
					+ SceneLinkLocation.TABLE_NAME
					+ " where "
					+ SceneLinkLocation.COLUMN_SCENE_ID
					+ " = ? and "
					+ SceneLinkLocation.COLUMN_LOCATION_ID
					+ " = ?";
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, scene.getId());
			stmt.setInt(2, location.getId());
			ResultSet rs = stmt.executeQuery();
			rs.next();
			if (rs.getInt(1) > 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<SceneLinkLocation> doSelectAll() {
		try {
			List<SceneLinkLocation> list = new ArrayList<SceneLinkLocation>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + SceneLinkLocation.TABLE_NAME);
			sql.append(" order by " + SceneLinkLocation.COLUMN_ID);

			Statement stmt = PersistenceManager.getInstance()
				.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				SceneLinkLocation link = makeLink(rs);
				logger.debug("doSelectAll: " + link);
				list.add(link);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static SceneLinkLocation doSelectById(int id) throws Exception {
		String sql = "select * from " + SceneLinkLocation.TABLE_NAME
			+ " where " + SceneLinkLocation.COLUMN_ID + " = ?";
		PreparedStatement stmt = PersistenceManager.getInstance()
			.getConnection().prepareStatement(sql);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		SceneLinkLocation link = null;
		int c = 0;
		while (rs.next() && c < 2) {
			link = makeLink(rs);
			++c;
		}
		if (c == 0) {
			return null;
		}
		if (c > 1) {
			throw new Exception("more than one record found");
		}
		logger.debug("doSelectById: " + link);
		return link;
	}
	
	public static List<SceneLinkLocation> doSelectBySceneId(int sceneId) {
		try {
			List<SceneLinkLocation> list = new ArrayList<SceneLinkLocation>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + SceneLinkLocation.TABLE_NAME);
			sql.append(" where " + SceneLinkLocation.COLUMN_SCENE_ID + " = ?");
			sql.append(" order by " + SceneLinkLocation.COLUMN_LOCATION_ID);
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, sceneId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(makeLink(rs));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<SceneLinkLocation> doSelectByLocation(Location location){
		return doSelectByLocationId(location.getId());
	}

	public static List<SceneLinkLocation> doSelectByLocationId(int locationId){
		try {
			List<SceneLinkLocation> list = new ArrayList<SceneLinkLocation>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + SceneLinkLocation.TABLE_NAME);
			sql.append(" where " + SceneLinkLocation.COLUMN_LOCATION_ID + " = ?");
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, locationId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(makeLink(rs));
			}
			return list;
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static List<SceneLinkLocation> doSelect(Scene scene,
			Location location) {
		return doSelect(scene.getId(), location.getId());
	}
	
	public static List<SceneLinkLocation> doSelect(int sceneId, int locationId) {
		try {
			List<SceneLinkLocation> list = new ArrayList<SceneLinkLocation>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + SceneLinkLocation.TABLE_NAME);
			sql.append(" where " + SceneLinkLocation.COLUMN_SCENE_ID + " = ?");
			sql.append(" and " + SceneLinkLocation.COLUMN_LOCATION_ID + " = ?");
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, sceneId);
			stmt.setInt(2, locationId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(makeLink(rs));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<SceneLinkLocation>();
	}

	public static void doDelete(SceneLinkLocation link) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + SceneLinkLocation.TABLE_NAME);
		sql.append(" where " + SceneLinkLocation.COLUMN_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
			.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, link.getId());
		stmt.execute();
	}
	
	public static void doDeleteBySceneId(int sceneId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + SceneLinkLocation.TABLE_NAME);
		sql.append(" where " + SceneLinkLocation.COLUMN_SCENE_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
			.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, sceneId);
		stmt.execute();
	}

	public static void doDeleteByLocationId(int strandId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + SceneLinkLocation.TABLE_NAME);
		sql.append(" where " + SceneLinkLocation.COLUMN_LOCATION_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
			.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, strandId);
		stmt.execute();
	}
	
	public static int doCountByLocation(Location location) {
		try {
			final String sql = "select count("
				+ SceneLinkLocation.COLUMN_ID
				+ ") from "
				+ SceneLinkLocation.TABLE_NAME
				+ " where "
				+ SceneLinkLocation.COLUMN_LOCATION_ID
				+ " = ?";
			PreparedStatement stmt = PersistenceManager.getInstance()
				.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, location.getId());
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static SceneLinkLocation makeLink(ResultSet rs) throws SQLException {
		SceneLinkLocation link = new SceneLinkLocation(rs.getInt(SceneLinkLocation.COLUMN_ID));
		link.setSceneId(rs.getInt(SceneLinkLocation.COLUMN_SCENE_ID));
		link.setLocationId(rs.getInt(SceneLinkLocation.COLUMN_LOCATION_ID));
		return link;
	}
	
	public static void makeLinks(int sceneId, SceneDialog dlg){
		try {
			// remove all links
			doDeleteBySceneId(sceneId);

			// insert the new ones
			JList list = dlg.getLocationList();
			DefaultListModel model = (DefaultListModel) list.getModel();
			for (Object o : model.toArray()) {
				Location location = (Location) o;
				SceneLinkLocation link = new SceneLinkLocation();
				link.setSceneId(sceneId);
				link.setLocationId(location.getId());
				link.save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
