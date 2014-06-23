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

import java.awt.Color;
import java.awt.Dimension;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;

import org.apache.log4j.Logger;

import ch.intertec.storybook.toolkit.HtmlTools;
import ch.intertec.storybook.toolkit.swing.ColorUtil;


public class Strand extends DbTable implements Comparable<Strand> {
	
	private static final long serialVersionUID = 1849664401566743629L;

	private static Logger logger = Logger.getLogger(Strand.class);
	
	public static final String TABLE_NAME = "strand";
	
	public enum Column implements IDbColumn {
		ID(new DbColumn("id")),
		NAME(new DbColumn("name", "msg.dlg.mng.strands.name")),
		ABBREVIATION(new DbColumn("abbreviation", "msg.dlg.strand.abbr")),
		COLOR(new DbColumn("color", "msg.dlg.mng.strands.color")),
		SORT(new DbColumn("sort", "msg.order")),
		NOTES(new DbColumn("notes"));
		final private DbColumn column;
		private Column(DbColumn column) { this.column = column; }
		public DbColumn getDbColumn() { return column; };
		public String toString() { return column.toString(); };
	}
	
	private String abbreviation;
	private String name;
	private Color color;
	private int sort;
	private String notes;

	public Strand() {
		super(TABLE_NAME);
		isNew = true;
		int sort = StrandPeer.getSortMax();
		++sort;
		setSort(sort);
	}

	/**
	 * This method must be packaged private! It is used
	 * by {@link StrandPeer} only.
	 * 
	 * @param id
	 *            the id
	 */
	Strand(int id) {
		super(TABLE_NAME);
		this.id = id;
		isNew = false;
	}

	@Override
	public boolean save() throws Exception {
		try {
			String sql;
			if (isNew) {
				// insert
				sql = "insert into "
					+ TABLE_NAME
					+ "(" + Column.ABBREVIATION
					+ ", " + Column.NAME
					+ ", " + Column.COLOR
					+ ", " + Column.SORT
					+ ", " + Column.NOTES
					+ ") values(?, ?, ?, ?, ?)";
			} else {
				// update
				sql = "update " + TABLE_NAME
					+ " set "
					+ Column.ABBREVIATION + " = ?, "
					+ Column.NAME + " = ?, "
					+ Column.COLOR + " = ?, "
					+ Column.SORT + " = ?, "
					+ Column.NOTES + " = ? "
					+ "where " + Column.ID + " = ?";
			}
			PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql);
			// sets for insert & update
			stmt.setString(1, getAbbreviation());
			stmt.setString(2, getName());
			stmt.setInt(3, getColor().getRGB());
			stmt.setInt(4, getSort());
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
	
	@Override
	public String getLabelText(){
		return getAbbreviation();
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getAbbreviation() {
		return this.abbreviation;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setColor(int color) {
		this.color = new Color(color);
	}

	public Color getColor() {
		return this.color;
	}

	public String getHTMLColor() {
		return ColorUtil.getHexName(color);
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int getSort() {
		return this.sort;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNotes() {
		return notes == null ? "" : notes;
	}

	public String getInfo() {
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("<html>");
			buf.append(HtmlTools.getHeadWithCSS());
			buf.append("<body>");

			buf.append(HtmlTools.getColoredTitle(getColor(), this.toString()));

			HtmlTools.formateNotes(buf, getNotes(), false);

			buf.append("</body>");
			buf.append("</html>");
			return buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public JLabel getColoredLabel() {
		JLabel lb = new JLabel();
		lb.setBackground(color);
		lb.setPreferredSize(new Dimension(10, 10));
		return lb;
	}
		
	@Override
	public String toString() {
		return getName() + " (" + getAbbreviation() + ")";
	}
	
	@Override
	public int compareTo(Strand strand) {
		return compareTo(strand);
	}	
}
