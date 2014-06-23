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


public class SceneLinkStrand extends DbTable {
	
	private static final long serialVersionUID = 8811892436286646232L;

	private static Logger logger = Logger.getLogger(SceneLinkStrand.class);
	
	public static final String TABLE_NAME = "scene_strand";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_SCENE_ID = "scene_id";
	public static final String COLUMN_STRAND_ID = "strand_id";

	private int sceneId;
	private int strandId;
	
	public SceneLinkStrand() {
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
	SceneLinkStrand(int id) {
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
					+ "(" + COLUMN_SCENE_ID
					+ ", " + COLUMN_STRAND_ID
					+ ") values(?, ?)";
			} else {
				// update
				sql = "update " + TABLE_NAME
					+ " set "
					+ COLUMN_SCENE_ID + " = ?, "
					+ COLUMN_STRAND_ID + " = ? "
					+ "where " + COLUMN_ID + " = ?";
			}
			PreparedStatement stmt = PersistenceManager.getInstance()
				.getConnection().prepareStatement(sql);
			// sets for insert & update
			stmt.setInt(1, getSceneId());
			stmt.setInt(2, getStrandId());
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
	
	public Strand getStrand(){
		try {
			Strand strand = StrandPeer.doSelectById(getStrandId());
			return strand;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "id: " + id
			+ ", scene id: " + getSceneId()
			+ ", strand id: " + getStrandId();
	}
	
	public int getSceneId() {
		return sceneId;
	}

	public void setSceneId(int sceneId) {
		this.sceneId = sceneId;
	}

	public void setScene(Scene scene) {
		this.sceneId = scene.getId();
	}

	public int getStrandId() {
		return strandId;
	}

	public void setStrandId(int strandId) {
		this.strandId = strandId;
	}
	
	public void setStrand(Strand strand) {
		this.strandId = strand.getId();
	}
}
