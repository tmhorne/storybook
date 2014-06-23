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

public class SceneLinkSbCharacterPeer {

	static Logger logger = Logger.getLogger(SceneLinkSbCharacterPeer.class);

	/**
	 * Has to be package private!
	 * 
	 * @throws Exception
	 */
	static void createTable() throws Exception {
		String sql;
		Statement stmt;
		// drop if exists
		logger.debug("createTable: drop table " + SceneLinkSbCharacter.TABLE_NAME);
		sql = "drop table " + SceneLinkSbCharacter.TABLE_NAME + " if exists";
		stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);

		// create
		logger.debug("createTable: create table " + SceneLinkSbCharacter.TABLE_NAME);
		sql = "create table "
			+ SceneLinkSbCharacter.TABLE_NAME
			+ " (" + SceneLinkSbCharacter.COLUMN_ID + " identity primary key,"
			+ SceneLinkSbCharacter.COLUMN_SCENE_ID + " int,"
			+ SceneLinkSbCharacter.COLUMN_CHARACTER_ID + " int)";
		stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);
	}
	
	public static Boolean hasLinks(Scene scene, SbCharacter character) {
		try {
			final String sql = "select count("
					+ SceneLinkSbCharacter.COLUMN_ID
					+ ") from "
					+ SceneLinkSbCharacter.TABLE_NAME
					+ " where "
					+ SceneLinkSbCharacter.COLUMN_SCENE_ID
					+ " = ? and "
					+ SceneLinkSbCharacter.COLUMN_CHARACTER_ID
					+ " = ?";
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, scene.getId());
			stmt.setInt(2, character.getId());
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

	public static List<SceneLinkSbCharacter> doSelectAll() {
		try {
			List<SceneLinkSbCharacter> list = new ArrayList<SceneLinkSbCharacter>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + SceneLinkSbCharacter.TABLE_NAME);
			sql.append(" order by " + SceneLinkSbCharacter.COLUMN_ID);

			Statement stmt = PersistenceManager.getInstance()
				.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				SceneLinkSbCharacter link = makeLink(rs);
				logger.debug("doSelectAll: " + link);
				list.add(link);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static SceneLinkSbCharacter doSelectById(int id) throws Exception {
		String sql = "select * from " + SceneLinkSbCharacter.TABLE_NAME
			+ " where " + SceneLinkSbCharacter.COLUMN_ID + " = ?";
		PreparedStatement stmt = PersistenceManager.getInstance()
			.getConnection().prepareStatement(sql);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		SceneLinkSbCharacter link = null;
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
	
	public static List<SceneLinkSbCharacter> doSelectBySceneId(int sceneId) {
		try{
			List<SceneLinkSbCharacter> list = new ArrayList<SceneLinkSbCharacter>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + SceneLinkSbCharacter.TABLE_NAME);
			sql.append(" where " + SceneLinkSbCharacter.COLUMN_SCENE_ID + " = ?");
			sql.append(" order by " + SceneLinkSbCharacter.COLUMN_CHARACTER_ID);		
			PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql.toString());
			stmt.setInt(1, sceneId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(makeLink(rs));
			}
			return list;
		} catch(Exception e){
			e.printStackTrace();
		}
		return new ArrayList<SceneLinkSbCharacter>();
	}

	public static List<SceneLinkSbCharacter> doSelectByCharacter(SbCharacter character){
		return doSelectByCharacterId(character.getId());
	}
	
	public static List<SceneLinkSbCharacter> doSelectByCharacterId(int characterId) {
		try{
			List<SceneLinkSbCharacter> list = new ArrayList<SceneLinkSbCharacter>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + SceneLinkSbCharacter.TABLE_NAME);
			sql.append(" where " + SceneLinkSbCharacter.COLUMN_CHARACTER_ID + " = ?");
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, characterId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(makeLink(rs));
			}
			return list;
		} catch(Exception e){
			e.printStackTrace();
		}
		return new ArrayList<SceneLinkSbCharacter>();
	}
	
	public static List<SceneLinkSbCharacter> doSelect(Scene scene,
			SbCharacter character) {
		return doSelect(scene.getId(), character.getId());
	}

	public static List<SceneLinkSbCharacter> doSelect(int sceneId, int characterId) {
		try {
			List<SceneLinkSbCharacter> list = new ArrayList<SceneLinkSbCharacter>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + SceneLinkSbCharacter.TABLE_NAME);
			sql.append(" where " + SceneLinkSbCharacter.COLUMN_SCENE_ID + " = ?");
			sql.append(" and " + SceneLinkSbCharacter.COLUMN_CHARACTER_ID
					+ " = ?");
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, sceneId);
			stmt.setInt(2, characterId);
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

	public static void doDelete(SceneLinkSbCharacter link) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + SceneLinkSbCharacter.TABLE_NAME);
		sql.append(" where " + SceneLinkSbCharacter.COLUMN_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
			.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, link.getId());
		stmt.execute();
	}
	
	public static void doDeleteBySceneId(int sceneId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + SceneLinkSbCharacter.TABLE_NAME);
		sql.append(" where " + SceneLinkSbCharacter.COLUMN_SCENE_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
			.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, sceneId);
		stmt.execute();
	}

	public static void doDeleteByCharacterId(int characterId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + SceneLinkSbCharacter.TABLE_NAME);
		sql.append(" where " + SceneLinkSbCharacter.COLUMN_CHARACTER_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
			.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, characterId);
		stmt.execute();
	}
	
	public static int doCountByCharacter(SbCharacter character) {
		try {
			final String sql = "select count("
				+ SceneLinkSbCharacter.COLUMN_ID
				+ ") from "
				+ SceneLinkSbCharacter.TABLE_NAME
				+ " where "
				+ SceneLinkSbCharacter.COLUMN_CHARACTER_ID
				+ " = ?";
			PreparedStatement stmt = PersistenceManager.getInstance()
				.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, character.getId());
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static SceneLinkSbCharacter makeLink(ResultSet rs) throws SQLException {
		SceneLinkSbCharacter link = new SceneLinkSbCharacter(rs.getInt(SceneLinkSbCharacter.COLUMN_ID));
		link.setSceneId(rs.getInt(SceneLinkSbCharacter.COLUMN_SCENE_ID));
		link.setCharacterId(rs.getInt(SceneLinkSbCharacter.COLUMN_CHARACTER_ID));
		return link;
	}
	
	public static void makeLinks(int sceneId, SceneDialog dlg){
		try {
			// remove all links
			doDeleteBySceneId(sceneId);

			// insert the new ones
			JList list = dlg.getCharacterList();
			DefaultListModel model = (DefaultListModel) list.getModel();
			for (Object o : model.toArray()) {
				SbCharacter character = (SbCharacter) o;
				SceneLinkSbCharacter link = new SceneLinkSbCharacter();
				link.setSceneId(sceneId);
				link.setCharacterId(character.getId());
				link.save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
