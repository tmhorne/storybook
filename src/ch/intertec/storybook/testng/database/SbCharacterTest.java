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

import ch.intertec.storybook.model.Gender;
import ch.intertec.storybook.model.GenderPeer;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.SbCharacterPeer;
import ch.intertec.storybook.toolkit.DbTools;

public class SbCharacterTest extends AbstractTest {

	private static final Logger logger = Logger.getLogger(SbCharacterTest.class);

	private static final int NUMBER_OF_ROWS = 20;
	
	public SbCharacterTest() {
		super();
	}

	@BeforeClass
	public void setUp() {
		SbCharacterTest test = new SbCharacterTest();
		test.getPersistenceManager().init("test");
		test.getPersistenceManager().initDbModel();
	}

	@Test
	public void testTableName(){
		logger.info("=== testTableName()");
		SbCharacter character = new SbCharacter();
		Assert.assertEquals(character.getTablename(), SbCharacter.TABLE_NAME);
	}
	
	@Test(dependsOnMethods = { "testTableName" })
	public void testInsert() throws Exception {
            Gender male = GenderPeer.doSelectById(Gender.MALE);
            Gender female = GenderPeer.doSelectById(Gender.FEMALE);
		logger.info("=== testInsert()");
		
		Calendar cal = Calendar.getInstance();
		
		SbCharacter john = new SbCharacter();
		john.setFirstname("John");
		john.setLastname("Smith");
		john.setAbbreviation("JS");
		cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1970);
		cal.set(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		john.setBirthday(DbTools.calendar2SQLDate(cal));
		cal.set(Calendar.YEAR, 2008);
		cal.set(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);		
		john.setDayOfDeath(DbTools.calendar2SQLDate(cal));
		john.setGender(male);
		john.setCategory(SbCharacter.CATEGORY_CENTRAL);
		john.setOccupation("cabinet maker");
		john.setDescription("He was a good guy.");
		Assert.assertTrue(john.save());
		
		SbCharacter anna = new SbCharacter();
		anna.setFirstname("Anna");
		anna.setLastname("Bush");
		anna.setAbbreviation("AB");
		cal.set(Calendar.YEAR, 1975);
		cal.set(Calendar.MONTH, 2);
		cal.set(Calendar.DAY_OF_MONTH, 2);
		anna.setBirthday(DbTools.calendar2SQLDate(cal));
		anna.setGender(female);
		anna.setCategory(SbCharacter.CATEGORY_CENTRAL);
		anna.setOccupation("nurse");
		anna.setDescription("She's a good girl.");
		Assert.assertTrue(anna.save());

		SbCharacter amy = new SbCharacter();
		amy.setFirstname("Amy");
		amy.setLastname("Winston");
		amy.setAbbreviation("AW");
		cal.set(Calendar.YEAR, 1980);
		cal.set(Calendar.MONTH, 3);
		cal.set(Calendar.DAY_OF_MONTH, 4);
		amy.setBirthday(DbTools.calendar2SQLDate(cal));
		amy.setGender(female);
		amy.setCategory(SbCharacter.CATEGORY_CENTRAL);
		amy.setOccupation("teacher");
		amy.setDescription("She's also a good girl.");
		Assert.assertTrue(amy.save());

		for (int i = 0; i < NUMBER_OF_ROWS - 3; ++i) {
			SbCharacter character = new SbCharacter();
			character.setFirstname("Firstname" + i);
			character.setLastname("Lastname" + i);
			character.setAbbreviation("FL" + i);
			cal.set(Calendar.YEAR, 1970 + i);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, i);
			character.setBirthday(DbTools.calendar2SQLDate(cal));
			character.setGender(
					i % 2 == 0 ? female :male);
			character.setCategory(SbCharacter.CATEGORY_MINOR);
			character.setOccupation("");
			character.setDescription("Person " + i);
			Assert.assertTrue(character.save());
		}
	}

	@Test(dependsOnMethods = { "testInsert" })
	public void testSelectAll() throws Exception {
		logger.info("=== testSelectAll()");
		List<SbCharacter> list = SbCharacterPeer.doSelectAll();
		Assert.assertEquals(list.size(), NUMBER_OF_ROWS);
	}
	
	@Test(dependsOnMethods = { "testSelectAll" })
	public void testCount() throws Exception {
		logger.info("=== testCount()");
		int count = SbCharacterPeer.doCount();
		Assert.assertEquals(count, NUMBER_OF_ROWS);
	}
	
	@Test(dependsOnMethods = { "testCount" })
	public void testIsAlive() throws Exception {
		SbCharacter character1 = SbCharacterPeer.doSelectById(1);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2000);
		cal.set(Calendar.MONTH, 6);
		cal.set(Calendar.DAY_OF_MONTH, 14);
		Assert.assertTrue(character1.isAlive(DbTools.calendar2SQLDate(cal)));

		cal.set(Calendar.YEAR, 2008);
		cal.set(Calendar.MONTH, 6);
		cal.set(Calendar.DAY_OF_MONTH, 14);
		Assert.assertFalse(character1.isAlive(DbTools.calendar2SQLDate(cal)));		

		SbCharacter character2 = SbCharacterPeer.doSelectById(2);
		Assert.assertTrue(character2.isAlive(DbTools.calendar2SQLDate(cal)));
	}
}
