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

import ch.intertec.storybook.toolkit.I18N;


@SuppressWarnings("serial")
public class TagLink extends DbTable {
	
	private static Logger logger = Logger.getLogger(TagLink.class);
	
	public static final String TABLE_NAME = "tag_link";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_TAG_ID = "tag_id";
	public static final String COLUMN_START_SCENE_ID = "start_scene_id";
	public static final String COLUMN_END_SCENE_ID = "end_scene_id";
	public static final String COLUMN_CHARACTER_ID = "character_id";
	public static final String COLUMN_LOCATION_ID = "location_id";

	protected int tagId;
	private int startSceneId;
	private int endSceneId;
	private int characterId;
	private int locationId;
	private boolean markedForDeletion = false;
	
	public TagLink() {
		super(TABLE_NAME);
		isNew = true;
	}

	/**
	 * This method must be packaged private! It is used
	 * by {@link StrandPeer} only.
	 * 
	 * @param id
	 *            the id
	 */
	TagLink(int id) {
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
					+ "(" + COLUMN_TAG_ID
					+ ", " + COLUMN_START_SCENE_ID
					+ ", " + COLUMN_END_SCENE_ID
					+ ", " + COLUMN_CHARACTER_ID
					+ ", " + COLUMN_LOCATION_ID
					+ ") values(?, ?, ?, ?, ?)";
			} else {
				// update
				sql = "update " + TABLE_NAME
					+ " set "
					+ COLUMN_TAG_ID + " = ?, "
					+ COLUMN_START_SCENE_ID + " = ?, "
					+ COLUMN_END_SCENE_ID + " = ?, "
					+ COLUMN_CHARACTER_ID + " = ?, "
					+ COLUMN_LOCATION_ID + " = ? "
					+ "where " + COLUMN_ID + " = ?";
			}
			PreparedStatement stmt = PersistenceManager.getInstance()
				.getConnection().prepareStatement(sql);
			// sets for insert & update
			stmt.setInt(1, getTagId());
			stmt.setInt(2, getStartSceneId());
			stmt.setInt(3, getEndSceneId());
			stmt.setInt(4, getCharacterId());
			stmt.setInt(5, getLocationId());
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
		return toString();
	}
	
	public Location getLocation(){
		try {
			Location location = LocationPeer.doSelectById(getLocationId());
			return location;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public SbCharacter getCharacter() {
		try {
			SbCharacter character = SbCharacterPeer
					.doSelectById(getCharacterId());
			return character;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Scene getStartScene() {
		try {
			Scene scene = ScenePeer.doSelectById(getStartSceneId());
			return scene;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Scene getEndScene() {
		try {
			Scene scene = ScenePeer.doSelectById(getEndSceneId());
			return scene;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Tag getTag() {
		try {
			return TagPeer.doSelectById(getTagId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public TagAssignmentData getInfo(boolean asHtml) {
		String nl = "\n";
		if (asHtml) {
			nl = "<br>";
		}
		String ret = "";
		int lines = 0;
		if (hasCharacter()) {
			SbCharacter character = getCharacter();
			if (character == null) {
				return new TagAssignmentData("debug: character is null");
			}
			ret += "- " + I18N.getMsgColon("msg.common.person") + " "
					+ character.toString() + nl;
			++lines;
			TagAssignmentData data = getPeriodIAD(asHtml);
			ret += data.getText();
			lines += data.getLines();
		}
		if (hasLocation()) {
			Location location = getLocation();
			if (location == null) {
				return new TagAssignmentData("debug: location is null");
			}
			ret += "- " + I18N.getMsgColon("msg.common.location") + " "
					+ location.toString() + nl;
			++lines;
			TagAssignmentData data = getPeriodIAD(asHtml);
			ret += data.getText();
			lines += data.getLines();
		}
		if (!hasCharacter() && !hasLocation()) {
			if (hasPeriod()) {
				ret += "- " + I18N.getMsgColon("msg.common.scenes") + " "
						+ getStartScene() + " - " + getEndScene() + nl;
				ret += "     - " + I18N.getMsgColon("msg.items.period") + " "
						+ getPeriod() + nl;
				lines += 3;
			} else if (hasStartScene()) {
				ret += "- " + I18N.getMsgColon("msg.common.scene") + " "
						+ getStartScene() + nl;
				ret += "     - " + I18N.getMsgColon("msg.common.date") + " "
						+ getStartScene().getDateStr() + nl;
				lines += 2;
			}
		}
		return new TagAssignmentData(ret, lines);
	}
	
	public TagAssignmentData getPeriodIAD(boolean asHtml) {
		String nl = "\n";
		String spc = "     ";
		if (asHtml) {
			nl = "<br>";
			spc = "&nbsp;&nbsp;&nbsp;";
		}
		String ret = "";
		int lines = 0;
		if (hasPeriod()) {
			ret += spc + "- " + I18N.getMsg("msg.item.start.scene") + " "
					+ getStartScene() + nl;
			ret += spc + "- " + I18N.getMsg("msg.item.end.scene") + " "
					+ getEndScene() + nl;
			ret += spc + "- " + I18N.getMsgColon("msg.items.period") + " "
					+ getPeriod().toString() + nl;
			lines += 3;
		} else if (hasStartScene()) {
			ret += spc + "- " + I18N.getMsg("msg.common.scene") + " "
					+ getStartScene() + nl;
			ret += spc + "- " + I18N.getMsgColon("msg.common.date") + " "
					+ getStartScene().getDateStr() + nl;
			lines += 2;
		}
		return new TagAssignmentData(ret, lines);
	}
	
	public boolean hasStartScene() {
		return this.getStartScene() != null;
	}

	public boolean hasLocationOrCharacter() {
		return hasLocation() || hasCharacter();
	}
	
	public boolean hasOnlyScene() {
		if (hasLocation() || hasCharacter()) {
			return false;
		}
		return this.hasStartScene();
	}

	public boolean hasEndScene() {
		return this.getEndScene() != null;
	}

	public boolean hasPeriod() {
		return (this.getStartScene() != null && this.getEndScene() != null);
	}

	public Period getPeriod() {
		if (hasPeriod()) {
			return new Period(getStartScene().getDate(), getEndScene()
					.getDate());
		}
		if (hasStartScene()) {
			return new Period(getStartScene().getDate(), getStartScene()
					.getDate());
		}
		return null;
	}

	public boolean isEmtpy() {
		if (!hasCharacter() && !hasLocation() && !hasStartScene()) {
			return true;
		}
		return false;
	}
	
	public boolean isMixedType(){
		if(hasLocation() && hasCharacter()){
			return true;
		}
		return false;
	}
	
	public boolean hasLocation() {
		return this.getLocation() != null;
	}
	
	public boolean hasCharacter() {
		return this.getCharacter() != null;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("id: " + id);
		buf.append(", tag id: " + getTagId());
		if (getTag() != null) {
			buf.append(", tag name: " + getTag().getName());
		}
		buf.append(", start scene id: " + getStartSceneId());
		buf.append(", end scene id: " + getEndSceneId());
		buf.append(", character id: " + getCharacterId());
		buf.append(", location id: " + getLocationId());
		return buf.toString();
	}
	
	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	
	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public void setTag(Tag tag) {
		this.tagId = tag.getId();
	}

	public Scene getScene() {
		return ScenePeer.doSelectById(this.getSceneId());
	}
	
	public int getSceneId() {
		return this.getStartSceneId();
	}

	public void setScene(Scene scene) {
		this.setStartScene(scene);
	}

	public void setSceneId(int sceneId) {
		this.setStartSceneId(sceneId);
	}

	public int getStartSceneId() {
		return startSceneId;
	}

	public void setStartScene(Scene startScene) {
		this.startSceneId = startScene.getId();
	}

	public void setStartSceneId(int startSceneId) {
		this.startSceneId = startSceneId;
	}

	public int getEndSceneId() {
		return endSceneId;
	}

	public void setEndSceneId(int endSceneId) {
		this.endSceneId = endSceneId;
	}

	public void setEndScene(Scene endScene) {
		this.endSceneId = endScene.getId();
	}

	public int getCharacterId() {
		return characterId;
	}

	public void setCharacterId(int characterId) {
		this.characterId = characterId;
	}

	public void removeCharacter() {
		this.characterId = -1;
	}
	
	public void setCharacter(SbCharacter character) {
		this.characterId = character.getId();
	}

	public void setLocation(Location location) {
		this.locationId = location.getId();
	}
	
	public void markForDeletion(boolean mark) {
		this.markedForDeletion = mark;
	}

	public boolean isMarkedForDeletion() {
		return this.markedForDeletion;
	}
}
