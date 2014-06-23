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
import ch.intertec.storybook.view.modify.ItemDialog;

public class ItemPeer {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ItemPeer.class);

    public static List<Item> doSelectAll() {
        List<Item> list = new ArrayList<Item>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            if (!PersistenceManager.getInstance().isConnectionOpen()) {
                return new ArrayList<Item>();
            }

            StringBuffer sql = new StringBuffer();
            sql.append("select * from ");
            sql.append(Tag.TABLE_NAME);
			sql.append(" where " + Tag.Column.TYPE + " = " + TagType.ITEM.ordinal());
            sql.append(" order by ");
            sql.append(Tag.Column.CATEGORY);
            sql.append(",");
            sql.append(Tag.Column.NAME);

            stmt = PersistenceManager.getInstance().getConnection().createStatement();
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
				Item item = ItemPeer.makeItem(rs);
				list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closeStatement(stmt);
        }
        return list;
    }
	
    public static Item doSelectById(int id) {
        Item item = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + Tag.TABLE_NAME);
			sql.append(" where " + Tag.Column.ID + " = ?");
			sql.append(" and type = " + TagType.ITEM.ordinal());
			stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql.toString());
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
            int c = 0;
            while (rs.next() && c < 2) {
                item = makeItem(rs);
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
        return item;
    }

    public static boolean doDelete(Item item) throws Exception {
        boolean ret = false;
        if (item != null && doCount() > 0) {
        	
        	// delete item assignments
        	ItemLinkPeer.doDeleteByItemId(item.getId());
        	
            // delete the item itself
            Statement stmt = null;
            try {
				String sql = "delete from " + Item.TABLE_NAME + " where "
						+ Item.Column.ID + " = " + item.getId();
				stmt = PersistenceManager.getInstance().getConnection()
						.createStatement();
				stmt.execute(sql);
				ret = true;
            } catch (Exception e) {
                e.printStackTrace();
			} finally {
				PersistenceManager.getInstance().closeStatement(stmt);
			}

			PCSDispatcher.getInstance().firePropertyChange(
					PCSDispatcher.Property.ITEM, item, null);
        }
        return ret;
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
            sql.append(Item.Column.ID);
            sql.append(") from ");
            sql.append(Item.TABLE_NAME);
			sql.append(" where type = " + TagType.ITEM.ordinal());
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

	public static List<String> doSelectDistinctCategory() {
		return TagPeer.doSelectDistinctCategory(TagType.ITEM);
	}
    
	public static void makeCopy(Item item) {
		try {
			Item copy = new Item();
			copy.setName(DbPeer.getCopyString(item.getName()));
			copy.setTagType(TagType.ITEM);
			copy.setCategory(item.getCategory());
			copy.setDescription(item.getDescription());
			copy.setNotes(item.getNotes());
			copy.save();
			PCSDispatcher.getInstance().firePropertyChange(
					PCSDispatcher.Property.ITEM, null, item);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void makeOrUpdateItem(ItemDialog dlg, boolean edit)
			throws Exception {
		Item item;
		Item old = null;
		if (edit) {
			item = dlg.getItem();
			old = ItemPeer.doSelectById(item.getId());
			old.markAsExpired();
		} else {
			item = new Item();
		}
		item.setTagType(TagType.ITEM);
		item.setCategory(dlg.getCategoryTextField().getText());
		item.setName(dlg.getNameTextField().getText());
		item.setDescription(dlg.getDescriptionTextArea().getText());
		item.setNotes(dlg.getNotesTextArea().getText());
		item.save();
		PCSDispatcher.getInstance().firePropertyChange(
				PCSDispatcher.Property.ITEM, old, item);
	}

    private static Item makeItem(ResultSet rs) throws SQLException {
		Item item = new Item(rs.getInt(Tag.Column.ID.toString()));
		item.setTagType(TagType.ITEM);
		item.setCategory(rs.getString(Tag.Column.CATEGORY.toString()));
		item.setName(rs.getString(Tag.Column.NAME.toString()));
		item.setDescription(rs.getString(Tag.Column.DESCRIPTION.toString()));
		item.setNotes(rs.getString(Tag.Column.NOTES.toString()));
		return item;
	}
    
	public static List<Item> doSelectByCategory(String category) {
		try {
			List<Item> list = new ArrayList<Item>();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + Tag.TABLE_NAME);
			sql.append(" where " + Tag.Column.CATEGORY + " = ?");
			sql.append(" order by " + Tag.Column.NAME);

			PreparedStatement stmt = PersistenceManager.getInstance()
					.getConnection().prepareStatement(sql.toString());
			stmt.setString(1, category);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Item item = makeItem(rs);
				list.add(item);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void renameCategory(String oldCategoryName,
			String newCategoryName) {
		try {
			List<Item> list = doSelectByCategory(oldCategoryName);
			for (Item item : list) {
				Item old = ItemPeer.doSelectById(item.getId());
				old.markAsExpired();
				item.setCategory(newCategoryName);
				item.save();
				PCSDispatcher.getInstance().firePropertyChange(
						PCSDispatcher.Property.ITEM, old, item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
