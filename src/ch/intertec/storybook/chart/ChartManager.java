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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.PCSDispatcher.Property;
import ch.intertec.storybook.toolkit.ReflectionTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;

public class ChartManager implements PropertyChangeListener {
	private Set<IChart> chartSet;

	private static ChartManager theInstance;

	// actions
	private AbstractAction characterSceneAction;
	private AbstractAction characterDateAction;
	private AbstractAction strandDateAction;
	private AbstractAction characterOccurrenceAction;
	private AbstractAction whoIsWhereWhenAction;
	private AbstractAction locationOccurrenceAction;
	private AbstractAction characterGanttChartAction;

	private ChartManager() {
		chartSet = new HashSet<IChart>();
		PCSDispatcher.getInstance().addPropertyChangeListener(Property.PROJECT,
				this);
	}

	public static ChartManager getInstance() {
		if (theInstance == null) {
			theInstance = new ChartManager();
		}
		return theInstance;
	}

	public JFrame getChart(String chartClassName) {
		JFrame frame = (JFrame) ReflectionTools.instance("chart."
				+ chartClassName);
		IChart chart = (IChart) frame;
		if (chartSet.size() == 0) {
			chartSet.add(chart);
			return frame;
		}
		for (IChart c : chartSet) {
			if (c.getChartName().equals(chart.getChartName())) {
				if (((JFrame) c).isShowing()) {
					return (JFrame) c;
				} else {
					chartSet.remove(c);
					chartSet.add(chart);
					return frame;
				}
			}
		}
		chartSet.add(chart);
		return frame;
	}

	public void showChart(String chartClassName) {
		JFrame frame = getChart(chartClassName);
		if (frame.isShowing()) {
			frame.setVisible(true);
		} else {
			SwingTools.showFrame(frame, MainFrame.getInstance());
		}
	}

	public void closeCharts() {
		for (IChart chart : chartSet) {
			((JFrame) chart).dispose();
		}
		chartSet = new HashSet<IChart>();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (PCSDispatcher.isPropertyFired(Property.PROJECT, evt)) {
			if (PCSDispatcher.isPropertyRemoved(evt)) {
				closeCharts();
			}
		}
	}

	@SuppressWarnings("serial")
	public AbstractAction getCharacterSceneAction() {
		if (characterSceneAction == null) {
			characterSceneAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					ChartManager.getInstance().showChart(
							"CharactersBySceneChart");
				}
			};
		}
		return characterSceneAction;
	}

	@SuppressWarnings("serial")
	public AbstractAction getCharacterDateAction() {
		if (characterDateAction == null) {
			characterDateAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					ChartManager.getInstance().showChart(
							"CharactersByDateChart");
				}
			};
		}
		return characterDateAction;
	}

	@SuppressWarnings("serial")
	public AbstractAction getStrandDateAction() {
		if (strandDateAction == null) {
			strandDateAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					ChartManager.getInstance().showChart("StrandsByDateChart");
				}
			};
		}
		return strandDateAction;
	}

	@SuppressWarnings("serial")
	public AbstractAction getCharacterOccurrenceAction() {
		if (characterOccurrenceAction == null) {
			characterOccurrenceAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					ChartManager.getInstance().showChart(
							"OccurrenceOfCharactersChart");
				}
			};
		}
		return characterOccurrenceAction;
	}

	@SuppressWarnings("serial")
	public AbstractAction getWhoIsWhereWhenAction() {
		if (whoIsWhereWhenAction == null) {
			whoIsWhereWhenAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					ChartManager.getInstance().showChart("WhoIsWhereWhenChart");
				}
			};
		}
		return whoIsWhereWhenAction;
	}

	@SuppressWarnings("serial")
	public AbstractAction getLocationOccurrenceAction() {
		if (locationOccurrenceAction == null) {
			locationOccurrenceAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					ChartManager.getInstance().showChart(
							"OccurrenceOfLocationsChart");
				}
			};
		}
		return locationOccurrenceAction;
	}

	@SuppressWarnings("serial")
	public AbstractAction getCharacterGanttChartAction() {
		if (characterGanttChartAction == null) {
			characterGanttChartAction = new AbstractAction() {
				public void actionPerformed(ActionEvent evt) {
					ChartManager.getInstance().showChart("CharacterGanttChart");
				}
			};
		}
		return characterGanttChartAction;
	}
}
