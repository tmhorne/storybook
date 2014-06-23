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
import java.sql.SQLException;

import javax.swing.Icon;

import org.apache.log4j.Logger;

import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class Gender extends DbTable {

	private static Logger logger = Logger.getLogger(Chapter.class);
	public static final String TABLE_NAME = "gender";

	public static final int MALE = 1;
	public static final int FEMALE = 2;

	public enum Column implements IDbColumn {
		ID(new DbColumn("id")),
		NAME(new DbColumn("name", "msg.dlg.mng.persons.gender")),
		PERMISSION(new DbColumn("permission", "msg.permission")),
		CHILDHOOD(new DbColumn("childhood", "msg.chart.gantt.childhood")),
		ADOLESCENCE(new DbColumn("adolescence", "msg.chart.gantt.adolescence")),
		ADULTHOOD(new DbColumn("adulthood", "msg.chart.gantt.adulthood")),
		RETIREMENT(new DbColumn("retirement", "msg.chart.gantt.retirement"));
		final private DbColumn column;

		private Column(DbColumn column) {
			this.column = column;
		}
		public DbColumn getDbColumn() {
			return column;
		}
		public String toString() {
			return column.toString();
		}
	}

	private String name;
	private int childhood = Constants.LifeCycle.CHILDHOOD_YEARS.getYears();
	private int adolescence = Constants.LifeCycle.ADOLESCENCE_YEARS.getYears();
	private int adulthood = Constants.LifeCycle.ADULTHOOD_YEARS.getYears();;
	private int retirement = Constants.LifeCycle.RETIREMENT_YEARS.getYears();;

	public Gender() {
		super(TABLE_NAME);
		this.isNew = true;
	}

	/**
	 * This method must be packaged private! It is used by {@link ScenePeer}
	 * only.
	 * 
	 * @param id the id
	 */
	public Gender(int id) {
		super(TABLE_NAME);
		this.id = id;
		this.isNew = false;
	}

	@Override
	public boolean save() throws Exception {
		boolean ret = false;
		PreparedStatement stmt = null;
		try {
			String sql;
			if (this.isNew) {
				// insert
				sql = "insert into " + TABLE_NAME + "( "
					+ Column.NAME + ", "
					+ Column.CHILDHOOD + ", "
					+ Column.ADOLESCENCE + ", "
					+ Column.ADULTHOOD + ", "
					+ Column.RETIREMENT + " "
					+ " ) values(?,?,?,?,?)";
			} else {
				// update
				sql = "update " + TABLE_NAME + " set "
					+ Column.NAME + " = ?, "
					+ Column.CHILDHOOD + " = ?, "
					+ Column.ADOLESCENCE + " = ?, "
					+ Column.ADULTHOOD + " = ?, "
					+ Column.RETIREMENT + " = ? "
					+ "where " + Column.ID + " = ?";
			}
			stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql);
			// insert & update
			stmt.setString(1, getName());
			stmt.setInt(2, getChildhood());
			stmt.setInt(3, getAdolescence());
			stmt.setInt(4, getAdulthood());
			stmt.setInt(5, getRetirement());
			if (!this.isNew) {
				// update
				stmt.setInt(6, getId());
			}
			if (stmt.executeUpdate() != 1) {
				throw new SQLException(this.isNew ? "insert" : "update"
						+ " failed");
			}
			if (this.isNew) {
				this.id = PersistenceManager.getInstance().getGeneratedId(stmt);
				this.isNew = false;
				logger.debug("save (insert): " + this);
			} else {
				logger.debug("save (update): " + this);
			}
			ret = true;
		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceManager.getInstance().closePrepareStatement(stmt);
		}
		return ret;
	}

	@Override
	public String getLabelText() {
		return this.name;
	}

	@Override
	public String toString() {
		String retour = this.name;
		switch (this.id) {
		case Gender.MALE:
			retour = I18N.getMsg("msg.dlg.person.gender.male");
			break;
		case Gender.FEMALE:
			retour = I18N.getMsg("msg.dlg.person.gender.female");
			break;
		}
		return retour;
	}

	public boolean isMale() {
		return this.id == Gender.MALE;
	}

	public boolean isFemale() {
		return this.id == Gender.FEMALE;
	}

	public boolean isMaleOrFemale() {
		return isMale() || isFemale();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean hasChildhood() {
		return childhood > 0 ? true : false;
	}

	public boolean hasAdolescence() {
		return adolescence > 0 ? true : false;
	}

	public boolean hasAdulthood() {
		return adulthood > 0 ? true : false;
	}

	public boolean hasRetirement() {
		return retirement > 0 ? true : false;
	}

	public int getChildhood() {
		return childhood;
	}

	public void setChildhood(int childhood) {
		this.childhood = childhood;
	}

	public int getAdolescence() {
		return adolescence;
	}

	public void setAdolescence(int adolescence) {
		this.adolescence = adolescence;
	}

	public int getAdulthood() {
		return adulthood;
	}

	public void setAdulthood(int adulthood) {
		this.adulthood = adulthood;
	}

	public int getRetirement() {
		return retirement;
	}

	public void setRetirement(int retirement) {
		this.retirement = retirement;
	}
	
	public Icon getIcon() {
		if (id == MALE) {
			return I18N.getIcon("icon.small.man");
		} else if (id == FEMALE) {
			return I18N.getIcon("icon.small.woman");
		}
		return I18N.getIcon("icon.small.character");
	}

	@Override
	public boolean equals(Object obj) {
		boolean retour = false;
		if (obj instanceof Gender) {
			retour = ((Gender) obj).id == this.id;
		}
		return retour;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 43 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}
}
