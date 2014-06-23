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

import ch.intertec.storybook.view.modify.CharacterDialog;

public class SbCharacterPeer {

    private static Logger logger = Logger.getLogger(SbCharacterPeer.class);

    /**
     * Has to be package private!
     *
     * @throws Exception
     */
    static void createTable() throws Exception {
        // drop if exists
        logger.debug("createTable: drop table " + SbCharacter.TABLE_NAME);
        String sql = "drop table " + SbCharacter.TABLE_NAME + " if exists";
        Statement stmt = PersistenceManager.getInstance().getConnection().createStatement();
        stmt.execute(sql);
        stmt.close();

        // create
        logger.debug("createTable: create table " + SbCharacter.TABLE_NAME);
        sql = "create table "
                + SbCharacter.TABLE_NAME
                + " (" + SbCharacter.Column.ID + " identity primary key,"
                + SbCharacter.Column.GENDER_ID + " integer,"
                + SbCharacter.Column.FIRSTNAME + " varchar(256),"
                + SbCharacter.Column.LASTNAME + " varchar(256),"
                + SbCharacter.Column.ABBREVIATION + " varchar(32),"
                + SbCharacter.Column.BIRTHDAY + " date,"
                + SbCharacter.Column.DAY_OF_DEATH + " date,"
                + SbCharacter.Column.OCCUPATION + " varchar(256),"
                + SbCharacter.Column.DESCRIPTION + " varchar(8192),"
                + SbCharacter.Column.COLOR + " int,"
                + SbCharacter.Column.NOTES + " varchar(4096),"
                + SbCharacter.Column.CATEGORY + " int)";
        stmt = PersistenceManager.getInstance().getConnection().createStatement();
        stmt.execute(sql);
        stmt.close();
    }

    public static List<SbCharacter> doSelectByCategory(int category) {
        List<SbCharacter> list = new ArrayList<SbCharacter>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            StringBuffer sql = new StringBuffer("select * from "
                    + SbCharacter.TABLE_NAME);
            sql.append(" where " + SbCharacter.Column.CATEGORY + " = "
                    + category);
            sql.append(" order by " + SbCharacter.Column.FIRSTNAME);
            sql.append("," + SbCharacter.Column.LASTNAME);

            stmt = PersistenceManager.getInstance().getConnection().createStatement();
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                SbCharacter character = makeCharacter(rs);
                list.add(character);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closeStatement(stmt);
        }
        return list;
    }

    public static List<SbCharacter> doSelectAll() {
    	return doSelectAll(true);
    }
    
    public static List<SbCharacter> doSelectAll(boolean orderByCategory) {
        List<SbCharacter> list = new ArrayList<SbCharacter>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            StringBuffer sql = new StringBuffer("select * from "
                    + SbCharacter.TABLE_NAME);
			sql.append(" order by ");
			if (orderByCategory) {
				sql.append(SbCharacter.Column.CATEGORY + ",");
			}
			sql.append(SbCharacter.Column.FIRSTNAME);
			sql.append("," + SbCharacter.Column.LASTNAME);

            stmt = PersistenceManager.getInstance().getConnection().createStatement();
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                SbCharacter character = makeCharacter(rs);
                list.add(character);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closeStatement(stmt);
        }
        return list;
    }

    public static SbCharacter doSelectById(int id) {
        SbCharacter character = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "select * from " + SbCharacter.TABLE_NAME
                    + " where " + SbCharacter.Column.ID + " = ?";
            stmt = PersistenceManager.getInstance().getConnection().prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                character = makeCharacter(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closePrepareStatement(stmt);
        }
        return character;
    }

    private static SbCharacter makeCharacter(ResultSet rs) throws SQLException {
        SbCharacter character = new SbCharacter(rs.getInt(SbCharacter.Column.ID.toString()));
        character.setGender(GenderPeer.doSelectById(rs.getInt(SbCharacter.Column.GENDER_ID.toString())));
        character.setFirstname(rs.getString(SbCharacter.Column.FIRSTNAME.toString()));
        character.setLastname(rs.getString(SbCharacter.Column.LASTNAME.toString()));
        character.setAbbreviation(rs.getString(SbCharacter.Column.ABBREVIATION.toString()));
        character.setBirthday(rs.getDate(SbCharacter.Column.BIRTHDAY.toString()));
        character.setDayOfDeath(rs.getDate(SbCharacter.Column.DAY_OF_DEATH.toString()));
        character.setOccupation(rs.getString(SbCharacter.Column.OCCUPATION.toString()));
        character.setDescription(rs.getString(SbCharacter.Column.DESCRIPTION.toString()));
        character.setNotes(rs.getString(SbCharacter.Column.NOTES.toString()));
        character.setCategory(rs.getInt(SbCharacter.Column.CATEGORY.toString()));
        int color = rs.getInt(SbCharacter.Column.COLOR.toString());
        if (color == 0) {
            character.setColor(null);
        } else {
            character.setColor(rs.getInt(SbCharacter.Column.COLOR.toString()));
        }
        return character;
    }

    public static void makeOrUpdateCharacter(CharacterDialog dlg, boolean edit)
            throws Exception {

        SbCharacter character;
        SbCharacter old = null;
        if (edit) {
            character = dlg.getCharacterTable();
            old = SbCharacterPeer.doSelectById(character.getId());
            old.markAsExpired();
        } else {
            character = new SbCharacter();
        }

        java.sql.Date birthdayDate;
        java.sql.Date dayOfDateDate;
        if (dlg.getBirthdayDateChooser().isEmpty()) {
            character.setBirthday(null);
        } else {
            birthdayDate = new java.sql.Date(dlg.getBirthdayDateChooser().getDate().getTime());
            character.setBirthday(birthdayDate);
        }
        if (dlg.getDayOfDeathDateChooser().isEmpty()) {
            character.setDayOfDeath(null);
        } else {
            dayOfDateDate = new java.sql.Date(dlg.getDayOfDeathDateChooser().getDate().getTime());
            character.setDayOfDeath(dayOfDateDate);
        }

        // gender
        character.setGender(dlg.getSelectedGender());

        // group
        if (dlg.getCentralCharacterRadioButton().isSelected()) {
            character.setCategory(SbCharacter.CATEGORY_CENTRAL);
        } else {
            character.setCategory(SbCharacter.CATEGORY_MINOR);
        }

        character.setAbbreviation(dlg.getAbbreviationTextField().getText());
        character.setFirstname(dlg.getFirstNameTextField().getText());
        character.setLastname(dlg.getLastNameTextField().getText());
        character.setOccupation(dlg.getOccupationTextField().getText());
        character.setDescription(dlg.getDescriptionTextArea().getText());
        character.setNotes(dlg.getNotesTextArea().getText());
        character.setColor(dlg.getShowColorLabel().getColor());
        character.save();
        
        PCSDispatcher.getInstance().firePropertyChange(
                PCSDispatcher.Property.CHARACTER, old, character);
    }

    public static int doCount() {
        int count = 0;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "select count(" + SbCharacter.Column.ID + ") from "
                    + SbCharacter.TABLE_NAME;
            stmt = PersistenceManager.getInstance().getConnection().createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closeStatement(stmt);
        }
        return count;
    }

    /**
     * Cascaded deletion of the given person.
     *
     * @param character
     *            the character to delete
     * @return false if character is null, true otherwise
     * @throws Exception
     */
    public static boolean doDelete(SbCharacter character) throws Exception {
        boolean retour = false;
        if (character != null) {
            String sql;
            Statement stmt = null;

            // delete scene links
            List<SceneLinkSbCharacter> list = SceneLinkSbCharacterPeer.doSelectByCharacterId(character.getId());
            for (SceneLinkSbCharacter link : list) {
                SceneLinkSbCharacterPeer.doDelete(link);
            }
            
    		// delete item assignments
    		TagLinkPeer.doDeleteByCharacterId(character.getId());
            
            try {
                // delete the character
                sql = "delete from " + SbCharacter.TABLE_NAME
                        + " where " + SbCharacter.Column.ID + " = "
                        + character.getId();
                stmt = PersistenceManager.getInstance().getConnection().createStatement();
                stmt.execute(sql);
                retour = true;
            } catch (SQLException exc) {
                exc.printStackTrace();
            } finally {
                PersistenceManager.getInstance().closeStatement(stmt);
            }
            PCSDispatcher.getInstance().firePropertyChange(
                    PCSDispatcher.Property.CHARACTER, character, null);
        }
        return retour;
    }
    
	public static void makeCopy(SbCharacter character) {
		try {
			SbCharacter copy = new SbCharacter();
			copy.setAbbreviation(DbPeer.getCopyString(character
					.getAbbreviation()));
			copy.setFirstname(character.getFirstname());
			copy.setLastname(character.getLastname());
			copy.setCategory(character.getCategory());
			copy.setGender(character.getGender());
			copy.setBirthday(character.getBirthday());
			copy.setColor(character.getColor());
			copy.setBirthday(character.getBirthday());
			copy.setDayOfDeath(character.getDayOfDeath());
			copy.setOccupation(character.getOccupation());
			copy.setDescription(character.getDescription());
			copy.setNotes(character.getNotes());
			copy.save();
			PCSDispatcher.getInstance().firePropertyChange(
					PCSDispatcher.Property.CHARACTER, null, character);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
