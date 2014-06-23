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

import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;

public class PartTest extends AbstractTest {

	private static final Logger logger = Logger.getLogger(PartTest.class);

	private static final int NUMBER_OF_ROWS = 3;
	
	public PartTest() {
		super();
	}

	@BeforeClass
	public void setUp() {
		PartTest test = new PartTest();
		test.getPersistenceManager().init("test");
		test.getPersistenceManager().initDbModel();
	}

	@Test
	public void testTableName(){
		logger.info("=== testTableName()");
		Part part = new Part();
		Assert.assertEquals(part.getTablename(), Part.TABLE_NAME);
	}
	
	@Test(dependsOnMethods = { "testTableName" })
	public void testInsert() throws Exception {
		logger.info("=== testInsert()");
		for (int i = 0; i < NUMBER_OF_ROWS; ++i) {
			Part part = new Part();
			part.setNumber(i + 2);
			part.setName("test location " + i);
			Assert.assertTrue(part.save());
		}
	}

	@Test(dependsOnMethods = { "testInsert" })
	public void testSelectAll() throws Exception {
		logger.info("=== testSelectAll()");
		List<Part> list = PartPeer.doSelectAll();
		Assert.assertEquals(list.size(), NUMBER_OF_ROWS + 1);
	}

	@Test(dependsOnMethods = { "testSelectAll" })
	public void testSelectByid() throws Exception {
		logger.info("=== testSelectByid()");
		Part part = PartPeer.doSelectById(2);
		Assert.assertNotNull(part);
	}
	
	@Test(dependsOnMethods = { "testSelectByid" })
	public void testCount() throws Exception {
		logger.info("=== testCount()");
		Assert.assertEquals(PartPeer.doCount(), NUMBER_OF_ROWS + 1);
	}		
}
