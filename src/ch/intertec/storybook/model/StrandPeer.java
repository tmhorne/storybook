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

import ch.intertec.storybook.view.modify.StrandDialog;

public class StrandPeer {

	private static Logger logger = Logger.getLogger(StrandPeer.class);

	/**
	 * Has to be package private!
	 * 
	 * @throws Exception
	 */
	static void createTable() throws Exception {
		String sql;
		Statement stmt;
		// drop if exists
		logger.debug("createTable: drop table " + Strand.TABLE_NAME);
		sql = "drop table " + Strand.TABLE_NAME + " if exists";
		stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);

		// create
		logger.debug("createTable: create table " + Strand.TABLE_NAME);
		sql = "create table "
			+ Strand.TABLE_NAME
			+ " (" + Strand.Column.ID + " identity primary key,"
			+ Strand.Column.ABBREVIATION + " varchar(16),"
			+ Strand.Column.NAME + " varchar(255),"
			+ Strand.Column.COLOR + " int,"
			+ Strand.Column.SORT + " int,"
			+ Strand.Column.NOTES + " varchar(4096))";
		stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);
	}

	public static List<Strand> doSelectAll() {
		try {
			List<Strand> list = new ArrayList<Strand>();
			StringBuffer sql = new StringBuffer("select * from " + Strand.TABLE_NAME);
			sql.append(" order by " + Strand.Column.SORT);
			sql.append(", " + Strand.Column.ID);

			Statement stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				Strand strand = makeStrand(rs);
				// logger.debug("doSelectAll: " + strand);
				list.add(strand);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Strand doSelectById(int id) {
		try{
			String sql = "select * from " + Strand.TABLE_NAME + " where "
					+ Strand.Column.ID + " = ?";
			PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			Strand strand = null;
			int c = 0;
			while (rs.next() && c < 2) {
				strand = makeStrand(rs);
				++c;
			}
			if (c == 0) {
				return null;
			}
			if (c > 1) {
				throw new Exception("more than one record found");
			}
			return strand;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static Strand doSelectBySort(int sort) {
		try{
			String sql = "select * from " + Strand.TABLE_NAME + " where "
					+ Strand.Column.SORT + " = ?";
			PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql);
			stmt.setInt(1, sort);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return makeStrand(rs);
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private static Strand makeStrand(ResultSet rs) throws SQLException {
		Strand strand = new Strand(rs.getInt(Strand.Column.ID.toString()));
		strand.setAbbreviation(rs.getString(Strand.Column.ABBREVIATION.toString()));
		strand.setName(rs.getString(Strand.Column.NAME.toString()));
		strand.setColor(rs.getInt(Strand.Column.COLOR.toString()));
		strand.setSort(rs.getInt(Strand.Column.SORT.toString()));
		strand.setNotes(rs.getString(Strand.Column.NOTES.toString()));
		return strand;
	}
	
	public static int doCount() {
		try{
			String sql = "select count(" + Strand.Column.ID + ") from "
					+ Strand.TABLE_NAME;
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
			if (c > 1) {
				logger.error("more than one record found");
				throw new Exception("more than one record found");
			}
			return count;
		} catch (Exception e){
			e.printStackTrace();
		}
		return 0;
	}

	public static int getSortMax() {
		try {
			String sql = "select max(" + Strand.Column.SORT + ") from "
					+ Strand.TABLE_NAME;
			Statement stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			return rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void resort() {
		try {
			int c = 0;
			for (Strand strand : doSelectAll()) {
				strand.setSort(c);
				strand.save();
				++c;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cascaded deletion of the given strand.
	 * 
	 * @param strand the strand to delete
	 * @return false if strand is null, true otherwise
	 * @throws Exception
	 */
	public static boolean doDelete(Strand strand) throws Exception {
		if (strand == null) {
			return false;
		}
		
		// don't delete last strand
		if (doCount() == 1) {
			return false;
		}

		// delete scenes
		for (Scene ch : ScenePeer.doSelectByStrandId(strand.getId())) {
			ScenePeer.doDelete(ch);
		}

		// delete the strand itself
		String sql = "delete from " + Strand.TABLE_NAME + " where "
				+ Strand.Column.ID + " = " + strand.getId();
		Statement stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);

		// re-order
		resort();
		
		PCSDispatcher.getInstance().firePropertyChange(
				PCSDispatcher.Property.STRAND.toString(), strand, null);

		return true;
	}
	
	public static void makeOrUpdateStrand(StrandDialog dlg, boolean edit)
			throws Exception {
		Strand strand;
		Strand old = null;
		if (edit) {
			strand = dlg.getStrand();
			old = StrandPeer.doSelectById(strand.getId());
			old.markAsExpired();
		} else {
			strand = new Strand();
		}
		strand.setAbbreviation(dlg.getAbbreviationTextField().getText());
		strand.setName(dlg.getNameTextField().getText());
		strand.setColor(dlg.getShowColorLabel().getColor());
		strand.setNotes(dlg.getNotesTextArea().getText());
		strand.save();
		PCSDispatcher.getInstance().firePropertyChange(
				PCSDispatcher.Property.STRAND.toString(), old, strand);
	}
		
	public static List<Strand> doSelectByCharacterAndDate(SbCharacter character, Date date){
		try{
			List<Strand> list = new ArrayList<Strand>();
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct s.*");
			sql.append(" from " + Scene.TABLE_NAME + " c,");
			sql.append(" " + SceneLinkSbCharacter.TABLE_NAME + " cp,");
			sql.append(" " + SbCharacter.TABLE_NAME + " p,");
			sql.append(" " + Strand.TABLE_NAME + " s");
			sql.append(" where cp." + SceneLinkSbCharacter.COLUMN_SCENE_ID + " = c." + Scene.Column.ID);
			sql.append(" and cp." + SceneLinkSbCharacter.COLUMN_CHARACTER_ID + " = p." + SbCharacter.Column.ID);
			sql.append(" and c." + Scene.Column.STRAND_ID + " = s." + Strand.Column.ID);
			sql.append(" and p." + SbCharacter.Column.ID + " = ?");
			sql.append(" and c." + Scene.Column.DATE + " = ?");
			PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
				.prepareStatement(sql.toString());
			stmt.setInt(1, character.getId());
			stmt.setDate(2, date);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Strand ch = makeStrand(rs);
				list.add(ch);
			}
			return list;
		} catch(Exception e){
			e.printStackTrace();
		}
		return new ArrayList<Strand>();
	}
	
	public static int doCountByDate(Date date, Strand strand) {
		try{
			// select count(id) from SCENE
			// where date='2008-03-17' and strand_id=1
			StringBuffer buf = new StringBuffer();
			buf.append("select count(id) from ");
			buf.append(Scene.TABLE_NAME);
			buf.append(" where ");
			buf.append(Scene.Column.DATE);
			buf.append(" = ? and ");
			buf.append(Scene.Column.STRAND_ID);
			buf.append(" = ?");
			PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(buf.toString());
			stmt.setDate(1, date);
			stmt.setInt(2, strand.getId());
			ResultSet rs = stmt.executeQuery();
			int count = 0;
			if(rs.next()){
				count = rs.getInt(1);
			}
			return count;
		} catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	public static Strand findLeftNeighbor(Strand strand) {
		int sort = strand.getSort();
		if (sort == 0) {
			// no left neighbor
			return null;
		}
		return doSelectBySort(sort - 1);
	}
	
	public static Strand findRightNeighbor(Strand strand) {
		int sort = strand.getSort();
		if (sort == doCount() - 1) {
			// no right neighbor
			return null;
		}
		return doSelectBySort(sort + 1);
	}
	
	public static void makeCopy(Strand strand) {
		try {
			Strand copy = new Strand();
			copy.setAbbreviation(DbPeer.getCopyString(strand.getAbbreviation()));
			copy.setName(strand.getName());
			copy.setColor(strand.getColor());
			copy.setName(strand.getName());
			copy.setNotes(strand.getNotes());
			copy.save();
			PCSDispatcher.getInstance().firePropertyChange(
					PCSDispatcher.Property.STRAND, null, strand);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
