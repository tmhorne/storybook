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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import ch.intertec.storybook.chart.legend.StrandsLegendPanel;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.SceneLinkSbCharacter;
import ch.intertec.storybook.model.SceneLinkSbCharacterPeer;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.ScenePeer.Order;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.ColorUtil;
import ch.intertec.storybook.toolkit.swing.ReadOnlyTable;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.ToolTipHeader;
import ch.intertec.storybook.toolkit.swing.table.ColorTableCellRenderer;
import ch.intertec.storybook.toolkit.swing.table.FixedColumnScrollPane;
import ch.intertec.storybook.toolkit.swing.table.HeaderTableCellRenderer;

@SuppressWarnings("serial")
public class CharactersBySceneChart extends AbstractChartFrame implements
		ActionListener, ChangeListener {
	
	private final static String CHART_NAME = "CharactersByScene";
	private final static int INIT_COL_WIDTH = 50;
	
	private List<JCheckBox> chbCategoryList;
	private JCheckBox chbShowUnusedChracters;
	private JTable table;
	private JSlider colSlider;
	private int savedSliderValue = 0;

	public CharactersBySceneChart(){
		super();
		PCSDispatcher.getInstance().addPropertyChangeListener(
				Property.ACTIVE_PART, this);
	}

	@Override
	protected void init() {
		// category check boxes
		chbCategoryList = ChartTools.createCategoryCheckBoxes(this);
		// show unused characters
		chbShowUnusedChracters = new JCheckBox();
		chbShowUnusedChracters.setText(
				I18N.getMsg("msg.chart.common.unused.characters"));
		chbShowUnusedChracters.setOpaque(false);
		chbShowUnusedChracters.addActionListener(this);
	}

	@Override
	protected void initGUI() {
		// title
		int activePartId = MainFrame.getInstance().getActivePartId();
		Object[] objs = new Object[3];
		objs[0] = ProjectTools.getProjectName();
		objs[1] = PartPeer.doSelectById(activePartId).getNumberStr();
		objs[2] = PartPeer.doSelectById(activePartId).getName();
		setTitle(I18N.getMsg("msg.report.person.scene.title", objs));

		// table
		table = createTable();
		FixedColumnScrollPane scroller = new FixedColumnScrollPane(table, 1);
		scroller.getRowHeader().setPreferredSize(new Dimension(200, 20));
		scroller.setPreferredSize(new Dimension(Integer.MAX_VALUE,
				Integer.MAX_VALUE));

		// character category
		JLabel lbCategory = new JLabel(I18N.getMsgColon("msg.common.category"));
		
		// options panel
		optionsPanel.add(lbCategory);
		for (JCheckBox chb : chbCategoryList) {
			optionsPanel.add(chb);
		}
		optionsPanel.add(chbShowUnusedChracters, "gap left 20");
		
		// column margin slider
		JLabel lbIcon = new JLabel(I18N.getIcon("icon.small.size"));
		colSlider = SwingTools.createSafeSlider(JSlider.HORIZONTAL, 5, 200,
				INIT_COL_WIDTH);
		colSlider.setMinorTickSpacing(1);
		colSlider.setMajorTickSpacing(2);
		colSlider.setSnapToTicks(false);
		colSlider.addChangeListener(this);
		colSlider.setOpaque(false);		
		
		// main panel
		panel.setPreferredSize(new Dimension(400, 300));
		panel.add(scroller, "grow");
		panel.add(lbIcon, "split 3, left");
		panel.add(colSlider, "left");
		panel.add(new StrandsLegendPanel(), "gap push");
	}
		
	private JTable createTable(){
		// characters
		List<SbCharacter> characterList =
			ChartTools.getCharactersBySelectedCategories(chbCategoryList);

		// scene list
		List<Scene> sceneList = ScenePeer
				.doSelectAll(Order.BY_CHAPTER_AND_SCENE_NUMBER);

		// column names
		String[] columnNames = new String[sceneList.size() + 1];
		columnNames[0] = "";
		int i = 1;
		for (Scene scene : sceneList) {
			columnNames[i] = scene.getChapterAndSceneNumber();
			++i;
		}

		// data array
		ArrayList<Object[]> rows = new ArrayList<Object[]>();
		int rowIndex = 0;
		String[] toolTipStr = new String[sceneList.size() + 1];
		for (SbCharacter character : characterList) {
			int columnIndex = 0;
			Object[] columns = new Object[sceneList.size() + 1];
			columns[columnIndex++] = character;
			boolean found = false;
			for (Scene scene : sceneList) {
				List<SceneLinkSbCharacter> links = SceneLinkSbCharacterPeer
						.doSelect(scene, character);
				if (links.size() > 0) {
					found = true;
					columns[columnIndex] = ColorUtil.darker(scene.getStrand()
							.getColor(), 0.05);
				} else {
					columns[columnIndex] = null;
				}
				toolTipStr[columnIndex] = scene.getInfo(true, true);
				++columnIndex;
			}
			if (!chbShowUnusedChracters.isSelected() && !found) {
				continue;
			}
			rows.add(columns);
			++rowIndex;
		}

		// store ArrayList in an array
		Object[][] data = new Object[rows.size()][];
		i = 0;
		for (Object[] columns : rows) {
			data[i++] = columns;
		}
		
		// create table
		JTable table = new ReadOnlyTable(data, columnNames);
		if (table.getModel().getRowCount() == 0) {
			return table;
		}
		
		// set table preferences
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(0).setCellRenderer(
				new HeaderTableCellRenderer());
		// doesn't work...
		// table.setDefaultRenderer(Color.class, new ColorTableCellRenderer());        
		for (int c = 1; c < table.getColumnCount(); ++c) {
			int columnIndex = table.getColumnModel().getColumn(c)
					.getModelIndex();
			Object o = table.getModel().getValueAt(0, columnIndex);
			TableColumn column = table.getColumnModel().getColumn(c);
			if (o == null || o instanceof Color) {
				column.setPreferredWidth(INIT_COL_WIDTH);
				column.setCellRenderer(new ColorTableCellRenderer(false));
			}
		}
		// disable auto resize mode
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// prohibit reordering
		table.getTableHeader().setReorderingAllowed(false);
		
		ToolTipHeader header = new ToolTipHeader(table.getColumnModel());
	    header.setToolTipStrings(toolTipStr);
	    header.setToolTipText("Default ToolTip TEXT");
	    table.setTableHeader(header);
		
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
	public void stateChanged(ChangeEvent e) {
		for (int c = 0; c < table.getColumnCount(); ++c) {
			TableColumn column = table.getColumnModel().getColumn(c);
			column.setPreferredWidth(colSlider.getValue());
		}
	}

	@Override
	protected void beforeRefresh() {
		savedSliderValue = colSlider.getValue();
	}

	@Override
	protected void afterRefresh() {
		colSlider.setValue(savedSliderValue);
	}
}

final class ChapterTableCellRenderer implements TableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(
			JTable table, Object value,
			boolean isSelected, boolean hasFocus,
			int row, int column) {
		Color newColor;
		if (isSelected) {
			Color selected = UIManager.getColor("Table.selectionBackground");
			Color color = Color.lightGray;
			newColor = ColorUtil.blend(color, selected, 0.8);
		} else {
			newColor = Color.lightGray;
		}
		JLabel label = new JLabel();
		label.setOpaque(true);
		label.setBackground(newColor);
		return label;
	}
}
