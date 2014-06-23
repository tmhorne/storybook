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
import java.beans.PropertyChangeEvent;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.Day;
import org.jfree.data.time.Week;
import org.jfree.ui.TextAnchor;

import ch.intertec.storybook.chart.jfreechart.JFreeChartTools;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.SceneLinkSbCharacter;
import ch.intertec.storybook.model.SceneLinkSbCharacterPeer;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;

@SuppressWarnings("serial")
public class CharactersByDateChart extends AbstractChartFrame implements
		ActionListener {

	private final static String CHART_NAME = "CharactersByDate";

	private ChartPanel chartPanel;
	private List<JCheckBox> chbCategoryList;

	public CharactersByDateChart() {
		super();
		PCSDispatcher.getInstance().addPropertyChangeListener(
				Property.ACTIVE_PART, this);
	}

	@Override
	protected void init() {
		// category check boxes
		chbCategoryList = ChartTools.createCategoryCheckBoxes(this);
	}

	@Override
	protected void initGUI() {
		int activePartId = MainFrame.getInstance().getActivePartId();
		Object[] objs = new Object[3];
		objs[0] = ProjectTools.getProjectName();
		objs[1] = PartPeer.doSelectById(activePartId).getNumberStr();
		objs[2] = PartPeer.doSelectById(activePartId).getName();
		setTitle(I18N.getMsg("msg.report.person.date.title", objs));

		// chart
		IntervalCategoryDataset dataset = createDataset();
		JFreeChart chart = createChart(dataset);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(400, 280));

		// character category
		JLabel lbCategory = new JLabel(I18N.getMsgColon("msg.common.category"));
		optionsPanel.add(lbCategory);
		for (JCheckBox chb : chbCategoryList) {
			optionsPanel.add(chb);
		}

		// main panel
		panel.add(chartPanel, "grow");
	}

	private IntervalCategoryDataset createDataset() {
		// get characters by category
		List<SbCharacter> list = ChartTools
				.getCharactersBySelectedCategories(chbCategoryList);

		final TaskSeries s1 = new TaskSeries("Serie 1");
		for (SbCharacter character : list) {
			TreeSet<Date> dateSet = new TreeSet<Date>();
			for (Chapter chapter : ChapterPeer.doSelectAll(true)) {
				for (Scene scene : ScenePeer.doSelectByChapter(chapter)) {
					List<SceneLinkSbCharacter> linkList = SceneLinkSbCharacterPeer
							.doSelect(scene, character);
					if (linkList.isEmpty()) {
						continue;
					}
					dateSet.add(scene.getDate());
				}
			}
			if (dateSet.isEmpty()) {
				continue;
			}
			Task task = new Task(character.toString(),
					new Week(dateSet.first()));
			for (Date date : dateSet) {
				Task subtask = new Task(character.toString(), new Day(date));
				task.addSubtask(subtask);
			}
			s1.add(task);
		}

		final TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(s1);

		return collection;
	}

	private JFreeChart createChart(final IntervalCategoryDataset dataset) {
		final JFreeChart chart = ChartFactory.createGanttChart(
				I18N.getMsg("msg.menu.tools.charts.overall.character.date"), // chart title
				I18N.getMsg("msg.common.person"), // domain axis label
				I18N.getMsg("msg.common.date"), // range axis label
				dataset, // data
				true, // include legend
				true, // tool tips
				false // URLs
				);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		GanttRenderer renderer = (GanttRenderer) plot.getRenderer();

		// hide legend
		JFreeChartTools.hideLegend(plot);

		// item label position
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator();
		renderer.setBaseItemLabelGenerator(generator);
		renderer.setBaseItemLabelsVisible(true);
		ItemLabelPosition pos = new ItemLabelPosition(ItemLabelAnchor.CENTER,
				TextAnchor.CENTER);
		renderer.setBasePositiveItemLabelPosition(pos);

		// set nice colors
		JFreeChartTools.setNiceSeriesColors(dataset, renderer);

		return chart;
	}

	@Override
	public String getChartName() {
		return CHART_NAME;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		refresh();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (PCSDispatcher.isPropertyFired(Property.ACTIVE_PART, evt)) {
			refresh();
			return;
		}
		super.propertyChange(evt);
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
