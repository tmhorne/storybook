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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.Gender;
import ch.intertec.storybook.model.GenderPeer;
import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.model.Tag;

public class GenerateDemoProject extends AbstractDemo {

	private Part part1;
	private Part part2;

	private Chapter chapter1;
	private Chapter chapter2;
	private Chapter chapter3;

	private Scene sceneBreakfastAtHome;
	private Scene sceneHomerInTheOffice;
	private Scene sceneTheKidsInSchool;
	private Scene sceneMargeWashingUp;
	private Scene sceneLisaVisitsHerDad;
	private Scene sceneTheNextDay;
	
	private Strand strandMain;
	private Strand strandHomer;
	private Strand strandKids;

	private SbCharacter characterHomer;
	private SbCharacter characterMarge;
	private SbCharacter characterBart;
	private SbCharacter characterLisa;
	private SbCharacter characterMaggie;
	private SbCharacter characterKrusty;

	private Location locationHome;
	private Location locationSchool;
	private Location locationOffice;
	private Location locationMall;
	@SuppressWarnings("unused")
	private Location locationMainStation;
	@SuppressWarnings("unused")
	private Location locationNYC;
	
	private Gender genderClow;
	
	private Item itemCar;
	private Item itemTv;
	private Item itemBike;
	private Item itemMonkey;
	
	private Tag tagSunny;
	private Tag tagRainy;
	
	private static Color[] colors;

	private GenerateDemoProject instance;

	private static final int NUMBER_OF_STRANDS = 3;

	public GenerateDemoProject() {
		super();
		colors = new Color[NUMBER_OF_STRANDS];
		colors[0] = new Color(255, 255, 200);
		colors[1] = new Color(245, 152, 152);
		colors[2] = new Color(152, 152, 245);
	}

	@BeforeClass
	public void setUp() {
		// nothing to do
	}

	@Test
	public void init() throws Exception {
		instance = new GenerateDemoProject();
		createObjects();
		instance.getPersistenceManager().closeConnection();
	}

	@Override
	public void createParts() throws Exception {
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
	protected void createChapters() throws Exception {
		// chapter 1
		chapter1 = new Chapter();
		chapter1.setPart(part1);
		chapter1.setChapterNo(1);
		chapter1.setTitle("An exciting day");
		chapter1.setDescription("Things gonna happen");
		chapter1.save();

		// chapter 2
		chapter2 = new Chapter();
		chapter2.setPart(part1);
		chapter2.setChapterNo(2);
		chapter2.setTitle("Another exciting day");
		chapter2.setDescription("More things gonna happen");
		chapter2.save();

		// chapter 3
		chapter3 = new Chapter();
		chapter3.setPart(part2);
		chapter3.setChapterNo(3);
		chapter3.setTitle("The next Part");
		chapter3.setDescription("[Parts can be usefull with large projects]");
		chapter3.save();
	}

	@Override
	protected void createStrands() throws Exception {
		// main strand
		strandMain = StrandPeer.doSelectById(1);
		strandMain.setAbbreviation("MN");
		strandMain.setName("Main Strand");
		strandMain.setColor(colors[0]);
		strandMain.setSort(0);
		strandMain.save();

		// Homer strand
		strandHomer = new Strand();
		strandHomer.setName("Homer Strand");
		strandHomer.setAbbreviation("HOM");
		strandHomer.setColor(colors[1]);
		strandHomer.setSort(1);
		strandHomer.save();

		// kids strand
		strandKids = new Strand();
		strandKids.setName("Kids Strand");
		strandKids.setAbbreviation("KIDS");
		strandKids.setColor(colors[2]);
		strandKids.setSort(2);
		strandKids.save();
	}

	@Override
	protected void createScenes() throws Exception {
		// create warning
		Calendar dummyDay = Calendar.getInstance();
		dummyDay.set(Calendar.YEAR, 2009);
		dummyDay.set(Calendar.MONTH, 2);
		dummyDay.set(Calendar.DAY_OF_MONTH, 16);

		// day 1
		Calendar day1 = Calendar.getInstance();
		day1.set(Calendar.YEAR, 2009);
		day1.set(Calendar.MONTH, 2);
		day1.set(Calendar.DAY_OF_MONTH, 17);

		sceneBreakfastAtHome = makeScene(
				day1,
				chapter1,
				strandMain,
				"Breakfast at home",
				"The Simpson are at home and eat breakfast. "
						+ "Marge says she had to go to the mall afterwards. "
						+ "After the breakfast, Homer goes to work. The kids "
						+ "go to school."
						+ "\n\n[Move over the labels to see a quick info. "
						+ "If it is a character, you also see the current age.]");
		makeLocationLink(sceneBreakfastAtHome, locationHome);
		makeCharacterLink(sceneBreakfastAtHome, characterHomer);
		makeCharacterLink(sceneBreakfastAtHome, characterMarge);
		makeCharacterLink(sceneBreakfastAtHome, characterBart);
		makeCharacterLink(sceneBreakfastAtHome, characterLisa);
		makeCharacterLink(sceneBreakfastAtHome, characterMaggie);
		makeStrandLink(sceneBreakfastAtHome, strandKids);
		makeStrandLink(sceneBreakfastAtHome, strandHomer);

		sceneHomerInTheOffice = makeScene(day1, chapter1, strandHomer,
				"Homer in the office",
				"Homer sits at his place and yawns all the time. "
						+ "It was a long night.");
		makeLocationLink(sceneHomerInTheOffice, locationOffice);
		makeCharacterLink(sceneHomerInTheOffice, characterHomer);

		sceneMargeWashingUp = makeScene(day1, chapter1, strandMain, "Marge washing up",
				"Marge makes the washing up. "
						+ "She looks outside the window. Suddenly a bright "
						+ "flash blinds her.");
		makeLocationLink(sceneMargeWashingUp, locationHome);
		makeCharacterLink(sceneMargeWashingUp, characterMarge);

		sceneTheKidsInSchool = makeScene(
				day1,
				chapter1,
				strandKids,
				"The kids in school",
				"Lisa has to make a math test. Of course she "
						+ "knows all the answers and is finished before all "
						+ " other kids."
						+ "\nBart has math, too, but he zones out. "
						+ "Because he is boring, he goes to the toilet. Then "
						+ "he throws a stink bomb to the teachers' lounge. "
						+ "\n\nThe school is evacuated. Lisa is mad about Bart. "
						+ "Although she had to go home, she goes to Dad's office instead.");
		makeLocationLink(sceneTheKidsInSchool, locationSchool);
		makeCharacterLink(sceneTheKidsInSchool, characterBart);
		makeCharacterLink(sceneTheKidsInSchool, characterLisa);

		sceneLisaVisitsHerDad = makeScene(
				day1,
				chapter1,
				strandHomer,
				"Lisa visits her Dad",
				"As Lisa enters the office, her dad is sleeping. "
						+ "Lisa calls up Homer and tells him about the event at school.");
		makeLocationLink(sceneLisaVisitsHerDad, locationOffice);
		makeCharacterLink(sceneLisaVisitsHerDad, characterHomer);
		makeCharacterLink(sceneLisaVisitsHerDad, characterLisa);
		makeStrandLink(sceneLisaVisitsHerDad, strandKids);

		// day2
		Calendar day2 = (Calendar) day1.clone();
		day2.add(Calendar.DAY_OF_YEAR, 1);
		// reset scene counter
		sceneCounter = 0;

		sceneTheNextDay = makeScene(
				day2,
				chapter2,
				strandMain,
				"The next day",
				"Next day everything seems fine. Except for Marge. "
						+ "Since she saw this mysterious flash, she behaves "
						+ "like a zombie..."
						+ "\n\n[Select \"View Part 2\" from the menu \"Parts\" to read more.]");
		makeLocationLink(sceneTheNextDay, locationHome);
		makeCharacterLink(sceneTheNextDay, characterMarge);
		makeCharacterLink(sceneTheNextDay, characterHomer);
		makeCharacterLink(sceneTheNextDay, characterLisa);
		makeCharacterLink(sceneTheNextDay, characterBart);
		makeStrandLink(sceneTheNextDay, strandKids);
		makeStrandLink(sceneTheNextDay, strandHomer);

		makeScene(day2, chapter2, strandKids, "It's your turn now",
				"And now it's on you to write the most exciting "
						+ "story ever. Now that you have the right tool in "
						+ "your hand. :-)"
						+ "\n\nVisit our Homepage: www.novelist.ch",
				Scene.Status.DONE.ordinal());

		// day3
		Calendar day3 = (Calendar) day2.clone();
		day3.add(Calendar.DAY_OF_YEAR, 1);
		// reset scene counter
		sceneCounter = 0;

		Scene chP2 = makeScene(day3, chapter3, strandMain, "And so on...",
				"And so the story goes on ..."
						+ "\n\n[Stories with a lot of scenes can be split "
						+ "into parts. This way you always have small "
						+ "and clear pieces.]");
		makeLocationLink(chP2, locationMall);
		makeCharacterLink(chP2, characterHomer);
		makeCharacterLink(chP2, characterMarge);
		makeCharacterLink(chP2, characterBart);
		makeCharacterLink(chP2, characterLisa);
		makeStrandLink(chP2, strandHomer);
		makeStrandLink(chP2, strandKids);
	}

	@Override
	protected void createLocations() throws Exception {
		final String countryUSA = "USA";
		final String countrySwitzerland = "Switzerland";
		final String citySpringfield = "Springfield";
		final String cityNYC = "New York City";
		final String cityZurich = "Zurich";

		locationHome = makeLocation("The Simpsons Home", citySpringfield,
				countryUSA, "Where the Simpsons live.");
		locationSchool = makeLocation("School", citySpringfield, countryUSA,
				"Where Bart and Lisa go to school.");
		locationOffice = makeLocation("Atomic Power Plant", citySpringfield,
				countryUSA, "Where Homer works.");
		locationMall = makeLocation("Mall", citySpringfield, countryUSA,
				"Where Marge goes shopping.");
		locationNYC = makeLocation("Central Park", cityNYC, countryUSA,
				"A nice park.");
		locationMainStation = makeLocation("Main Station", cityZurich,
				countrySwitzerland, "Main Train Station");
	}

	@Override
	protected void createTags() throws Exception {
		tagSunny = makeTag("sunny", "Weather", "sunny weather");
		tagRainy = makeTag("rainy", "Weather", "rainy weather");
		
		makeTagLink(tagSunny, sceneBreakfastAtHome, sceneMargeWashingUp);
		makeTagLink(tagRainy, sceneTheNextDay);
	}

	@Override
	protected void createItems() throws Exception {
		itemCar = makeItem("Blue Car", "Car", "A nice blue car.");
		itemTv = makeItem("TV", "Household", "TV makes you happy. No, really.");
		itemBike = makeItem("Bike", "Transport", "A cool mountain bike.");
		itemMonkey = makeItem("Monkey", "Animal", "Escaped from the zoo.");
		
		makeTagLink(itemCar, characterHomer, sceneBreakfastAtHome,
				sceneHomerInTheOffice);
		makeTagLink(itemTv, locationHome);
		makeTagLink(itemBike, characterBart);
		makeTagLink(itemMonkey, characterKrusty, sceneBreakfastAtHome,
				sceneMargeWashingUp);
		makeTagLink(itemMonkey, characterLisa, sceneTheNextDay);
	}
	
	@Override
	protected void createCharacters() throws Exception {
		Gender male = GenderPeer.doSelectById(Gender.MALE);
		Gender female = GenderPeer.doSelectById(Gender.FEMALE);
		characterHomer = makeCharacter(
				"Homer",
				"Simpson",
				"Ho",
				"Homer is the boorish father of the Simpson family. With his wife, Marge, he has three children: Bart, Lisa and Maggie. As the family's provider, he works at the Springfield Nuclear Power Plant. Homer embodies several American working class stereotypes: he is crude, overweight, incompetent, clumsy, and lazy; however, he is also fiercely devoted to his family. Despite the suburban blue-collar routine of his life, he has had a number of remarkable experiences."
						+ "\nReference: en.wikipedia.org", male, "1970-1-17",
				new Color(0x8ae09e), true);
		characterMarge = makeCharacter(
				"Marge",
				"Simpson",
				"Ma",
				"Marge is the well-meaning and extremely patient mother of the Simpson family. With her husband, Homer, she has three children: Bart, Lisa and Maggie. Marge is the moralistic force in her family and often provides a grounding voice in the midst of her family's antics. She always tries to maintain order in the Simpson household. She is often portrayed as a stereotypical television mother and is often included on lists of top \"TV moms\"."
						+ "\nReference: en.wikipedia.org", female, "1972-2-21",
				new Color(0xe0a08a), true);
		characterBart = makeCharacter(
				"Bart",
				"Simpson",
				"Ba",
				"At ten years of age, Bart is the eldest child and only son of Homer and Marge, and the brother of Lisa and Maggie. Bart's most prominent character traits are his mischievousness, rebelliousness and disrespect for authority."
						+ "\nReference: en.wikipedia.org", male, "1998-3-4",
				new Color(0xBFCFFF), true);
		characterLisa = makeCharacter(
				"Lisa",
				"Simpson",
				"Li",
				"At eight years of age, Lisa is the middle child of the Simpson family and eldest daughter of Homer and Marge, younger sister of Bart and elder sister of Maggie. She is highly intelligent, plays the saxophone, has been a vegetarian since the seventh season, has been a Buddhist since season thirteen and supports a number of different causes."
						+ "\nReference: en.wikipedia.org", female, "1999-4-28",
				new Color(0xFFBFEF), true);
		characterMaggie = makeCharacter(
				"Maggie",
				"Simpson",
				"Baby",
				"Maggie is the youngest child of Marge and Homer, and sister to Bart and Lisa. She is often seen sucking on her pacifier, and when she walks she trips over her clothing and falls on her face. Due to the fact that she can not talk, Maggie is the least seen and heard in the Simpson family."
						+ "\nReference: en.wikipedia.org", female, "2007-6-17",
				null, false);
		characterKrusty = makeCharacter(
				"Krusty the Clown",
				"",
				"Kru",
				"Krusty the Clown, or Herschel Krustofski (full name: Herschel Pinkes Remochel Krustofski)",
				genderClow, "1969-3-17", null, false);
	}

	@Override
	protected void createGenders() throws Exception {
		genderClow = makeGender("Clown", 0, 27, 99, 0);
	}
}
