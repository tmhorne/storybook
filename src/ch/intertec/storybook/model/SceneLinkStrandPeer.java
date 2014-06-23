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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ch.intertec.storybook.toolkit.swing.DbTableCheckBox;
import ch.intertec.storybook.view.modify.scene.SceneDialog;

public class SceneLinkStrandPeer {

	static Logger logger = Logger.getLogger(SceneLinkStrandPeer.class);

	/**
	 * Has to be package private!
	 * 
	 * @throws Exception
	 */
	static void createTable() throws Exception {
		String sql;
		Statement stmt;
		// drop if exists
		logger.debug("createTable: drop table " + SceneLinkStrand.TABLE_NAME);
		sql = "drop table " + SceneLinkStrand.TABLE_NAME + " if exists";
		stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);

		// create
		logger.debug("createTable: create table " + SceneLinkStrand.TABLE_NAME);
		sql = "create table "
			+ SceneLinkStrand.TABLE_NAME
			+ " (" + SceneLinkStrand.COLUMN_ID + " identity primary key,"
			+ SceneLinkStrand.COLUMN_SCENE_ID + " int,"
			+ SceneLinkStrand.COLUMN_STRAND_ID + " int)";
		stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);
	}
	
	public static Boolean hasLinks(Scene scene, Strand strand) {
		try {
			final String sql = "select count("
					+ SceneLinkStrand.COLUMN_ID
					+ ") from "
					+ SceneLinkStrand.TABLE_NAME
					+ " where "
					+ SceneLinkStrand.COLUMN_SCENE_ID
					+ " = ? and "
					+ SceneLinkStrand.COLUMN_STRAND_ID
					+ " = ?";
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, scene.getId());
			stmt.setInt(2, strand.getId());
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

	public static List<SceneLinkStrand> doSelectAll() {
		try {
			List<SceneLinkStrand> list = new ArrayList<SceneLinkStrand>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + SceneLinkStrand.TABLE_NAME);
			sql.append(" order by " + SceneLinkStrand.COLUMN_ID);

			Statement stmt = PersistenceManager.getInstance()
				.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				SceneLinkStrand link = makeLink(rs);
				logger.debug("doSelectAll: " + link);
				list.add(link);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static SceneLinkStrand doSelectById(int id) throws Exception {
		String sql = "select * from " + SceneLinkStrand.TABLE_NAME
			+ " where " + SceneLinkStrand.COLUMN_ID + " = ?";
		PreparedStatement stmt = PersistenceManager.getInstance()
			.getConnection().prepareStatement(sql);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		SceneLinkStrand link = null;
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
	
	public static List<SceneLinkStrand> doSelectBySceneId(int sceneId)
			throws Exception {
		List<SceneLinkStrand> list = new ArrayList<SceneLinkStrand>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from " + SceneLinkStrand.TABLE_NAME);
		sql.append(" where " + SceneLinkStrand.COLUMN_SCENE_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
				.prepareStatement(sql.toString());
		stmt.setInt(1, sceneId);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			list.add(makeLink(rs));
		}
		return list;
	}

	public static List<SceneLinkStrand> doSelect(int sceneId, int strandId)
			throws Exception {
		List<SceneLinkStrand> list = new ArrayList<SceneLinkStrand>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from " + SceneLinkStrand.TABLE_NAME);
		sql.append(" where " + SceneLinkStrand.COLUMN_SCENE_ID + " = ?");
		sql.append(" and " + SceneLinkStrand.COLUMN_STRAND_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
				.prepareStatement(sql.toString());
		stmt.setInt(1, sceneId);
		stmt.setInt(2, strandId);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			list.add(makeLink(rs));
		}
		return list;
	}

	public static void doDelete(SceneLinkStrand link) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + SceneLinkStrand.TABLE_NAME);
		sql.append(" where " + SceneLinkStrand.COLUMN_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
			.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, link.getId());
		stmt.execute();
	}
	
	public static void doDeleteBySceneId(int sceneId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + SceneLinkStrand.TABLE_NAME);
		sql.append(" where " + SceneLinkStrand.COLUMN_SCENE_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
			.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, sceneId);
		stmt.execute();
	}

	public static void doDeleteByStrandId(int strandId) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + SceneLinkStrand.TABLE_NAME);
		sql.append(" where " + SceneLinkStrand.COLUMN_STRAND_ID + " = ?");
		PreparedStatement stmt = PersistenceManager.getInstance()
			.getConnection().prepareStatement(sql.toString());
		stmt.setInt(1, strandId);
		stmt.execute();
	}

	public static SceneLinkStrand makeLink(ResultSet rs) throws SQLException {
		SceneLinkStrand link = new SceneLinkStrand(rs.getInt(SceneLinkStrand.COLUMN_ID));
		link.setSceneId(rs.getInt(SceneLinkStrand.COLUMN_SCENE_ID));
		link.setStrandId(rs.getInt(SceneLinkStrand.COLUMN_STRAND_ID));
		return link;
	}
	
	@SuppressWarnings("unchecked")
	public static void makeLinks(int sceneId, SceneDialog dlg){
		try {
			// remove all links
			doDeleteBySceneId(sceneId);

			// insert the new ones
			List<Object> list = dlg.getStrandLinksList();
			for (Object o : list) {
				DbTableCheckBox<Strand> tcb = (DbTableCheckBox<Strand>) o;
				if (tcb.isSelected()) {
					SceneLinkStrand link = new SceneLinkStrand();
					link.setSceneId(sceneId);
					link.setStrandId(tcb.getTable().getId());
					link.save();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<Strand> doSelectByDateAndStrand(Date date, Strand strand) {
		try {
			// select cs.STRAND_ID
			// from SCENE_STRAND cs, SCENE c, STRAND s
			// where cs.SCENE_ID = c.ID
			// and cs.STRAND_ID = s.ID
			// and c.DATE = '2008-03-1'
			// and c.STRAND_ID = '1'
				
			List<Strand> list = new ArrayList<Strand>();
			StringBuffer sql = new StringBuffer();
			sql.append("select cs." + SceneLinkStrand.COLUMN_STRAND_ID);
			sql.append(" from "
					+ SceneLinkStrand.TABLE_NAME + " cs, "
					+ Scene.TABLE_NAME + " c, "
					+ Strand.TABLE_NAME + " s");
			sql.append(" where cs." + SceneLinkStrand.COLUMN_SCENE_ID
					+ " = c." + Scene.Column.ID);
			sql.append(" and cs." + SceneLinkStrand.COLUMN_STRAND_ID
					+ " = s." + Strand.Column.ID);
			sql.append(" and c." + Scene.Column.DATE + " = ?");
			sql.append(" and c." + Scene.Column.STRAND_ID + " = ?");
			
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setDate(1, date);
			stmt.setInt(2, strand.getId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Strand s = StrandPeer.doSelectById(rs.getInt(1));
				list.add(s);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<Strand>();
	}
}
