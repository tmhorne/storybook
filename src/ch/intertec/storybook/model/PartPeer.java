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

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

import ch.intertec.storybook.action.ViewPartAction;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.view.modify.PartDialog;

public class PartPeer {

    private static Logger logger = Logger.getLogger(PartPeer.class);

    /**
     * Has to be package private!
     *
     * @throws Exception
     */
    static void createTable() throws SQLException {
        String sql;
        Statement stmt;
        // drop if exists
        logger.debug("createTable: drop table " + Part.TABLE_NAME);
        sql = "drop table " + Part.TABLE_NAME + " if exists";
        stmt = PersistenceManager.getInstance().getConnection().createStatement();
        stmt.execute(sql);
        stmt.close();

        // create
        logger.debug("createTable: create table " + Part.TABLE_NAME);
        sql = "create table "
                + Part.TABLE_NAME
                + " (" + Part.Column.ID + " identity primary key,"
                + Part.Column.NUMBER + " int,"
                + Part.Column.NAME + " varchar(32))";
        stmt = PersistenceManager.getInstance().getConnection().createStatement();
        stmt.execute(sql);
        stmt.close();
    }

    public static Part getFirstPart() {
        return doSelectAll().get(0);
    }

    public static List<Part> doSelectAll() {
        List<Part> list = new ArrayList<Part>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            if (!PersistenceManager.getInstance().isConnectionOpen()) {
                return new ArrayList<Part>();
            }

            StringBuffer sql = new StringBuffer();
            sql.append("select * from ");
            sql.append(Part.TABLE_NAME);
            sql.append(" order by ");
            sql.append(Part.Column.NUMBER);

            stmt = PersistenceManager.getInstance().getConnection().createStatement();
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                Part part = makePart(rs);
                list.add(part);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closeStatement(stmt);
        }
        return list;
    }

    public static Part doSelectById(int id) {
        Part part = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "select * from " + Part.TABLE_NAME + " where "
                    + Part.Column.ID + " = ?";
            stmt = PersistenceManager.getInstance().getConnection().prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            int c = 0;
            while (rs.next() && c < 2) {
                part = makePart(rs);
                ++c;
            }
            if (c > 1) {
                throw new Exception("more than one record found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closePrepareStatement(stmt);
        }
        return part;
    }

    private static Part makePart(ResultSet rs) throws SQLException {
        Part part = new Part(rs.getInt(Part.Column.ID.toString()));
        part.setNumber(rs.getInt(Part.Column.NUMBER.toString()));
        part.setName(rs.getString(Part.Column.NAME.toString()));
        return part;
    }

    public static int doCount() {
        int count = 0;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            if (!ProjectTools.isProjectOpen()) {
                return 0;
            }

            StringBuffer sql = new StringBuffer();
            sql.append("select count(");
            sql.append(Part.Column.ID);
            sql.append(") from ");
            sql.append(Part.TABLE_NAME);
            stmt = PersistenceManager.getInstance().getConnection().createStatement();
            rs = stmt.executeQuery(sql.toString());
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

    public static int getIdOfNextPart(int partId) {
        List<Part> partList = doSelectAll();
        int index = 0;
        try {
            for (Part part : partList) {
                if (partId == part.getId()) {
                    return partList.get(index + 1).getId();
                }
                ++index;
            }
        } catch (IndexOutOfBoundsException e) {
            // ignore
        }
        return -1;
    }

    public static int getIdOfPreviousPart(int partId) {
        List<Part> partList = doSelectAll();
        int index = 0;
        try {
            for (Part part : partList) {
                if (partId == part.getId()) {
                    return partList.get(index - 1).getId();
                }
                ++index;
            }
        } catch (IndexOutOfBoundsException e) {
            // ignore
        }
        return -1;
    }

    /**
     * Cascaded deletion of the given part.
     *
     * @param part the part to delete
     * @return false if part is null, true otherwise
     * @throws Exception
     */
    public static boolean doDelete(Part part) throws Exception {
        boolean retour = false;
        if (part != null && doCount() > 1) {
            // delete chapters
            for (Chapter chapter : ChapterPeer.doSelectByPart(part)) {
                ChapterPeer.doDelete(chapter);
            }

            // delete the part itself
            Statement stmt = null;
            try {
                String sql = "delete from " + Part.TABLE_NAME
                    + " where " + Part.Column.ID + " = " + part.getId();
                stmt = PersistenceManager.getInstance().getConnection().createStatement();
                stmt.execute(sql);
                retour = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                PersistenceManager.getInstance().closeStatement(stmt);
            }

            // active part has been deleted?
            if (part.getId() == MainFrame.getInstance().getActivePartId()) {
                // switch to first part
                Part p = PartPeer.getFirstPart();
                AbstractAction action = new ViewPartAction();
                action.putValue(ViewPartAction.ACTION_KEY_PART_ID,
                        p.getId());
                action.actionPerformed(null);
            }

            PCSDispatcher.getInstance().firePropertyChange(
                    PCSDispatcher.Property.PART.toString(), part, null);
        }
        return retour;
    }

    public static void makeOrUpdatePart(PartDialog dlg, boolean edit)
            throws Exception {
        Part part;
        Part old = null;
        if (edit) {
            part = dlg.getPart();
            old = PartPeer.doSelectById(part.getId());
            old.markAsExpired();
        } else {
            part = new Part();
        }
        part.setNumberStr(dlg.getNumberTextField().getText());
        part.setName(dlg.getNameTextField().getText());
        part.save();
        PCSDispatcher.getInstance().firePropertyChange(
                PCSDispatcher.Property.PART.toString(), old, part);
    }

    public static boolean checkIfPartIsValid(int partId) {
        for (Part part : doSelectAll()) {
            if (part.getId() == partId) {
                return true;
            }
        }
        return false;
    }
    
	public static Date getMaxDateForPart(int partId) {
		Date date = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append("select max(s.");
			sql.append(Scene.Column.DATE);
			sql.append(") as max");
			sql.append(" from " + Scene.TABLE_NAME + " s");
			sql.append(" left outer join " + Chapter.TABLE_NAME + " cha");
			sql.append(" on s." + Scene.Column.CHAPTER_ID + " = cha."
					+ Chapter.Column.ID);
			sql.append(" where cha." + Chapter.Column.PART_ID + " = ?");

			stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql.toString());
			((PreparedStatement) stmt).setInt(1, partId);
			rs = ((PreparedStatement) stmt).executeQuery();
			if (rs.next()) {
				date = rs.getDate("max");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closeStatement(stmt);
		}
		return date;
	}

	public static int getNextPartNo() {
		int max = 0;
		for (Part part : doSelectAll()) {
			if (max < part.getNumber()) {
				max = part.getNumber();
			}
		}
		if (max > 0) {
			return max + 1;
		}
		return 1;
	}
	
	public static void makeCopy(Part part) {
		try {
			Part copy = new Part();
			copy.setName(DbPeer.getCopyString(part.getName()));
			copy.setNumber(getNextPartNo());
			copy.save();
			PCSDispatcher.getInstance().firePropertyChange(
					PCSDispatcher.Property.PART, null, part);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
