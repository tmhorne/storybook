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

import java.io.File;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.LocationPeer;

public class LocationTest extends AbstractTest {

	private static final Logger logger = Logger.getLogger(LocationTest.class);

	private static final int NUMBER_OF_ROWS = 5;
	
	private static final String city1 = "New York City";
	private static final String city2 = "Zurich";
	private static final String country1 = "USA";
	private static final String country2 = "Switzerland";
	
	public LocationTest() {
		super();
	}

	@BeforeClass
	public void setUp() {
		LocationTest test = new LocationTest();
		test.getPersistenceManager().create(
				new File("/home/martin/workspace_java/storybook/temp/LocationTest"));
		test.getPersistenceManager().initDbModel();
		test.getPersistenceManager().getConnection();
	}

	@Test
	public void testTableName(){
		logger.info("=== testTableName()");
		Location location = new Location();
		Assert.assertEquals(location.getTablename(), Location.TABLE_NAME);
	}
	
	@Test(dependsOnMethods = { "testTableName" })
	public void testInsertWithId() throws Exception {
		logger.info("=== testInsertWidthId()");
		Random r = new Random();
		int lastId = 0;
		for (int i = 0; i < NUMBER_OF_ROWS; ++i) {
			Location location = new Location();
			location.setName("test location " + i);
			location.setCity(i % 2 == 0 ? city1 : city2);
			location.setCountry(i % 2 == 0 ? country1 : country2);
			Assert.assertTrue(location.save());
			logger.debug("id:" + location.getId());
			Assert.assertTrue(location.changeId(lastId + r.nextInt(100)));
			lastId = location.getId();
		}
	}

	@Test(dependsOnMethods = { "testInsertWithId" })
	public void testInsert() throws Exception {
		logger.info("=== testInsert()");
		for (int i = 0; i < NUMBER_OF_ROWS; ++i) {
			Location location = new Location();
			location.setName("test location " + i);
			location.setCity(i % 2 == 0 ? city1 : city2);
			location.setCountry(i % 2 == 0 ? country1 : country2);
			Assert.assertTrue(location.save());
		}
	}

	@Test(dependsOnMethods = { "testInsert" })
	public void testSelectAll() throws Exception {
		logger.info("=== testSelectAll()");
		List<Location> list = LocationPeer.doSelectAll();
		Assert.assertEquals(list.size(), NUMBER_OF_ROWS * 2);
	}

//	@Test(dependsOnMethods = { "testSelectAll" })
//	public void testSelectByid() throws Exception {
//		logger.info("=== testSelectByid()");
//		Location location = LocationPeer.doSelectById(2);
//		Assert.assertNotNull(location);
//	}
	
	@Test(dependsOnMethods = { "testSelectAll" })
	public void testCount() throws Exception {
		logger.info("=== testCount()");
		Assert.assertEquals(LocationPeer.doCount(), NUMBER_OF_ROWS * 2);
	}	
}
