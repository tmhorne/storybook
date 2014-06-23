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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;

import org.apache.commons.lang3.ArrayUtils;

import ch.intertec.storybook.chart.legend.CharactersLegendPanel;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.LocationPeer;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.ReadOnlyTable;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.table.FixedColumnScrollPane;
import ch.intertec.storybook.toolkit.swing.table.WiwwContainer;
import ch.intertec.storybook.toolkit.swing.table.WiwwTableCellRenderer;

@SuppressWarnings("serial")
public class WhoIsWhereWhenChart extends AbstractChartFrame implements
		ActionListener, ChangeListener {

	private final static String CHART_NAME = "WhoIsWhenWhere";
	private final static int INIT_COL_WIDTH = 200;

	private List<JCheckBox> chbCategoryList;
	private Set<SbCharacter> foundCharacters;
	private JTable table;
	private JSlider colSlider;
	private int savedSliderValue = 0;

	public WhoIsWhereWhenChart() {
		super();
		PCSDispatcher.getInstance().addPropertyChangeListener(
				Property.ACTIVE_PART, this);
	}

	@Override
	protected void init() {
		// category check boxes
		chbCategoryList = ChartTools.createCategoryCheckBoxes(this);
		foundCharacters = new TreeSet<SbCharacter>();
	}

	@Override
	protected void initGUI() {
		SwingTools.setWaitCursor();

		// title
		Object[] objs = new Object[1];
		objs[0] = ProjectTools.getProjectName();
		setTitle(I18N.getMsg("msg.report.person.location.time.title", objs));

		// table
		table = createTable();
		FixedColumnScrollPane scroller = new FixedColumnScrollPane(table, 2);
		scroller.getRowHeader().setPreferredSize(new Dimension(300, 20));
		scroller.setPreferredSize(new Dimension(Integer.MAX_VALUE,
				Integer.MAX_VALUE));

		// character category
		JLabel lbCategory = new JLabel(I18N.getMsgColon("msg.common.category"));

		// options panel
		optionsPanel.add(lbCategory);
		for (JCheckBox chb : chbCategoryList) {
			optionsPanel.add(chb);
		}

		// column margin slider
		JLabel lbIcon = new JLabel(I18N.getIcon("icon.small.size"));
		colSlider = SwingTools.createSafeSlider(JSlider.HORIZONTAL, 20, 600,
				INIT_COL_WIDTH);
		colSlider.setMinorTickSpacing(1);
		colSlider.setMajorTickSpacing(2);
		colSlider.setSnapToTicks(false);
		colSlider.addChangeListener(this);
		colSlider.setOpaque(false);
		
		// main panel
		panel.setPreferredSize(new Dimension(400, 300));
		panel.add(scroller, "grow");
		panel.add(lbIcon, "split 2, left");
		panel.add(colSlider, "left");
		panel.add(new CharactersLegendPanel(foundCharacters), "gap push");

		SwingTools.setDefaultCursor();
	}

	private JTable createTable() {
		// characters
		List<SbCharacter> characterList = ChartTools
				.getCharactersBySelectedCategories(chbCategoryList);

		// locations
		List<Location> locationList = LocationPeer.doSelectAll();

		// dates
		Set<Date> dateSet = ScenePeer.doSelectDistinctDate();

		// column names
		Object[] columnNames = ArrayUtils.addAll(
				new Object[] { I18N.getMsg("msg.common.location"), "" },
				dateSet.toArray());

		// build rows
		foundCharacters.clear();
		ArrayList<Object[]> rows = new ArrayList<Object[]>();
		for (Location location : locationList) {
			Object[] row = new Object[columnNames.length];
			int i = 0;
			row[i++] = location.getName();
			row[i++] = location.getCountryCity();
			boolean found = false;
			for (Date date : dateSet) {
				WiwwContainer container = new WiwwContainer(date, location,
						characterList);
				row[i] = container;
				if (container.isFound()) {
					foundCharacters.addAll(container.getCharacterList());
					found = true;
				}
				++i;
			}
			if (found) {
				rows.add(row);
			}
		}

		// fill data array
		Object[][] data = new Object[rows.size()][];
		int i = 0;
		for (Object[] columns : rows) {
			data[i++] = columns;
		}

		// create table
		JTable table = new ReadOnlyTable(data, columnNames);
		for (int c = 2; c < table.getColumnCount(); ++c) {
			TableColumn column = table.getColumnModel().getColumn(c);
			column.setPreferredWidth(120);
			column.setCellRenderer(new WiwwTableCellRenderer());
		}
		// disable auto resize mode
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// prohibit reordering
		table.getTableHeader().setReorderingAllowed(false);

		return table;
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
		savedSliderValue = colSlider.getValue();
	}

	@Override
	protected void afterRefresh() {
		colSlider.setValue(savedSliderValue);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		for (int c = 0; c < table.getColumnCount(); ++c) {
			TableColumn column = table.getColumnModel().getColumn(c);
			column.setPreferredWidth(colSlider.getValue());
		}
	}
}
