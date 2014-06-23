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

package ch.intertec.storybook.chart;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.time.DateUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.Layer;

import ch.intertec.storybook.chart.jfreechart.JFreeChartTools;
import ch.intertec.storybook.model.Gender;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class CharacterGanttChart extends AbstractChartFrame implements
		ActionListener {

	public final static String CHART_NAME = "CharacterGantt";

	private ChartPanel chartPanel;
	private List<JCheckBox> chbCategoryList;
	private List<JCheckBox> chbCharacterList;
	private JCheckBox chbShowPeriodsOfLife;
	private JPanel characterPanel;
	private Set<Integer> selectedCharactersIdSet;
	
	@Override
	protected void init() {
		// category check boxes
		chbCategoryList = ChartTools.createCategoryCheckBoxes(this);
		chbCharacterList = ChartTools.createCharacterCheckBoxes(
				chbCategoryList, this);
		chbShowPeriodsOfLife = new JCheckBox(
				I18N.getMsg("msg.chart.gantt.periods.life"));
		chbShowPeriodsOfLife.setOpaque(false);
		chbShowPeriodsOfLife.setSelected(true);
		chbShowPeriodsOfLife.addActionListener(this);
		
		characterPanel = new JPanel(new MigLayout("wrap 9"));
		selectedCharactersIdSet = new TreeSet<Integer>();
	}
	
	@Override
	protected void initGUI() {
		setTitle(I18N.getMsg("msg.chart.gantt.characters.title"));
		
		// character check boxes
		characterPanel.removeAll();
		chbCharacterList = ChartTools.createCharacterCheckBoxes(
				chbCategoryList, this);
		for (JCheckBox chb : chbCharacterList) {
			characterPanel.add(chb);
			int id = (Integer) chb.getClientProperty(
					SbCharacter.SB_CHARACTER_ID_KEY);
			if (selectedCharactersIdSet.contains(id)) {
				chb.setSelected(true);
			}
		}
		
		// chart
		IntervalCategoryDataset dataset = createDataset();
		JFreeChart chart = createChart(dataset);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(400, 280));
		
		// options panel
		JLabel lbCategory = new JLabel(I18N.getMsgColon("msg.common.category"));
		optionsPanel.add(lbCategory);
		for (JCheckBox chb : chbCategoryList) {
			optionsPanel.add(chb);
		}
		// show periods of life
		optionsPanel.add(chbShowPeriodsOfLife, "wrap,gap left 20");
		// character panel
		optionsPanel.add(characterPanel, "span");

		// main panel
		panel.add(chartPanel, "grow");
	}

	private IntervalCategoryDataset createDataset() {
		// get characters by category
		List<SbCharacter> list = ChartTools
				.getCharacterListBySelectedCharacters(chbCharacterList);

		final TaskSeries serieLifetime = new TaskSeries(
				I18N.getMsg("msg.chart.gantt.lifetime"));
		final TaskSeries serieChildhood = new TaskSeries(
				I18N.getMsg("msg.chart.gantt.childhood"));
		final TaskSeries serieAdolescence = new TaskSeries(
				I18N.getMsg("msg.chart.gantt.adolescence"));
		final TaskSeries serieAdulthood = new TaskSeries(
				I18N.getMsg("msg.chart.gantt.adulthood"));
		final TaskSeries serieRetirement = new TaskSeries(
				I18N.getMsg("msg.chart.gantt.retirement"));
		

		for (SbCharacter character : list) {
			Date birthday = character.getBirthday();
			if (birthday == null) {
				birthday = ScenePeer.getFirstDate(false);
			}
			Date dayOfDeath = character.getDayOfDeath();
			if (dayOfDeath == null) {
				dayOfDeath = ScenePeer.getLastDate(false);
			}

			SimpleTimePeriod lifetime = new SimpleTimePeriod(birthday,
					dayOfDeath);
			Task task = new Task(character.toString(), lifetime);
			serieLifetime.add(task);
			
			if (!chbShowPeriodsOfLife.isSelected()) {
				continue;
			}
			
			Gender gender = character.getGender();
			Date childhoodEnd = DateUtils.addYears(birthday,
					gender.getChildhood());
			SimpleTimePeriod childhood = new SimpleTimePeriod(birthday,
					childhoodEnd);
			task = new Task(character.toString(), childhood);
			serieChildhood.add(task);
			
			Date adolescenceEnd = DateUtils.addYears(childhoodEnd,
					gender.getAdolescence());
			SimpleTimePeriod adolescence = new SimpleTimePeriod(childhoodEnd,
					adolescenceEnd);
			task = new Task(character.toString(), adolescence);
			serieAdolescence.add(task);
			
			Date adulthoodEnd = DateUtils.addYears(
					adolescenceEnd, gender.getAdulthood());
			SimpleTimePeriod adulthood = new SimpleTimePeriod(
					adolescenceEnd, adulthoodEnd);
			task = new Task(character.toString(), adulthood);
			serieAdulthood.add(task);
			
			Date retirementEnd = DateUtils.addYears(
					adulthoodEnd, gender.getRetirement());
			SimpleTimePeriod retirement = new SimpleTimePeriod(
					adulthoodEnd, retirementEnd);
			task = new Task(character.toString(), retirement);
			serieRetirement.add(task);			
		}

		final TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(serieLifetime);
		if (chbShowPeriodsOfLife.isSelected()) {
			collection.add(serieChildhood);
			collection.add(serieAdolescence);
			collection.add(serieAdulthood);
			collection.add(serieRetirement);
		}
		return collection;
	}

	private JFreeChart createChart(final IntervalCategoryDataset dataset) {
		final JFreeChart chart = ChartFactory.createGanttChart(
			I18N.getMsg("msg.chart.gantt.characters.title"), // chart title
            I18N.getMsg("msg.common.person"), // domain axis label
            I18N.getMsg("msg.common.date"), // range axis label
            dataset,             // data
            true,                // include legend
            true,                // tool tips
            false                // URLs
		);
		CategoryPlot plot = (CategoryPlot)chart.getPlot();
		GanttRenderer renderer = (GanttRenderer) plot.getRenderer();
		
		// show marker "project duration"
		Date start = ScenePeer.getFirstDate(false);
		Date end = ScenePeer.getLastDate(false);
		plot.addRangeMarker(
				JFreeChartTools.getDateIntervalMarker(start, end,
						I18N.getMsg("msg.chart.common.project.duration")),
				Layer.BACKGROUND);
		
		// set nice colors
		JFreeChartTools.setNiceSeriesColors(dataset, renderer);

		return chart;
	}

	@Override
	public String getChartName() {
		return CHART_NAME;
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() instanceof JCheckBox) {
			JCheckBox chb = (JCheckBox) evt.getSource();
			if (ChartTools.COMP_NAME_CHB_CHARACTER.equals(chb.getName())) {
				Integer id = (Integer) chb.getClientProperty(
						SbCharacter.SB_CHARACTER_ID_KEY);
				if (chb.isSelected()) {
					selectedCharactersIdSet.add(id);
				} else {
					selectedCharactersIdSet.remove(id);
				}
			}
		}
		refresh();
	}

	@Override
	protected void beforeRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void afterRefresh() {
		// TODO Auto-generated method stub
		
	}
}
