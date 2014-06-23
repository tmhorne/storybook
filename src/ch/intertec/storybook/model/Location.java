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

import org.apache.log4j.Logger;

import ch.intertec.storybook.toolkit.HtmlTools;
import ch.intertec.storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class Location extends DbTable implements Comparable<Location> {

    private static Logger logger = Logger.getLogger(Location.class);
    public static final String TABLE_NAME = "location";

    public enum Column implements IDbColumn {
        ID(new DbColumn("id")),
        NAME(new DbColumn("name", "msg.dlg.mng.loc.name")),
        CITY(new DbColumn("city", "msg.dlg.mng.loc.city")),
        COUNTRY(new DbColumn("country", "msg.dlg.mng.loc.country")),
        DESCRIPTION(new DbColumn("description")),
        ADDRESS(new DbColumn("address", "msg.dlg.location.address")),
        NOTES(new DbColumn("notes"));
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
    private String city;
    private String country;
    private String description;
    private String address;
    private String notes;

    public Location() {
        super(TABLE_NAME);
        this.isNew = true;
    }
    
	public Location(boolean isVolatile) {
		super(TABLE_NAME, isVolatile);
		this.isNew = true;
	}

    /**
     * This method must be packaged private! It is used
     * by {@link StrandPeer} only.
     *
     * @param id
     *            the id
     */
    Location(int id) {
        super(TABLE_NAME);
        this.id = id;
        this.isNew = false;
    }

    @Override
    public boolean save() throws Exception {
        PreparedStatement stmt = null;
        boolean retour = false;
        try {
            String sql;
            if (this.isNew) {
                // insert
                sql = "insert into "
                        + TABLE_NAME
                        + "(" + Column.NAME
                        + ", " + Column.CITY
                        + ", " + Column.COUNTRY
                        + ", " + Column.DESCRIPTION
                        + ", " + Column.ADDRESS
                        + ", " + Column.NOTES
                        + ") values(?, ?, ?, ?, ?, ?)";
            } else {
                // update
                sql = "update " + TABLE_NAME
                        + " set "
                        + Column.NAME + " = ?, "
                        + Column.CITY + " = ?, "
                        + Column.COUNTRY + " = ?, "
                        + Column.DESCRIPTION + " = ?, "
                        + Column.ADDRESS + " = ?, "
                        + Column.NOTES + " = ? "
                        + "where " + Column.ID + " = ?";
            }
            stmt = PersistenceManager.getInstance().getConnection().prepareStatement(sql);
            // sets for insert & update
            stmt.setString(1, getName());
            stmt.setString(2, getCity());
            stmt.setString(3, getCountry());
            stmt.setString(4, getDescription());
            stmt.setString(5, getAddress());
            stmt.setString(6, getNotes());
            if (!this.isNew) {
                // sets for update only
                stmt.setInt(7, getId());
            }
            if (stmt.executeUpdate() != 1) {
                throw new SQLException(this.isNew ? "insert" : "update" + " failed");
            }
            if (this.isNew) {
                this.id = PersistenceManager.getInstance().getGeneratedId(stmt);
                logger.debug("save (insert): ID=" + getId() + " " + this);
                this.isNew = false;
            } else {
                logger.debug("save (update): ID=" + getId() + " " + this);
            }
            retour = true;
        } catch (SQLException e) {
            throw e;
        } finally {
            PersistenceManager.getInstance().closePrepareStatement(stmt);
        }
        return retour;
    }

    @Override
    public String getLabelText() {
        StringBuffer buf = new StringBuffer(getName());
        if (getCity().isEmpty()) {
            return buf.toString();
        }
        buf.append(", ");
        buf.append(getCity());
        return buf.toString();
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city == null ? "" : city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country == null ? "" : country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address == null ? "" : address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNotes() {
        return notes == null ? "" : notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCountryCity() {
        if (country.isEmpty()) {
            return city;
        }
        if (city.isEmpty()) {
            return country;
        }
        return country + ", " + city;
    }

    @Override
    public String toString() {
        if (isToStringUsedForList()) {
            String countryCity = getCountryCity();
            if (countryCity.isEmpty()) {
                return getName();
            }
            return countryCity + ": " + getName();
        } else {
            if (getCity().isEmpty()) {
                return getName();
            }
            return getName() + ", " + getCity();
        }
    }

    public String getInfo() {
        return getInfo(true);
    }

    public String getInfo(boolean shorten) {
        StringBuffer buf = new StringBuffer();
        buf.append("<html>");
        
        buf.append(HtmlTools.getHeadWithCSS());
        buf.append("<body>");
        
        buf.append(HtmlTools.getTitle(this.toString()));
        
        buf.append("<table>");
		if (this.getAddress() != null && !this.getAddress().isEmpty()) {
			buf.append(HtmlTools.getRow2Cols(
					I18N.getMsgColon("msg.dlg.location.address"), getAddress()));
		}
		if (this.getCountry() != null && !this.getCountry().isEmpty()) {
			buf.append(HtmlTools.getRow2Cols(
					I18N.getMsgColon("msg.dlg.location.country"), getCountry()));
		}
		buf.append("</table>");

        HtmlTools.formateDescr(buf, getDescription(), shorten);
        HtmlTools.formateNotes(buf, getNotes(), shorten);

        buf.append("</body>");
        buf.append("</html>");

        return buf.toString();
    }

    @Override
    public int compareTo(Location location) {
        return toString().compareTo(location.toString());
    }
}
