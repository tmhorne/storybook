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

import ch.intertec.storybook.toolkit.PrefManager;

public class PreferencePeer {

	private static Logger logger = Logger.getLogger(PreferencePeer.class);

	public static void updateTable() throws Exception {
		String sql;
		Statement stmt;
		logger.debug("updateTable: update table " + Preference.TABLE_NAME);
		sql = "alter table "
			+ Preference.TABLE_NAME
			+ " alter column "
			+ Preference.COLUMN_STRING_VALUE
			+ " varchar(8192)";
		stmt = PrefManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);		
	}
	
	public static void createTable() throws Exception {
		String sql;
		Statement stmt;

		// create
		logger.debug("createTable: create table " + Preference.TABLE_NAME);
		sql = "create table IF NOT EXISTS "
			+ Preference.TABLE_NAME
			+ " ("
			+ Preference.COLUMN_ID + " identity primary key,"
			+ Preference.COLUMN_KEY + " varchar(64),"
			+ Preference.COLUMN_STRING_VALUE + " varchar(8192),"
			+ Preference.COLUMN_INTEGER_VALUE + " int,"
			+ Preference.COLUMN_BOOLEAN_VALUE + " bool"
			+ ")";
		stmt = PrefManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);
	}
	
	public static List<Preference> doSelectAll() {
		try {
			if(!PrefManager.getInstance().isConnectionOpen()){
				return new ArrayList<Preference>();
			}
			
			List<Preference> list = new ArrayList<Preference>();
			StringBuffer sql = new StringBuffer("select * from " + Preference.TABLE_NAME);
			sql.append(" order by " + Preference.COLUMN_KEY);

			Statement stmt = PrefManager.getInstance().getConnection()
					.createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				Preference pref = makePreference(rs);
				logger.debug("doSelectAll: " + pref);
				list.add(pref);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Preference doSelectById(int id) throws Exception {
		String sql = "select * from " + Preference.TABLE_NAME + " where "
				+ Preference.COLUMN_ID + " = ?";
		PreparedStatement stmt = PrefManager.getInstance().getConnection()
				.prepareStatement(sql);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		Preference pref = null;
		int c = 0;
		while (rs.next() && c < 2) {
			pref = makePreference(rs);
			++c;
		}
		if (c == 0) {
			return null;
		}
		if (c > 1) {
			throw new Exception("more than one record found");
		}
		return pref;
	}

	public static Preference doSelectByKey(String key) throws Exception {
		String sql = "select * "
			+ "from " + Preference.TABLE_NAME
			+ " where " + Preference.COLUMN_KEY + " = ?";
		PreparedStatement stmt = PrefManager.getInstance().getConnection()
				.prepareStatement(sql);
		stmt.setString(1, key);
		ResultSet rs = stmt.executeQuery();
		Preference pref = null;
		int c = 0;
		while (rs.next() && c < 2) {
			pref = makePreference(rs);
			++c;
		}
		if (c == 0) {
			return null;
		}
		if (c > 1) {
			throw new Exception("more than one record found");
		}
		return pref;
	}
	
	public static int doCount() {
		try {
			String sql = "select count(" + Preference.COLUMN_ID + ") from "
					+ Preference.TABLE_NAME;
			Statement stmt = PrefManager.getInstance().getConnection()
					.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			return rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}


	private static Preference makePreference(ResultSet rs) throws SQLException {
		Preference pref = new Preference(rs.getInt(Preference.COLUMN_ID));
		pref.setKey(rs.getString(Preference.COLUMN_KEY));
		pref.setStringValue(rs.getString(Preference.COLUMN_STRING_VALUE));
		pref.setIntegerValue(rs.getInt(Preference.COLUMN_INTEGER_VALUE));
		pref.setBooleanValue(rs.getBoolean(Preference.COLUMN_BOOLEAN_VALUE));
		return pref;
	}
	
	public static boolean doDelete(Preference pref) throws Exception {
		if (pref == null) {
			return false;
		}
		logger.debug("doDelete: " + pref);
		String sql = "delete from " + Preference.TABLE_NAME
			+ " where " + Preference.COLUMN_ID + " = " + pref.getId();
		Statement stmt = PrefManager.getInstance().getConnection().createStatement();
		stmt.execute(sql);
		return true;
	}		
}
