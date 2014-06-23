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

public class IdeasPeer {

    private static Logger logger = Logger.getLogger(IdeasPeer.class);
    public final static int ORDER_BY_DEFAULT = 0;
    public final static int ORDER_BY_STATUS = 1;
    public final static int ORDER_BY_NOTE = 2;

    /**
     * Has to be package private!
     * @throws Exception
     */
    static void createTable()throws SQLException {
        String sql;
        Statement stmt;

        logger.debug("drop table " + Idea.TABLE_NAME + " if exists");
        sql = "drop table " + Idea.TABLE_NAME + " if exists";
        stmt = PersistenceManager.getInstance().getConnection().createStatement();
        stmt.execute(sql);
        stmt.close();

        logger.debug("create table " + Idea.TABLE_NAME);
        sql = "create table " + Idea.TABLE_NAME + " ("
                + Idea.Column.ID + " identity primary key,"
                + Idea.Column.STATUS + " int,"
                + Idea.Column.NOTE + " varchar(8192),"
                + Idea.Column.CATEGORY + " varchar(1024))";
        stmt = PersistenceManager.getInstance().getConnection().createStatement();
        stmt.execute(sql);
        stmt.close();
    }

    public static List<Idea> doSelectAll() {
        return doSelectAll(ORDER_BY_DEFAULT);
    }

    public static List<Idea> doSelectAll(int flag) {
        Statement stmt = null;
        ResultSet rs = null;
        List<Idea> list = new ArrayList<Idea>();
        try {
            StringBuffer sql = new StringBuffer(
                    "select * from " + Idea.TABLE_NAME);

            switch (flag) {
                case ORDER_BY_DEFAULT:
                    sql.append(" order by ");
                    sql.append(Idea.Column.ID);
                    break;
                case ORDER_BY_STATUS:
                    sql.append(" order by ");
                    sql.append(Idea.Column.STATUS);
                    sql.append(", ");
                    sql.append(Idea.Column.NOTE);
                    break;
                case ORDER_BY_NOTE:
                    sql.append(" order by ");
                    sql.append(Idea.Column.NOTE);
                    break;
                default:
                    break;
            }
            stmt = PersistenceManager.getInstance().getConnection().createStatement();
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                list.add(makeIdea(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closeStatement(stmt);
        }
        return list;
    }

    public static List<Idea> doSelectIdeas() {
        Statement stmt = null;
        ResultSet rs = null;
        List<Idea> list = new ArrayList<Idea>();
        try {
            StringBuffer sql = new StringBuffer(
                    "select * from " + Idea.TABLE_NAME);
            sql.append(" order by ").append(Idea.Column.STATUS);
            sql.append(" , lower(").append(Idea.Column.CATEGORY).append(")");
            sql.append(" , lower(").append(Idea.Column.NOTE).append(")");
            stmt = PersistenceManager.getInstance().getConnection().createStatement();
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                list.add(makeIdea(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closeStatement(stmt);
        }
        return list;
    }

    public static List<Idea> doSelectIdeasByStatus(Idea.Status status) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Idea> list = new ArrayList<Idea>();
        try {
            StringBuffer sql = new StringBuffer(
                    "select * from " + Idea.TABLE_NAME);
            sql.append(" where status = ? ");
            sql.append(" order by ").append(Idea.Column.STATUS);
            sql.append(" , lower(").append(Idea.Column.CATEGORY).append(")");
            sql.append(" , lower(").append(Idea.Column.NOTE).append(")");
            stmt = PersistenceManager.getInstance().getConnection().prepareStatement(sql.toString());
            stmt.setInt(1, status.getIndex());
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(makeIdea(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closePrepareStatement(stmt);
        }
        return list;
    }

    private static Idea makeIdea(ResultSet rs) throws SQLException {
        Idea idea = new Idea(rs.getInt(Idea.Column.ID.toString()));
        idea.setStatus(Idea.Status.fromIndex(rs.getInt(Idea.Column.STATUS.toString())));
        idea.setNote(rs.getString(Idea.Column.NOTE.toString()));
        idea.setCategory(rs.getString(Idea.Column.CATEGORY.toString()));
        return idea;
    }

    /**
     * Cascaded deletion of the given scenes and all its links
     * to the tables
     * <ol>
     * <li>strand</li>
     * <li>person</li>
     * <li>location</li>
     * </ol>
     *
     * @param idea
     *            the idea to delete
     * @return true if ch is not null, false otherwise
     * @throws Exception
     */
    public static boolean doDelete(Idea idea) throws Exception {
        boolean retour = false;
        if (idea != null) {
            Statement stmt = null;
            try {
                // delete the idea
                String sql = "delete from " + Idea.TABLE_NAME
                        + " where " + Idea.Column.ID + " = " + idea.getId();
                stmt = PersistenceManager.getInstance().getConnection().createStatement();
                stmt.execute(sql.toString());
                retour = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                PersistenceManager.getInstance().closeStatement(stmt);
            }
        }
        return retour;
    }

    public static boolean insertIdea(int ideaId, Idea.Status status, String text, String category) {
        boolean retour = true;
        try {
            Idea gn = null;
            if (ideaId != -1) {
                gn = new Idea(ideaId);
            } else {
                gn = new Idea();
            }
            gn.setStatus(status);
            gn.setNote(text);
            gn.setCategory(category);
            gn.save();
            PCSDispatcher.getInstance().firePropertyChange(
                    PCSDispatcher.Property.IDEAS.toString(), gn, null);
        } catch (SQLException e) {
            retour = false;
            e.printStackTrace();
        }
        return retour;
    }
}
