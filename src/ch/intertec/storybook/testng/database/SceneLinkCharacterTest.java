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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ch.intertec.storybook.model.SceneLinkSbCharacter;
import ch.intertec.storybook.model.SceneLinkSbCharacterPeer;

public class SceneLinkCharacterTest extends AbstractTest {

	private static final Logger logger = Logger.getLogger(SceneLinkCharacterTest.class);

	private static final int NUMBER_OF_ROWS = 4;
	
	public SceneLinkCharacterTest() {
		super();
	}

	@BeforeClass
	public void setUp() {
		// log4j
		BasicConfigurator.configure();

		SceneLinkCharacterTest test = new SceneLinkCharacterTest();
		test.getPersistenceManager().init("test");
		test.getPersistenceManager().initDbModel();
	}

	@Test
	public void testTableName(){
		logger.info("=== testTableName()");
		SceneLinkSbCharacter link = new SceneLinkSbCharacter();
		Assert.assertEquals(link.getTablename(), SceneLinkSbCharacter.TABLE_NAME);
	}
	
	@Test(dependsOnMethods = { "testTableName" })
	public void testInsert() throws Exception {
		logger.info("=== testInsert()");
		for (int i = 0; i < NUMBER_OF_ROWS; ++i) {
			SceneLinkSbCharacter link = new SceneLinkSbCharacter();
			link.setSceneId(i < 2 ? 1 : 2);
			link.setCharacterId(2 + i);
			Assert.assertTrue(link.save());
		}
	}

	@Test(dependsOnMethods = { "testInsert" })
	public void testSelectAll() throws Exception {
		logger.info("=== testSelectAll()");
		List<SceneLinkSbCharacter> list = SceneLinkSbCharacterPeer.doSelectAll();
		Assert.assertEquals(list.size(), NUMBER_OF_ROWS);
	}

	@Test(dependsOnMethods = { "testSelectAll" })
	public void testUpdateById() throws Exception {
		logger.info("=== testUpdateById()");
		SceneLinkSbCharacter link = SceneLinkSbCharacterPeer.doSelectById(3);
		Assert.assertNotNull(link);
		link.setSceneId(33);
		link.setCharacterId(55);
		Assert.assertTrue(link.save());
	}
	
	@Test(dependsOnMethods = { "testUpdateById" })
	public void testSelectBySceneId() throws Exception {
		logger.info("=== testSelectBySceneId()");
		List<SceneLinkSbCharacter> list = SceneLinkSbCharacterPeer.doSelectBySceneId(1);
		Assert.assertEquals(list.size(), 2);
	}
	
	@Test(dependsOnMethods = { "testSelectBySceneId" })
	public void testDeleteBySceneId() throws Exception {
		logger.info("=== testDeleteBySceneId()");
		List<SceneLinkSbCharacter> list = SceneLinkSbCharacterPeer.doSelectBySceneId(2);
		Assert.assertFalse(list.isEmpty());
		SceneLinkSbCharacterPeer.doDeleteBySceneId(2);
		list = SceneLinkSbCharacterPeer.doSelectBySceneId(2);
		Assert.assertTrue(list.isEmpty());
	}
	
	@Test(dependsOnMethods = { "testDeleteBySceneId" })
	public void testSelect() throws Exception {
		logger.info("=== testSelect()");
		SceneLinkSbCharacterPeer.doSelectAll();
		List<SceneLinkSbCharacter> list = SceneLinkSbCharacterPeer.doSelect(33, 55);
		Assert.assertEquals(list.size(), 1);
	}
}
