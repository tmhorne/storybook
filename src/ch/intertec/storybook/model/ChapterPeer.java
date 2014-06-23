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
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.view.modify.ChapterDialog;

public class ChapterPeer {
	
	private static Logger logger = Logger.getLogger(ChapterPeer.class);

	/**
	 * Has to be package private!
	 * @throws Exception
	 */
	static void createTable() throws Exception {
		logger.debug("createTable: drop table "
				+ Chapter.TABLE_NAME
				+ " if exists");
		String sql = "drop table " + Chapter.TABLE_NAME + " if exists";
		Statement stmt = PersistenceManager
			.getInstance().getConnection().createStatement();
		stmt.execute(sql);

		logger.debug("create table " + Chapter.TABLE_NAME);
		sql = "create table " + Chapter.TABLE_NAME + " ("
				+ Chapter.Column.ID + " identity primary key,"
				+ Chapter.Column.PART_ID + " int,"
				+ Chapter.Column.CHAPTER_NO + " int,"
				+ Chapter.Column.TITLE + " varchar(64),"
				+ Chapter.Column.DESCRIPTION + " varchar(2048),"
				+ Chapter.Column.NOTES + " varchar(4096))";
		stmt = PersistenceManager
			.getInstance().getConnection().createStatement();
		stmt.execute(sql);
	}

	public static List<Chapter> doSelectAll() {
		return doSelectAll(false);
	}
	
	public static List<Chapter> doSelectAll(boolean partDepending) {
		try{
			List<Chapter> list = new ArrayList<Chapter>();
			StringBuffer sql = new StringBuffer("select * from ");
			sql.append(Chapter.TABLE_NAME);
			if (partDepending) {
				sql.append(" where part_id = ");
				sql.append(MainFrame.getInstance().getActivePartId());
			}			
			sql.append(" order by ");
			sql.append(Chapter.Column.CHAPTER_NO);
			Statement stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				Chapter ch = makeChapter(rs);
				list.add(ch);
			}
			return list;
		} catch(Exception e){
			e.printStackTrace();
		}
		return new ArrayList<Chapter>();
	}

    public static int doCount() {
        int count = 0;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            if (!ProjectTools.isProjectOpen()) {
                return 0;
            }

            StringBuffer sql = new StringBuffer();
            sql.append("select count(");
            sql.append(Chapter.Column.ID);
            sql.append(") from ");
            sql.append(Chapter.TABLE_NAME);
            stmt = PersistenceManager.getInstance().getConnection().createStatement();
            rs = stmt.executeQuery(sql.toString());
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closeStatement(stmt);
        }
        return count;
    }
	
	public static List<Integer> doSelectIds() throws Exception {
		return doSelectIds(true);
	}
	
	public static List<Integer> doSelectIds(boolean partDepending)
			throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select " + Chapter.Column.CHAPTER_NO);
		sql.append(" from " + Chapter.TABLE_NAME);
		if (partDepending) {
			sql.append(" where part_id = ");
			sql.append(MainFrame.getInstance().getActivePartId());
		}
		sql.append(" order by " + Chapter.Column.CHAPTER_NO);
		Statement stmt = PersistenceManager.getInstance().getConnection()
				.createStatement();
		List<Integer> list = new ArrayList<Integer>();
		ResultSet rs = stmt.executeQuery(sql.toString());
		while (rs.next()) {
			int chapterNo = rs.getInt(Chapter.Column.CHAPTER_NO.toString());
			list.add(chapterNo);
		}
		return list;
	}
	
	public static List<Chapter> doSelectByPart(Part part) {
		try{
			List<Chapter> list = new ArrayList<Chapter>();
			StringBuffer sql = new StringBuffer("select * from ");
			sql.append(Chapter.TABLE_NAME);
			sql.append(" where part_id = ");
			sql.append(part.getId());
			sql.append(" order by ");
			sql.append(Chapter.Column.CHAPTER_NO);
			Statement stmt = PersistenceManager.getInstance().getConnection()
					.createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				Chapter ch = makeChapter(rs);
				list.add(ch);
			}
			return list;
		} catch(Exception e){
			e.printStackTrace();
		}
		return new ArrayList<Chapter>();
	}

	public static Chapter doSelectById(int id) {
		try{
			String sql = "select * from " + Chapter.TABLE_NAME
					+ " where " + Chapter.Column.ID + " = ?";
			PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
					.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			Chapter ch = null;
			int c = 0;
			while (rs.next() && c < 2) {
				ch = makeChapter(rs);
				++c;
			}
			if (c == 0) {
				return null;
			}
			if (c > 1) {
				throw new Exception("more than one record found");
			}
			return ch;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Integer> doSelectByChapterNo(int chapterNo) throws Exception {
		List<Integer> list = new ArrayList<Integer>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from " + Chapter.TABLE_NAME);
		sql.append(" where " + Chapter.Column.CHAPTER_NO + " = ?");
		sql.append(" order by " + Chapter.Column.CHAPTER_NO);
		PreparedStatement stmt = PersistenceManager.getInstance().getConnection()
				.prepareStatement(sql.toString());
		stmt.setInt(1, chapterNo);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			list.add(new Integer(rs.getInt(Chapter.Column.CHAPTER_NO.toString())));
		}
		return list;
	}

	private static Chapter makeChapter(ResultSet rs) throws SQLException {
		Chapter ch = new Chapter(rs.getInt(Chapter.Column.ID.toString()));
		ch.setPartId(rs.getInt(Chapter.Column.PART_ID.toString()));
		ch.setChapterNo(rs.getInt(Chapter.Column.CHAPTER_NO.toString()));
		ch.setTitle(rs.getString(Chapter.Column.TITLE.toString()));
		ch.setDescription(rs.getString(Chapter.Column.DESCRIPTION.toString()));
		ch.setNotes(rs.getString(Chapter.Column.NOTES.toString()));
		return ch;
	}	
	
	public static Chapter makeOrUpdateChapter(ChapterDialog dlg, boolean edit)
			throws Exception {
		
		Chapter chapter;
		Chapter old = null;
		if (edit) {
			chapter = dlg.getChapter();
			old = ChapterPeer.doSelectById(chapter.getId());
			old.markAsExpired();
		} else {
			chapter = new Chapter();
		}
		
		// get selected strand from combo box
		Part part = (Part) dlg.getPartComboBox().getSelectedItem();
		
		chapter.setChapterNo(Integer.parseInt(dlg.getChapterNoTF().getText()));
		chapter.setPart(part);
		chapter.setTitle(dlg.getTitleTF().getText());
		chapter.setDescription(dlg.getDescriptionTA().getText());
		chapter.setNotes(dlg.getNotesTA().getText());
		chapter.save();
		PCSDispatcher.getInstance().firePropertyChange(
				PCSDispatcher.Property.CHAPTER.toString(), old, chapter);
		return chapter;
	}

	public static boolean doDelete(Chapter chapter) throws Exception {
		if (chapter == null) {
			return false;
		}
		
		// in affected scenes set the chapter id to 0
		for (Scene scene : ScenePeer.doSelectAll()) {
			if (scene.getChapterId() == chapter.getId()) {
				scene.setChapterId(-1);
				scene.save();
			}
		}
		
		Statement stmt;
		String sql = "delete from " + Chapter.TABLE_NAME
			+ " where " + Chapter.Column.ID + " = " + chapter.getId();
		stmt = PersistenceManager.getInstance().getConnection().createStatement();
		stmt.execute(sql);
		
		chapter.markAsExpired();
		
		PCSDispatcher.getInstance().firePropertyChange(
				PCSDispatcher.Property.CHAPTER.toString(), chapter, null);
		return true;		
	}	

	public static List<Integer> getChapterNumbersAsIntegerList() {
		List<Integer> list = new ArrayList<Integer>();
		for (Chapter chapter : doSelectAll()) {
			list.add(chapter.getChapterNo());
		}
		return list;
	}
	
	public static void checkChapterNumbers(List<Integer> notFoundList,
			List<Integer> foundTwiceList) {
		try {
			List<Integer> chapterNumberList = getChapterNumbersAsIntegerList();
			if (chapterNumberList.isEmpty()) {
				return;
			}
			Integer min = Collections.min(chapterNumberList);
			Integer max = Collections.max(chapterNumberList);			
			for (int i = min.intValue(); i < max.intValue() + 1; ++i) {
				List<Integer> list = doSelectByChapterNo(i);
				if (list.isEmpty()) {
					// chapter not found
					notFoundList.add(new Integer(i));
				}
				if (list.size() > 1) {
					// chapter found twice or more
					foundTwiceList.add(new Integer(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int getNextChapterNo() {
		int max = 0;
		for (Chapter chapter : doSelectAll()) {
			if (max < chapter.getChapterNo()) {
				max = chapter.getChapterNo();
			}
		}
		if (max > 0) {
			return max + 1;
		}
		return 1;
	}

	public static Date getLastDate(Chapter chapter) {
		List<Date> list = ScenePeer.doSelectDistinctDateByChapter(chapter);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(list.size() - 1);
	}

	public static void renumberScenes(Chapter chapter) {
		try {
			int counter = 1;
			for (Scene scene : ScenePeer.doSelectByChapter(chapter)) {
				Scene old = ScenePeer.doSelectById(scene.getId());
				old.markAsExpired();
				scene.setSceneNo(counter);
				scene.save();
//				PCSDispatcher.getInstance().firePropertyChange(Property.SCENE,
//						old, scene);
				++counter;
			}
			PCSDispatcher.getInstance().firePropertyChange(Property.CHAPTER,
					null, chapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void generateChapters(int number) {
		try {
			int start = getNextChapterNo();
			for (int i = start; i < start + number; ++i) {
				Chapter chapter = new Chapter();
				chapter.setPartId(MainFrame.getInstance().getActivePartId());
				chapter.setChapterNo(i);
				chapter.setTitle(I18N.getMsg("msg.common.chapter") + " " + i);
				chapter.setDescription("");
				chapter.save();
				PCSDispatcher.getInstance().firePropertyChange(
						Property.CHAPTER, null, chapter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<DbTable> getChapterSceneList(){
		// combined chapter / scene list
		List<DbTable> list = new ArrayList<DbTable>();
		for (Chapter chapter : doSelectAll(true)) {
			list.add(chapter);
			list.addAll(ScenePeer.doSelectByChapter(chapter));
		}
		return list;
	}
	
	public static void makeCopy(Chapter chapter) {
		try {
			Chapter copy = new Chapter();
			copy.setTitle(DbPeer.getCopyString(chapter.getTitle()));
			copy.setChapterNo(getNextChapterNo());
			copy.setPart(chapter.getPart());
			copy.setDescription(chapter.getDescription());
			copy.setNotes(chapter.getNotes());
			copy.save();
			PCSDispatcher.getInstance().firePropertyChange(
					PCSDispatcher.Property.CHAPTER, null, chapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
