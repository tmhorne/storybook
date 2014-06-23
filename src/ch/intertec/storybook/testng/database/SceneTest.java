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

package ch.intertec.storybook.testng.database;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.toolkit.DbTools;

public class SceneTest extends AbstractTest {

	private static final Logger logger = Logger.getLogger(SceneTest.class);

	private static int NUMBER_OF_ROWS = 10;
	
	public SceneTest() {
		super();
	}

	@BeforeClass
	public void setUp() {		
		SceneTest test = new SceneTest();
		test.getPersistenceManager().init("test");
		test.getPersistenceManager().initDbModel();
	}

	@Test
	public void testInsert() throws Exception {
		logger.info("=== testInsert()");
		
		// insert strands
		StrandTest testStrand = new StrandTest();
		testStrand.testInsert();

		// scene counter, starts with 1
		int c = 1;
		for (int i = 0; i < NUMBER_OF_ROWS; ++i) {
			Scene scene = new Scene();
			scene.setStrandId(1 + i % 3);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2008);
			cal.set(Calendar.MONTH, 2);
			cal.set(Calendar.DAY_OF_MONTH, 1 + i % 4);
			scene.setDate(cal);
			scene.setSceneNo(c);
			scene.setText("test " + i);
			Assert.assertTrue(scene.save());
			++c;
		}
		
		// insert a cell with the same date
		++NUMBER_OF_ROWS;
		Scene tc = new Scene();
		tc.setStrandId(1);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2008);
		cal.set(Calendar.MONTH, 2);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		tc.setDate(cal);
		tc.setSceneNo(c);
		tc.setText("second on " + cal);
		Assert.assertTrue(tc.save());		
	}

	@Test(dependsOnMethods = { "testInsert" })
	public void testSelectAll() throws Exception {
		logger.info("=== testSelectAll()");
		List<Scene> list = ScenePeer.doSelectAll();
		Assert.assertEquals(list.size(), NUMBER_OF_ROWS);
	}

	@Test(dependsOnMethods = { "testSelectAll" })
	public void testUpdateById() throws Exception {
		logger.info("=== testUpdateById()");
		Scene tc = ScenePeer.doSelectById(3);
		Assert.assertNotNull(tc);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2008);
		cal.set(Calendar.MONTH, 7);
		cal.set(Calendar.DAY_OF_MONTH, 17);
		tc.setDate(cal);
		tc.setText("Texas");
		Assert.assertTrue(tc.save());
	}

	@Test(dependsOnMethods = { "testUpdateById" })
	public void testSelectByid() throws Exception {
		logger.info("=== testSelectByid()");
		int id = 3;
		int scene = 3;
		Scene sc = ScenePeer.doSelectById(id);
		Assert.assertNotNull(sc);
		Assert.assertEquals(sc.getSceneNo(), scene);
	}

	@Test(dependsOnMethods = { "testSelectByid" })
	public void testUpdateByInvalidId() throws Exception {
		logger.info("=== testUpdateByInvalidId()");
		int id = 38392;
		Assert.assertNull(ScenePeer.doSelectById(id));
	}

	@Test(dependsOnMethods = { "testUpdateByInvalidId" })
	public void testDeleteById() throws Exception {
		logger.info("=== testDeleteById()");
		int id = 6;
		Scene tc = ScenePeer.doSelectById(id);
		Assert.assertNotNull(tc);
		Assert.assertTrue(ScenePeer.doDelete(tc));
	}

	@Test(dependsOnMethods = { "testDeleteById" })
	public void testSelectByInvalidId() throws Exception {
		logger.info("=== testSelectByInvalidId()");
		int id = 6;
		Assert.assertNull(ScenePeer.doSelectById(id));
	}

	@Test(dependsOnMethods = { "testSelectByInvalidId" })
	public void testDeleteByInvalidId() throws Exception {
		logger.info("=== testDeleteByInvalidId()");
		int id = 843920;
		Scene tc = ScenePeer.doSelectById(id);
		Assert.assertFalse(ScenePeer.doDelete(tc));
	}

	@Test(dependsOnMethods = { "testDeleteByInvalidId" })
	public void testSelectAllOrderByDate() throws Exception {
		logger.info("=== testSelectAllOrderByDate()");
		List<Scene> list = ScenePeer
				.doSelectAll(ScenePeer.Order.BY_DATE_AND_STRAND_ID);
		Assert.assertEquals(list.size(), NUMBER_OF_ROWS - 1);
	}

	@Test(dependsOnMethods = { "testSelectAllOrderByDate" })
	public void testSelectDistinctDate() throws Exception {
		logger.info("=== testSelectDistinctDate()");
		Set<Date> set = ScenePeer.doSelectDistinctDate();
		Assert.assertEquals(set.size(), 5);
	}

	@Test(dependsOnMethods = { "testSelectDistinctDate" })
	public void testSelectByDate() throws Exception {
		logger.info("=== testSelectByDate()");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2008);
		cal.set(Calendar.MONTH, 2);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date date = DbTools.calendar2SQLDate(cal);
		List<Scene> sceneList = ScenePeer.doSelectByDate(date);
		Assert.assertEquals(sceneList.size(), 4);
	}
	
	@Test(dependsOnMethods = { "testSelectByDate" })
	public void testSelectDistinctStrandId() throws Exception {
		logger.info("=== testSelectDistinctStrandId()");
		List<Integer> list = ScenePeer.doSelectDistinctStrandId();
		Assert.assertEquals(list.size(), 3);
	}

	@Test(dependsOnMethods = { "testSelectDistinctStrandId" })
	public void testSelectByStrandIdAndDate() throws Exception {
		logger.info("=== testSelectByStrandIdAndDate()");
		int strandId = 1;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2008);
		cal.set(Calendar.MONTH, 2);
		cal.set(Calendar.DAY_OF_MONTH, 4);
		Date date = DbTools.calendar2SQLDate(cal);
		List<Scene> sceneList = ScenePeer.doSelectByStrandIdAndDate(strandId, date);
		Assert.assertEquals(sceneList.size(), 1);
	}
	
	@Test(dependsOnMethods = { "testSelectByStrandIdAndDate" })
	public void testMaxNumber() throws Exception {
		logger.info("=== testMaxNumber()");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2008);
		cal.set(Calendar.MONTH, 2);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date date = DbTools.calendar2SQLDate(cal);
		int c = ScenePeer.getMaxScenesByDate(date);
		Assert.assertEquals(c, 2);
	}
}
