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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JRadioButton;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.log4j.Logger;

import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.Scene.Status;
import ch.intertec.storybook.toolkit.DbTools;
import ch.intertec.storybook.view.modify.scene.SceneDialog;

public class ScenePeer {

	private static Logger logger = Logger.getLogger(ScenePeer.class);
	public final static int UNASIGNED_CHAPTER_ID = -1;
	
	public enum Order {
		DEFAULT, BY_DATE_AND_STRAND_ID, BY_SCENE_NO, BY_CHAPTER_AND_SCENE_NUMBER
	}

	/**
	 * Has to be package private!
	 * 
	 * @throws Exception
	 */
	static void createTable() throws Exception {
		String sql;
		Statement stmt;

		logger.debug("drop table " + Scene.TABLE_NAME + " if exists");
		sql = "drop table " + Scene.TABLE_NAME + " if exists";
		stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);
		stmt.close();

		logger.debug("create table " + Scene.TABLE_NAME);
		sql = "create table " + Scene.TABLE_NAME + " (" + Scene.Column.ID
				+ " identity primary key," + Scene.Column.CHAPTER_ID + " int,"
				+ Scene.Column.STRAND_ID + " int," + Scene.Column.SCENE_NO
				+ " int," + Scene.Column.DATE + " date," + Scene.Column.TITLE
				+ " varchar(512)," + Scene.Column.SUMMARY + " varchar(32768),"
				+ Scene.Column.STATUS + " int" + ","
				+ Scene.Column.RELATIVE_DATE_DIFFERENCE + " int" + ","
				+ Scene.Column.RELATIVE_SCENE_ID + " int" + ","
				+ Scene.Column.NOTES + " varchar(32768))";
		stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		stmt.execute(sql);
		stmt.close();
	}

	public static List<Scene> doSelectAll() {
		return doSelectAll(Order.DEFAULT);
	}

	public static List<Scene> doSelectAll(Order order) {
		List<Scene> list = new ArrayList<Scene>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer("select scene.* from "
					+ Scene.TABLE_NAME);

			switch (order) {
			case DEFAULT:
				sql.append(" order by ");
				sql.append(Scene.Column.ID);
				break;
			case BY_DATE_AND_STRAND_ID:
				sql.append(" order by ");
				sql.append(Scene.Column.DATE);
				sql.append(", ");
				sql.append(Scene.Column.STRAND_ID);
				break;
			case BY_SCENE_NO:
				sql.append(" order by ");
				sql.append(Scene.Column.SCENE_NO);
				sql.append(", ");
				sql.append(Scene.Column.ID);
				break;
			case BY_CHAPTER_AND_SCENE_NUMBER:
				sql.append(" left join ");
				sql.append(Chapter.TABLE_NAME);
				sql.append(" on ");
				sql.append(Scene.TABLE_NAME + "." + Scene.Column.CHAPTER_ID);
				sql.append(" = ");
				sql.append(Chapter.TABLE_NAME + "." + Chapter.Column.ID);
				sql.append(" order by ");
				sql.append(Chapter.Column.CHAPTER_NO + ", "
						+ Scene.Column.SCENE_NO);
				break;
			}
			stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				Scene scene = makeScene(rs);
				list.add(scene);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closeStatement(stmt);
		}
		return list;
	}

	public static List<Scene> doSelectAllByChapterNumberExcept(
			Scene sceneToExclude) {
		// we retrieve all scenes which are, at any depth, related
		// to this scene, so that this scene can not be date related to them.
		// doing so, we avoid cycles.
		List<Scene> postRelatedScenes = new ArrayList<Scene>();
		ScenePeer.getAllPostRelatedScenes(sceneToExclude, postRelatedScenes);

		List<Scene> list = new ArrayList<Scene>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer("select ");
			sql.append(" scene.").append(Scene.Column.ID).append(", scene.")
					.append(Scene.Column.CHAPTER_ID).append(", scene.")
					.append(Scene.Column.STRAND_ID).append(", scene.")
					.append(Scene.Column.DATE).append(", scene.")
					.append(Scene.Column.SCENE_NO).append(", scene.")
					.append(Scene.Column.TITLE).append(", scene.")
					.append(Scene.Column.SUMMARY).append(", scene.")
					.append(Scene.Column.STATUS).append(", scene.")
					.append(Scene.Column.NOTES).append(", scene.")
					.append(Scene.Column.RELATIVE_DATE_DIFFERENCE)
					.append(", scene.").append(Scene.Column.RELATIVE_SCENE_ID)
					.append(" from ").append(Scene.TABLE_NAME).append(", ")
					.append(Chapter.TABLE_NAME).append(" where ");
			if (sceneToExclude != null) {
				sql.append(Scene.TABLE_NAME).append(".")
						.append(Scene.Column.ID).append(" != ")
						.append(sceneToExclude.id).append(" and ");
			}
			sql.append(Chapter.TABLE_NAME).append(".")
					.append(Chapter.Column.ID).append(" = ")
					.append(Scene.TABLE_NAME).append(".")
					.append(Scene.Column.CHAPTER_ID).append(" order by ")
					.append(Chapter.Column.CHAPTER_NO).append(", ")
					.append(Scene.Column.ID);

			stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				Scene scene = new Scene(rs.getInt(Scene.Column.ID.toString())) {
					/**
					 * 
					 */
					private static final long serialVersionUID = -7659238965780683379L;

					public String toString() {
						String text = this.getChapterAndSceneNumber();
						text += " ("
								+ FastDateFormat.getDateInstance(
										FastDateFormat.MEDIUM).format(
										this.getDate()) + ")";
						return text;
					}
				};
				if (!postRelatedScenes.contains(scene)) {
					scene.setChapterId(rs.getInt(Scene.Column.CHAPTER_ID
							.toString()));
					scene.setStrandId(rs.getInt(Scene.Column.STRAND_ID
							.toString()));
					scene.setDate(rs.getDate(Scene.Column.DATE.toString()));
					scene.setSceneNo(rs.getInt(Scene.Column.SCENE_NO.toString()));
					scene.setTitle(rs.getString(Scene.Column.TITLE.toString()));
					scene.setText(rs.getString(Scene.Column.SUMMARY
							.toString()));
					scene.setStatus(rs.getInt(Scene.Column.STATUS.toString()));
					scene.setNotes(rs.getString(Scene.Column.NOTES.toString()));
					scene.setRelativeDateDifference(rs
							.getInt(Scene.Column.RELATIVE_DATE_DIFFERENCE
									.toString()));
					scene.setRelativeSceneId(rs
							.getInt(Scene.Column.RELATIVE_SCENE_ID.toString()));
					list.add(scene);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closeStatement(stmt);
		}
		return list;
	}

	public static List<Scene> doSelectTasks() {
		List<Scene> list = new ArrayList<Scene>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer("select * from "
					+ Scene.TABLE_NAME);
			sql.append(" where " + Scene.Column.STATUS + " in (1,2,3,4)");
			sql.append(" order by " + Scene.Column.CHAPTER_ID);
			sql.append(" , " + Scene.Column.SCENE_NO);
			stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				Scene scene = makeScene(rs);
				list.add(scene);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closeStatement(stmt);
		}
		return list;
	}

	public static List<Scene> doSelectByDate(Date date) {
		List<Scene> list = new ArrayList<Scene>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + Scene.TABLE_NAME);
			sql.append(" where " + Scene.Column.DATE + " = ?");
			sql.append(" order by " + Scene.Column.STRAND_ID);
			stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql.toString());
			stmt.setDate(1, date);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Scene scene = makeScene(rs);
				list.add(scene);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closePrepareStatement(stmt);
		}
		return list;
	}

	public static int doCountByDateAndStrandId(Date date, int strandId) {
		int count = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select count(" + Scene.Column.ID + ")");
			sql.append(" from " + Scene.TABLE_NAME);
			sql.append(" where " + Scene.Column.DATE + " = ?");
			sql.append(" and " + Scene.Column.STRAND_ID + " = ?");
			stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql.toString());
			stmt.setDate(1, date);
			stmt.setInt(2, strandId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closePrepareStatement(stmt);
		}
		return count;
	}

	public static int doCountByDate(Date date) {
		int count = -1;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select count(" + Scene.Column.ID + ")");
			sql.append(" from " + Scene.TABLE_NAME);
			sql.append(" where " + Scene.Column.DATE + " = ?");
			stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql.toString());
			stmt.setDate(1, date);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closePrepareStatement(stmt);
		}
		return count;
	}

	public static int doCountByStrandId(int strandId) {
		int count = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select count(" + Scene.Column.ID + ")");
			sql.append(" from " + Scene.TABLE_NAME);
			sql.append(" where " + Scene.Column.STRAND_ID + " = ?");
			stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql.toString());
			stmt.setInt(1, strandId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closePrepareStatement(stmt);
		}
		return count;
	}

	public static List<Integer> doSelectBySceneNumber(int sceneNo,
			Chapter chapter) throws Exception {
		List<Integer> list = new ArrayList<Integer>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from " + Scene.TABLE_NAME);
			sql.append(" where " + Scene.Column.SCENE_NO + " = ?");
			sql.append(" and " + Scene.Column.CHAPTER_ID + " = ?");
			sql.append(" order by " + Scene.Column.STRAND_ID);
			stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql.toString());
			stmt.setInt(1, sceneNo);
			stmt.setInt(2, chapter.getId());
			rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(new Integer(
						rs.getInt(Scene.Column.SCENE_NO.toString())));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closePrepareStatement(stmt);
		}
		return list;
	}

	public static Scene doSelectById(int id) {
		Scene scene = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select * from " + Scene.TABLE_NAME + " where "
					+ Scene.Column.ID + " = ?";
			stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				scene = makeScene(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closePrepareStatement(stmt);
		}
		return scene;
	}

	public static List<Scene> doSelectByStrandId(int strandId) throws Exception {
		List<Scene> list = new ArrayList<Scene>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select scene.* from " + Scene.TABLE_NAME);
			
			sql.append(" left join ");
			sql.append(Chapter.TABLE_NAME);
			sql.append(" on ");
			sql.append(Scene.TABLE_NAME + "." + Scene.Column.CHAPTER_ID);
			sql.append(" = ");
			sql.append(Chapter.TABLE_NAME + "." + Chapter.Column.ID);
			
			sql.append(" where " + Scene.Column.STRAND_ID + " = ?");
			
			sql.append(" order by ");
			sql.append(Scene.Column.STRAND_ID);
			sql.append(", " + Chapter.Column.CHAPTER_NO);
			sql.append(", " + Scene.Column.SCENE_NO);
			
			stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql.toString());
			stmt.setInt(1, strandId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Scene scene = makeScene(rs);
				list.add(scene);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closePrepareStatement(stmt);
		}
		return list;
	}

	public static List<Scene> doSelectByChapter(Chapter chapter) {
		return doSelectByChapterId(chapter.getId());
	}

	public static List<Scene> doSelectByChapterId(int chapterId) {
		List<Scene> list = new ArrayList<Scene>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select * from ");
			buf.append(Scene.TABLE_NAME);
			buf.append(" where ");
			buf.append(Scene.Column.CHAPTER_ID);
			buf.append(" = ? ");
			buf.append(" order by ");
			buf.append(Scene.Column.CHAPTER_ID);
			buf.append(", ");
			buf.append(Scene.Column.SCENE_NO);
			buf.append(", ");
			buf.append(Scene.Column.ID);
			stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(buf.toString());
			stmt.setInt(1, chapterId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Scene scene = makeScene(rs);
				list.add(scene);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closePrepareStatement(stmt);
		}
		return list;
	}

	public static List<Scene> doSelectByStrandIdAndDate(int strandId, Date date)
			throws Exception {
		List<Scene> list = new ArrayList<Scene>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from ");
			sql.append(Scene.TABLE_NAME);
			sql.append(" where ");
			sql.append(Scene.Column.STRAND_ID);
			sql.append(" = ?");
			sql.append(" and ");
			sql.append(Scene.Column.DATE);
			sql.append(" = ?");
			sql.append(" order by ");
			sql.append(Scene.Column.CHAPTER_ID);
			sql.append(" , ");
			sql.append(Scene.Column.SCENE_NO);
			sql.append(" , ");
			sql.append(Scene.Column.ID);
			stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql.toString());
			stmt.setInt(1, strandId);
			stmt.setDate(2, date);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Scene scene = makeScene(rs);
				list.add(scene);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closePrepareStatement(stmt);
		}
		return list;
	}

	private static Scene makeScene(ResultSet rs) throws SQLException {
		Scene scene = new Scene(rs.getInt(Scene.Column.ID.toString()));
		scene.setChapterId(rs.getInt(Scene.Column.CHAPTER_ID.toString()));
		scene.setStrandId(rs.getInt(Scene.Column.STRAND_ID.toString()));
		scene.setDate(rs.getDate(Scene.Column.DATE.toString()));
		scene.setSceneNo(rs.getInt(Scene.Column.SCENE_NO.toString()));
		scene.setTitle(rs.getString(Scene.Column.TITLE.toString()));
		scene.setText(rs.getString(Scene.Column.SUMMARY.toString()));
		scene.setStatus(rs.getInt(Scene.Column.STATUS.toString()));
		scene.setNotes(rs.getString(Scene.Column.NOTES.toString()));
		scene.setRelativeDateDifference(rs
				.getInt(Scene.Column.RELATIVE_DATE_DIFFERENCE.toString()));
		scene.setRelativeSceneId(rs.getInt(Scene.Column.RELATIVE_SCENE_ID
				.toString()));
		return scene;
	}

	public static int getMaxScenesByDate(Date date) {
		int max = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			List<Strand> strandList = StrandPeer.doSelectAll();
			for (Strand strand : strandList) {
				StringBuffer sql = new StringBuffer();
				sql.append("select count(" + Scene.Column.ID + ")");
				sql.append(" from " + Scene.TABLE_NAME);
				sql.append(" where " + Scene.Column.STRAND_ID + "=?");
				sql.append(" and " + Scene.Column.DATE + "=?");
				stmt = PersistenceManager.getInstance().getConnection()
						.prepareStatement(sql.toString());
				// sets for insert & update
				stmt.setInt(1, strand.getId());
				stmt.setDate(2, date);
				rs = stmt.executeQuery();
				if (rs.next()) {
					int count = rs.getInt(1);
					if (count > max) {
						max = count;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closePrepareStatement(stmt);
		}
		return max;
	}

	public static TreeSet<Date> doSelectDistinctDate() {
		return doSelectDistinctDate(true);
	}

	public static TreeSet<Date> doSelectDistinctDate(boolean partDepending) {
		TreeSet<Date> dates = new TreeSet<Date>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();

			// select distinct(S_DATE) from (
			// select s.DATE as S_DATE, cha.PART_ID as CHA_PART_ID
			// from SCENE s
			// left outer join CHAPTER cha
			// on s.CHAPTER_ID = cha.ID
			// where cha.PART_ID = '1'
			// ) order by S_DATE

			sql.append("select distinct(S_DATE) from (");
			sql.append("select s.");
			sql.append(Scene.Column.DATE);
			sql.append(" as S_DATE, cha.");
			sql.append(Chapter.Column.PART_ID);
			sql.append(" as CHA_PART_ID");
			sql.append(" from " + Scene.TABLE_NAME + " s");
			sql.append(" left outer join " + Chapter.TABLE_NAME + " cha");
			sql.append(" on s." + Scene.Column.CHAPTER_ID + " = cha."
					+ Chapter.Column.ID);
			if (partDepending) {
				sql.append(" where cha." + Chapter.Column.PART_ID + " = ?");
			}
			sql.append(") order by S_DATE");

			if (partDepending) {
				stmt = PersistenceManager.getInstance().getConnection()
						.prepareStatement(sql.toString());
				((PreparedStatement) stmt).setInt(1, MainFrame.getInstance()
						.getActivePartId());
				rs = ((PreparedStatement) stmt).executeQuery();
			} else {
				stmt = PersistenceManager.getInstance().getConnection()
						.createStatement();
				rs = stmt.executeQuery(sql.toString());
			}
			while (rs.next()) {
				Date date = rs.getDate("S_DATE");
				dates.add(date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closeStatement(stmt);
		}

		if (partDepending) {
			try {
				// add dates from unassigned scenes
				StringBuffer sql = new StringBuffer();
				sql.append("select distinct(s." + Scene.Column.DATE + ")");
				sql.append(" from " + Scene.TABLE_NAME + " s");
				sql.append(" where s." + Scene.Column.CHAPTER_ID + " = '-1'");
				sql.append(" order by s." + Scene.Column.DATE);
				stmt = PersistenceManager.getInstance().getConnection()
						.createStatement();
				rs = stmt.executeQuery(sql.toString());
				while (rs.next()) {
					Date date = rs.getDate(Scene.Column.DATE.toString());
					dates.add(date);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				PersistenceManager.getInstance().closeResultSet(rs);
				PersistenceManager.getInstance().closeStatement(stmt);
			}
		}
		return dates;
	}

	public static List<Date> doSelectDistinctDateByChapter(Chapter chapter) {
		List<Date> list = new ArrayList<Date>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct(" + Scene.Column.DATE + ")");
			sql.append(" from " + Scene.TABLE_NAME);
			sql.append(" where " + Scene.Column.CHAPTER_ID + " = "
					+ chapter.getId());
			sql.append(" order by " + Scene.Column.DATE);
			stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				Date date = rs.getDate(Scene.Column.DATE.toString());
				list.add(date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closeStatement(stmt);
		}
		return list;
	}

	public static Date getFirstDate() {
		return getFirstDate(true);
	}

	/**
	 * Gets the first date used.
	 * 
	 * @return the first date
	 */
	public static Date getFirstDate(boolean partDepending) {
		Set<Date> dateSet = doSelectDistinctDate(partDepending);
		if (dateSet.isEmpty()) {
			return DbTools.getNowAsSqlDate();
		}
		Object[] a = dateSet.toArray();
		Arrays.sort(a);
		return (Date) a[0];
	}

	public static Date getLastDate() {
		return getLastDate(true);
	}

	/**
	 * Gets the last date used.
	 * 
	 * @return the last date
	 */
	public static Date getLastDate(boolean partDepending) {
		Set<Date> dateSet = doSelectDistinctDate(partDepending);
		if (dateSet.isEmpty()) {
			return DbTools.getNowAsSqlDate();
		}
		Object[] a = dateSet.toArray();
		Arrays.sort(a);
		return (Date) a[a.length - 1];
	}

	public static List<Integer> doSelectDistinctStrandId() throws Exception {
		List<Integer> list = new ArrayList<Integer>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct(" + Scene.Column.STRAND_ID + ")");
			sql.append(" from " + Scene.TABLE_NAME);
			sql.append(" order by " + Scene.Column.STRAND_ID);
			stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				int strandId = rs.getInt(Scene.Column.STRAND_ID.toString());
				list.add(strandId);
			}
		} catch (SQLException exc) {
			exc.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closeStatement(stmt);
		}
		return list;
	}

	public static int doCount() {
		int count = 0;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			if (PersistenceManager.getInstance().isConnectionOpen()) {
				StringBuffer sql = new StringBuffer();
				sql.append("select count(s.");
				sql.append(Scene.Column.ID);
				sql.append(")");
				sql.append(" from ");
				sql.append(Scene.TABLE_NAME);
				sql.append(" s, ");
				sql.append(Chapter.TABLE_NAME);
				sql.append(" c where s.");
				sql.append(Scene.Column.CHAPTER_ID);
				sql.append(" = c.");
				sql.append(Chapter.Column.ID);
				sql.append(" and s.");
				sql.append(Scene.Column.SCENE_NO);
				sql.append(" > -1");
				sql.append(" and c.");
				sql.append(Chapter.Column.PART_ID);
				sql.append(" = ");
				sql.append(MainFrame.getInstance().getActivePartId());
				stmt = PersistenceManager.getInstance().getConnection()
						.createStatement();
				rs = stmt.executeQuery(sql.toString());
				if (rs.next()) {
					count = rs.getInt(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closeStatement(stmt);
		}
		return count;
	}

	public static List<Integer> doSelect() throws Exception {
		List<Integer> list = new ArrayList<Integer>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select " + Scene.Column.SCENE_NO);
			sql.append(" from " + Scene.TABLE_NAME);
			sql.append(" order by " + Scene.Column.SCENE_NO);
			stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				int sceneNo = rs.getInt(Scene.Column.SCENE_NO.toString());
				list.add(sceneNo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closeStatement(stmt);
		}
		return list;
	}

	/**
	 * Cascaded deletion of the given scenes and all its links to the tables
	 * <ol>
	 * <li>strand</li>
	 * <li>person</li>
	 * <li>location</li>
	 * </ol>
	 * 
	 * @param scene
	 *            the scene to delete
	 * @return true if ch is not null, false otherwise
	 * @throws Exception
	 */
	public static boolean doDelete(Scene scene) throws Exception {
		boolean retour = false;
		if (scene != null) {
			int sceneId = scene.getId();
			// delete strand links
			SceneLinkStrandPeer.doDeleteBySceneId(sceneId);
			// delete character links
			SceneLinkSbCharacterPeer.doDeleteBySceneId(sceneId);
			// delete location links
			SceneLinkLocationPeer.doDeleteBySceneId(sceneId);
			// delete item assignments
			TagLinkPeer.doDeleteBySceneId(sceneId);

			if (ScenePeer.sceneHasDirectlyDateRelatedScenes(scene)) {
				// we need to update them
				// is this scene date related to another one ?
				if (scene.getRelativeSceneId() != -1) {
					// it is date related
					// we need to shift the relation
					ScenePeer.shiftDateRelatedRelations(scene);
				} else {
					// it is not date related to any other one
					// the related scenes will become not date related
					ScenePeer.setDateRelatedScenesAsNotDateRelated(scene);
				}
			}

			// delete the scene
			Statement stmt = null;
			try {
				String sql = "delete from " + Scene.TABLE_NAME + " where "
						+ Scene.Column.ID + " = " + scene.getId();
				stmt = PersistenceManager.getInstance().getConnection()
						.createStatement();
				stmt.execute(sql);
				retour = true;
			} catch (SQLException exc) {
				exc.printStackTrace();
			} finally {
				PersistenceManager.getInstance().closeStatement(stmt);
			}
			PCSDispatcher.getInstance().firePropertyChange(
					PCSDispatcher.Property.SCENE.toString(), scene, null);
		}
		return retour;
	}

	private static void shiftDateRelatedRelations(Scene scene) throws Exception {
		if (scene != null) {
			List<Scene> relatedScenes = ScenePeer.getDateRelatedScenes(scene);
			for (Scene relatedScene : relatedScenes) {
				relatedScene.setRelativeSceneId(scene.getRelativeSceneId());
				relatedScene.setRelativeDateDifference(relatedScene
						.getRelativeDateDifference()
						+ scene.getRelativeDateDifference());
				relatedScene.save();
			}
		}
	}

	private static void setDateRelatedScenesAsNotDateRelated(Scene scene) {
		if (scene != null) {
			// delete the scene
			Statement stmt = null;
			try {
				String sql = "UPDATE " + Scene.TABLE_NAME + " set "
						+ Scene.Column.RELATIVE_DATE_DIFFERENCE + " = 0, "
						+ Scene.Column.RELATIVE_SCENE_ID + " = -1 " + " WHERE "
						+ Scene.Column.RELATIVE_SCENE_ID + " = "
						+ scene.getId();
				stmt = PersistenceManager.getInstance().getConnection()
						.createStatement();
				stmt.executeUpdate(sql);
			} catch (SQLException exc) {
				exc.printStackTrace();
			} finally {
				PersistenceManager.getInstance().closeStatement(stmt);
			}
		}
	}

	private static boolean sceneHasDirectlyDateRelatedScenes(Scene scene) {
		boolean retour = false;
		if (scene != null) {
			// delete the scene
			Statement stmt = null;
			ResultSet rs = null;
			try {
				String sql = "select count(id) as nb from " + Scene.TABLE_NAME
						+ " where " + Scene.Column.RELATIVE_SCENE_ID + " = "
						+ scene.getId();
				stmt = PersistenceManager.getInstance().getConnection()
						.createStatement();
				rs = stmt.executeQuery(sql);
				if (rs.next()) {
					int nb = rs.getInt("nb");
					retour = (nb > 0);
				}
			} catch (SQLException exc) {
				exc.printStackTrace();
			} finally {
				PersistenceManager.getInstance().closeResultSet(rs);
				PersistenceManager.getInstance().closeStatement(stmt);
			}
		}
		return retour;
	}

	public static void doDeleteByStrandId(int strandId) throws Exception {
		Statement stmt = null;
		try {
			String sql = "delete from " + Scene.TABLE_NAME + " where "
					+ Scene.Column.STRAND_ID + " = " + strandId;
			stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			stmt.execute(sql);
		} catch (SQLException exc) {
			exc.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeStatement(stmt);
		}
	}

	public static Scene makeOrUpdateScene(SceneDialog dlg, boolean edit)
			throws Exception {

		// get date from date chooser
		java.sql.Date date;
		date = new java.sql.Date(dlg.getDateChooser().getDate().getTime());

		// get selected strand from combo box
		Strand strand = (Strand) dlg.getStrandComboBox().getSelectedItem();

		// get selected chapter from combo box
		Chapter chapter = (Chapter) dlg.getChapterComboBox().getSelectedItem();

		// get selected status from combo box
		Status status = (Status) dlg.getStatusComboBox().getSelectedItem();

		Scene scene = null;
		Scene old = null;
		if (edit) {
			scene = dlg.getScene();
			old = ScenePeer.doSelectById(scene.getId());
			old.markAsExpired();
		} else {
			scene = new Scene();
		}
		scene.setDate(date);
		scene.setStrand(strand);
		scene.setChapter(chapter);
		scene.setStatus(status.ordinal());
		if (dlg.getSceneTextField().getText().isEmpty()) {
			scene.setSceneNo(0);
		} else {
			scene.setSceneNoStr(dlg.getSceneTextField().getText());
		}
		scene.setTitle(dlg.getTitleTextArea().getText());
		scene.setText(dlg.getSummaryTextArea().getText());
		scene.setNotes(dlg.getNotesTextArea().getText());

		JRadioButton relativeDateBtn = dlg.getRelativeDateButton();
		if (relativeDateBtn.isSelected()) {
			// this scene has a date relative to another scene
			scene.setRelativeDateDifference((Integer) dlg
					.getRelativeDateSpinner().getValue());
			Scene relativeScene = (Scene) dlg.getRelativeSceneComboBox()
					.getSelectedItem();
			scene.setRelativeSceneId(relativeScene.getId());
		} else {
			scene.setRelativeDateDifference(0);
			scene.setRelativeSceneId(-1);
		}

		scene.save();

		ScenePeer.updateRelatedScenesDates(scene);

		// make strand links
		SceneLinkStrandPeer.makeLinks(scene.getId(), dlg);

		// make character links
		SceneLinkSbCharacterPeer.makeLinks(scene.getId(), dlg);

		// make location links
		SceneLinkLocationPeer.makeLinks(scene.getId(), dlg);

		// fire property change event
		PCSDispatcher.getInstance().firePropertyChange(
				PCSDispatcher.Property.SCENE, old, scene);
		return scene;
	}

	private static void updateRelatedScenesDates(Scene scene) {
		List<Scene> scenes = ScenePeer.getDateRelatedScenes(scene);
		for (Scene relatedScene : scenes) {
			Date d = (Date) scene.getDate().clone();
			d = new Date(DateUtils.addDays(d,
					relatedScene.getRelativeDateDifference()).getTime());
			relatedScene.setDate(d);
			try {
				relatedScene.save();
			} catch (Exception e) {
				e.printStackTrace();
			}
			ScenePeer.updateRelatedScenesDates(relatedScene);
		}
	}

	public static void getAllPostRelatedScenes(Scene scene,
			List<Scene> postRelatedScenes) {
		List<Scene> scenes = ScenePeer.getDateRelatedScenes(scene);
		for (Scene relatedScene : scenes) {
			postRelatedScenes.add(relatedScene);
			ScenePeer.getAllPostRelatedScenes(relatedScene, postRelatedScenes);
		}
	}

	public static List<Scene> getDateRelatedScenes(Scene scene) {
		List<Scene> scenes = new ArrayList<Scene>();
		if (scene != null) {
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				StringBuffer sql = new StringBuffer();
				sql.append("select * from ");
				sql.append(Scene.TABLE_NAME);
				sql.append(" where ");
				sql.append(Scene.Column.RELATIVE_SCENE_ID);
				sql.append(" = ?");
				stmt = PersistenceManager.getInstance().getConnection()
						.prepareStatement(sql.toString());
				stmt.setInt(1, scene.getId());
				rs = stmt.executeQuery();
				while (rs.next()) {
					Scene s = makeScene(rs);
					scenes.add(s);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				PersistenceManager.getInstance().closeResultSet(rs);
				PersistenceManager.getInstance().closePrepareStatement(stmt);
			}
		}
		return scenes;
	}

	public static List<Integer> getSceneNumbersAsIntegerList(Chapter chapter)
			throws Exception {
		List<Integer> list = new ArrayList<Integer>();
		for (Scene scene : doSelectByChapter(chapter)) {
			list.add(scene.getSceneNo());
		}
		return list;
	}

	public static void checkScenceNumbers(Chapter chapter,
			List<Integer> notFoundList, List<Integer> foundTwiceList) {
		try {
			List<Integer> sceneNumberList = getSceneNumbersAsIntegerList(chapter);
			if (sceneNumberList.isEmpty()) {
				return;
			}
			Integer min = Collections.min(sceneNumberList);
			Integer max = Collections.max(sceneNumberList);
			for (int i = min.intValue(); i < max.intValue() + 1; ++i) {
				List<Integer> list = doSelectBySceneNumber(i, chapter);
				if (list.isEmpty()) {
					// scene not found
					notFoundList.add(new Integer(i));
				}
				if (list.size() > 1) {
					// scene found twice or more
					foundTwiceList.add(new Integer(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getNextSceneNo(Chapter chapter) {
		try {
			int max = 0;
			for (Scene scene : doSelectByChapter(chapter)) {
				if (max < scene.getSceneNo()) {
					max = scene.getSceneNo();
				}
			}
			if (max > 0) {
				return max + 1;
			} else {
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static boolean makeSceneUnassigned(int sceneId) {
		try {
			Chapter oldChapter = ScenePeer.doSelectById(sceneId).getChapter();
			
			Scene old = doSelectById(sceneId);
			old.markAsExpired();
			Scene scene = doSelectById(sceneId);
			scene.setChapterId(-1);
			scene.setSceneNo(-1);
			scene.save();
			PCSDispatcher.getInstance().firePropertyChange(Property.SCENE, old,
					scene);
			
			PCSDispatcher.getInstance().firePropertyChange(Property.CHAPTER,
					oldChapter, null);

			Chapter unassignedChapter = new Chapter(true);
			PCSDispatcher.getInstance().firePropertyChange(Property.CHAPTER,
					null, unassignedChapter);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean insertScene(int sceneId, int chapterId, int newSceneNo) {
		try {
			Chapter oldChapter = ScenePeer.doSelectById(sceneId).getChapter();
			
			Chapter chapter = ChapterPeer.doSelectById(chapterId);
			int counter = 1;

			Scene scene = doSelectById(sceneId);
			scene.setChapterId(chapterId);
			scene.setSceneNo(ScenePeer.UNASIGNED_CHAPTER_ID);
			scene.save();

			for (Scene s : ScenePeer.doSelectByChapterId(chapterId)) {
				if (s.getSceneNo() == -1) {
					// insert scene
					s.setSceneNo(newSceneNo);
					s.save();
					++counter;
					continue;
				}
				if (s.getSceneNo() < newSceneNo) {
					++counter;
					continue;
				}
				s.setSceneNo(counter + 1);
				s.save();
				++counter;
			}

			// renumber scenes
			ChapterPeer.renumberScenes(chapter);

			PCSDispatcher.getInstance().firePropertyChange(Property.CHAPTER,
					oldChapter, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean moveSceneToBegin(int sceneId, int destChapterId) {
		try {
			Chapter oldChapter = ScenePeer.doSelectById(sceneId).getChapter();
			
			Scene scene = ScenePeer.doSelectById(sceneId);
			if (destChapterId != -1) {
				scene.setChapterId(destChapterId);
			}
			scene.setSceneNo(-1);
			scene.save();
			
			ChapterPeer.renumberScenes(scene.getChapter());
			
			PCSDispatcher.getInstance().firePropertyChange(Property.CHAPTER,
					oldChapter, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean moveSceneToEnd(int sceneId, int destChapterId) {
		// currently not used
		Scene scene = ScenePeer.doSelectById(sceneId);
		int next = -1;
		if (destChapterId != -1) {
			scene.setChapterId(destChapterId);
			next = getNextSceneNo(ChapterPeer.doSelectById(destChapterId));
		} else {
			next = getNextSceneNo(ChapterPeer
					.doSelectById(scene.getChapterId()));
		}
		if (next == -1) {
			next = 1;
		}
		scene.setSceneNo(next);
		try {
			scene.save();
			ChapterPeer.renumberScenes(scene.getChapter());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean swapScenes(int sourceId, int destId) {
		if (sourceId == destId) {
			return true;
		}

		try {
			Scene sourceOld = doSelectById(sourceId);
			sourceOld.markAsExpired();
			Scene sourceScene = doSelectById(sourceId);
			Scene destOld = doSelectById(destId);
			destOld.markAsExpired();
			Scene destScene = doSelectById(destId);
			int sourceSceneNo = sourceScene.getSceneNo();
			Chapter sourceChapter = sourceScene.getChapter();
			int sourceChapterId = sourceChapter.getId();
			int destSceneNo = destScene.getSceneNo();
			Chapter destChapter = destScene.getChapter();
			int destChapterId = destChapter.getId();
			sourceScene.setSceneNo(destSceneNo);
			sourceScene.setChapterId(destChapterId);
			destScene.setSceneNo(sourceSceneNo);
			destScene.setChapterId(sourceChapterId);
			
			sourceScene.save();
			PCSDispatcher.getInstance().firePropertyChange(Property.SCENE,
					sourceOld, sourceScene);
			destScene.save();
			PCSDispatcher.getInstance().firePropertyChange(Property.SCENE,
					destOld, destScene);

			PCSDispatcher.getInstance().firePropertyChange(Property.CHAPTER,
					sourceChapter, null);
			PCSDispatcher.getInstance().firePropertyChange(Property.CHAPTER,
					null, destChapter);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Date getPreviousChronologicalSceneDateInStrand(Scene scene) {
		Date retour = null;

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select max(").append(Scene.Column.DATE)
					.append(") as max").append(" from ")
					.append(Scene.TABLE_NAME).append(" where ")
					.append(Scene.Column.STRAND_ID).append(" = ? ")
					.append(" and ").append(Scene.Column.DATE).append(" < ? ");
			stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql.toString());
			stmt.setInt(1, scene.getStrandId());
			stmt.setDate(2, scene.getDate());
			rs = stmt.executeQuery();
			if (rs.next()) {
				retour = rs.getDate("max");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.getInstance().closeResultSet(rs);
			PersistenceManager.getInstance().closePrepareStatement(stmt);
		}

		return retour;
	}
}
