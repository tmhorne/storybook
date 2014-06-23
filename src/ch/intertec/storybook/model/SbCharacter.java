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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import ch.intertec.storybook.toolkit.DbTools;
import ch.intertec.storybook.toolkit.HtmlTools;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.ColorUtil;

@SuppressWarnings("serial")
public class SbCharacter extends DbTable implements Comparable<SbCharacter> {

    private static Logger logger = Logger.getLogger(SbCharacter.class);
    public static final String TABLE_NAME = "person";

	public enum Column implements IDbColumn {
		ID(new DbColumn("id")),
		GENDER_ID(new DbColumn("gender_id", "msg.dlg.mng.persons.gender")),
		FIRSTNAME(new DbColumn("firstname", "msg.dlg.person.firstname")),
		LASTNAME(new DbColumn("lastname", "msg.dlg.person.lastname")),
		ABBREVIATION(new DbColumn("abbreviation", "msg.dlg.person.abbr")),
		BIRTHDAY(new DbColumn("birthday", "msg.dlg.mng.persons.birthday")),
		DAY_OF_DEATH(new DbColumn("dayofdeath")),
		OCCUPATION(new DbColumn("occupation", "msg.dlg.person.occupation")),
		DESCRIPTION(new DbColumn("description")),
		COLOR(new DbColumn("color")),
		NOTES(new DbColumn("notes")),
		CATEGORY(new DbColumn("category", "msg.dlg.mng.persons.category"));
		
		final private DbColumn column;
		private Column(DbColumn column) { this.column = column; }
		public DbColumn getDbColumn() { return column; }
		public String toString() { return column.toString(); }
	}
    
    public static final String CATEGORY_KEY = "category";
    public static final String SB_CHARACTER_ID_KEY = "sbcharacter";
    public static final int CATEGORY_CENTRAL = 1;
    public static final int CATEGORY_MINOR = 2;
    
    private String firstname;
    private String lastname;
    private String abbreviation;
    private Date birthday;
    private Date dayOfDeath;
    private String occupation;
    private String description;
    private Color color;
    private String notes;
    private int category;
    private Gender gender;

    public SbCharacter() {
        super(TABLE_NAME);
        this.isNew = true;
        this.gender = GenderPeer.doSelectById(Gender.MALE);
    }

	public SbCharacter(boolean isVolatile) {
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
    SbCharacter(int id) {
        super(TABLE_NAME);
        this.id = id;
        this.isNew = false;
    }

    @Override
    public boolean save() throws Exception {
        boolean retour = false;
        PreparedStatement stmt = null;
        try {
            String sql;
            if (this.isNew) {
                // insert
                sql = "insert into "
                        + TABLE_NAME
                        + "(" + Column.GENDER_ID
                        + ", " + Column.FIRSTNAME
                        + ", " + Column.LASTNAME
                        + ", " + Column.ABBREVIATION
                        + ", " + Column.BIRTHDAY
                        + ", " + Column.DAY_OF_DEATH
                        + ", " + Column.OCCUPATION
                        + ", " + Column.DESCRIPTION
                        + ", " + Column.COLOR
                        + ", " + Column.NOTES
                        + ", " + Column.CATEGORY
                        + ") values(?,?,?,?,?,?,?,?,?,?,?)";
            } else {
                // update
                sql = "update " + TABLE_NAME
                        + " set "
                        + Column.GENDER_ID + " = ?, "
                        + Column.FIRSTNAME + " = ?, "
                        + Column.LASTNAME + " = ?, "
                        + Column.ABBREVIATION + " = ?, "
                        + Column.BIRTHDAY + " = ?, "
                        + Column.DAY_OF_DEATH + " = ?, "
                        + Column.OCCUPATION + " = ?, "
                        + Column.DESCRIPTION + " = ?, "
                        + Column.COLOR + " = ?, "
                        + Column.NOTES + " = ?, "
                        + Column.CATEGORY + " = ? "
                        + "where " + Column.ID + " = ?";
            }
            stmt = PersistenceManager.getInstance().getConnection().prepareStatement(sql);
            // sets for insert & update
            stmt.setInt(1, getGender().getId());
            stmt.setString(2, getFirstname());
            stmt.setString(3, getLastname());
            stmt.setString(4, getAbbreviation());
            stmt.setDate(5, getBirthday());
            stmt.setDate(6, getDayOfDeath());
            stmt.setString(7, getOccupation());
            stmt.setString(8, getDescription());
            if (getColor() == null) {
                stmt.setNull(9, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(9, getColor().getRGB());
            }
            stmt.setString(10, getNotes());
            stmt.setInt(11, getCategory());
            if (!this.isNew) {
                // sets for update only
                stmt.setInt(12, getId());
            }
            if (stmt.executeUpdate() != 1) {
                throw new SQLException(isNew ? "insert" : "update" + " failed");
            }
            if (this.isNew) {
                this.id = PersistenceManager.getInstance().getGeneratedId(stmt);
                logger.debug("save (insert): " + this);
                this.isNew = false;
            } else {
                logger.debug("save (update): " + this);
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
        return toString() + " (" + getAbbreviation() + ")";
    }

    public Boolean isAlive(Date now) {
        if (getDayOfDeath() == null) {
            return true;
        }
        return now.after(getDayOfDeath()) ? false : true;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getAbbreviation() {
        return abbreviation == null ? "" : abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getName() {
        if (getLastname().isEmpty()) {
            return getFirstname();
        }
        return getFirstname() + " " + getLastname();
    }

    public String getFirstname() {
        return firstname == null ? "" : firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname == null ? "" : lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getBirthdayStr() {
        return birthday == null ? "" : birthday.toString();
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setBirthdayStr(String dateStr) {
        try {
            Date birthday = DbTools.dateStrToSqlDate(dateStr);
            setBirthday(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getDayOfDeath() {
        return dayOfDeath;
    }

    public String getDayOfDeathStr() {
        return dayOfDeath == null ? "" : dayOfDeath.toString();
    }

    public void setDayOfDeath(Date dayOfDeath) {
        this.dayOfDeath = dayOfDeath;
    }

    public String getOccupation() {
        return occupation == null ? "" : occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getDescription() {
        return description == null ? "" : description;
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

    public Color getColor() {
        return color;
    }

    public String getHTMLColor() {
        if (color == null) {
            return "white";
        }
        return ColorUtil.getHexName(color);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(int color) {
        this.color = new Color(color);
    }

    public int getCategory() {
        return category;
    }
    
	public String getCategoryStr() {
		switch (category) {
		case CATEGORY_CENTRAL:
			return I18N.getMsg("msg.category.central.character");
		case CATEGORY_MINOR:
			return I18N.getMsg("msg.category.minor.character");
		}
		return "";
	}

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
	public String toString() {
		if (isToStringUsedForList()) {
			if (getCategory() == CATEGORY_MINOR) {
				return getName();
			}
			return "* " + getName();
		}
		if (getAbbreviation() != null && getAbbreviation().length() > 0) {
			return getName() + " (" + getAbbreviation() + ")";
		}
		return getName();
	}

    public int calculateAge(Scene scene) {
        if (birthday == null) {
            return -1;
        }

        // Create a calendar object with the date of birth
        Calendar dateOfBirth = new GregorianCalendar();
        dateOfBirth.setTime(birthday);

        // character already dead?
        if (!isAlive(scene.getDate())) {
            Calendar death = new GregorianCalendar();
            death.setTime(getDayOfDeath());
            int age = death.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
            Calendar dateOfBirth2 = new GregorianCalendar();
            dateOfBirth2.add(Calendar.YEAR, age);
            if (death.before(dateOfBirth2)) {
                age--;
            }
            return age;
        }

        // Create a calendar object with today's date
        Calendar today = new GregorianCalendar();
        today.setTime(scene.getDate());

        // Get age based on year
        int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);

        // Add the tentative age to the date of birth to get this year's birthday
        dateOfBirth.add(Calendar.YEAR, age);

        // If this year's birthday has not happened yet, subtract one from age
        if (today.before(dateOfBirth)) {
            age--;
        }
        return age;
    }

    public String getInfo() {
        return getInfo(null);
    }

    public String getInfo(Scene scene) {
        return getInfo(scene, true);
    }

    public String getInfo(Scene scene, boolean shorten) {
        StringBuffer buf = new StringBuffer();
        buf.append("<html>");
        buf.append(HtmlTools.getHeadWithCSS());
        buf.append("<body>");
        
		buf.append(HtmlTools.getColoredTitle(getColor(), this.toString()));

        buf.append("<table>");
		buf.append(HtmlTools.getRow2Cols(
				I18N.getMsgColon("msg.dlg.person.gender"),
				getGender().toString()));
		
		if (scene != null) {
			int age = calculateAge(scene);
			boolean dead = !isAlive(scene.getDate());
			if (age != -1) {
				StringBuffer txt = new StringBuffer();
				txt.append(calculateAge(scene));
				if (dead) {
					txt.append(" (+)");
				}
				buf.append(HtmlTools.getRow2Cols(
						I18N.getMsgColon("msg.dlg.person.age"), txt));
			}
		}

		if (!getBirthdayStr().isEmpty()) {
			buf.append(HtmlTools.getRow2Cols(
					I18N.getMsgColon("msg.dlg.person.birthday"),
					getBirthdayStr()));
		}
		if (!getDayOfDeathStr().isEmpty()) {
			buf.append(HtmlTools.getRow2Cols(
					I18N.getMsgColon("msg.dlg.person.death"),
					getDayOfDeathStr()));
		}
		if (!getOccupation().isEmpty()) {
			buf.append(HtmlTools.getRow2Cols(
					I18N.getMsgColon("msg.dlg.person.occupation"),
					getOccupation()));
		}
        
        buf.append("</table>");

        HtmlTools.formateDescr(buf, getDescription(), shorten);
        HtmlTools.formateNotes(buf, getNotes(), shorten);
        
        buf.append("</body>");
        buf.append("</html>");
        return buf.toString();
    }

    @Override
    public int compareTo(SbCharacter character) {
        return toString().compareTo(character.toString());
    }
}
