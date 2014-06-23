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
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;

public class GenerateTestProjects extends AbstractTest {

	private static Logger logger = Logger.getLogger(GenerateTestProjects.class);

	private static Color[] colorArray;

	private GenerateTestProjects instance;

	private static final int TEST1_NUMBER_OF_STRANDS = 3;
	private static final int TEST1_NUMBER_OF_SCENES = 2;
	private static final int TEST2_NUMBER_OF_STRANDS = 4;
	private static final int TEST2_NUMBER_OF_SCENES = 10;

	public GenerateTestProjects() {
		colorArray = new Color[TEST2_NUMBER_OF_STRANDS + 1];
		colorArray[1] = new Color(0xFFCC80);
		colorArray[2] = new Color(0xE6FFBF);
		colorArray[3] = new Color(0xC8C8FF);
		colorArray[4] = new Color(0xFFBFEF);
	}

	@BeforeClass
	public void setUp(){
		// nothing to do
	}
	
	@Test
	public void generateTestData() throws Exception {
		instance = new GenerateTestProjects();
		generateDb1();
		generateDb2();
	}

	public void generateDb1() throws Exception {
		instance.getPersistenceManager().init("Test 01");
		instance.getPersistenceManager().initDbModel();

		Part part2 = new Part();
		part2.setNumber(2);
		part2.setName("Part 2");
		part2.save();
		
		Chapter chapter1 = new Chapter();
		chapter1.setPart(PartPeer.getFirstPart());
		chapter1.setChapterNo(1);
		chapter1.setTitle("Chapter 1");
		chapter1.setDescription("");
		chapter1.save();

		Chapter chapter2 = new Chapter();
		chapter2.setPart(part2);
		chapter2.setChapterNo(2);
		chapter2.setTitle("Chapter 2");
		chapter2.setDescription("");
		chapter2.save();

		// scene counter, starts with 1
		int c = 1;
		for (int i = 0; i < TEST1_NUMBER_OF_STRANDS; ++i) {
			Strand strand;
			if(i == 0){
				// get default strand
				strand = StrandPeer.doSelectById(1);
			} else {
				strand = new Strand();
				strand.setName("test strand " + i);
				strand.setAbbreviation("TS"+i);
				strand.setColor(colorArray[i]);
				strand.setSort(i);
				strand.save();
			}
			logger.info("inserted strand: " + strand);
			for (int j = 0; j < TEST1_NUMBER_OF_SCENES; ++j) {
				Scene scene = new Scene();
				scene.setStrand(strand);
				if (j % 2 == 0) {
					scene.setChapter(chapter1);
				} else {
					scene.setChapter(chapter2);
				}
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, 2008);
				cal.set(Calendar.MONTH, 2);
				cal.set(Calendar.DAY_OF_MONTH, 1 + j);
				scene.setDate(cal);
				scene.setSceneNo(c);
				scene.setTitle("scene " + i + "." + j);
				scene.setText("test: " + i + "." + j);
				scene.setStatus(Scene.Status.DONE.ordinal());
				scene.save();
				logger.info("inserted strand cell: " + scene);
				++c;
			}
			// make scene on same date
			if(strand.getId() == 2){
				Scene scene = new Scene();
				scene.setChapter(chapter1);
				scene.setStrand(strand);
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, 2008);
				cal.set(Calendar.MONTH, 2);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				scene.setDate(cal);
				scene.setSceneNo(c);
				scene.setText("second item on 2008-03-01");
				scene.save();
				logger.info("inserted scene: " + scene);
				++c;
			}
		}
		
		// make characters
		SbCharacterTest characterTest = new SbCharacterTest();
		characterTest.testInsert();

		// make locations
		LocationTest locationTest = new LocationTest();
		locationTest.testInsert();

		instance.getPersistenceManager().closeConnection();
	}

	public void generateDb2() throws Exception {
		instance.getPersistenceManager().init("Test 02");
		instance.getPersistenceManager().initDbModel();

		Chapter chapter1 = new Chapter();
		chapter1.setPart(PartPeer.getFirstPart());
		chapter1.setChapterNo(1);
		chapter1.setTitle("Chapter 1");
		chapter1.setDescription("First chapter");
		chapter1.save();

		Chapter chapter2 = new Chapter();
		chapter2.setPart(PartPeer.getFirstPart());
		chapter2.setChapterNo(2);
		chapter2.setTitle("Chapter 2");
		chapter2.setDescription("Second chapter");
		chapter2.save();

		// scene counter starts with 1
		int c = 1;
		for (int i = 0; i < TEST2_NUMBER_OF_STRANDS; ++i) {
			Strand strand;
			if(i == 0){
				// get default strand
				strand = StrandPeer.doSelectById(1);
			} else {
				strand = new Strand();
				strand.setName("test strand " + i);
				strand.setAbbreviation("TS"+i);
				strand.setColor(colorArray[i]);
				strand.setSort(i);
				strand.save();
			}
			logger.info("inserted strand: " + strand);
			for (int j = 0; j < TEST2_NUMBER_OF_SCENES; ++j) {
				Scene scene = new Scene();
				scene.setChapter(i % 2 == 0 ? chapter1 : chapter2);
				scene.setStrand(strand);
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, 2008);
				cal.set(Calendar.MONTH, 7);
				cal.set(Calendar.DAY_OF_MONTH, 1 + j);
				scene.setDate(cal);
				scene.setSceneNo(c);
				scene.setTitle("scene " + i + "." + j);
				scene.setText("test: " + i + "." + j);
				scene.setStatus(Scene.Status.DONE.ordinal());
				scene.save();
				logger.info("inserted scene: " + scene);
				++c;
			}
		}
		
		// make characters
		SbCharacterTest characterTest = new SbCharacterTest();
		characterTest.testInsert();
		
		// make locations
		LocationTest locationTest = new LocationTest();
		locationTest.testInsert();

		instance.getPersistenceManager().closeConnection();
	}
}
