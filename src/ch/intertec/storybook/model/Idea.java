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
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import ch.intertec.storybook.toolkit.I18N;

public class Idea extends DbTable implements Comparable<Idea> {
    private static final long serialVersionUID = 4061522274479132518L;
    public static final String TABLE_NAME = "ideas";

    @Override
    public String getLabelText() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public enum Column implements IDbColumn {
        ID(new DbColumn("id")),
        STATUS(new DbColumn("status")),
        NOTE(new DbColumn("note")),
        CATEGORY(new DbColumn("category"));
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
    private boolean isNew;
    private Idea.Status status;
    private String note;
    private String category;

    public static enum Status {

        NOT_STARTED("msg.ideas.status.not_started", Color.black, 0),
        STARTED("msg.ideas.status.started", new Color(0xAA80FF), 1),
        COMPLETED("msg.ideas.status.completed", new Color(0xFF80FF), 2),
        ABANDONED("msg.ideas.status.abandoned", new Color(0xFF3300), 3);
        private String msgKey;
        private Color color;
        private int index;

        private Status(String msgKey, Color color, int index) {
            this.msgKey = msgKey;
            this.color = color;
            this.index = index;
        }

        public Color getColor() {
            return color;
        }

        public int getIndex() {
            return this.index;
        }

        public String toString() {
            return I18N.getMsg(msgKey);
        }

        public static Idea.Status fromIndex(int index) {
            Idea.Status retour = null;
            for (Idea.Status status : Idea.Status.values()) {
                if (status.getIndex() == index) {
                    retour = status;
                    break;
                }
            }
            return retour;
        }
    };

    public Idea() {
        super(TABLE_NAME);
        this.isNew = true;
    }

    /**
     * This method must be packaged private! It is used by
     * {@link ScenePeer} only.
     * @param id the id
     */
    Idea(int id) {
        super(TABLE_NAME);
        this.id = id;
        this.isNew = false;
    }

    @Override
    public boolean save() throws SQLException {
        PreparedStatement stmt = null;
        boolean retour = false;
        try {
            String sql;
            if (this.isNew) {
                // insert
                sql = "insert into " + TABLE_NAME
                        + "(" + Column.STATUS
                        + ", " + Column.NOTE
                        + ", " + Column.CATEGORY
                        + ") values(?, ?, ?)";
            } else {
                // update
                sql = "update " + TABLE_NAME
                        + " set "
                        + Column.STATUS + " = ?, "
                        + Column.NOTE + " = ?, "
                        + Column.CATEGORY + " = ? "
                        + "where " + Column.ID + " = ?";
            }
            stmt = PersistenceManager.getInstance().getConnection().prepareStatement(sql);
            stmt.setInt(1, getStatus().getIndex());
            stmt.setString(2, getNote());
            stmt.setString(3, getCategory());
            if (!this.isNew) {
                // update
                stmt.setInt(4, getId());
            }
            if (stmt.executeUpdate() != 1) {
                throw new SQLException(this.isNew ? "insert" : "update"
                        + " failed");
            }
            if (this.isNew) {
                this.id = PersistenceManager.getInstance().getGeneratedId(stmt);
                this.isNew = false;
            }
            retour = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeStatement(stmt);
        }
        return retour;
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer(this.getStatusStr()).append(" : ").append(this.getNote());
        return buff.toString();
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return getNote(false, 0);
    }

    public String getNote(boolean truncate, int length) {
        if (!truncate) {
            return this.note;
        }
        return truncate(this.note, length);
    }

    public void setStatus(Idea.Status status) {
        this.status = status;
    }

    public Idea.Status getStatus() {
        return this.status;
    }

    public String getStatusStr() {
        return status.toString();
    }

    private String truncate(String str, int length) {
        if (str.isEmpty()) {
            return "";
        }
        String substr = StringUtils.substring(str, 0, length);
        if (str.length() > 35) {
            return substr + "...";
        }
        return substr;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInfo() {
        StringBuffer buf = new StringBuffer();
        buf.append("<html><b>" + this + "</b>");
        buf.append("<div>");
        buf.append(getNote());
        buf.append("</div>");
        buf.append("<div style='padding-top:4px;padding-bottom:4px;'>");
        return buf.toString();
    }

    @Override
    public int compareTo(Idea scene) {
        return toString().compareTo(scene.toString());
    }
}
