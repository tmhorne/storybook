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

import ch.intertec.storybook.model.Tag.TagType;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.view.modify.TagDialog;

public class TagPeer {

    private static Logger logger = Logger.getLogger(TagPeer.class);

    public static void createTable() throws SQLException {
        String sql;
        Statement stmt;
        // drop if exists
        logger.debug("createTable: drop table " + Tag.TABLE_NAME);
        sql = "drop table " + Tag.TABLE_NAME + " if exists";
        stmt = PersistenceManager.getInstance().getConnection().createStatement();
        stmt.execute(sql);
        stmt.close();

        // create
        logger.debug("createTable: create table " + Tag.TABLE_NAME);
        sql = "create table "
                + Tag.TABLE_NAME
                + " (" + Tag.Column.ID + " identity primary key,"
                + Tag.Column.TYPE + " int,"
                + Tag.Column.CATEGORY + " varchar(1024),"
                + Tag.Column.NAME + " varchar(1024),"
                + Tag.Column.DESCRIPTION + " varchar(4096),"
        		+ Tag.Column.NOTES + " varchar(4096))";
        stmt = PersistenceManager.getInstance().getConnection().createStatement();
        stmt.execute(sql);
        stmt.close();
    }

    public static List<String> doSelectDistinctCategory() {
    	return doSelectDistinctCategory(TagType.TAG);
    }
    
	public static List<String> doSelectDistinctCategory(TagType tagType) {
		List<String> list = new ArrayList<String>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct category from ");
			sql.append(Tag.TABLE_NAME);
			sql.append(" where " + Tag.Column.TYPE + " = " + tagType.ordinal());
			sql.append(" order by ");
			sql.append(Tag.Column.CATEGORY);
			stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				list.add(rs.getString(1));
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
    
	public static List<Tag> doSelectByCategory(String category) {
		try {
			List<Tag> list = new ArrayList<Tag>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + Tag.TABLE_NAME);
			sql.append(" where " + Tag.Column.CATEGORY + " = ?");
			sql.append(" order by " + Tag.Column.NAME);

			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setString(1, category);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Tag tag = makeTag(rs);
				list.add(tag);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
    public static List<Tag> doSelectAll() {
        List<Tag> list = new ArrayList<Tag>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            if (!PersistenceManager.getInstance().isConnectionOpen()) {
                return new ArrayList<Tag>();
            }

            StringBuffer sql = new StringBuffer();
            sql.append("select * from ");
            sql.append(Tag.TABLE_NAME);
			sql.append(" where " + Tag.Column.TYPE + " = " + TagType.TAG.ordinal());
            sql.append(" order by ");
            sql.append(Tag.Column.CATEGORY);
            sql.append(",");
            sql.append(Tag.Column.NAME);

            stmt = PersistenceManager.getInstance().getConnection().createStatement();
            rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				Tag tag = makeTag(rs);
				list.add(tag);
			}
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closeStatement(stmt);
        }
        return list;
    }

    public static Tag doSelectById(int id) {
        Tag tag = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + Tag.TABLE_NAME);
			sql.append(" where " + Tag.Column.ID + " = ?");
			sql.append(" and type = " + TagType.TAG.ordinal());
			stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql.toString());
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			int c = 0;
            while (rs.next() && c < 2) {
                tag = makeTag(rs);
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
        return tag;
    }

	private static Tag makeTag(ResultSet rs) throws SQLException {
		Tag tag = new Tag(rs.getInt(Tag.Column.ID.toString()));
		tag.setTagType(TagType.TAG);
		tag.setCategory(rs.getString(Tag.Column.CATEGORY.toString()));
		tag.setName(rs.getString(Tag.Column.NAME.toString()));
		tag.setDescription(rs.getString(Tag.Column.DESCRIPTION.toString()));
		tag.setNotes(rs.getString(Tag.Column.NOTES.toString()));
		return tag;
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
            sql.append(Tag.Column.ID);
            sql.append(") from ");
            sql.append(Tag.TABLE_NAME);
            sql.append(" where type = " + TagType.TAG.ordinal());
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

    /**
     * Cascaded deletion of the given tag.
     *
     * @param tag the tag to delete
     * @return false if tag is null, true otherwise
     * @throws Exception
     */
    public static boolean doDelete(Tag tag) throws Exception {
        boolean ret = false;
        if (tag != null && doCount() > 0) {
        	
        	// delete tag assignments
        	TagLinkPeer.doDeleteByTagId(tag.getId());
        	
            // delete the tag itself
            Statement stmt = null;
            try {
                String sql = "delete from " + Tag.TABLE_NAME
                    + " where " + Tag.Column.ID + " = " + tag.getId();
                stmt = PersistenceManager.getInstance().getConnection().createStatement();
                stmt.execute(sql);
                ret = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                PersistenceManager.getInstance().closeStatement(stmt);
			}

			PCSDispatcher.getInstance().firePropertyChange(
					PCSDispatcher.Property.TAG, tag, null);
        }
        return ret;
    }

	public static void makeOrUpdateTag(TagDialog dlg, boolean edit)
			throws Exception {
		Tag tag;
		Tag old = null;
		if (edit) {
			tag = dlg.getTag();
			old = TagPeer.doSelectById(tag.getId());
			old.markAsExpired();
		} else {
			tag = new Tag();
		}
		tag.setTagType(TagType.TAG);
		tag.setCategory(dlg.getCategoryTextField().getText());
		tag.setName(dlg.getNameTextField().getText());
		tag.setDescription(dlg.getDescriptionTextArea().getText());
		tag.setNotes(dlg.getNotesTextArea().getText());
		tag.save();
		PCSDispatcher.getInstance().firePropertyChange(
				PCSDispatcher.Property.TAG, old, tag);
	}
	
	public static void makeCopy(Tag tag) {
		try {
			Tag copy = new Tag();
			copy.setName(DbPeer.getCopyString(tag.getName()));
			copy.setTagType(TagType.TAG);
			copy.setCategory(tag.getCategory());
			copy.setDescription(tag.getDescription());
			copy.setNotes(tag.getNotes());
			copy.save();
			PCSDispatcher.getInstance().firePropertyChange(
					PCSDispatcher.Property.TAG, null, tag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void renameCategory(String oldCategoryName,
			String newCategoryName) {
		try {
			List<Tag> list = doSelectByCategory(oldCategoryName);
			for (Tag tag : list) {
				Tag old = TagPeer.doSelectById(tag.getId());
				old.markAsExpired();
				tag.setCategory(newCategoryName);
				tag.save();
				PCSDispatcher.getInstance().firePropertyChange(
						PCSDispatcher.Property.TAG, old, tag);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
