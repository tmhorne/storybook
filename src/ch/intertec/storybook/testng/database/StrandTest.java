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

import java.awt.Color;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ch.intertec.storybook.model.SceneLinkStrand;
import ch.intertec.storybook.model.SceneLinkStrandPeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.model.DbTable;

public class StrandTest extends AbstractTest {

	private static final Logger logger = Logger.getLogger(StrandTest.class);

	private static Color[] colorArray;

	private static final int NUMBER_OF_ROWS = 3;
	
	public StrandTest() {
		super();
		colorArray = new Color[NUMBER_OF_ROWS];
		colorArray[0] = new Color(0xFFCC80);
		colorArray[1] = new Color(0xE6FFBF);
		colorArray[2] = new Color(0xC8C8FF);
	}

	@BeforeClass
	public void setUp() {
		StrandTest test = new StrandTest();
		test.getPersistenceManager().init("test");
		test.getPersistenceManager().initDbModel();
	}

	@Test
	public void testTableName(){
		logger.info("=== testTableName()");
		Strand trace = new Strand();
		Assert.assertEquals(trace.getTablename(), Strand.TABLE_NAME);
	}
	
	@Test(dependsOnMethods = { "testTableName" })
	public void testInsert() throws Exception {
		logger.info("=== testInsert()");
		for (int i = 0; i < NUMBER_OF_ROWS; ++i) {
			Strand trace = new Strand();
			trace.setAbbreviation("TR" + i);
			trace.setName("test trace " + i);
			trace.setColor(colorArray[i]);
			Assert.assertTrue(trace.save());
		}
	}

	@Test(dependsOnMethods = { "testInsert" })
	public void testSelectAll() throws Exception {
		logger.info("=== testSelectAll()");
		List<Strand> list = StrandPeer.doSelectAll();
		Assert.assertEquals(list.size(), NUMBER_OF_ROWS + 1);
	}

	@Test(dependsOnMethods = { "testSelectAll" })
	public void testUpdateById() throws Exception {
		logger.info("=== testUpdateById()");
		Strand trace = StrandPeer.doSelectById(2);
		Assert.assertNotNull(trace);
		trace.setName("changed test trace");
		trace.setColor(new Color(543534));
		Assert.assertTrue(trace.save());
	}

	@Test(dependsOnMethods = { "testUpdateById" })
	public void testSelectByid() throws Exception {
		logger.info("=== testSelectByid()");
		DbTable trace = StrandPeer.doSelectById(2);
		Assert.assertNotNull(trace);
	}
	
	@Test(dependsOnMethods = { "testSelectByid" })
	public void testCount() throws Exception {
		logger.info("=== testCount()");
		Assert.assertEquals(StrandPeer.doCount(), NUMBER_OF_ROWS + 1);
	}
	
	@Test(dependsOnMethods = { "testCount" })
	public void testEquals() throws Exception {
		logger.info("=== testEquals()");
		DbTable trace1 = StrandPeer.doSelectById(1);
		DbTable trace2 = StrandPeer.doSelectById(3);
		DbTable trace3 = StrandPeer.doSelectById(1);
		Assert.assertEquals(trace1.hashCode(), trace3.hashCode());
		Assert.assertFalse(trace1.hashCode() == trace2.hashCode());
		Assert.assertTrue((trace1.equals(trace3)));
		Assert.assertFalse((trace1.equals(trace2)));
	}
	
	@Test(dependsOnMethods = { "testEquals" })
	public void testDeleteTrace() throws Exception {
		for (int i = 0; i < 6; ++i) {
			SceneLinkStrand link = new SceneLinkStrand();
			link.setSceneId(i + 1);
			link.setStrandId((i % 3) + 2);
			link.save();
		}
		int traceId = 2;
		Strand trace = StrandPeer.doSelectById(traceId);
		StrandPeer.doDelete(trace);
		Assert.assertTrue(
				SceneLinkStrandPeer.doSelect(2, traceId).isEmpty());
//		Assert.assertTrue(
//				SceneLinkTracePeer.doSelect(4, traceId).isEmpty());
//		Assert.assertFalse(
//				SceneLinkTracePeer.doSelect(6, 3).isEmpty());
	}
}
