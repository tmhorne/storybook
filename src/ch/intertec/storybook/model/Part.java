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

import org.apache.log4j.Logger;

import ch.intertec.storybook.toolkit.HtmlTools;


public class Part extends DbTable {
	
	private static final long serialVersionUID = -4666448225012493045L;

	private static Logger logger = Logger.getLogger(Part.class);

	public static final String TABLE_NAME = "part";

	public enum Column implements IDbColumn {
		ID(new DbColumn("id")),
		NUMBER(new DbColumn("number", "msg.dlg.mng.parts.number")),
		NAME(new DbColumn("name", "msg.dlg.mng.parts.name"));
		final private DbColumn column;
		private Column(DbColumn column) { this.column = column; }
		public DbColumn getDbColumn() { return column; };
		public String toString() { return column.toString(); };
	}
	
	private int number;
	private String name;

	public Part() {
		super(TABLE_NAME);
		isNew = true;
	}

	/**
	 * This method must be packaged private! It is used
	 * by {@link PartPeer} only.
	 * 
	 * @param id
	 *            the id
	 */
	Part(int id) {
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
					+ "(" + Column.NUMBER
					+ ", " + Column.NAME
					+ ") values(?, ?)";
			} else {
				// update
				sql = "update " + TABLE_NAME
					+ " set "
					+ Column.NUMBER + " = ?, "
					+ Column.NAME + " = ? "
					+ "where " + Column.ID + " = ?";
			}
			PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql);
			// sets for insert & update
			stmt.setInt(1, getNumber());
			stmt.setString(2, getName());
			if (!isNew) {
				// sets for update only
				stmt.setInt(3, getId());
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
		return toString();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public String getInfo() {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		
		buf.append(HtmlTools.getHeadWithCSS());
		buf.append("<body>");

		buf.append(HtmlTools.getTitle(this.toString()));
		
		buf.append("</body>");
		buf.append("</html>");
		return buf.toString();
	}
	
	@Override
	public String toString() {
		return getNumberStr() + ": " + getName();
	}

	public int getNumber() {
		return number;
	}

	public String getNumberStr() {
		return "" + number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
	public void setNumberStr(String numberStr) throws NumberFormatException {
		setNumber(Integer.parseInt(numberStr));
	}	
}
