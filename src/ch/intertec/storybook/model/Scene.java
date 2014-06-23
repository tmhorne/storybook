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
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.Border;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.log4j.Logger;

import ch.intertec.storybook.toolkit.DbTools;
import ch.intertec.storybook.toolkit.HtmlTools;
import ch.intertec.storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class Scene extends DbTable implements Comparable<Scene>, Cloneable {

	private static int cloneId = -1000;

    private static Logger logger = Logger.getLogger(Scene.class);
    public static final String TABLE_NAME = "scene";
    
    public enum Column implements IDbColumn {

        ID(new DbColumn("id")),
        CHAPTER_ID(new DbColumn("chapter_id")),
        STRAND_ID(new DbColumn("strand_id")),
        DATE(new DbColumn("date")),
        SCENE_NO(new DbColumn("sceneno")),
        TITLE(new DbColumn("title")),
        SUMMARY(new DbColumn("summary")),
        STATUS(new DbColumn("status")),
        NOTES(new DbColumn("notes")),
        RELATIVE_DATE_DIFFERENCE(new DbColumn("relative_date_difference")),
        RELATIVE_SCENE_ID(new DbColumn("relative_scene_id"));
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

    public static enum Status {
        NONE("msg.status.done", Color.black, "icon.small.status.done"),
        OUTLINE("msg.status.outline", new Color(0xAA80FF), "icon.small.status.outline"),
        DRAFT("msg.status.draft", new Color(0xFF80FF), "icon.small.status.draft"),
        FIRST_EDIT("msg.status.1st.edit", new Color(0xFF3300), "icon.small.status.edit1"),
        SECOND_EDIT("msg.status.2nd.edit", new Color(0xFFC266), "icon.small.status.edit2"),
        DONE("msg.status.done", Color.black, "icon.small.status.done");
        private String msgKey;
        private Color color;
        private Icon icon;
        private Status(String msgKey, Color color, String iconRes) {
            this.msgKey = msgKey;
            this.color = color;
            this.icon = I18N.getIcon(iconRes);
        }
        public Color getColor() { return color; }
        public Icon getIcon() { return icon; }
        public String toString() { return I18N.getMsg(msgKey); }
    }
    
    private boolean isNew;
    private int chapterId;
    private int strandId;
    private Date date;
    private int sceneNo;
    private String text;
    private int status;
    private String title;
    private String notes;
    private int relativeDateDifference;
    private int relativeSceneId;
    
    private boolean toStringShowDate = false;
    private int toStringTruncateLength = 90;

    public Scene() {
        super(TABLE_NAME);
        this.isNew = true;
        this.relativeSceneId = -1;
    }

	public Scene(boolean isVolatile) {
		super(TABLE_NAME, isVolatile);
		this.isNew = true;
		this.relativeSceneId = -1;
	}
    
    /**
     * This method must be packaged private! It is used by
     * {@link ScenePeer} only.
     * @param id the id
     */
    Scene(int id) {
        super(TABLE_NAME);
        this.id = id;
        this.realId = this.id;
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
                sql = "insert into " + TABLE_NAME
                        + "(" + Column.CHAPTER_ID
                        + ", " + Column.STRAND_ID
                        + ", " + Column.DATE
                        + ", " + Column.SCENE_NO
                        + ", " + Column.TITLE
                        + ", " + Column.SUMMARY
                        + ", " + Column.STATUS
                        + ", " + Column.RELATIVE_DATE_DIFFERENCE
                        + ", " + Column.RELATIVE_SCENE_ID
                        + ", " + Column.NOTES
                        + ") values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            } else {
                // update
                sql = "update " + TABLE_NAME
                        + " set "
                        + Column.CHAPTER_ID + " = ?, "
                        + Column.STRAND_ID + " = ?, "
                        + Column.DATE + " = ?, "
                        + Column.SCENE_NO + " = ?, "
                        + Column.TITLE + " = ?, "
                        + Column.SUMMARY + " = ?, "
                        + Column.STATUS + " = ?, "
                        + Column.RELATIVE_DATE_DIFFERENCE + " = ?, "
                        + Column.RELATIVE_SCENE_ID + " = ?, "
                        + Column.NOTES + " = ? "
                        + "where " + Column.ID + " = ?";
            }
            stmt = PersistenceManager.getInstance().getConnection().prepareStatement(sql);
            // insert & update
            stmt.setInt(1, getChapterId());
            stmt.setInt(2, getStrandId());
            stmt.setDate(3, getDate());
            stmt.setInt(4, getSceneNo());
            stmt.setString(5, getTitle());
            stmt.setString(6, getText());
            stmt.setInt(7, getStatus());
            stmt.setInt(8, getRelativeDateDifference());
            stmt.setInt(9, getRelativeSceneId());
            stmt.setString(10, getNotes());
            if (!this.isNew) {
                // update
                stmt.setInt(11, getId());
            }
            if (stmt.executeUpdate() != 1) {
                throw new SQLException(this.isNew ? "insert" : "update"
                        + " failed");
            }
            if (this.isNew) {
                this.id = PersistenceManager.getInstance().getGeneratedId(stmt);
                this.realId = this.id;
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

	public String getGraphLabelText() {
		return getChapterAndSceneNumber() + ": " + getTitle(true, 50);
	}
    
    @Override
    public String getLabelText() {
        return getSceneNoStr();
    }

    public String getChapterAndSceneNumber() {
        StringBuffer buf = new StringBuffer();
        if (getChapter() != null) {
            buf.append(getChapter().getChapterNoStr());
        } else {
            buf.append("x");
        }
        buf.append(".");
        if (getSceneNo() > 0) {
            buf.append(getSceneNoStr());
        } else {
            buf.append("x");
        }
        return buf.toString();
    }

    public void setToStringShowDate(boolean showDate) {
    	this.toStringShowDate = showDate;
    }

	public void setToStringTruncateLength(int truncateLength) {
		this.toStringTruncateLength = truncateLength;
	}
	
	public String toString() {
		String ret = "";
		if (getSceneNo() > -1) {
			ret = getChapterAndSceneNumber() + ": "
					+ getTitle(true, toStringTruncateLength);
		} else {
			ret = getTitle(true, toStringTruncateLength);
		}
		if (toStringShowDate) {
			ret += " (" + getDateStr() + ")";
		}
		return ret;
	}

    public void setText(String summary) {
        this.text = summary;
    }

    public String getText() {
        return getText(false, 0);
    }

    public String getText(boolean truncate, int length) {
        if (!truncate) {
            return text;
        }
        return truncate(text, length);
    }

    public String getShortText() {
        if (text.isEmpty()) {
            return "";
        }
        return StringUtils.substring(text, 0, 64);
    }

    public void setSceneNo(int sceneNo) {
        this.sceneNo = sceneNo;
    }

    public void setSceneNoStr(String sceneNoStr) throws NumberFormatException {
        setSceneNo(Integer.parseInt(sceneNoStr));
    }

    public int getSceneNo() {
        return sceneNo;
    }

    public String getSceneNoStr() {
        return new Integer(sceneNo).toString();
    }

    public int getStrandId() {
        return strandId;
    }

    public Strand getStrand() {
        return StrandPeer.doSelectById(strandId);
    }

    public void setStrand(Strand strand) {
        this.strandId = strand.getId();
    }

    public void setStrandId(int strandId) {
        this.strandId = strandId;
    }

    public Date getDate() {
        return date;
    }

    public String getDateStr() {
        return FastDateFormat.getDateInstance(FastDateFormat.LONG).format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(Calendar cal) {
        setDate(DbTools.calendar2SQLDate(cal));
    }

    public int getChapterId() {
        return chapterId;
    }

    public Chapter getChapter() {
        return ChapterPeer.doSelectById(chapterId);
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public void setChapter(Chapter chapter) {
        chapterId = chapter.getId();
    }

    public String getTitle() {
        return getTitle(false, 0);
    }

    public String getTitle(boolean truncate, int length) {
        if (truncate == false) {
            return title;
        }
        return truncate(title, length);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes == null ? "" : notes;
    }

	private String truncate(String str, int length) {
		if (str == null || str.isEmpty()) {
			return "";
		}
		String substr = StringUtils.substring(str, 0, length);
		if (str.length() > length) {
			return substr + "...";
		}
		return substr;
	}

	public String getInfo() {
		return getInfo();
	}
	
	public String getInfo(boolean shorten) {
		return getInfo(shorten, false);
	}
	
    public String getInfo(boolean shorten, boolean asTable) {
        StringBuffer buf = new StringBuffer();
        buf.append("<html>");
        buf.append(HtmlTools.getHeadWithCSS());
        buf.append("<body>");
        
        if(asTable){
        	buf.append("<table width='200'><tr><td>");
        }
        
        buf.append(HtmlTools.getTitle(this.toString()));
        
		buf.append("<div>");
		if (shorten) {
			buf.append(getText(true, 500));
		} else {
			buf.append(getText());
		}
		buf.append("</div>");
        
        HtmlTools.formateNotes(buf, getNotes(), false);

        if(asTable){
        	buf.append("</td></tr></table>");
        }
        
		buf.append("</body>");
		buf.append("</html>");
        return buf.toString();
    }

    public int getRelativeDateDifference() {
		return relativeDateDifference;
	}

	public void setRelativeDateDifference(int relativeDateDifference) {
		this.relativeDateDifference = relativeDateDifference;
	}

	public int getRelativeSceneId() {
		return relativeSceneId;
	}

	public void setRelativeSceneId(int relativeSceneId) {
		this.relativeSceneId = relativeSceneId;
	}

	public static Color getStatusColor(int status) {
		return Scene.Status.values()[status].getColor();
	}

	public Color getStatusColor() {
		return getStatusColor(status);
	}

	public static Icon getStatusIcon(int status) {
		return Scene.Status.values()[status].getIcon();
	}

	public Icon getStatusIcon() {
		return getStatusIcon(status);
	}

	public static Border getStatusBorder(int status) {
		Color color = getStatusColor(status);
		return BorderFactory.createLineBorder(color, 2);
	}

	public Border getStatusBorder() {
		return getStatusBorder(status);
	}

	public JLabel getStatusLabel() {
		return getStatusLabel(status);
	}
	
	public static JLabel getStatusLabel(int status) {
		JLabel lb = new JLabel();
		lb.setIcon(getStatusIcon(status));
		if (status != Scene.Status.DONE.ordinal()) {
			lb.setBorder(getStatusBorder(status));
		}
		lb.setToolTipText(I18N.getMsgColon("msg.status") + " "
				+ getStatusStr(status));
		return lb;
	}

	public static String getStatusStr(int status) {
		return Status.values()[status].toString();
	}

    public String getStatusStr() {
    	return getStatusStr(status);
    }
    
    public void setStatus(int status) {
    	this.status = status;
    }
    
    public int getStatus() {
    	return status;
    }
	
	@Override
    public int compareTo(Scene scene) {
        return toString().compareTo(scene.toString());
    }
    
	@Override
	public boolean equals(Object obj) {
		if (this == null || obj == null) {
			return false;
		}
		if (!(obj instanceof Scene)) {
			return false;
		}

		Scene other = (Scene) obj;
		if (isClone() || other.isClone()) {
			if (getRealId() == other.getRealId()) {
				return true;
			}
			return false;
		}

		if (getId() == other.getId()) {
			return true;
		}
		return false;
	}
    
	@Override
	public Scene clone() {
		try {
			Scene clone = (Scene) super.clone();
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
