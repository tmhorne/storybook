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
import java.util.ArrayList;

import org.apache.log4j.Logger;

import ch.intertec.storybook.toolkit.HtmlTools;
import ch.intertec.storybook.toolkit.I18N;


@SuppressWarnings("serial")
public class Tag extends DbTable implements Cloneable {
	public static final String TABLE_NAME = "tag";
	
	protected static int cloneId = -1000;	

	private static Logger logger = Logger.getLogger(Tag.class);

	private static boolean toStringReturnCategory = false;

	public enum TagType {
		TAG, ITEM;
		public static TagType fromInt(int i) {
			return TagType.class.getEnumConstants()[i];
		}
	}
	
	public enum Column implements IDbColumn {
		ID(new DbColumn("id")),
		TYPE(new DbColumn("type")),
		CATEGORY(new DbColumn("category", "msg.item.category")),
		NAME(new DbColumn("name", "msg.item.name")),
		DESCRIPTION(new DbColumn("description")),
		NOTES(new DbColumn("notes"));
		final private DbColumn column;
		private Column(DbColumn column) { this.column = column; }
		public DbColumn getDbColumn() { return column; }
		public String toString() { return column.toString(); }
	}

	public enum AssignmentType {
		None(""),
		Scene("Scene"),
		Character("Character"),
		Location("Location"),
		Conflict("Conflict"),
		Invalid("Invalid"),
		Mixed("Mixed");
		final private String name;

		private AssignmentType(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}
	
	protected TagType type;
	private String category;
	private String name;
	private String description;
	private String notes;

	public Tag() {
		super(TABLE_NAME);
		isNew = true;
		type = TagType.TAG;
	}
	
	public Tag(boolean isVolatile) {
		super(TABLE_NAME, isVolatile);
		this.isNew = true;
		type = TagType.TAG;
	}

	/**
	 * This method must be packaged private! It is used
	 * by {@link StrandPeer} only.
	 * 
	 * @param id
	 *            the id
	 */
	Tag(int id) {
		super(TABLE_NAME);
		this.id = id;
		this.realId = this.id;
		isNew = false;
		type = TagType.TAG;
	}

	@Override
	public boolean save() throws Exception {
		try {
			String sql;
			if (isNew) {
				// insert
				sql = "insert into "
					+ TABLE_NAME
					+ "(" + Column.TYPE
					+ "," + Column.CATEGORY
					+ "," + Column.NAME
					+ ", " + Column.DESCRIPTION
					+ ", " + Column.NOTES
					+ ") values(?, ?, ?, ?, ?)";
			} else {
				// update
				sql = "update " + TABLE_NAME
					+ " set "
					+ Column.TYPE + " = ?, "
					+ Column.CATEGORY + " = ?, "
					+ Column.NAME + " = ?, "
					+ Column.DESCRIPTION + " = ?, "
					+ Column.NOTES + " = ? "
					+ "where " + Column.ID + " = ?";
			}
			PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql);
			// sets for insert & update
			stmt.setInt(1, getTagType().ordinal());
			stmt.setString(2, getCategory());
			stmt.setString(3, getName());
			stmt.setString(4, getDescription());
			stmt.setString(5, getNotes());
			if (!isNew) {
				// sets for update only
				stmt.setInt(6, getId());
			}
			if (stmt.executeUpdate() != 1) {
				throw new SQLException(isNew ? "insert" : "update" + " failed");
			}
			if (isNew) {
				ResultSet rs = stmt.getGeneratedKeys();
				int count = 0;
				while (rs.next()) {
					if (count > 0) {
						throw new SQLException("error: got more than one id");
					}
					this.id = rs.getInt(1);
					this.realId = this.id;
					logger.debug("save (insert): " + this);
					++count;
				}
				isNew = false;
			} else {
				logger.debug("save (update): " + this);
			}
			return true;
		} catch (SQLException e) {
			throw e;
		}
	}

	public TagType getTagType() {
		return type;
//		try {
//			String sql = "select " + Tag.Column.TYPE + " from "
//					+ Tag.TABLE_NAME + " where " + Tag.Column.ID + " = ?";
//			PreparedStatement stmt = PersistenceManager.getInstance()
//					.getConnection().prepareStatement(sql);
//			stmt.setInt(1, id);
//			ResultSet rs = stmt.executeQuery();
//			rs.next();
//			int ret = rs.getInt(Tag.Column.TYPE.toString());
//			return TagType.fromInt(ret);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return TagType.TAG;
	}
	
	public Scene getScene() {
		TagLink il = this.getLink();
		return ScenePeer.doSelectById(il.getStartSceneId());
	}

	public Location getLocation() {
		TagLink il = this.getLink();
		return LocationPeer.doSelectById(il.getLocationId());
	}

	public SbCharacter getCharacter() {
		TagLink il = this.getLink();
		return SbCharacterPeer.doSelectById(il.getCharacterId());
	}

	public ArrayList<TagLink> getLinks() {
		try {
			return TagLinkPeer.doSelectByTagId(this.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public TagLink getLink() {
		ArrayList<TagLink> links = this.getLinks();
		if (links.size() == 0) {
			return null;
		}
		return links.get(0);
	}

	public boolean hasConflicts() {
		try {
			ArrayList<TagLink> links = getLinks();
			
			if (links.size() == 0) {
				// no links, no conflicts
				return false;
			}
			if (links.size() > 1) {
				// more than one link found
				for (TagLink link : links) {
					if (link.isMixedType()) {
						return true;
					}
				}
				// not a mixed type
				return false;
			}
			
			// one record found
			TagLink link = links.get(0);
			int count = 0;
			if (link.hasCharacter()) {
				++count;
			}
			if (link.hasLocation()) {
				++count;
			}
			if (link.hasStartScene()) {
				++count;
			} else if (link.hasEndScene()) {
				++count;
			}
			if (count > 1) {
				// more than one assignments found -> conflicts possible
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public AssignmentType getAssignmentType() {
		if (this.hasConflicts()) {
			return AssignmentType.Conflict;
		}

		ArrayList<TagLink> links = this.getLinks();
		if (links == null || links.isEmpty()) {
			return AssignmentType.None;
		}
		if(links.size()>1){
			return AssignmentType.Mixed;
		}
		TagLink il = links.get(0);
		if (il.getCharacterId() != -1) {
			return AssignmentType.Character;
		}
		if (il.getLocationId() != -1) {
			return AssignmentType.Location;
		}
		if (il.getStartSceneId() != -1 || il.getEndSceneId() != -1) {
			return AssignmentType.Scene;
		}
		return AssignmentType.None;
	}
	
	@Override
	public String getLabelText(){
		return toString();
	}
	
	public void setTagType(TagType type) {
		this.type = type;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getInfo() {
		StringBuffer buf = new StringBuffer();
        buf.append("<html>");
        buf.append(HtmlTools.getHeadWithCSS());
        buf.append("<body>");

		String title = this.getName();
		if (!this.getCategory().isEmpty()) {
			title += " (" + this.getCategory() + ")";
		}
		buf.append(HtmlTools.getTitle(title));
		
		// description and notes
		String descr = this.getDescription();
		if (descr != null && !descr.isEmpty()) {
			buf.append("<p>" + descr);
		}
		String notes = this.getNotes();
		if (notes != null && !notes.isEmpty()) {
			buf.append("<p>" + notes);
		}

		// tag links
		ArrayList<TagLink> links = this.getLinks();
		if (!links.isEmpty()) {
			buf.append("<p>" + I18N.getMsgColon("msg.items.links"));
			buf.append("<br>" + getAssignedToAsHTML().getText());
			for (TagLink link : links) {
				if (link.hasPeriod()) {
					buf.append("<br>" + I18N.getMsgColon("msg.items.period"));
					buf.append("<br>" + link.getPeriod());
				}
			}
		}
		
		return buf.toString();
	}
	
	public TagAssignmentData getAssignedToAsHTML() {
		return getAssignedTo(true);
	}

	public TagAssignmentData getAssignedTo() {
		return getAssignedTo(false);
	}

	public TagAssignmentData getAssignedTo(boolean asHtml) {
		try {
			String text = "";
			int lines = 0;
			ArrayList<TagLink> links = getLinks();
			for (TagLink link : links) {
				TagAssignmentData data = link.getInfo(asHtml);
				text += data.getText();
				lines += data.getLines();
			}
			--lines;
			return new TagAssignmentData(text, lines);
		} catch (Exception e) {
			e.printStackTrace();
			return new TagAssignmentData(e.getMessage());
		}
	}
	
	public static void setToStringCategory(boolean returnCategory) {
		toStringReturnCategory = returnCategory;
	}
	
	@Override
	public String toString() {
		if (toStringReturnCategory) {
			if (getCategory() == null) {
				return getName();
			}
			if (getCategory().isEmpty()) {
				return getName();
			}
			return getCategory() + ": " + getName();
		}
		
		return getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == null || obj == null) {
			return false;
		}
		if (!(obj instanceof Tag)) {
			return false;
		}

		Tag other = (Tag) obj;
		if (isClone() || other.isClone()) {
			if (getRealId() == other.getRealId()) {
				return true;
			}
			return false;
		}

		if (getId() == other.getId()) {
			if (getAssignmentType() != null
					&& getAssignmentType() == other.getAssignmentType()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + getTablename().hashCode();
		hash = hash * 31 + new Integer(getId()).hashCode();
		if (getAssignmentType() != null) {
			hash = hash * 31 + getAssignmentType().hashCode();
		}
		return hash;
	}
	
	@Override
	public Tag clone() {
		try {
			Tag clone = (Tag) super.clone();
			clone.id = cloneId--;
			clone.realId = id;
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return null;
	}
}
