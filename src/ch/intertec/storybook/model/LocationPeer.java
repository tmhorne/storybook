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

import ch.intertec.storybook.view.modify.LocationDialog;

public class LocationPeer {

	private static Logger logger = Logger.getLogger(LocationPeer.class);

	/**
	 * Has to be package private!
	 * 
	 * @throws Exception
	 */
	static void createTable() throws Exception {
		String sql;
		Statement stmt;
		// drop if exists
		logger.debug("createTable: drop table " + Location.TABLE_NAME);
		sql = "drop table " + Location.TABLE_NAME + " if exists";
		stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);

		// create
		logger.debug("createTable: create table " + Location.TABLE_NAME);
		sql = "create table "
			+ Location.TABLE_NAME
			+ " (" + Location.Column.ID + " identity primary key,"
			+ Location.Column.NAME + " varchar(32),"
			+ Location.Column.CITY + " varchar(32),"
			+ Location.Column.COUNTRY + " varchar(32),"
			+ Location.Column.DESCRIPTION + " varchar(8192),"
			+ Location.Column.ADDRESS + " varchar(64),"
			+ Location.Column.NOTES + " varchar(4096))";
		stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);
	}

	public static List<Location> doSelectAll() {
		try {
			List<Location> list = new ArrayList<Location>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + Location.TABLE_NAME);
			sql.append(" order by " + Location.Column.COUNTRY + ",");
			sql.append(Location.Column.CITY + "," + Location.Column.NAME);

			Statement stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				Location Location = makeLocation(rs);
				list.add(Location);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Location doSelectById(int id) {
		try {
			String sql = "select * from " + Location.TABLE_NAME + " where "
					+ Location.Column.ID + " = ?";
			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			Location Location = null;
			int c = 0;
			while (rs.next() && c < 2) {
				Location = makeLocation(rs);
				++c;
			}
			if (c == 0) {
				return null;
			}
			if (c > 1) {
				throw new Exception("more than one record found");
			}
			return Location;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Location> doSelectByCountry(String country) {
		try {
			List<Location> list = new ArrayList<Location>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + Location.TABLE_NAME);
			sql.append(" where " + Location.Column.COUNTRY + " = ?");
			sql.append(" order by " + Location.Column.NAME);

			PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
				.prepareStatement(sql.toString());
			stmt.setString(1, country);
			ResultSet rs = stmt.executeQuery();			
			while (rs.next()) {
				Location Location = makeLocation(rs);
				list.add(Location);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Location> doSelectByCountryAndCity(String country,
			String city) {
		try {
			List<Location> list = new ArrayList<Location>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + Location.TABLE_NAME);
			sql.append(" where " + Location.Column.COUNTRY + " = ?");
			sql.append(" and " + Location.Column.CITY + " = ?");
			sql.append(" order by " + Location.Column.NAME);

			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setString(1, country);
			stmt.setString(2, city);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Location Location = makeLocation(rs);
				list.add(Location);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<Location> doSelectByCity(String city) {
		try {
			List<Location> list = new ArrayList<Location>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + Location.TABLE_NAME);
			sql.append(" where " + Location.Column.CITY + " = ?");
			sql.append(" order by " + Location.Column.NAME);

			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setString(1, city);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Location Location = makeLocation(rs);
				list.add(Location);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void renameCity(String oldCityName, String newCityName) {
		try {
			List<Location> list = doSelectByCity(oldCityName);
			for (Location location : list) {
				Location old = LocationPeer.doSelectById(location.getId());
				old.markAsExpired();
				location.setCity(newCityName);
				location.save();
				PCSDispatcher.getInstance().firePropertyChange(
						PCSDispatcher.Property.LOCATION, old, location);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void renameCountry(String oldCountryName,
			String newCountryName) {
		try {
			List<Location> list = doSelectByCountry(oldCountryName);
			for (Location location : list) {
				Location old = LocationPeer.doSelectById(location.getId());
				old.markAsExpired();
				location.setCountry(newCountryName);
				location.save();
				PCSDispatcher.getInstance().firePropertyChange(
						PCSDispatcher.Property.LOCATION, old, location);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Location makeLocation(ResultSet rs) throws SQLException {
		Location location = new Location(rs.getInt(Location.Column.ID.toString()));
		location.setName(rs.getString(Location.Column.NAME.toString()));
		location.setCity(rs.getString(Location.Column.CITY.toString()));
		location.setCountry(rs.getString(Location.Column.COUNTRY.toString()));
		location.setDescription(rs.getString(Location.Column.DESCRIPTION.toString()));
		location.setAddress(rs.getString(Location.Column.ADDRESS.toString()));
		location.setNotes(rs.getString(Location.Column.NOTES.toString()));
		return location;
	}

	public static void makeOrUpdateLocation(LocationDialog dlg, boolean edit)
			throws Exception {
		Location location;
		Location old = null;
		if (edit) {
			location = dlg.getLocationTable();
			old = LocationPeer.doSelectById(location.getId());
			old.markAsExpired();
		} else {
			location = new Location();
		}
		location.setName(dlg.getNameTextField().getText());
		location.setCity(dlg.getCityAutoCombo().getText());
		location.setCountry(dlg.getCountryTextField().getText());
		location.setDescription(dlg.getDescriptionTextArea().getText());
		location.setAddress(dlg.getAddressTextField().getText());
		location.setNotes(dlg.getNotesTextArea().getText());
		location.save();
		
		PCSDispatcher.getInstance().firePropertyChange(
				PCSDispatcher.Property.LOCATION, old, location);
	}


	public static int doCount() {
		try {
			String sql = "select count(" + Location.Column.ID + ") from "
					+ Location.TABLE_NAME;
			Statement stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int c = 0;
			int count = 0;
			while (rs.next() && c < 2) {
				count = rs.getInt(1);
				++c;
			}
			if (c == 0) {
				return -1;
			}
			return count;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Cascaded deletion of the given location.
	 * 
	 * @param location
	 *            the location to delete
	 * @return false if location is null, true otherwise
	 * @throws Exception
	 */
	public static boolean doDelete(Location location) throws Exception {
		if (location == null) {
			return false;
		}
		
		logger.debug("doDelete: " + location);
		String sql;
		Statement stmt;

		// delete scene links
		List<SceneLinkLocation> list
				= SceneLinkLocationPeer.doSelectByLocationId(location.getId());
		for (SceneLinkLocation link : list) {
			SceneLinkLocationPeer.doDelete(link);
		}
		
		// delete item assignments
		TagLinkPeer.doDeleteByLocationId(location.getId());
		
		// delete the location itself
		sql = "delete from " + Location.TABLE_NAME
			+ " where " + Location.Column.ID + " = " + location.getId();
		stmt = PersistenceManager.getInstance().getConnection().createStatement();
		stmt.execute(sql);
		
		PCSDispatcher.getInstance().firePropertyChange(
				PCSDispatcher.Property.LOCATION, location, null);

		return true;
	}	
	
	public static List<Location> doSelectByCharacterAndDate(SbCharacter character, Date date) {
		try {
			// select DISTINCT(cl.LOCATION_ID)
			// from SCENE as ch,
			//   SCENE_PERSON as cp,
			//   SCENE_LOCATION as cl
			// where cp.SCENE_ID = ch.ID
			//   and cl.SCENE_ID = ch.ID
			//   and ch.date='2008-03-17'
			//   and cp.PERSON_ID='1'	
			List<Location> list = new ArrayList<Location>();
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct(cl." + SceneLinkLocation.COLUMN_LOCATION_ID + ")");
			sql.append(" from " + Scene.TABLE_NAME + " as ch,");
			sql.append(" " + SceneLinkSbCharacter.TABLE_NAME + " cp,");
			sql.append(" " + SceneLinkLocation.TABLE_NAME + " cl");
			sql.append(" where cp." + SceneLinkSbCharacter.COLUMN_SCENE_ID);
			sql.append(" = ch." + Scene.Column.ID);
			sql.append(" and cl."  + SceneLinkLocation.COLUMN_SCENE_ID);
			sql.append(" = ch." + Scene.Column.ID);
			sql.append(" and cp." + SceneLinkSbCharacter.COLUMN_CHARACTER_ID + " = ?");
			if (date != null) {
				sql.append(" and ch." + Scene.Column.DATE + " = ?");
			}
			PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql.toString());
			stmt.setInt(1, character.getId());
			if (date != null) {
				stmt.setDate(2, date);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				list.add(LocationPeer.doSelectById(id));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<Location>();
	}
	
	public static int doCountByCharacterLocationDate(SbCharacter character,
			Location location, Date date) {
		try {

			// select COUNT(sl.LOCATION_ID)
			// from SCENE as sc,
			// SCENE_PERSON as sp,
			// SCENE_LOCATION as sl
			// where sp.SCENE_ID = sc.ID
			// and sl.SCENE_ID = sc.ID
			// and sp.PERSON_ID='1'
			// and sl.LOCATION_ID = '3'
			// and sc.date='2008-03-17'
			
			StringBuffer sql = new StringBuffer();
			sql.append("select count(sl." + SceneLinkLocation.COLUMN_LOCATION_ID + ")");
			sql.append(" from " + Scene.TABLE_NAME + " as sc,");
			sql.append(" " + SceneLinkSbCharacter.TABLE_NAME + " sp,");
			sql.append(" " + SceneLinkLocation.TABLE_NAME + " sl");
			sql.append(" where sp." + SceneLinkSbCharacter.COLUMN_SCENE_ID);
			sql.append(" = sc." + Scene.Column.ID);
			sql.append(" and sl."  + SceneLinkLocation.COLUMN_SCENE_ID);
			sql.append(" = sc." + Scene.Column.ID);
			sql.append(" and sp." + SceneLinkSbCharacter.COLUMN_CHARACTER_ID + " = ?");
			sql.append(" and sl." + SceneLinkLocation.COLUMN_LOCATION_ID + " = ?");
			if (date != null) {
				sql.append(" and sc." + Scene.Column.DATE + " = ?");
			}
			PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql.toString());
			stmt.setInt(1, character.getId());
			stmt.setInt(2, location.getId());
			if (date != null) {
				stmt.setDate(3, date);
			}
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static List<Location> doSelectByCharacters(List<SbCharacter> characterList) {
		try {
			if (characterList == null || characterList.isEmpty()) {
				return new ArrayList<Location>();
			}
			List<Location> list = new ArrayList<Location>();
			StringBuffer sql = new StringBuffer();
			
			// select DISTINCT(cl.LOCATION_ID),
			//   lo.COUNTRY, lo.CITY, lo.NAME  
			// from SCENE as ch,
			//   SCENE_PERSON as cp,
			//   SCENE_LOCATION as cl,
			//   LOCATION as lo
			// where cp.SCENE_ID = ch.ID
			//   and cl.SCENE_ID = ch.ID
			//   and (cp.PERSON_ID='3' or cp.PERSON_ID='4')
			// order by lo.COUNTRY, lo.CITY, lo.NAME 
			sql.append("select DISTINCT(cl." + SceneLinkLocation.COLUMN_LOCATION_ID + "),");
			sql.append(" lo." + Location.Column.COUNTRY + ",");
			sql.append(" lo." + Location.Column.CITY + ",");
			sql.append(" lo." + Location.Column.NAME);
			sql.append(" from " + Scene.TABLE_NAME + " as ch,");
			sql.append(" " + SceneLinkSbCharacter.TABLE_NAME + " cp,");
			sql.append(" " + SceneLinkLocation.TABLE_NAME + " cl,");
			sql.append(" " + Location.TABLE_NAME + " lo");
			sql.append(" where cp." + SceneLinkSbCharacter.COLUMN_SCENE_ID);
			sql.append(" = ch." + Scene.Column.ID);
			sql.append(" and cl."  + SceneLinkLocation.COLUMN_SCENE_ID);
			sql.append(" = ch." + Scene.Column.ID);
			sql.append(" and cl."  + SceneLinkLocation.COLUMN_LOCATION_ID);
			sql.append(" = lo." + Location.Column.ID);			
			sql.append(" and ( cp." + SceneLinkSbCharacter.COLUMN_CHARACTER_ID + " = ?");
			for (int i = 1; i < characterList.size(); ++i) {
				sql.append(" or cp." + SceneLinkSbCharacter.COLUMN_CHARACTER_ID + " = ?");
			}
			sql.append(")");
			sql.append(" order by " + Location.Column.COUNTRY + ", ");
			sql.append(" " + Location.Column.CITY + ", ");
			sql.append(" " + Location.Column.NAME);
				
			PreparedStatement stmt = PersistenceManager.getInstance().getConnection().prepareStatement(sql.toString());
			stmt.setInt(1, characterList.get(0).getId());
			for (int i = 1; i < characterList.size(); ++i) {
				stmt.setInt(1 + i, characterList.get(i).getId());
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				list.add(LocationPeer.doSelectById(id));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<Location>();
	}
	
	public static List<String> doSelectDistinctCountry() {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct(" + Location.Column.COUNTRY + ")");
			sql.append(" from " + Location.TABLE_NAME);
			sql.append(" order by " + Location.Column.COUNTRY);
			Statement stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			List<String> list = new ArrayList<String>();
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				String str = rs.getString(Location.Column.COUNTRY.name());
				list.add(str);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	
	public static List<String> doSelectDistinctCity() {
		return doSelectDistinctCity(null);
	}
	
	public static List<String> doSelectDistinctCity(String country) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct(" + Location.Column.CITY + ")");
			sql.append(" from " + Location.TABLE_NAME);
			if (country != null) {
				sql.append(" where " + Location.Column.COUNTRY + " = ?");
			}
			sql.append(" order by " + Location.Column.CITY);
			PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
				.prepareStatement(sql.toString());
			if (country != null) {
				stmt.setString(1, country);
			}
			List<String> list = new ArrayList<String>();
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String str = rs.getString(Location.Column.CITY.name());
				list.add(str);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	
	public static void makeCopy(Location location) {
		try {
			Location copy = new Location();
			copy.setName(DbPeer.getCopyString(location.getName()));
			copy.setAddress(location.getAddress());
			copy.setCity(location.getCity());
			copy.setCountry(location.getCountry());
			copy.setDescription(location.getDescription());
			copy.setNotes(location.getNotes());
			copy.save();
			PCSDispatcher.getInstance().firePropertyChange(
					PCSDispatcher.Property.LOCATION, null, location);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
