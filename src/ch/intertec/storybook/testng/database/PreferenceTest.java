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

import ch.intertec.storybook.model.Preference;
import ch.intertec.storybook.model.PreferencePeer;
import ch.intertec.storybook.toolkit.PrefManager;

public class PreferenceTest extends AbstractTest {

	private static final Logger logger = Logger.getLogger(PreferenceTest.class);

	private PrefManager pm;
	
	public PreferenceTest() {
		super();
	}

	@BeforeClass
	public void setUp() {
		PreferenceTest test = new PreferenceTest();
		pm = test.getPreferenceManager();
	}

	
	@Test
	public void testInsert() throws Exception {
		logger.info("=== testInsert()");
		pm.setValue("testKeyString", "just a string value");
		pm.setValue("testKeyInteger", 42);
		pm.setValue("testKeyBoolean", true);
	}

	@Test(dependsOnMethods = { "testInsert" })
	public void testSelectAll() throws Exception {
		logger.info("=== testSelectAll()");
		List<Preference> list = PreferencePeer.doSelectAll();
		Assert.assertEquals(list.size(), 3);
	}
	
	@Test(dependsOnMethods = { "testSelectAll" })
	public void testSelectByKeyString() throws Exception {
		logger.info("=== testSelectByKeyString()");
		String value = pm.getStringValue("testKeyString");
		Assert.assertEquals(value, "just a string value");
	}
	
	@Test(dependsOnMethods = { "testSelectByKeyString" })
	public void testInsertWithSameKey() throws Exception {
		logger.info("=== testInsertWithSameKey()");
		pm.setValue("testKeyString", "just an other string value");
		List<Preference> list = PreferencePeer.doSelectAll();
		Assert.assertEquals(list.size(), 3);
	}
	
	@Test(dependsOnMethods = { "testInsertWithSameKey" })
	public void testSelectByKeyInteger() throws Exception {
		logger.info("=== testSelectByKeyInteger()");
		int value = pm.getIntegerValue("testKeyInteger");
		Assert.assertEquals(value, 42);
	}

	@Test(dependsOnMethods = { "testSelectByKeyInteger" })
	public void testSelectByKeyBoolean() throws Exception {
		logger.info("=== testSelectByKeyBoolean()");
		boolean value = pm.getBooleanValue("testKeyBoolean");
		Assert.assertEquals(value, true);
	}	

}
