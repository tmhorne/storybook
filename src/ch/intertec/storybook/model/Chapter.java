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

import org.apache.log4j.Logger;

import ch.intertec.storybook.toolkit.HtmlTools;
import ch.intertec.storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class Chapter extends DbTable {

	private static Logger logger = Logger.getLogger(Chapter.class);
	
	public static final String TABLE_NAME = "chapter";
	
	public enum Column implements IDbColumn {
		ID(new DbColumn("id")),
		PART_ID(new DbColumn("part_id", "msg.common.part")),
		CHAPTER_NO(new DbColumn("chapterno", "msg.dlg.chapter.number")),
		TITLE(new DbColumn("title", "msg.dlg.chapter.title")),
		DESCRIPTION(new DbColumn("description", "msg.dlg.chapter.description")),
		NOTES(new DbColumn("notes"));
		final private DbColumn column;
		private Column(DbColumn column) { this.column = column; }
		public DbColumn getDbColumn() { return column; };
		public String toString() { return column.toString(); };
	}
	
	private boolean isNew;
	private boolean isForUnassignedScenes;
	private boolean pcsState;

	private int partId;
	private int chapterNo;
	private String title;
	private String description;
	private String notes;

	public Chapter() {
		super(TABLE_NAME);
		isNew = true;
		isForUnassignedScenes = false;
		pcsState = true;
	}

	public Chapter(boolean isForUnassignedScenes) {
		super(TABLE_NAME);
		isNew = true;
		this.isForUnassignedScenes = isForUnassignedScenes;
		pcsState = true;
	}

	/**
	 * This method must be packaged private! It is used by
	 * {@link ScenePeer} only.
	 * @param id the id
	 */
	Chapter(int id) {
		super(TABLE_NAME);
		this.id = id;
		isNew = false;
		isForUnassignedScenes = false;
	}

	@Override
	public boolean save() throws Exception {
		try {
			String sql;
			if (isNew) {
				// insert
				sql = "insert into " + TABLE_NAME
						+ "(" + Column.PART_ID
						+ ", " + Column.CHAPTER_NO
						+ ", " + Column.TITLE
						+ ", " + Column.DESCRIPTION
						+ ", " + Column.NOTES
						+ ") values(?, ?, ?, ?, ?)";
			} else {
				// update
				sql = "update " + TABLE_NAME
						+ " set "
						+ Column.PART_ID + " = ?, "
						+ Column.CHAPTER_NO + " = ?, "						
						+ Column.TITLE + " = ?, "
						+ Column.DESCRIPTION + " = ?, "
						+ Column.NOTES + " = ? "
						+ "where " + Column.ID + " = ?";
			}
			PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql);
			// insert & update
			stmt.setInt(1, getPartId());
			stmt.setInt(2, getChapterNo());
			stmt.setString(3, getTitle());
			stmt.setString(4, getDescription());
			stmt.setString(5, getNotes());
			if (!isNew) {
				// update
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
	
	public boolean hasScenesAssigned() {
		if (doCountScenes() > 0) {
			return true;
		}
		return false;
	}
	
	public int doCountScenes() {
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select count (");
			buf.append(Scene.Column.ID);
			buf.append(") from ");
			buf.append(Scene.TABLE_NAME);
			buf.append(" where ");
			buf.append(Scene.Column.CHAPTER_ID);
			buf.append(" = ");
			buf.append(getId());
			Statement stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			ResultSet rs = stmt.executeQuery(buf.toString());
			return rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public String getLabelText(){
		return toString();
	}
	
	public String getInfo(){
		try{
			StringBuffer buf = new StringBuffer();
			
			buf.append("<html>");
			buf.append(HtmlTools.getHeadWithCSS());
			buf.append("<body>");

			buf.append(HtmlTools.getTitle(this.toString()));
			
			HtmlTools.formateDescr(buf, getDescription(), false);
			HtmlTools.formateNotes(buf, getNotes(), false);
			
			buf.append("<hr style='margin:5px'/>");
			
			for (Scene scene : ScenePeer.doSelectByChapter(this)) {
				buf.append("<br>");
				if (scene.getSceneNo() == 0) {
					buf.append("<i>");
				}
				buf.append(scene);
				if (scene.getTitle() == null || scene.getTitle().isEmpty()) {
					buf.append(scene.getText(true, 35));
				}
				if (scene.getSceneNo() == 0) {
					buf.append("</i>");
				}
			}
			
			// check whether some scenes are missing or found twice
			java.util.List<Integer> notFoundList = new ArrayList<Integer>();
			java.util.List<Integer> foundTwiceList = new ArrayList<Integer>();
			ScenePeer.checkScenceNumbers(this, notFoundList, foundTwiceList);
			if(!notFoundList.isEmpty() || !foundTwiceList.isEmpty()){
				buf.append("<p>");	
			}
			if (!notFoundList.isEmpty()) {
				buf.append(I18N.getMsg("msg.warning.missing.scenes",
						notFoundList.toString()));
				if (!foundTwiceList.isEmpty()) {
					buf.append("<br>");
				}
			}
			if (!foundTwiceList.isEmpty()) {
				buf.append(I18N.getMsg("msg.warning.scenes.twice",
						foundTwiceList.toString()));
			}
			
			buf.append("</body>");
			buf.append("</html>");
			return buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public int getPartId() {
		return partId;
	}
	
	public Part getPart() {
		return PartPeer.doSelectById(partId);
	}	
	
	public void setPart(Part part) {
		this.partId = part.getId();
	}

	public void setPartId(int partId) {
		this.partId = partId;
	}

	public int getChapterNo() {
		return chapterNo;
	}

	public String getChapterNoStr() {
		if (chapterNo == 0) {
			return "-";
		}
		return Integer.toString(chapterNo);
	}

	public void setChapterNo(int chapterNo) {
		this.chapterNo = chapterNo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNotes() {
		return notes == null ? "" : notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public boolean isForUnassignedScenes(){
		return isForUnassignedScenes;
	}
	
	public boolean getPcsState() {
		return pcsState;
	}

	public void clearPcsState() {
		pcsState = false;
	}
	
	@Override
	public String toString() {
		return getChapterNo() + ": " + getTitle();
	}
}
