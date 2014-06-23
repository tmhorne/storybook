/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008-2009 Martin Mustun

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
import java.io.File;
import java.sql.Date;
import java.util.Calendar;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.Gender;
import ch.intertec.storybook.model.GenderPeer;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.toolkit.RandomTextGenerator;
import ch.intertec.storybook.toolkit.swing.SwingTools;

public class GenerateLargeDemoProject extends AbstractDemo {

	private Part part1;
	private Part part2;

	private Chapter[] chapters;
	private Strand[] strands;
	private SbCharacter[] characters;
	private Location[] locations;
	
	private final String[] firstNamesMale = { "Joe", "Ben", "Gilip",
			"Alberemy", "Seal", "Tert", "Peth", "Alexancis", "Philbert",
			"Clane", "Josharreg", "Jest", "Grence", "Duan", "Earrio",
			"Kelvadon", "Wennest", "Juliftor", "Dent", "Billiam", "Edwaymond",
			"Chrique", "Daniel", "Bob", "Bill" };
	private final String[] firstNamesFemale = { "Anna", "Amy", "Verri",
			"Colorette", "Lindy", "Jacquelly", "Andsay", "Stalie", "Jeanda",
			"Glorole", "Melyn", "Annifer", "Danette", "Jeah", "Roberiam",
			"Gerace", "Katachel", "Dail", "Maura", "Soniffany", "Wane",
			"Linnifer" };
	private final String[] lastNames = { "Smith", "Bush", "Grishall", "Gill",
			"Hilterce", "Hamston", "Jack", "Nice", "Wardy", "Mckins",
			"Colliams", "Whinnez", "Howman", "Hanks", "Evarshop", "Reemidt",
			"Fershings", "Howarza", "Rodriffin", "Monanks", "Chald", "Jores",
			"Abbott", "Anderson", "Andrews", "Armstrong", "Atkins", "Bond",
			"Barker", "Boyle", "Brandon", "Dillon" };
	private final String[] cities = { "Franklin", "Salem", "Washington",
			"Springfield", "Clinton", "Georgetown", "Greenville", "Madison",
			"Fairview", "Manchester", "Oak Grove", "Marion", "Ashland",
			"Oxford", "Centerville", "Berryville", "Black Oak", "Brookland",
			"Carlisle", "Cotter", "De Queen", "Fountain Lake" };
	private final String[] countries = { "USA", "Canada", "Switzerland" };
	private final String[] locationNames = { "Pershing Road", "Near South",
			"Calumet Park Beach", "Auburn Gresham",
			"Center for Green Technology", "North Lawndale", "West Pullman",
			"Pasteur Park", "Marquette Park", "Riis Park", "Nature Museum",
			"Main Station", "Gym", "Science Building", "Security Center",
			"Forest", "House of Secrets", "Health Service Center",
			"Campus Branch Office", "Media Center", "Cafe", "Church",
			"Restaurant", "Mall", "Look-out", "Beach", "Island", "Bus Stop",
			"Hospital", "Baths", "Indoor Swimming Pool", "Sports Field",
			"School", "Kindergarten", "Cinema", "Office", "City Hall",
			"Gas Station" };
	private final Color[] colors = { SwingTools.getNiceBlue(),
			SwingTools.getNiceRed(), SwingTools.getNiceYellow() };
	
	private GenerateLargeDemoProject instance;
		
	private static final int NUMBER_OF_STRANDS = 3;
	private static final int NUMBER_OF_CHAPTERS = 9;
	private static final int NUMBER_OF_CHARACTERS = 15;
	private static final int NUMBER_OF_LOCATIONS = 25;
	private static final int NUMBER_OF_SCENES = 50;
	
	private static Color[] strandColors;
	
	public GenerateLargeDemoProject(){
		super(new File("/home/martin/workspace_java/storybook/temp/LargeDemo"));
		strands = new Strand[NUMBER_OF_STRANDS];
		strandColors = new Color[NUMBER_OF_STRANDS];
		strandColors[0] = new Color(255, 255, 200);
		strandColors[1] = new Color(245, 152, 152);
		strandColors[2] = new Color(152, 152, 245);
		chapters = new Chapter[NUMBER_OF_CHAPTERS];
		characters = new SbCharacter[NUMBER_OF_CHARACTERS];
		locations = new Location[NUMBER_OF_LOCATIONS];
	}
	
	@BeforeClass
	public void setUp() {
		// nothing to do
	}

	@Test
	public void init() throws Exception {
		instance = new GenerateLargeDemoProject();
		createObjects();
		instance.getPersistenceManager().closeConnection();
	}

	@Override
	protected void createChapters() throws Exception {
		for (int i = 0; i < NUMBER_OF_CHAPTERS; ++i) {
			Chapter chapter = new Chapter();
			chapter.setPart(i < 8 ? part1 : part2);
			chapter.setChapterNo(i + 1);
			chapter.setTitle("Chapter " + (i+1));
			chapter.setDescription("");
			chapter.save();
			chapters[i] = chapter;
		}
	}

	@Override
	protected void createCharacters() throws Exception {
		for (int i = 0; i < NUMBER_OF_CHARACTERS; ++i) {
			SbCharacter character = new SbCharacter();
			int gender = randomGenerator.nextInt() % 2 + 1;
			character.setGender(GenderPeer.doSelectById(gender));
			String firstName = "";
			if (gender == Gender.FEMALE) {
				firstName = getRandomValue(firstNamesFemale);
			} else {
				firstName = getRandomValue(firstNamesMale);
			}
			firstName = getRandomValue(firstNamesFemale);
			character.setFirstname(firstName);
			String lastname = getRandomValue(lastNames);
			character.setLastname(lastname);
			character.setCategory(i < 10 ? SbCharacter.CATEGORY_CENTRAL
					: SbCharacter.CATEGORY_MINOR);
			character.setAbbreviation(firstName.substring(0, 2)
					+ lastname.substring(0, 2));
			Date birthday = getRandomBirthday();
			character.setBirthday(birthday);
			character.setDayOfDeath(getRandomDayOfDeath(birthday));
			if (randomGenerator.nextBoolean()) {
				character.setColor(getRandomColor());
			}
			character.save();
			characters[i] = character;
		}
	}

	@Override
	protected void createLocations() throws Exception {
		for (int i = 0; i < NUMBER_OF_LOCATIONS; ++i) {
			Location location = new Location();
			location.setName(getRandomLocationName());
			location.setCountry(getRandomValue(countries));
			location.setCity(getRandomValue(cities));
			location.save();
			locations[i] = location;
		}
	}

	@Override
	protected void createParts() throws Exception {
		// part 1
		part1 = PartPeer.getFirstPart();
		part1.setName("Intro");
		part1.save();

		// part 2
		part2 = new Part();
		part2.setNumber(2);
		part2.setName("The Day after the Intro");
		part2.save();
	}

	@Override
	protected void createScenes() throws Exception {		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2008);
		cal.set(Calendar.MONTH, 2);
		cal.set(Calendar.DAY_OF_MONTH, 16);

		for (int i = 0; i < NUMBER_OF_SCENES; ++i) {
			if (randomGenerator.nextInt(3) == 0) {
				cal.add(Calendar.DAY_OF_YEAR, 1);
			}
			Scene scene = makeScene(
					cal,
					getRandomChapter(),
					getRandomStrand(),
					RandomTextGenerator.generateText(30),
					RandomTextGenerator.generateText(1000),
					getRandomStatus()
					);
			for (int a = 0; a < randomGenerator.nextInt(3) + 1; ++a) {
				makeLocationLink(scene, getRandomLocation());
			}
			for (int a = 0; a < randomGenerator.nextInt(4) + 1; ++a) {
				makeCharacterLink(scene, getRandomCharacter());
			}
		}
		
		// sort scenes
		for (Chapter chapter : chapters) {
			ChapterPeer.renumberScenes(chapter);
		}
	}

	@Override
	protected void createStrands() throws Exception {
		strands[0] = StrandPeer.doSelectById(1);
		for (int i = 1; i < NUMBER_OF_STRANDS; ++i) {
			Strand strand = new Strand();
			strand.setAbbreviation("S" + i);
			strand.setName("Strand " + i);
			strand.setColor(strandColors[i - 1]);
			strand.setSort(i);
			strand.save();
			strands[i] = strand;
		}
	}

	private static String getRandomValue(String[] strs) {
		int i = randomGenerator.nextInt(strs.length);
		return strs[i];
	}
	
	private static Date getRandomBirthday() {
		Calendar cal = Calendar.getInstance();
		cal.set(1950 + randomGenerator.nextInt(55),
				randomGenerator.nextInt(12),
				randomGenerator.nextInt(31));
		return new Date(cal.getTime().getTime());
	}
	
	private static Date getRandomDayOfDeath(Date birthday) {
		Date date = null;
		while (date == null || birthday.after(date)) {
			date = getRandomBirthday();
		}
		return date;
	}

	private Chapter getRandomChapter() {
		return chapters[randomGenerator.nextInt(chapters.length)];
	}
	
	private Strand getRandomStrand() {
		return strands[randomGenerator.nextInt(strands.length)];
	}
	
	private Location getRandomLocation() {
		return locations[randomGenerator.nextInt(locations.length)];
	}

	private SbCharacter getRandomCharacter() {
		return characters[randomGenerator.nextInt(characters.length)];
	}
	
	private Color getRandomColor() {
		return colors[randomGenerator.nextInt(colors.length)];
	}
	
	private int getRandomStatus() {
		Scene.Status[] allStatus = Scene.Status.values();
		int i = randomGenerator.nextInt(allStatus.length);
		return Scene.Status.values()[i].ordinal();
	}
	
	private String getRandomLocationName() {
		return locationNames[randomGenerator.nextInt(locationNames.length)];
	}

	@Override
	protected void createTags() throws Exception {
	}
	
	@Override
	protected void createItems() throws Exception {
	}

	@Override
	protected void createGenders() throws Exception {
	}
}
